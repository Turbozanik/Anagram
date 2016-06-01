package by.example.roman.anagram;

/**
 * Created by Roman on 19.02.2016.
 */
public class User {
    String login;
    String duelCount;
    String _id;
    String topDifficultyLevel;
    String email;
    String badgesCount;
    String lastUpdateDate;
    String facebookId;

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    String twitterId;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDuelCount() {
        return duelCount;
    }

    public void setDuelCount(String duelCount) {
        this.duelCount = duelCount;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTopDifficultyLevel() {
        return topDifficultyLevel;
    }

    public void setTopDifficultyLevel(String topDifficultyLevel) {
        this.topDifficultyLevel = topDifficultyLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBadgesCount() {
        return badgesCount;
    }

    public void setBadgesCount(String badgesCount) {
        this.badgesCount = badgesCount;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public User(String login, String duelCount, String _id, String topDifficultyLevel, String email, String badgesCount, String lastUpdateDate,String facebookId,String twitterId) {
        this.login = login;
        this.duelCount = duelCount;
        this._id = _id;
        this.topDifficultyLevel = topDifficultyLevel;
        this.email = email;
        this.badgesCount = badgesCount;
        this.lastUpdateDate = lastUpdateDate;
        this.facebookId = facebookId;
        this.twitterId = twitterId;
    }
}
