import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LeaderboardUI extends JFrame {

  private JComboBox<String> difficultyBox;
  private JComboBox<String> modeBox;
  private JTable table;
  private DefaultTableModel model;

  public LeaderboardUI() {
    setTitle("Minesweeper Leaderboard");
    setSize(600, 500);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    setLayout(new BorderLayout());

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout());

    difficultyBox = new JComboBox<>(new String[] {"Easy", "Medium", "Hard"});
    modeBox = new JComboBox<>(new String[] {"Singleplayer", "Multiplayer"});

    JButton loadButton = new JButton("Load Leaderboard");
    loadButton.addActionListener(e -> loadLeaderboard());

    controlPanel.add(new JLabel("Difficulty:"));
    controlPanel.add(difficultyBox);
    controlPanel.add(new JLabel("Mode:"));
    controlPanel.add(modeBox);
    controlPanel.add(loadButton);

    add(controlPanel, BorderLayout.NORTH);

    model = new DefaultTableModel();
    model.addColumn("Rank");
    model.addColumn("Player");
    model.addColumn("Wins");
    model.addColumn("Time / MP Wins");

    table = new JTable(model);
    add(new JScrollPane(table), BorderLayout.CENTER);

    setVisible(true);
  }

  private void loadLeaderboard() {
    model.setRowCount(0);

    int difficultyId = difficultyBox.getSelectedIndex() + 1;
    String mode = modeBox.getSelectedItem().toString();

    try (Connection conn = Database.getConnection()) {

      if (mode.equals("Singleplayer")) {
        loadSingleplayer(conn, difficultyId);
      } else {
        loadMultiplayer(conn, difficultyId);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void loadSingleplayer(Connection conn, int difficultyId)
      throws SQLException {
    String sql = "SELECT p.name,p.total_wins,p.best_time "
                 + "FROM players p "
                 + "JOIN games g ON g.player_id = p.player_id "
                 + "WHERE g.difficulty_id = ? AND g.result = 'WIN' "
                 + "GROUP BY p.player_id "
                 + "ORDER BY p.total_wins DESC, p.best_time ASC LIMIT 10";

    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, difficultyId);

    ResultSet rs = stmt.executeQuery();

    int rank = 1;
    while (rs.next()) {
      model.addRow(new Object[] {rank++, rs.getString("name"),
                                 rs.getInt("total_wins"),
                                 rs.getDouble("best_time") + "s"});
    }
  }

  private void loadMultiplayer(Connection conn, int difficultyId)
      throws SQLException {
    String sql = "SELECT p.name, COUNT(mg.winner_player_id) AS wins "
                 + "FROM multiplayer_games mg "
                 + "JOIN players p ON p.player_id = mg.winner_player_id "
                 + "WHERE mg.difficulty_id = ? "
                 + "GROUP BY p.player_id "
                 + "ORDER BY wins DESC LIMIT 10";

    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, difficultyId);

    ResultSet rs = stmt.executeQuery();

    int rank = 1;
    while (rs.next()) {
      model.addRow(new Object[] {rank++, rs.getString("name"),
                                 rs.getInt("wins"),
                                 rs.getInt("wins") + " wins"});
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(LeaderboardUI::new);
  }
}
