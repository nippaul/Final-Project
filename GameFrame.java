package ticTacToe;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameFrame extends JFrame {

    private final Board boardModel;
    private final JButton[][] buttons;
    private final int size;
    private final boolean vsBot;
    private String player1Skin, player2Skin;
    private boolean player1Turn;
    private final JLabel turnLabel;
    private final String botDifficulty;
    private JButton lastMoveBtn = null;
    private final Random random = new Random();

    private static final int RIPPLE_DELAY_MS = 120;

    public GameFrame(int size, boolean vsBot, String skin1, String skin2, String difficulty) {
        this.size = size;
        this.vsBot = vsBot;
        this.player1Skin = skin1;
        this.player2Skin = skin2;
        this.botDifficulty = difficulty;
        this.boardModel = new Board(size);

        // ---------------- Randomize first player if vsBot ----------------
        if (vsBot) {
            this.player1Turn = random.nextBoolean(); // true = player first, false = bot first
            if (!player1Turn) {
                // Bot is first → swap skins so bot uses player1Skin
                String temp = player1Skin;
                player1Skin = player2Skin;
                player2Skin = temp;
            }
        } else {
            // PvP: player1 always starts first
            this.player1Turn = true;
        }

        // ---------------- Swing UI Setup ----------------
        setTitle("TicTacToe");
        setSize(600, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        turnLabel = new JLabel(getTurnText(), SwingConstants.CENTER);
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        turnLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(turnLabel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(size, size));
        grid.setBackground(new Color(30, 30, 30));

        buttons = new JButton[size][size];
        Font btnFont = new Font("Segoe UI Emoji", Font.PLAIN, size == 3 ? 60 : size == 4 ? 50 : 40);

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JButton btn = new JButton("");
                btn.setFont(btnFont);
                btn.setForeground(Color.WHITE);
                btn.setBackground(new Color(60, 60, 60));
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

                final int rr = r, cc = c;
                btn.addActionListener(e -> handlePlayerMove(rr, cc));

                buttons[r][c] = btn;
                grid.add(btn);
            }
        }

        add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(30, 30, 30));
        bottom.setLayout(new GridLayout(1, 2, 20, 0));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnRestart = createButton("Restart");
        JButton btnMenu = createButton("Main Menu");

        btnRestart.addActionListener(e -> restartGame());
        btnMenu.addActionListener(e -> {
            dispose();
            new MenuFrame();
        });

        bottom.add(btnRestart);
        bottom.add(btnMenu);

        add(bottom, BorderLayout.SOUTH);

        // ---------------- Bot first move if bot starts first ----------------
        if (vsBot && !player1Turn) {
            javax.swing.Timer t = new javax.swing.Timer(300, e -> {
                ((javax.swing.Timer) e.getSource()).stop();
                botMove();
            });
            t.setRepeats(false);
            t.start();
        }

        setVisible(true);
    }

    private String getTurnText() {
        return player1Turn ? "Player 1's Turn (" + player1Skin + ")" : "Player 2's Turn (" + player2Skin + ")";
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return btn;
    }

    // ---------------- Handle Player Moves ----------------
    private void handlePlayerMove(int r, int c) {
        if (boardModel.getCell(r, c) != null) return;

        if (lastMoveBtn != null) lastMoveBtn.setBackground(new Color(60, 60, 60));

        String skin = player1Turn ? player1Skin : player2Skin;
        boardModel.setCell(r, c, skin);
        buttons[r][c].setText(skin);
        buttons[r][c].setBackground(Color.ORANGE);
        lastMoveBtn = buttons[r][c];

        String[][] state = boardModel.getCellsCopy();

        if (GameLogic.checkWin(state, r, c, skin)) {
            List<int[]> winCells = getWinningPositions(state, r, c, skin);
            if (!winCells.isEmpty()) animateRipple(winCells);
            turnLabel.setText((player1Turn ? "Player 1" : "Player 2") + " Wins!");
            disableBoard();
            return;
        }

        if (GameLogic.isFull(state)) {
            turnLabel.setText("Draw!");
            return;
        }

        player1Turn = !player1Turn;
        turnLabel.setText(getTurnText());

        if (vsBot && !player1Turn) {
            javax.swing.Timer t = new javax.swing.Timer(300, e -> {
                ((javax.swing.Timer) e.getSource()).stop();
                botMove();
            });
            t.setRepeats(false);
            t.start();
        }
    }

    // ---------------- Winning Positions & Ripple Animation ----------------
    private List<int[]> getWinningPositions(String[][] state, int r, int c, String skin) {
        List<int[]> result = new ArrayList<>();
        int[][] dirs = {{1,0},{0,1},{1,1},{1,-1}};
        int needed = (size == 3 ? 3 : (size == 4 ? 3 : 4));

        for (int[] d : dirs) {
            List<int[]> line = new ArrayList<>();
            int br = r, bc = c;
            while (valid(br-d[0], bc-d[1]) && skin.equals(state[br-d[0]][bc-d[1]])) { br -= d[0]; bc -= d[1]; }
            int fr = br, fc = bc;
            while (valid(fr, fc) && skin.equals(state[fr][fc])) { line.add(new int[]{fr, fc}); fr+=d[0]; fc+=d[1]; }
            if (line.size() >= needed) {
                for (int i = 0; i + needed <= line.size(); i++) {
                    List<int[]> sub = line.subList(i, i + needed);
                    boolean contains = false;
                    for (int[] p : sub) if (p[0] == r && p[1] == c) { contains = true; break; }
                    if (contains) { result.addAll(sub); return result; }
                }
            }
        }
        return result;
    }

    private void animateRipple(List<int[]> winCells) {
        for (int[] p : winCells) buttons[p[0]][p[1]].setBackground(new Color(60,60,60));
        final int total = winCells.size();
        final int[] idx = {0};
        javax.swing.Timer ripple = new javax.swing.Timer(RIPPLE_DELAY_MS, null);
        ripple.addActionListener(e -> {
            if (idx[0] >= total) { ((javax.swing.Timer)e.getSource()).stop();
                for (int[] p : winCells) buttons[p[0]][p[1]].setBackground(Color.GREEN);
                return;
            }
            int[] p = winCells.get(idx[0]); buttons[p[0]][p[1]].setBackground(Color.GREEN);
            idx[0]++;
        });
        ripple.setInitialDelay(0);
        ripple.start();
    }

    // ---------------- Bot Logic ----------------
    private void botMove() {
        int[] move;
        switch (botDifficulty) {
            case "Easy": move = botEasy(); break;
            case "Normal": move = botNormal(); break;
            case "Hard": move = botHard(); break;
            case "Advanced": move = botAdvanced(); break;
            default: move = botEasy(); break;
        }
        if (move != null) SwingUtilities.invokeLater(() -> handlePlayerMove(move[0], move[1]));
    }

    private List<int[]> availableMoves(String[][] state) {
        List<int[]> out = new ArrayList<>();
        for (int r = 0; r < size; r++) for (int c = 0; c < size; c++) if (state[r][c]==null) out.add(new int[]{r,c});
        return out;
    }
    private List<int[]> availableMoves() { return availableMoves(boardModel.getCellsCopy()); }

    private int[] botEasy() {
        List<int[]> avail = availableMoves();
        if (avail.isEmpty()) return null;
        return avail.get(random.nextInt(avail.size()));
    }

    private int[] botNormal() {
        int[] mv = findWinningOrBlockingMove(player2Skin); if (mv!=null) return mv;
        mv = findWinningOrBlockingMove(player1Skin); if (mv!=null) return mv;
        return botEasy();
    }

    private int[] botHard() {
        int[] mv = findWinningOrBlockingMove(player2Skin); if (mv!=null) return mv;
        mv = findWinningOrBlockingMove(player1Skin); if (mv!=null) return mv;
        if (size%2==1 && boardModel.getCell(size/2,size/2)==null) return new int[]{size/2,size/2};
        List<int[]> corners = cornersAvailable();
        if (!corners.isEmpty()) return corners.get(random.nextInt(corners.size()));
        return botEasy();
    }

    private int[] botAdvanced() {
        String ai = player2Skin; String human = player1Skin;
        int needed = (size==3?3:(size==4?3:4));
        int depthLimit = (size==5?6:Integer.MAX_VALUE);
        int bestScore = Integer.MIN_VALUE; int[] bestMove = null;
        for (int[] m: availableMoves()) {
            boardModel.setCell(m[0], m[1], ai);
            int score = minimax(boardModel.getCellsCopy(), false, ai, human, 0, depthLimit, Integer.MIN_VALUE, Integer.MAX_VALUE, needed);
            boardModel.setCell(m[0], m[1], null);
            if (score>bestScore) { bestScore=score; bestMove=m; }
        }
        if (bestMove==null) return botHard();
        return bestMove;
    }

    private int minimax(String[][] state, boolean isAiTurn, String ai, String human, int depth, int depthLimit, int alpha, int beta, int needed){
        if(GameLogic.existsWinFor(state, ai, needed)) return 10-depth;
        if(GameLogic.existsWinFor(state, human, needed)) return depth-10;
        if(GameLogic.isFull(state)) return 0;
        if(depth>=depthLimit) return 0;

        if(isAiTurn){
            int maxEval = Integer.MIN_VALUE;
            for(int[] m: availableMoves(state)){
                state[m[0]][m[1]]=ai;
                int eval = minimax(state,false,ai,human,depth+1,depthLimit,alpha,beta,needed);
                state[m[0]][m[1]]=null;
                maxEval=Math.max(maxEval,eval);
                alpha=Math.max(alpha,eval);
                if(beta<=alpha) break;
            }
            return maxEval;
        }else{
            int minEval=Integer.MAX_VALUE;
            for(int[] m: availableMoves(state)){
                state[m[0]][m[1]]=human;
                int eval=minimax(state,true,ai,human,depth+1,depthLimit,alpha,beta,needed);
                state[m[0]][m[1]]=null;
                minEval=Math.min(minEval,eval);
                beta=Math.min(beta,eval);
                if(beta<=alpha) break;
            }
            return minEval;
        }
    }

    private int[] findWinningOrBlockingMove(String skin) {
        for (int r=0;r<size;r++) for (int c=0;c<size;c++) {
            if(boardModel.getCell(r,c)==null){
                boardModel.setCell(r,c,skin);
                boolean wins = GameLogic.checkWin(boardModel.getCellsCopy(),r,c,skin);
                boardModel.setCell(r,c,null);
                if(wins) return new int[]{r,c};
            }
        }
        return null;
    }

    private List<int[]> cornersAvailable() {
        List<int[]> corners = new ArrayList<>();
        int[][] pts = {{0,0},{0,size-1},{size-1,0},{size-1,size-1}};
        for(int[] p: pts) if(boardModel.getCell(p[0],p[1])==null) corners.add(p);
        return corners;
    }

    // ---------------- Utilities ----------------
    private void restartGame() {
        boardModel.reset();
        if(vsBot) {
            player1Turn = random.nextBoolean();
            if(!player1Turn) {
                String temp = player1Skin;
                player1Skin = player2Skin;
                player2Skin = temp;
            }
        } else player1Turn=true;

        turnLabel.setText(getTurnText());
        lastMoveBtn=null;
        for(int r=0;r<size;r++) for(int c=0;c<size;c++){
            buttons[r][c].setText("");
            buttons[r][c].setEnabled(true);
            buttons[r][c].setBackground(new Color(60,60,60));
        }
        if(vsBot && !player1Turn) SwingUtilities.invokeLater(this::botMove);
    }

    private void disableBoard(){
        for(int r=0;r<size;r++) for(int c=0;c<size;c++) buttons[r][c].setEnabled(false);
    }

    private boolean valid(int r,int c){ return r>=0 && r<size && c>=0 && c<size; }
}
