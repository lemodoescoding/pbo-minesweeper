import java.util.*;

public interface GameListener {
    void onTilesRevealed(List<Tile> revealed);
    void onUpdateMinesLeft(int minesLeft);
    void onUpdateTime(int seconds);
    void onGameWon(int seconds);
    void onGameLost();
    void onReset();
}