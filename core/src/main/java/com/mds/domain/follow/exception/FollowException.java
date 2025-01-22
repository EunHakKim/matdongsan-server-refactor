package com.mds.domain.follow.exception;

import com.mds.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class FollowException extends BusinessException {
    private final FollowErrorCode followErrorCode;

    public FollowException(FollowErrorCode followErrorCode) {
        super(followErrorCode);
        this.followErrorCode = followErrorCode;
    }
}
