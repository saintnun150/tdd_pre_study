package chap07_test_double.autoDebitRegist;

public interface AutoDebitInfoRepository {
    AutoDebitInfo findOne(String userId);
    void save(AutoDebitInfo newInfo);
}
