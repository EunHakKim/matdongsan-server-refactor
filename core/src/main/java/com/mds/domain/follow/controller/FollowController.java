package com.mds.domain.follow.controller;

import com.mds.common.utils.SecurityUtils;
import com.mds.domain.follow.service.FollowService;
import com.mds.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Follow API", description = "팔로우 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/follow")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우")
    @PostMapping("/{followerId}")
    public ResponseEntity<Void> follow(
            @PathVariable Long followerId
    ) {
        followService.follow(SecurityUtils.getLoggedInMemberId(), followerId);
        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "언팔로우")
    @DeleteMapping("/{followerId}")
    public ResponseEntity<Void> unfollow(
            @PathVariable Long followerId
    ) {
        followService.unfollow(SecurityUtils.getLoggedInMemberId(), followerId);
        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "팔로우 리스트 조회")
    @GetMapping
    public ResponseEntity<List<MemberDto.MemberSummary>> getFollowers(
    ) {
        return ResponseEntity.ok()
                .body(followService.getFollowers(SecurityUtils.getLoggedInMemberId()));
    }
}
