package chap06_testcode_composition;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileReadTest {

    //@DisplayName("test name")
    @Test
    void noDataFile_Then_Exception() {
        // 우연히 파일이 존재할 경우에는 테스트에 성공함.
        File file = new File("badpath.txt");
        assertThrows(IllegalArgumentException.class, () -> {
            MathUtils.sum(file);
        });
    }

    //@DisplayName("test name")
    @Test
    void noDataFile_Then_Exception2() {
        // 파일이 없는 상황으로 만듦
        givenNoFile("badpath.txt");

        File file = new File("badpath.txt");
        assertThrows(IllegalArgumentException.class, () -> {
            MathUtils.sum(file);
        });
    }

    private void givenNoFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean delete = file.delete();
            if (!delete) {
                throw new RuntimeException("Could not delete file " + path);
            }
        }
    }

    //@DisplayName("test name")
    @Test
    void dataFileSumTest2() {
        // 외부 상황을 명시적으로 구성
        givenDataFile("resource/datafile.txt", "1", "2", "3");
        File file = new File("resource/datafile.txt");
       long sum = MathUtils.sum(file);
        assertEquals(10L, sum);

    }

    private void givenDataFile(String path, String... lines) {
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            Files.write(filePath, Arrays.asList(lines));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
