package com.mds.domain.story.exception;

import com.mds.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class StoryException extends BusinessException {
    private final StoryErrorCode storyErrorCode;

    public StoryException(StoryErrorCode storyErrorCode) {
        super(storyErrorCode);
        this.storyErrorCode = storyErrorCode;
    }
}
