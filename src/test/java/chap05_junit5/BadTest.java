package chap05_junit5;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BadTest {
    // 잘못된 테스트 작성 예시
    private FileOperator op = new FileOperator();
    private static File file; // 두 테스트가 데이터를 공유할 목적으로 필드 사용

    //@DisplayName("test name")
    @Test
    @Order(2)
    void fileCreationTest() {
        File createdFile = op.createFile();
        assertTrue(createdFile.length() > 0);
        this.file = createdFile;
    }

    //@DisplayName("test name")
    @Test
    @Order(1)
    void readFileTest() {
        long data = op.readDate(file);
        assertTrue(data > 0);
    }

    private class FileOperator {

        public File createFile() {
            ClassLoader classLoader = getClass().getClassLoader();
            return new File(classLoader.getResource("test.txt").getFile());
        }

        public long readDate(File file) {
            return file.length();
        }
    }
}
