import java.util.*;

public class MultiplayerGame extends Game {

    private int playerCount;
    private List<String> playerNameList;
    private boolean[] eliminated;
    private int currentPlayerIndex = 0;
    private MultiplayerListener multiplayerListener;

    public MultiplayerGame(Difficulty difficulty,
                           GameListener listener,
                           MultiplayerListener multiplayerListener,
                           List<String> playerList){
        super(difficulty, listener);
        this.playerNameList = new ArrayList<>(playerList);
        this.playerCount = playerList.size();
        this.multiplayerListener = multiplayerListener;
        this.eliminated = new boolean[playerCount];
    }

    @Override
    public void handleLeftClick(Cell cell){
        if (isGameOver) return;
        if (cell.isFlagged()) return;

        if (firstClick) {
            board.placeMine(cell.r, cell.c);
            startTimer();
            firstClick = false;
        }

        List<Cell> revealed = board.reveal(cell.r, cell.c);
        if (revealed.isEmpty()) return;

        listener.onCellsRevealed(revealed);

        for (Cell revealedCell : revealed) {
            if (revealedCell.isMine()) {
                eliminated[currentPlayerIndex] = true;
                if (multiplayerListener != null) {
                    multiplayerListener.onPlayerEliminated(currentPlayerIndex, playerNameList.get(currentPlayerIndex));
                }
                advanceTurn();
                checkEnd();
                return;
            }
        }

        advanceTurn();
    }

    @Override
    public void handleRightClick(Cell cell){
        if (isGameOver) return;
        if (cell.isRevealed()) return;

        boolean nowFlagged = !cell.isFlagged();
        cell.setFlagged(nowFlagged);
        mineLeft += nowFlagged ? -1 : 1;
        listener.onUpdateRemainingMine(mineLeft);

        if (nowFlagged) {
            if (board.flaggedAllMine()) {
                isGameOver = true;
                stopTimer();
                String winnerName = playerNameList.get(currentPlayerIndex);
                if (multiplayerListener != null) {
                    multiplayerListener.onMultiplayerGameEnded(winnerName);
                } else {
                    listener.onGameWon(seconds);
                }
                return;
            }
        }
    }

    private void advanceTurn(){
        if (playerCount == 0) return;
        int previous = currentPlayerIndex;
        int loopCount = 0;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % playerCount;
            loopCount++;
            if (loopCount > playerCount) break; 
        } while (eliminated[currentPlayerIndex]);

        if (multiplayerListener != null && currentPlayerIndex != previous) {
            multiplayerListener.onPlayerTurnChanged(currentPlayerIndex, playerNameList.get(currentPlayerIndex));
        }
    }

    private void checkEnd(){
        int aliveCount = 0;
        int lastAliveIndex = -1;

        for (int i = 0; i < playerCount; i++) {
            if (!eliminated[i]) {
                aliveCount++;
                lastAliveIndex = i;
            }
        }

        if (aliveCount == 1) {
            isGameOver = true;
            stopTimer();
            String winner = playerNameList.get(lastAliveIndex);
            if (multiplayerListener != null) {
                multiplayerListener.onMultiplayerGameEnded(winner);
            } else {
                listener.onGameWon(seconds);
            }
        }
    }

    public List<String> getAllPlayers(){
        return Collections.unmodifiableList(playerNameList);
    }
}
