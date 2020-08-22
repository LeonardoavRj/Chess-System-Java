package Chess.pieces;

import Chess.ChessPiece;
import Chess.Color;
import boardGame.Board;
import boardGame.Position;

public class Rook extends ChessPiece {

	public Rook(Board board, Color color) {
		super(board, color);
	}
	@Override
	public String toString() {
		return "R";
	}
	@Override
	public boolean[][] possibleMoves() {
<<<<<<< HEAD
		boolean[][] mat = new boolean [getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
		//above
		p.setValues(position.getRow() -1, position.getColumn());
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setRow(p.getRow() - 1);
		}
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		//left
				p.setValues(position.getRow(), position.getColumn() - 1);
				while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
					p.setColumn(p.getColumn() - 1);
				}
				if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
		//right
				p.setValues(position.getRow(), position.getColumn() + 1);
				while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
					p.setColumn(p.getColumn() + 1);
				}
				if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}

		//below
				p.setValues(position.getRow() +1, position.getColumn());
				while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
					p.setRow(p.getRow() + 1);
				}
				if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
		
=======
		boolean[][] mat = new boolean [getBoard().getColumns()][getBoard().getRows()];
>>>>>>> ab94d69e768b6bc8b643362078b6a536394783c4
		return mat;
	}

}
