import java.util.*;

public class MultiplayerGame extends Game {
  private int mpGameId = -1;
  private int[] playerDatabaseIds;
  private int playerCount;
  private List<String> playerNameList;
  private boolean[] eliminated;
  private int currentPlayerIndex = 0;
  private int activeBombCount;
  private int eliminationCounter = 1;
  private MultiplayerListener multiplayerListener;

  public MultiplayerGame(Difficulty difficulty, GameListener listener,
                         MultiplayerListener multiplayerListener,
                         List<String> playerList) {
    super(difficulty, listener);
    this.playerNameList = new ArrayList<>(playerList);
    this.playerCount = playerList.size();
    this.multiplayerListener = multiplayerListener;
    this.eliminated = new boolean[playerCount];
    this.activeBombCount = difficulty.mines;

    int difficultyId = Database.getDifficultyId(difficulty.name());
    mpGameId = Database.createMultiplayerGame(difficultyId, null);

    // Map player names → player_id and insert participants
    playerDatabaseIds = new int[playerCount];

    for (int i = 0; i < playerCount; i++) {
      String name = playerNameList.get(i);

      int pid = Database.createPlayerIfNotExists(name);
      playerDatabaseIds[i] = pid;

      Database.addMultiplayerParticipant(mpGameId, pid);
    }
  }

  @Override
  public void handleLeftClick(Cell cell) {
    if (isGameOver)
      return;
    if (cell.isFlagged())
      return;

    if (firstClick) {
      board.placeMine(cell.r, cell.c);
      startTimer();
      firstClick = false;
    }

    List<Cell> revealed = board.reveal(cell.r, cell.c);
    if (revealed.isEmpty())
      return;

    listener.onCellsRevealed(revealed);

    for (Cell revealedCell : revealed) {
      tilesRevealed++;
      if (revealedCell.isMine()) {
        tilesRevealed--;
        eliminated[currentPlayerIndex] = true;
        listener.onUpdateRemainingMine(--mineLeft);

        int pid = playerDatabaseIds[currentPlayerIndex];
        double timeAlive = seconds;

        // Save elimination info into database
        Database.updateElimination(mpGameId, pid, eliminationCounter++,
                                   timeAlive);

        if (multiplayerListener != null) {
          multiplayerListener.onPlayerEliminated(
              currentPlayerIndex, playerNameList.get(currentPlayerIndex));
        }
        advanceTurn();
        checkEnd();
        return;
      }
    }

    if (tilesRevealed == difficulty.rows * difficulty.cols - difficulty.mines) {
      triggerWin();
      return;
    }

    advanceTurn();
  }

  @Override
  public void handleRightClick(Cell cell) {
    if (isGameOver)
      return;
    if (cell.isRevealed())
      return;

    boolean nowFlagged = !cell.isFlagged();
    cell.setFlagged(nowFlagged);
    mineLeft += nowFlagged ? -1 : 1;
    listener.onUpdateRemainingMine(mineLeft);
  }

  private void advanceTurn() {
    if (playerCount == 0)
      return;
    int previous = currentPlayerIndex;
    int loopCount = 0;
    do {
      currentPlayerIndex = (currentPlayerIndex + 1) % playerCount;
      loopCount++;
      if (loopCount > playerCount)
        break;
    } while (eliminated[currentPlayerIndex]);

    if (multiplayerListener != null && currentPlayerIndex != previous) {
      multiplayerListener.onPlayerTurnChanged(
          currentPlayerIndex, playerNameList.get(currentPlayerIndex));
    }
  }

  private void checkEnd() {
    int aliveCount = 0;
    int lastAliveIndex = -1;

    for (int i = 0; i < playerCount; i++) {
      if (!eliminated[i]) {
        aliveCount++;
        lastAliveIndex = i;
      }
    }

    if (aliveCount == 1) {
      triggerWin();
    }
  }

  private void triggerWin() {
    isGameOver = true;
    stopTimer();
    String winnerName = playerNameList.get(currentPlayerIndex);

    int winnerId = playerDatabaseIds[currentPlayerIndex];

    // Update winner in multiplayer game header
    Database.createMultiplayerGame(Database.getDifficultyId(difficulty.name()),
                                   winnerId);

    // Winner elimination record (order = 0 means champion)
    Database.updateElimination(mpGameId, winnerId, 0, seconds);
    Database.updateWin(winnerId, seconds);

    if (multiplayerListener != null) {
      multiplayerListener.onMultiplayerGameEnded(winnerName);
    } else {
      listener.onGameWon(seconds);
    }
  }

  public List<String> getAllPlayers() {
    return Collections.unmodifiableList(playerNameList);
  }
}
