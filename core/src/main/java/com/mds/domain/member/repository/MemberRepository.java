package com.mds.domain.member.repository;

import com.mds.domain.member.entity.Member;
import com.mds.domain.member.exception.MemberErrorCode;
import com.mds.domain.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    default Member findByIdOrThrow(Long memberId) {
        return findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
        );
    }
    Optional<Member> findByEmail(String email);
}
