package Chess;

import Chess.pieces.King;
import Chess.pieces.Rook;
import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;

public class ChessMatch {
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	
	public ChessMatch() {
		board = new Board(8,8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece [board.getRows()][board.getColumns()];
		 for(int i=0; i<board.getRows();i++) {
			 for(int j=0;j<board.getColumns();j++) {
				 mat[i][j] = (ChessPiece) board.piece(i, j);
			 }
		 }
		 return mat;
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();
		valedateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition,ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		valedateSourcePosition(source);
		valedateTargetPosition(source,target);
		Piece capturedPiece = makeMove(source,target);
		nextTurn();
		return(ChessPiece) capturedPiece;	
	}
	
	private Piece makeMove(Position source,Position target) {
		Piece p = board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		return capturedPiece;
	}
	
	private void valedateSourcePosition(Position position) {
		
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is not pieces on source position");
		}
		if(currentPlayer !=((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The piece chosen is not yours");			
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is not possible moves for the chosen pieces");
		}
		
	}
	
	private void valedateTargetPosition(Position source, Position Target) {
		if(!board.piece(source).possibleMove(Target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
		turn ++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private void placeNewPiece(char column , int row , ChessPiece piece ) {
		board.placePiece( piece,new ChessPosition(column,row).toPosition());
		
	}
	private void initialSetup() {
		placeNewPiece('C', 1, new Rook(board, Color.WHITE));
        placeNewPiece('C', 2, new Rook(board, Color.WHITE));
        placeNewPiece('D', 2, new Rook(board, Color.WHITE));
        placeNewPiece('E', 2, new Rook(board, Color.WHITE));
        placeNewPiece('E', 1, new Rook(board, Color.WHITE));
        placeNewPiece('D', 1, new King(board, Color.WHITE));

        placeNewPiece('C', 7, new Rook(board, Color.BLACK));
        placeNewPiece('C', 8, new Rook(board, Color.BLACK));
        placeNewPiece('D', 7, new Rook(board, Color.BLACK));
        placeNewPiece('E', 7, new Rook(board, Color.BLACK));
        placeNewPiece('E', 8, new Rook(board, Color.BLACK));
        placeNewPiece('D', 8, new King(board, Color.BLACK));
	}
}
