package com.mds.domain.dashboard.service;

import com.mds.OpenAiClient;
import com.mds.common.utils.ResponseParser;
import com.mds.domain.child.entity.Child;
import com.mds.domain.child.repository.ChildRepository;
import com.mds.domain.dashboard.entity.QuestionAnswer;
import com.mds.domain.member.exception.MemberErrorCode;
import com.mds.domain.member.exception.MemberException;
import com.mds.domain.member.repository.MemberRepository;
import com.mds.domain.dashboard.dto.DashboardDto;
import com.mds.domain.dashboard.entity.StoryQuestion;
import com.mds.domain.story.entity.mongo.Language;
import com.mds.domain.story.entity.mongo.Story;
import com.mds.domain.dashboard.repository.QuestionAnswerRepository;
import com.mds.domain.dashboard.repository.StoryQuestionRepository;
import com.mds.domain.story.repository.mongo.StoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final StoryQuestionRepository storyQuestionRepository;
    private final MemberRepository memberRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final ChildRepository childRepository;
    private final StoryRepository storyRepository;
    private final OpenAiClient openAiClient;
    private final ResponseParser responseParser;

    /**
     * 동화 질문 생성
     * @param storyId
     * @return
     */
    @Transactional
    public DashboardDto.StoryQuestionResponse registerQuestions(String storyId) {
        Story story = storyRepository.findByIdOrThrow(storyId);

        // StoryQuestion 엔티티 생성 및 저장
        StoryQuestion storyQuestion = storyQuestionRepository.save(
                StoryQuestion.builder()
                        .storyId(storyId)
                        .language(story.getLanguage())
                        .build()
        );

        // 동화 질문 생성 요청 및 파싱
        List<Map<String, String>> parsedQuestions = responseParser.extractQuestions(
                responseParser.extractChatGptContent(
                    openAiClient.requestQuestion(
                            story.getAge(),
                            story.getLanguage().equals(Language.EN) ? "english" : "korean",
                            story.getContent()
                    )
                )
        );

        // QuestionAnswer 엔티티 생성 및 저장
        List<QuestionAnswer> questionAnswers = parsedQuestions.stream()
                .map(question -> QuestionAnswer.builder()
                        .question(question.get("Q").trim())
                        .sampleAnswer(question.get("A").trim())
                        .storyQuestion(storyQuestion)
                        .build())
                .toList();

        questionAnswerRepository.saveAll(questionAnswers);

        return DashboardDto.StoryQuestionResponse.builder()
                .storyquestion(storyQuestion)
                .build();
    }

    /**
     * 부모 qna로그 전체 가져오기
     * @param memberId
     * @return
     */
    public Page<DashboardDto.ParentQnaLogResponse> getQnaLog(Long memberId, Pageable pageable) {
        if(!memberRepository.existsById(memberId)) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        //자녀 아이디 조회
        List<Long> childIds = childRepository.findByMemberId(memberId)
                .stream()
                .map(Child::getId)
                .toList();

        //자녀 아이디를 통해 storyQuestion 조회
        Page<StoryQuestion> childQuestions = storyQuestionRepository.findAllByChildIdIn(childIds, pageable);
        return mapQuestionsToDto(childQuestions);
    }

    /**
     * 자녀 필터 QnA 로그 가져오기
     * @param childId
     * @param pageable
     * @return
     */
    public Page<DashboardDto.ParentQnaLogResponse> getChildQnaLog(Long childId, Pageable pageable) {
        Page<StoryQuestion> childQuestions = storyQuestionRepository.findByChildId(childId, pageable);
        return mapQuestionsToDto(childQuestions);
    }

    /**
     * StoryQuestion 목록을 DTO로 매핑
     * @param childQuestions
     * @return
     */
    private Page<DashboardDto.ParentQnaLogResponse> mapQuestionsToDto(Page<StoryQuestion> childQuestions) {
        List<String> storyIds = childQuestions.getContent().stream()
                .map(StoryQuestion::getStoryId)
                .distinct()
                .toList();

        Map<String, String> storyTitleMap = storyRepository.findByIdIn(storyIds).stream()
                .collect(Collectors.toMap(Story::getId, Story::getTitle));

        return childQuestions.map(question -> DashboardDto.ParentQnaLogResponse.builder()
                .id(question.getId())
                .createAt(question.getCreatedAt())
                .title(storyTitleMap.get(question.getStoryId()))
                .child(question.getChild().getName())
                .build());
    }

    /**
     * QnA 상세 조회하기
     * @param qnaId
     * @return
     */
    public List<DashboardDto.QnAs> getQnaDetail(Long qnaId) {
        StoryQuestion storyQuestion = storyQuestionRepository.findByIdOrThrow(qnaId);

        return storyQuestion.getQuestionAnswers().stream()
                .map(DashboardDto.QnAs::new)
                .toList();
    }
}
