
import java.sql.*;

class Database {

  private static final String URL = "jdbc:mysql://localhost:3306/fp_pbo";
  private static final String USER = "root";
  private static final String PASS = "testingA123_";

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      System.out.println("MySQL JDBC Driver Loaded.");
    } catch (ClassNotFoundException e) {
      System.err.println("❌ Driver NOT found!");
      e.printStackTrace();
    }
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASS);
  }

  public static int createPlayerIfNotExists(String name) {
    String sql = "INSERT IGNORE INTO players (name) VALUES (?)";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, name);
      stmt.executeUpdate();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return getPlayerId(name);
  }

  public static int getPlayerId(String name) {
    String sql = "SELECT player_id FROM players WHERE name = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, name);
      ResultSet rs = stmt.executeQuery();

      if (rs.next())
        return rs.getInt(1);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return -1;
  }

  public static void updateWin(int playerId, double timeTaken) {
        String sql = """
            UPDATE players
            SET total_wins = total_wins + 1,
                total_games = total_games + 1,
                best_time = IF(best_time IS NULL OR ? < best_time, ?, best_time)
            WHERE player_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setDouble(1, timeTaken);
          stmt.setDouble(2, timeTaken);
          stmt.setInt(3, playerId);
          stmt.executeUpdate();

        } catch (Exception e) {
          e.printStackTrace();
        }
  }

  public static void updateGamesPlayed(int playerId) {
        String sql = """
            UPDATE players
            SET total_games = total_games + 1
            WHERE player_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setInt(1, playerId);
          stmt.executeUpdate();

        } catch (Exception e) {
          e.printStackTrace();
        }
  }

  public static void incrementGames(int playerId) {
    String sql =
        "UPDATE players SET total_games = total_games + 1 WHERE player_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, playerId);
      stmt.executeUpdate();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static int getDifficultyId(String difficultyName) {
    String sql =
        "SELECT difficulty_id FROM difficulties WHERE difficulty_name = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, difficultyName);
      ResultSet rs = stmt.executeQuery();

      if (rs.next())
        return rs.getInt(1);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return -1;
  }

  public static void saveSingleplayerGame(int playerId, int difficultyId,
                                          double durationSeconds, boolean win) {

        String sql = """
            INSERT INTO games (player_id, difficulty_id, duration_seconds, result)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setInt(1, playerId);
          stmt.setInt(2, difficultyId);
          stmt.setDouble(3, durationSeconds);
          stmt.setString(4, win ? "WIN" : "LOSE");

          stmt.executeUpdate();

        } catch (Exception e) {
          e.printStackTrace();
        }
  }

  public static int createMultiplayerGame(int difficultyId,
                                          Integer winnerPlayerId) {
        String sql = """
            INSERT INTO multiplayer_games (difficulty_id, winner_player_id)
            VALUES (?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

          stmt.setInt(1, difficultyId);

          if (winnerPlayerId == null)
            stmt.setNull(2, Types.INTEGER);
          else
            stmt.setInt(2, winnerPlayerId);

          stmt.executeUpdate();

          ResultSet rs = stmt.getGeneratedKeys();
          if (rs.next())
            return rs.getInt(1);

        } catch (Exception e) {
          e.printStackTrace();
        }

        return -1;
  }

  public static void addMultiplayerParticipant(int gameId, int playerId) {
        String sql = """
            INSERT INTO multiplayer_participants (mp_game_id, player_id)
            VALUES (?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setInt(1, gameId);
          stmt.setInt(2, playerId);
          stmt.executeUpdate();

        } catch (Exception e) {
          e.printStackTrace();
        }
  }

  public static void updateElimination(int gameId, int playerId, int order,
                                       double timeAlive) {
    String sql =
        "UPDATE multiplayer_participants SET elimination_order = ?, "
        + "time_alive_seconds = ? WHERE mp_game_id = ? AND player_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, order);
      stmt.setDouble(2, timeAlive);
      stmt.setInt(3, gameId);
      stmt.setInt(4, playerId);
      stmt.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /* ============================================================
       LEADERBOARD FUNCTIONS
       ============================================================ */

  public static void printSingleplayerLeaderboard(int difficultyId) {
    String sql = "SELECT p.name,p.total_wins,p.best_time FROM players p JOIN "
                 + "games g ON g.player_id = p.player_id WHERE g.difficulty_id "
                 + "= ? AND g.result = 'WIN' GROUP BY p.player_id ORDER BY "
                 + "p.total_wins DESC,p.best_time ASC LIMIT 20";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, difficultyId);
      ResultSet rs = stmt.executeQuery();

      System.out.println("\n=== SINGLEPLAYER LEADERBOARD (Difficulty: " +
                         difficultyId + ") ===");
      while (rs.next()) {
        System.out.println(
            rs.getString("name") + " | Wins: " + rs.getInt("total_wins") +
            " | Best Time: " + rs.getDouble("best_time") + "s");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printMultiplayerLeaderboard(int difficultyId) {
    String sql = "SELECT p.name, COUNT(mg.winner_player_id) AS wins FROM "
                 + "multiplayer_games mg JOIN players p ON p.player_id = "
                 + "mg.winner_player_id WHERE mg.difficulty_id = ? GROUP BY "
                 + "p.player_id ORDER BY wins DESC LIMIT 20";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, difficultyId);
      ResultSet rs = stmt.executeQuery();

      System.out.println("\n=== MULTIPLAYER LEADERBOARD (Difficulty: " +
                         difficultyId + ") ===");
      while (rs.next()) {
        System.out.println(rs.getString("name") +
                           " | Wins: " + rs.getInt("wins"));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
