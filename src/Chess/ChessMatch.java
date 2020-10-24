package Chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Chess.pieces.Bishop;
import Chess.pieces.King;
import Chess.pieces.Knight;
import Chess.pieces.Pawn;
import Chess.pieces.Queen;
import Chess.pieces.Rook;
import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;

public class ChessMatch {
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVunerable;
	private ChessPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8,8);
		turn = 1;
		currentPlayer = Color.WHITE;
		check = false;
		checkMate = false;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVunerable() {
		return enPassantVunerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
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
		valedateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source,target);
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
		//#specialMove promotion
		promoted = null;
		if(movedPiece instanceof Pawn) {
			if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0)||(movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
					
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))){
			checkMate = true;
		}
		else {
			nextTurn();
		}
		//# special move en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVunerable = movedPiece;
		}
		else {
			enPassantVunerable = null;
		}
		    return(ChessPiece) capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			throw new InvalidParameterException("Invalid type for promoted");
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type,promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type,Color color) {
		if(type.equals("B")) return new Bishop(board,color);
		if(type.equals("N")) return new Knight(board,color);
		if(type.equals("Q")) return new Queen(board,color);
		return new Rook(board,color);
	}
	
	private Piece makeMove(Position source,Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		//#specialMove castling KingSize rook
			if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
				Position sourceT = new Position(source.getRow(),source.getColumn() + 3);
				Position targetT = new Position(source.getRow(),source.getColumn() + 1);
				ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
				board.placePiece (rook, targetT);
				rook.increaseMoveCount();
			}
			
		//#specialMove castling QueenSize rook
			if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
				Position sourceT = new Position(source.getRow(),source.getColumn() - 4);
				Position targetT = new Position(source.getRow(),source.getColumn() - 1);
				ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
				board.placePiece (rook, targetT);
				rook.increaseMoveCount();
			}
		
		//#specialMove en passant
			if(p instanceof Pawn) {
				if (source.getColumn() != target.getColumn() && capturedPiece == null) {
					Position pawnPosition;
					if(p.getColor() == Color.WHITE) {
						pawnPosition = new Position(target.getRow() + 1,target.getColumn());
					}else {
						pawnPosition = new Position(target.getRow() - 1,target.getColumn());
					}
					capturedPiece = board.removePiece(pawnPosition);
					capturedPieces.add(capturedPiece);
					piecesOnTheBoard.remove(capturedPiece);
				}
			}
		
	  return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);

		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);;
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		//#specialundoMove castling KingSize rook
			if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
				Position sourceT = new Position(source.getRow(),source.getColumn() + 3);
				Position targetT = new Position(source.getRow(),source.getColumn() + 1);
				ChessPiece rook = (ChessPiece)board.removePiece(targetT);
				board.placePiece (rook, sourceT);
				rook.decreaseMoveCount();
			}
		//#specialundoMove castling QueenSize rook
			if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
				Position sourceT = new Position(source.getRow(),source.getColumn() - 4);
				Position targetT = new Position(source.getRow(),source.getColumn() - 1);
				ChessPiece rook = (ChessPiece)board.removePiece(targetT);
				board.placePiece (rook, sourceT);
				rook.decreaseMoveCount();
			}
			
			//#specialUndMove en passant
			if(p instanceof Pawn) {
				if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVunerable) {
					ChessPiece pawn = (ChessPiece)board.removePiece(target);
					Position pawnPosition;
					if(p.getColor() == Color.WHITE) {
						pawnPosition = new Position(3,target.getColumn());
					}else {
						pawnPosition = new Position(4,target.getColumn());
					}
					board.placePiece(pawn, pawnPosition);
				}
			}
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
	
	private Color opponent(Color color) {
		return (color == Color.WHITE)? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
// aqui	
	private boolean testCheckMate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p: list) {
			boolean[][] mat = p.possibleMoves();
			for(int i = 0; i < board.getRows();i++) {
				for(int j = 0; j < board.getColumns();j++) {
					if(mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i,j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column , int row , ChessPiece piece ) {
		board.placePiece( piece,new ChessPosition(column,row).toPosition());
		piecesOnTheBoard.add(piece);		
	}
	
	private void initialSetup() {
		// White piece 1º line
		placeNewPiece('A', 1, new Rook(board, Color.WHITE));
		placeNewPiece('B', 1, new Knight(board, Color.WHITE));
		placeNewPiece('C', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('D', 1, new Queen(board, Color.WHITE));
        placeNewPiece('E', 1, new King(board, Color.WHITE, this));
        placeNewPiece('F', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('G', 1, new Knight(board, Color.WHITE));
        placeNewPiece('H', 1, new Rook(board, Color.WHITE));
       // White piece 2º line
        placeNewPiece('A', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('B', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('C', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('D', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('E', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('F', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('G', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('H', 2, new Pawn(board, Color.WHITE, this));
       
        // Black piece 1º line
        placeNewPiece('A', 8, new Rook(board, Color.BLACK));
        placeNewPiece('B', 8, new Knight(board, Color.BLACK));
        placeNewPiece('C', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('D', 8, new Queen(board, Color.BLACK));
        placeNewPiece('E', 8, new King(board, Color.BLACK, this));
        placeNewPiece('F', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('G', 8, new Knight(board, Color.BLACK));
        placeNewPiece('H', 8, new Rook(board, Color.BLACK));
        // Black piece 2º line
        placeNewPiece('A', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('B', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('C', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('D', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('E', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('F', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('G', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('H', 7, new Pawn(board, Color.BLACK, this));
        
	}
}
