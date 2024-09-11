package piece;

import main.GamePanel;

public class Knight extends Piece{
	public Knight(int col, int row, int color) {
		super(col, row, color);
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w-knight.png");
		} else {
			image = getImage("/piece/b-knight.png");
		}
	}
	
	@Override
	public int checkMove(int targetCol, int targetRow) {
		int primary_check = super.checkMove(targetCol, targetRow);
		if (primary_check < 0)
			return primary_check;
		Piece target = GamePanel.getPiece(targetCol, targetRow);
		if ((Math.abs(targetCol - prevCol) == 2 && Math.abs(targetRow - prevRow) == 1)
				|| (Math.abs(targetCol - prevCol) == 1 && Math.abs(targetRow - prevRow) == 2)) {
			if (target == null)
				return 1;
			return 2;
		}
		return 0;
	}
}
