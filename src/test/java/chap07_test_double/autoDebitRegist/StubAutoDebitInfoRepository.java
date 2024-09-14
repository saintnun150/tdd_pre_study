package chap07_test_double.autoDebitRegist;

public class StubAutoDebitInfoRepository implements AutoDebitInfoRepository {
    @Override
    public AutoDebitInfo findOne(String userId) {
        return null;
    }

    @Override
    public void save(AutoDebitInfo newInfo) {

    }
}
