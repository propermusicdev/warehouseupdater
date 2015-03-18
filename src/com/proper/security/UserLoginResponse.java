package com.proper.security;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by Lebel on 27/05/2014.
 */
public class UserLoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String RequestedInitials;
    private int UserId;
    private String UserFirstName;
    private String UserLastName;
    private String UserCode;
    private String Response;

    public UserLoginResponse() {
    }

    public UserLoginResponse(String requestedInitials, int userId, String userFirstName, String userLastName, String userCode, String response) {
        RequestedInitials = requestedInitials;
        UserId = userId;
        UserFirstName = userFirstName;
        UserLastName = userLastName;
        UserCode = userCode;
        this.Response = response;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @JsonProperty("RequestedInitials")
    public String getRequestedInitials() {
        return RequestedInitials;
    }

    public void setRequestedInitials(String requestedInitials) {
        RequestedInitials = requestedInitials;
    }

    @JsonProperty("UserId")
    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    @JsonProperty("UserFirstName")
    public String getUserFirstName() {
        return UserFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        UserFirstName = userFirstName;
    }

    @JsonProperty("UserLastName")
    public String getUserLastName() {
        return UserLastName;
    }

    public void setUserLastName(String userLastName) {
        UserLastName = userLastName;
    }

    @JsonProperty("UserCode")
    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    @JsonProperty("Response")
    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }
}
