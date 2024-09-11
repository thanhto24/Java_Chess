package main;

public class Board {
	public static final int NUM_ROW = 8;
	public static final int NUM_COL = 8;
	public static final int SQUARE_SIZE = 90;
	public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;
	
	public void draw(java.awt.Graphics g) {
		for (int row = 0; row < NUM_ROW; row++) {
			for (int col = 0; col < NUM_COL; col++) {
				if ((row + col) % 2 == 0) {
					g.setColor(new java.awt.Color(255, 206, 158));
				} else {
					g.setColor(new java.awt.Color(209, 139, 71));
				}
				g.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
	}
}
