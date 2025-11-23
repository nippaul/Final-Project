package ticTacToe;

public class GameLogic {

    // check if placing 'skin' at (r,c) causes a win (assumes board[r][c] == skin)
    public static boolean checkWin(String[][] board, int r, int c, String skin) {
        int size = board.length;
        int needed = (size == 3 ? 3 : (size == 4 ? 3 : 4));
        int[][] dirs = {{1,0},{0,1},{1,1},{1,-1}};

        for (int[] d : dirs) {
            int count = 1;
            int rr = r + d[0], cc = c + d[1];
            while (inBounds(rr, cc, size) && skin.equals(board[rr][cc])) { count++; rr += d[0]; cc += d[1]; }
            rr = r - d[0]; cc = c - d[1];
            while (inBounds(rr, cc, size) && skin.equals(board[rr][cc])) { count++; rr -= d[0]; cc -= d[1]; }
            if (count >= needed) return true;
        }
        return false;
    }

    public static boolean isFull(String[][] board) {
        int n = board.length;
        for (int r = 0; r < n; r++) for (int c = 0; c < n; c++) if (board[r][c] == null) return false;
        return true;
    }

    private static boolean inBounds(int r, int c, int size) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    // Scans entire board to see if 'skin' has any winning line (used by minimax)
    public static boolean existsWinFor(String[][] board, String skin, int needed) {
        int n = board.length;
        int[][] dirs = {{1,0},{0,1},{1,1},{1,-1}};
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (skin.equals(board[r][c])) {
                    for (int[] d : dirs) {
                        int count = 1;
                        int rr = r + d[0], cc = c + d[1];
                        while (inBounds(rr, cc, n) && skin.equals(board[rr][cc])) { count++; rr += d[0]; cc += d[1]; }
                        rr = r - d[0]; cc = c - d[1];
                        while (inBounds(rr, cc, n) && skin.equals(board[rr][cc])) { count++; rr -= d[0]; cc -= d[1]; }
                        if (count >= needed) return true;
                    }
                }
            }
        }
        return false;
    }
}
