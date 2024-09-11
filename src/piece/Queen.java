package piece;

import main.GamePanel;

public class Queen extends Piece{
	public Queen(int col, int row, int color) {
		super(col, row, color);
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w-queen.png");
		} else {
			image = getImage("/piece/b-queen.png");
		}
	}
	
	@Override
	public int checkMove(int targetCol, int targetRow) {
		Bishop b = new Bishop(prevCol, prevRow, color);
		Rook r = new Rook(prevCol, prevRow, color);
		return Math.max(b.checkMove(targetCol, targetRow), r.checkMove(targetCol, targetRow));
	}
}
