package piece;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import main.Board;
import main.GamePanel;

public class Piece implements Cloneable{
	public BufferedImage image;
	public int x, y;
	public int col, row, prevCol, prevRow;
	public int color;
	
	public Piece(int col, int row, int color) {
		this.x = col * Board.SQUARE_SIZE;
		this.y = row * Board.SQUARE_SIZE;
		this.col = col;
		this.row = row;
		this.color = color;
		this.prevCol = col;
		this.prevRow = row;
	}
	@Override
    public Piece clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Shouldn't happen
        }
    }
	public BufferedImage getImage(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public int getCol() {
		return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public int getRow() {
		return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public void update() {
		prevCol = col;
		prevRow = row;
		col = getCol();
		row = getRow();
		x = col * Board.SQUARE_SIZE;
		y = row * Board.SQUARE_SIZE;
	}
	
	public void update_by_row_col() {
		prevCol = col;
		prevRow = row;
		x = col * Board.SQUARE_SIZE;
		y = row * Board.SQUARE_SIZE;
	}
	
	public void resetPosition() {
		col = prevCol;
		row = prevRow;
        x = col * Board.SQUARE_SIZE;
        y = row * Board.SQUARE_SIZE;
    }
	
	public int checkMove(int targetCol, int targetRow) {
		if (targetCol < 0 || targetCol >= Board.NUM_COL || targetRow < 0 || targetRow >= Board.NUM_ROW) {
			return -1;
		}
		if (targetCol == prevCol && targetRow == prevRow) {
            return -2;
		}
		Piece target = GamePanel.getPiece(targetCol, targetRow);
		System.out.println(target);
		if (target != null && target.color == color) {
			return -3;
		}
		return 0;
	}
	
	
	public void draw(java.awt.Graphics g) {
		g.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
	
	
}
