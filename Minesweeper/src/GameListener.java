import java.util.*;

public interface GameListener {
    void onCellsRevealed(List<Cell> revealed);
    void onUpdateRemainingMine(int minesLeft);
    void onUpdateTime(int seconds);
    void onGameWon(int seconds);
    void onGameLost();
    void onReset();
}