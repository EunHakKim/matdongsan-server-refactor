package com.mds.domain.dashboard.exception;

import com.mds.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class DashboardException extends BusinessException {
    private final DashboardErrorCode dashboardErrorCode;

    public DashboardException(DashboardErrorCode dashboardErrorCode) {
        super(dashboardErrorCode);
        this.dashboardErrorCode = dashboardErrorCode;
    }
}
