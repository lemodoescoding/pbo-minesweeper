import java.awt.Point;
import java.util.*;

public class Board {
    private final int rowTotal;
    private final int colTotal;
    private final int totalMine;
    private Cell[][] cells;
    private List<Point> mineList = new ArrayList<>();

    public Board(int rowTotal, int colTotal, int mineTotal){
        this.rowTotal = rowTotal;
        this.colTotal = colTotal;
        this.totalMine = mineTotal;
        cells = new Cell[rowTotal][colTotal];
    }

    public void setCell(Cell[][] cells){
        this.cells = cells;
    }

    public int getRowSize(){ return rowTotal; }
    public int getColSize(){ return colTotal; }
    public int getTotalMine(){ return totalMine; }

    public void placeMine(int safeRowPoint, int safeColPoint){
        mineList.clear();
        Random rand = new Random();

        while (mineList.size() < totalMine) {
            int r = rand.nextInt(rowTotal);
            int c = rand.nextInt(colTotal);

            boolean inSafe3x3 = Math.abs(r - safeRowPoint) <= 1 && Math.abs(c - safeColPoint) <= 1;
            if (inSafe3x3) continue;

            boolean mineIsPlaced = false; //cek duplikat
            for (Point p : mineList) {
                if (p.x == r && p.y == c) {
                    mineIsPlaced = true;
                    break;
                }
            }

            if (mineIsPlaced) continue;

            mineList.add(new Point(r, c));
            cells[r][c].setMine();
        }
        computeAllAdjacents();
    }

    private void computeAllAdjacents(){
        for (int r = 0; r < rowTotal; r++){
            for (int c = 0; c < colTotal; c++){
                countAdjacentMine(r, c);
            }
        }
    }

    public void countAdjacentMine(int r, int c){
        int count = 0;
        for (int deltaRow = -1; deltaRow <= 1; deltaRow++){
            for (int deltaCol = -1; deltaCol <= 1; deltaCol++){
                if (deltaRow == 0 && deltaCol == 0) continue;
                int neighborRow = r + deltaRow;
                int neighborCol = c + deltaCol;
                if (!isInsideBoard(neighborRow, neighborCol)) continue;
                if (cells[neighborRow][neighborCol].isMine()) count++;
            }
        }
        cells[r][c].setAdjacentMine(count);
    }

    public int countAdjacentFlag(int r, int c){
        int count = 0;
        for (int deltaRow = -1; deltaRow <= 1; deltaRow++){
            for (int deltaCol = -1; deltaCol <= 1; deltaCol++){
                if (deltaRow == 0 && deltaCol == 0) continue;
                int neighborRow = r + deltaRow;
                int neighborCol = c + deltaCol;
                if (!isInsideBoard(neighborRow, neighborCol)) continue;
                if (cells[neighborRow][neighborCol].isFlagged()) count++;
            }
        }
        return count;
    }

    public List<Cell> reveal(int row, int col) {
        List<Cell> toReveal = new ArrayList<>();
        Cell start = cells[row][col];

        if (start.isFlagged()) return toReveal;

        if (start.isRevealed()) {
            int adjacentFlag = countAdjacentFlag(row, col);
            int adjacentMine = start.getAdjacentMine();

            if (adjacentFlag != adjacentMine) return toReveal;

            List<Cell> adjacentCell = new ArrayList<>();
            for (int deltaRow = -1; deltaRow <= 1; deltaRow++){
                for (int deltaCol = -1; deltaCol <= 1; deltaCol++){
                    if (deltaRow == 0 && deltaCol == 0) continue;
                    int nRow = row + deltaRow;
                    int nCol = col + deltaCol;
                    if (!isInsideBoard(nRow, nCol)) continue;
                    Cell neighbor = cells[nRow][nCol];
                    if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                        adjacentCell.add(neighbor);
                    }
                }
            }

            if (adjacentCell.isEmpty()) return toReveal;

            List<Cell> connectedCell = new ArrayList<>();
            for (Cell neighbor : adjacentCell) {
                if (neighbor.isMine()) {
                    neighbor.setRevealed(true);
                    connectedCell.add(neighbor);
                    toReveal.addAll(connectedCell);
                    return toReveal;
                }
                connectedCell.addAll(floodReveal(neighbor.r, neighbor.c));
            }
            toReveal.addAll(connectedCell);
            return toReveal;
        }

        if (start.isMine()) {
            start.setRevealed(true);
            toReveal.add(start);
            return toReveal;
        }

        toReveal.addAll(floodReveal(row, col));
        return toReveal;
    }

    private List<Cell> floodReveal(int startRow, int startCol) {
        List<Cell> toReveal = new ArrayList<>();
        List<Point> stack = new ArrayList<>();
        boolean[][] queued = new boolean[rowTotal][colTotal];

        stack.add(new Point(startRow, startCol));

        while (!stack.isEmpty()) {
            Point p = stack.remove(stack.size() - 1);
            int currentRow = p.x;
            int currentCol = p.y;
            if (!isInsideBoard(currentRow, currentCol)) continue;
            Cell currentCell = cells[currentRow][currentCol];

            if (currentCell.isRevealed() || currentCell.isFlagged()) continue;

            currentCell.setRevealed(true);
            toReveal.add(currentCell);

            if (currentCell.getAdjacentMine() == 0) {
                for (int deltaRow = -1; deltaRow <= 1; deltaRow++){
                    for (int deltaCol = -1; deltaCol <= 1; deltaCol++){
                        if (deltaRow == 0 && deltaCol == 0) continue;

                        int neighborRow = currentRow + deltaRow;
                        int neighborCol = currentCol + deltaCol;

                        if (!isInsideBoard(neighborRow, neighborCol)) continue;
                        if (!queued[neighborRow][neighborCol]) {
                            stack.add(new Point(neighborRow, neighborCol));
                            queued[neighborRow][neighborCol] = true;
                        }
                    }
                }
            }
        }
        return toReveal;
    }

    public List<Point> getMineSet(){
        return Collections.unmodifiableList(mineList);
    }

    public void revealAllMine() {
        for (Point p : mineList) {
            cells[p.x][p.y].setRevealed(true);
        }
    }

    public boolean unrevealedCellExist(int r, int c){
        for (int deltaRow = -1; deltaRow <= 1; deltaRow++){
            for (int deltaCol = -1; deltaCol <= 1; deltaCol++){
                if (deltaRow == 0 && deltaCol == 0) continue;
                int neighborRow = r + deltaRow;
                int neighborCol = c + deltaCol;
                if (!isInsideBoard(neighborRow, neighborCol)) continue;
                Cell neighbor = cells[neighborRow][neighborCol];
                if (!neighbor.isRevealed() && !neighbor.isFlagged()) return true;
            }
        }
        return false;
    }

    // public boolean gameWon(){

    // }

    public boolean flaggedAllMine() {
        if (mineList.isEmpty()) return false;
        for (Point p : mineList) {
            Cell c = cells[p.x][p.y];
            if (!c.isFlagged()) return false;
        }
        return true;
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < rowTotal && col >= 0 && col < colTotal;
    }
}
