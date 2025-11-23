package ticTacToe;

import javax.swing.*;
import java.awt.*;

public class SetupFrame extends JFrame {

    private JComboBox<String> boardSizeBox, modeBox, themeBox, difficultyBox;

    public SetupFrame() {
        setTitle("Game Setup");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Game Setup", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new GridLayout(9, 1, 0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        JLabel lblBoard = createLabel("Select Board Size:");
        String[] sizes = {"3 x 3", "4 x 4", "5 x 5"};
        boardSizeBox = new JComboBox<>(sizes);
        styleCombo(boardSizeBox);

        JLabel lblMode = createLabel("Select Mode:");
        String[] modes = {"Play with Friend", "Play with Bot"};
        modeBox = new JComboBox<>(modes);
        styleCombo(modeBox);

        JLabel lblTheme = createLabel("Select Theme:");
        // First choice is default X / O
        String[] themes = {"X O", "🔥 💧", "🐱 🐶", "🍕 🍔", "☀ 🌙", "🌸 🌻"};
        themeBox = new JComboBox<>(themes);
        styleCombo(themeBox);

        JLabel lblDifficulty = createLabel("Bot Difficulty:");
        String[] difficulties = {"Easy", "Normal", "Hard", "Advanced"};
        difficultyBox = new JComboBox<>(difficulties);
        styleCombo(difficultyBox);

        // Show/hide difficulty based on mode
        modeBox.addActionListener(e -> {
            boolean isBot = modeBox.getSelectedIndex() == 1;
            lblDifficulty.setVisible(isBot);
            difficultyBox.setVisible(isBot);
        });
        lblDifficulty.setVisible(false);
        difficultyBox.setVisible(false);

        JButton btnStart = createButton("Start Game");

        panel.add(lblBoard);
        panel.add(boardSizeBox);
        panel.add(lblMode);
        panel.add(modeBox);
        panel.add(lblTheme);
        panel.add(themeBox);
        panel.add(lblDifficulty);
        panel.add(difficultyBox);
        panel.add(btnStart);

        add(panel, BorderLayout.CENTER);

        btnStart.addActionListener(e -> {
            int boardSize = boardSizeBox.getSelectedIndex() + 3; // 3,4,5
            boolean vsBot = modeBox.getSelectedIndex() == 1;
            String theme = (String) themeBox.getSelectedItem();

            String skin1, skin2;
            if ("X O".equals(theme)) {
                skin1 = "X";
                skin2 = "O";
            } else {
                // split paired emojis by space
                String[] parts = theme.split(" ");
                skin1 = parts[0];
                skin2 = parts.length > 1 ? parts[1] : "O";
            }

            String difficulty = vsBot ? (String) difficultyBox.getSelectedItem() : "None";

            dispose();
            new GameFrame(boardSize, vsBot, skin1, skin2, difficulty);
        });

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 20));
        return lbl;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(new Color(60, 60, 60));
        combo.setForeground(Color.WHITE);
        // Use emoji-safe font for combos so emojis show in list
        combo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        combo.setFocusable(false);
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
}
