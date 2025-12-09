import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton{
        int r;
        int c;
        
        public MineTile(int r, int c){
            this.r = r;
            this.c = c;
        }
    }
    
    int tileSize = 64;
    int rows = 8;
    int cols = rows;
    int boardsWidth = cols * tileSize;
    int boardsHeight = rows * tileSize;
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    MineTile[][] boardTiles = new MineTile[rows][cols];
    
    Minesweeper(){
        frame.setSize(boardsWidth, boardsHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        textLabel.setFont(new Font("Arial", Font.BOLD, 32));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);
        
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);
        
        boardPanel.setLayout(new GridLayout(rows, cols));
        frame.add(boardPanel);
        
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                MineTile tile = new MineTile(r, c);
                boardTiles[r][c] = tile;
                
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(tile.getFont().deriveFont(Font.PLAIN, 16f));
                // tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
                tile.setText("💣");
                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);
    }
}
