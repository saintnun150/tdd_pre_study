package chap07_test_double.userRegist;

public class User {
    private String id;
    private String pw;
    private String email;

    public User(String id, String pw, String email) {
        this.id = id;
        this.pw = pw;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
