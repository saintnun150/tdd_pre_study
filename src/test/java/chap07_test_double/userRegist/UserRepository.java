package chap07_test_double.userRegist;

public interface UserRepository {
    void save(User user);
    User findById(String id);
}
