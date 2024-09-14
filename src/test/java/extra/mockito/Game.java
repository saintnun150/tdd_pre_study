package extra.mockito;

public class Game {
    private GameNumGen gameNumGen;

    public Game(GameNumGen genMock) {
        this.gameNumGen = genMock;
    }

    public void init(GameLevel gameLevel) {
        gameNumGen.generate(gameLevel);
    }
}
