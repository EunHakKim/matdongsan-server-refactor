package com.mds.domain.story.service;

import com.mds.OpenAiClient;
import com.mds.TtsClient;
import com.mds.common.utils.ResponseParser;
import com.mds.common.utils.S3Utils;
import com.mds.domain.follow.repository.FollowRepository;
import com.mds.domain.library.service.LibraryService;
import com.mds.domain.member.entity.Member;
import com.mds.domain.member.repository.MemberRepository;
import com.mds.domain.story.entity.StoryLike;
import com.mds.domain.story.entity.mongo.Language;
import com.mds.domain.story.entity.mongo.Story;
import com.mds.domain.story.dto.StoryDto;
import com.mds.domain.story.exception.StoryErrorCode;
import com.mds.domain.story.exception.StoryException;
import com.mds.domain.story.repository.StoryLikeRepository;
import com.mds.domain.story.repository.mongo.StoryRepository;
import com.mds.model.TtsClientResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoryService {

    private final StoryRepository storyRepository;
    private final MemberRepository memberRepository;
    private final StoryLikeRepository storyLikeRepository;
    private final LibraryService libraryService;
    private final StoryCacheService storyCacheService;
    private final FollowRepository followRepository;
    private final TtsClient ttsClient;
    private final OpenAiClient openAiClient;
    private final S3Utils s3Utils;
    private final ResponseParser responseParser;

    /**
     * 동화 생성
     * @param memberId
     * @param requestDto
     * @return
     */
    @Transactional
    public StoryDto.StoryCreationResponse registerStory(Long memberId, StoryDto.StoryCreationRequest requestDto) {
        Language language = Language.fromString(requestDto.getLanguage());
        Member member = memberRepository.findByIdOrThrow(memberId);

        Map<String, String> storyDetails = responseParser.extractStoryDetails(
                openAiClient.requestStory(
                        requestDto.getAge(),
                        language == Language.EN ? "EN" : "KO",
                        requestDto.getGiven()
                )
        );

        Story save = storyRepository.save(Story.builder()
                .age(requestDto.getAge())
                .language(language)
                .given(requestDto.getGiven())
                .title(storyDetails.get("title"))
                .content(storyDetails.get("content"))
                .memberId(memberId)
                .author(member.getNickname())
                .coverUrl("")
                .build());

        // 동화 요약 및 커버 이미지 생성 요청
        String summary = openAiClient.requestSummary(storyDetails.get("content"));
        String imageUrl = responseParser.extractImageUrl(openAiClient.requestImage(summary));
        String url = s3Utils.uploadImageFromUrl("cover/", save.getId(), imageUrl);
        save.updateCoverUrl(url);
        storyRepository.save(save);

        // 생성된 동화를 최근 동화에 포함
        libraryService.addRecentStories(memberId,save.getId());

        return StoryDto.StoryCreationResponse.builder()
                .story(save)
                .build();
    }

    /**
     * 동화 상세 조회
     * @param storyId
     * @param memberId
     * @return
     */
    public StoryDto.StoryDetail getStoryDetail(String storyId, Long memberId) {
        Story story = storyCacheService.getStory(storyId);

        // 조회하는 동화를 최근 동화에 포함
        libraryService.addRecentStories(memberId, storyId);

        return StoryDto.StoryDetail.builder()
                .story(story)
                .isLiked(storyLikeRepository.existsByStoryIdAndMemberId(storyId, memberId))
                .isFollowed(followRepository.existsByFollowingIdAndFollowerId(memberId, story.getMemberId()))
                .isMyStory(memberId.equals(story.getMemberId()))
                .build();
    }

    /**
     * 동화 상세 수정
     * @param memberId
     * @param storyId
     * @param requestDto
     * @return
     */
    @Transactional
    public StoryDto.StoryDetail updateStoryDetail(Long memberId, String storyId, StoryDto.StoryUpdateRequest requestDto) {
        Story story = storyCacheService.updateStory(storyId, requestDto);

        if (!story.getMemberId().equals(memberId)) {
            throw new StoryException(StoryErrorCode.STORY_EDIT_PERMISSION_DENIED);  // 동화의 주인만 동화 상세 수정 가능
        }

        return StoryDto.StoryDetail.builder()
                .story(storyRepository.save(story))
                .isLiked(storyLikeRepository.existsByStoryIdAndMemberId(storyId, memberId))
                .isFollowed(false)
                .isMyStory(true)
                .build();
    }

    /**
     * 동화 좋아요
     * @param storyId
     * @param memberId
     */
    @Transactional
    public void likeStory(String storyId, Long memberId) {
        Story story = storyCacheService.likeStory(storyId);

        if (storyLikeRepository.findByStoryIdAndMemberId(storyId, memberId).isPresent()) {
            throw new StoryException(StoryErrorCode.LIKE_ALREADY_EXISTS);   // 이미 좋아요를 누른 경우
        }

        storyLikeRepository.save(StoryLike.builder()
                .storyId(storyId)
                .member(memberRepository.findByIdOrThrow(memberId))
                .build());

        storyRepository.save(story);
    }

    /**
     * 동화 좋아요 취소
     * @param storyId
     * @param memberId
     */
    @Transactional
    public void unlikeStory(String storyId, Long memberId) {
        Story story = storyCacheService.unlikeStory(storyId);

        storyLikeRepository.delete(storyLikeRepository.findByStoryIdAndMemberId(storyId, memberId)
                .orElseThrow(
                        () -> new StoryException(StoryErrorCode.LIKE_NOT_EXISTS)    //좋아요를 누르지 않고 취소 시도
                ));

        storyRepository.save(story);
    }

    /**
     * 동화 TTS 반환 - TTS가 이미 있다면 그대로 전달, 없다면 TTS 생성 요청 후 전달
     * @param storyId
     * @return
     */
    @Transactional
    public StoryDto.TTSResponse getOrRegisterStoryTTS(String storyId) {
        Story story = storyRepository.findByIdOrThrow(storyId);

        // 이미 해당 동화의 TTS가 저장되어 있다면 반환
        if (!story.getTtsUrl().isBlank()){
            return StoryDto.TTSResponse.builder()
                    .ttsUrl(story.getTtsUrl())
                    .timestamps(story.getTimestamps())
                    .build();
        }
        TtsClientResult ttsClientResult = ttsClient.requestTts(
                storyId, story.getLanguage() == Language.EN ? "EN" : "KO", story.getContent(), "tts"
        );

        storyRepository.save(story.updateTTSUrl(ttsClientResult.ttsUrl(), ttsClientResult.timestamps()));
        return StoryDto.TTSResponse.builder()
                .ttsUrl(ttsClientResult.ttsUrl())
                .timestamps(ttsClientResult.timestamps())
                .build();
    }
}
