package ticTacToe;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {

    public MenuFrame() {
        setTitle("TicTacToe");
        setSize(600, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("TicTacToe", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new GridLayout(2, 1, 0, 25));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton btnPlay = createButton("Play");
        JButton btnExit = createButton("Exit");

        panel.add(btnPlay);
        panel.add(btnExit);

        add(panel, BorderLayout.CENTER);

        btnPlay.addActionListener(e -> {
            dispose();
            new SetupFrame();
        });

        btnExit.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18)); // slightly smaller
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return btn;
    }
}
