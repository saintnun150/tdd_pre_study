package chap07_test_double.userRegist;

public class SpyEmailNotifier implements EmailNotifier {
    private boolean notified;
    private String email;

    public boolean isNotified() {
        return notified;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public void sendRegisterEmail(String email) {
        this.notified = true;
        this.email = email;
    }
}
