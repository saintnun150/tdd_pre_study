package chap07_test_double.autoDebitRegist;

public class AutoDebitInfoRepository {
    public AutoDebitInfo findOne(String userId) {
        return new AutoDebitInfo(null, null, null);
    }

    public void save(AutoDebitInfo newInfo) {

    }
}
