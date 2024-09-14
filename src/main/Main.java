package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // Create the window
        JFrame frame = new JFrame("Chess Java");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(600, 600); // Set a size for the initial menu window
        frame.setLayout(new BorderLayout());

        // Create a "Play" button for the main menu
        JButton playButton = new JButton("Play");

        // Customize the Play button appearance
        playButton.setFont(new Font("Arial", Font.BOLD, 30)); // Large, bold font
        playButton.setBackground(new Color(50, 205, 50)); // Set a green background
        playButton.setForeground(Color.WHITE); // Set text color to white
        playButton.setFocusPainted(false); // Remove the focus border
        playButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand when hovering

        // Round the corners of the button
        playButton.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 5, true));
        playButton.setContentAreaFilled(true);
        playButton.setOpaque(true);

        // Add an action listener to the button
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save the current window position
//                Point currentLocation = frame.getLocation();

                // When "Play" is clicked, start the game and display the GamePanel
                frame.getContentPane().removeAll(); // Remove the play button
                GamePanel gp = new GamePanel(); // Create the GamePanel
                frame.add(gp); // Add the game panel to the frame

                // Revalidate and pack the frame
                frame.revalidate(); // Refresh the frame to show the game panel
                frame.pack();

                // Restore the window position after adding the game panel
//                frame.setLocation(currentLocation);
                frame.setLocationRelativeTo(null); // Center the window
                frame.setVisible(true);

                // Optionally, center the frame again:
                // frame.setLocationRelativeTo(null);

                gp.start(); // Start the game logic
            }
        });

        // Create a label for the text "By @thanhto24, HCMUS"
        JLabel creditsLabel = new JLabel("By @thanhto24, HCMUS");
        creditsLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Set font style
        creditsLabel.setForeground(Color.GRAY); // Set text color to gray
        creditsLabel.setHorizontalAlignment(SwingConstants.LEFT); // Align text to the left
        creditsLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0)); // Add some padding

        // Create a panel to place the label in the bottom left
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(creditsLabel, BorderLayout.WEST); // Align the label to the left

        // Add the button to the frame
        frame.add(playButton, BorderLayout.CENTER);

        // Add the label to the bottom of the frame
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Display the window
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }
}
