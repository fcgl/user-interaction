package com.fcgl.madrid.user.payload.response;

import com.fcgl.madrid.user.payload.InternalStatus;

public class LoginResponse {
    private InternalStatus internalStatus;
    private AuthResponse response;

    public LoginResponse(InternalStatus internalStatus, AuthResponse response) {
        this.internalStatus = internalStatus;
        this.response = response;
    }

    public InternalStatus getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(InternalStatus internalStatus) {
        this.internalStatus = internalStatus;
    }

    public AuthResponse getResponse() {
        return response;
    }

    public void setResponse(AuthResponse response) {
        this.response = response;
    }

}
