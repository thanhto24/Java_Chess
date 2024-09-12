package main;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Chess Java");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		GamePanel gp = new GamePanel();
		frame.add(gp);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		gp.start();
	}
}