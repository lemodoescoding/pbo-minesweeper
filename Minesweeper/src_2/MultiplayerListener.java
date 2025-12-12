public interface MultiplayerListener {
    void onPlayerTurnChanged(int playerIndex, String playerName);
    void onPlayerEliminated(int playerIndex, String playerName);
    void onMultiplayerGameEnded(String winnerName);
}
