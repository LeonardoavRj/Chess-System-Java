package Chess.pieces;

import Chess.ChessPiece;
import Chess.Color;
import boardGame.Board;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}
	@Override
	public String toString() {
		return "K";
	}
	@Override
	public boolean[][] possibleMoves() {
<<<<<<< HEAD
		boolean[][] mat = new boolean [getBoard().getRows()][getBoard().getColumns()];
=======
		boolean[][] mat = new boolean [getBoard().getColumns()][getBoard().getRows()];
>>>>>>> ab94d69e768b6bc8b643362078b6a536394783c4
		return mat;
	}

}
