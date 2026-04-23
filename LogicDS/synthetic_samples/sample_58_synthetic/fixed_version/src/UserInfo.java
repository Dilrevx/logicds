public class UserInfo {

    private String userId;
    private UserStatus userStatus = UserStatus.ACTIVE;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }
}
