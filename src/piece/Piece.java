package piece;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
//import java.util.List;
import java.util.Vector;


import main.Board;
import main.GamePanel;
import main.Cell;

public class Piece implements Cloneable{
	public BufferedImage image;
	public int x, y;
	public int col, row, prevCol, prevRow;
	public int color;
	public Vector<Cell> manageCell = new Vector<Cell>();
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
	
//	public void updateManageCell() {
//		manageCell.clear();
//		for (int r = 0; r < Board.NUM_ROW; r++) {
//			for (int c = 0; c < Board.NUM_COL; c++) {
//				int check = checkMove(c, r);
//				if (check > 0) {
//					manageCell.add(new Cell(c, r));
//				}
//			}
//		}
//	}
	
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
//		System.out.println("Piece checkMove from " + col + " " + row + " to " + targetCol + " " + targetRow + " by " + getSymbol() + color + " not implemented yet.");
		
		if (targetCol < 0 || targetCol >= Board.NUM_COL || targetRow < 0 || targetRow >= Board.NUM_ROW) {
			return -1;
		}
		if (targetCol == prevCol && targetRow == prevRow) {
            return -2;
		}
		Piece target = GamePanel.getPiece(targetCol, targetRow);
//		System.out.println(target);
		if (target != null && target.color == color) {
			return -3;
		}
		if (GamePanel.promotion)
			return -4;
		
		Piece temp = this.clone();
		temp.col = targetCol;
		temp.row = targetRow;
		GamePanel.pieces.remove(this);
		if (target != null) {
			GamePanel.pieces.remove(target);
		}
		GamePanel.pieces.add(temp);
		if (color == GamePanel.currentPlayer) {
			if (KingInCheck(color)) {
				GamePanel.pieces.add(this);
				if (target != null) {
					GamePanel.pieces.add(target);
				}
				GamePanel.pieces.remove(temp);
				return -5;
			}
		}
		GamePanel.pieces.add(this);
		if (target != null) {
			GamePanel.pieces.add(target);
		}
		GamePanel.pieces.remove(temp);
		return 0;
	}
	
	public boolean KingInCheck(int color)
	{
		Piece king = null;
		if (GamePanel.selectedPiece != null && GamePanel.selectedPiece.color == color && GamePanel.selectedPiece instanceof King) {
			king = GamePanel.selectedPiece;
		}
		else {
		for (int r = 0; r < Board.NUM_ROW; r++) {
			for (int c = 0; c < Board.NUM_COL; c++) {
				Piece target = GamePanel.getPiece(c, r);
				if (target != null && target.color == color) {
					if (target instanceof King) {
						king = target;
						break;
					}
				}
			}
			if (king != null)
				break;
		}
		}
		if (king == null)
		{
			System.out.println("King not found");
			return false;

		}
		for (int r = 0; r < Board.NUM_ROW; r++) {
			for (int c = 0; c < Board.NUM_COL; c++) {
				Piece target = GamePanel.getPiece(c, r);
				if (target != null && target.color != color) {
					int check = target.checkMove(king.col, king.row);
					if (check > 0) {
//						System.out.println("King " + color + " in check by " + target.getSymbol() + target.color + check);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public void draw(java.awt.Graphics g) {
		g.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
	public char getSymbol() {
        return ' ';
    }
	
}
