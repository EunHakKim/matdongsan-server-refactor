package com.mds.domain.child.exception;

import com.mds.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class ChildException extends BusinessException {
    private final ChildErrorCode childErrorCode;

    public ChildException(ChildErrorCode childErrorCode) {
        super(childErrorCode);
        this.childErrorCode = childErrorCode;
    }
}
