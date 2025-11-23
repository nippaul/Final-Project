package ticTacToe;

public class Board {
    private final String[][] cells;
    private final int size;

    public Board(int size) {
        this.size = size;
        this.cells = new String[size][size];
        reset();
    }

    public void reset() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                cells[r][c] = null;
            }
        }
    }

    public String getCell(int r, int c) {
        return cells[r][c];
    }

    public void setCell(int r, int c, String value) {
        cells[r][c] = value;
    }

    public int getSize() {
        return size;
    }

    public String[][] getCellsCopy() {
        String[][] copy = new String[size][size];
        for (int r = 0; r < size; r++) System.arraycopy(cells[r], 0, copy[r], 0, size);
        return copy;
    }
}

