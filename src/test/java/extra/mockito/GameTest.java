package extra.mockito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class GameTest {
    @DisplayName("모의 객체의 메서드가 호출 됐는 지 검증")
    @Test
    void test1() {
        GameNumGen genMock = mock(GameNumGen.class);
        Game game = new Game(genMock);
        game.init(GameLevel.EASY);

        // 메서드 호출 여부: any(), anyInt() etc..
        then(genMock)
                .should()
//                .generate(GameLevel.EASY);
                .generate(any());
    }
}
