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
    int minesToSet = 10;
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    MineTile[][] boardTiles = new MineTile[rows][cols];
    ArrayList<MineTile> mineList;

    int tilesClicked = 0;
    boolean isGameOver = false;
    
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
                
                tile.setFocusable(true);
                tile.setMargin(new Insets(0, 0, 0, 0));
                // tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16)); // this doesnt work
                tile.setFont(tile.getFont().deriveFont(Font.PLAIN, 24)); // so i use this instead
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e){
                        if(isGameOver) return;
                        MineTile tile = (MineTile)e.getSource();
                        // Left click
                        if(e.getButton() == MouseEvent.BUTTON1){
                            if(tile.getText() == ""){
                                if(mineList.contains(tile)){
                                    gameOver();
                                }
                                else{
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // Right click
                        else if(e.getButton() == MouseEvent.BUTTON3){
                            if(tile.getText() == "" && tile.isEnabled()){
                                tile.setText("🚩");
                            }
                            else if(tile.getText() == "🚩"){
                                tile.setText("");
                            }
                        }
                    }
                });
                
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
        setMines();
    }

    void setMines(){
        mineList = new ArrayList<MineTile>();
        Random rand = new Random();
        
        while(mineList.size() < minesToSet){
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            MineTile tile = boardTiles[r][c];
            if(!mineList.contains(tile)){
                mineList.add(tile);
                // For testing purposes, show the mines
                // tile.setText("💣");
            }
        }
    }

    void gameOver(){
        for(MineTile tile : mineList){
            tile.setText("💣");
        }
        isGameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c){
        if(r < 0 || r >= rows || c < 0 || c >= cols) return;
        MineTile tile = boardTiles[r][c];
        if(!tile.isEnabled()) return;
        tile.setEnabled(false);
        tilesClicked++;
        int minesCount = 0;

        minesCount += countMine(r-1, c-1);  // top-left
        minesCount += countMine(r-1, c);    // top
        minesCount += countMine(r-1, c+1);  // top-right
        minesCount += countMine(r, c-1);    // left
        minesCount += countMine(r, c+1);    // right
        minesCount += countMine(r+1, c-1);  // bottom-left
        minesCount += countMine(r+1, c);    // bottom
        minesCount += countMine(r+1, c+1);  // bottom-right

        if(minesCount > 0){
            tile.setText(Integer.toString(minesCount));
        }
        else{
            tile.setText("");
            checkMine(r-1, c-1);  // top-left
            checkMine(r-1, c);    // top
            checkMine(r-1, c+1);  // top-right
            checkMine(r, c-1);    // left
            checkMine(r, c+1);    // right
            checkMine(r+1, c-1);  // bottom-left
            checkMine(r+1, c);    // bottom
            checkMine(r+1, c+1);  // bottom-right
        }
        
        if(tilesClicked == (rows * cols) - mineList.size()){
            isGameOver = true;
            textLabel.setText("You Win!");
        }
    }  
    
    int countMine(int r, int c){
        if(r < 0 || r >= rows || c < 0 || c >= cols) return 0;
        if(mineList.contains(boardTiles[r][c])) return 1;
        return 0;
    }
}
