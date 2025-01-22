package com.mds.domain.module.exception;

import com.mds.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class ModuleException extends BusinessException {
    private final ModuleErrorCode moduleErrorCode;

    public ModuleException(ModuleErrorCode moduleErrorCode) {
        super(moduleErrorCode);
        this.moduleErrorCode = moduleErrorCode;
    }
}