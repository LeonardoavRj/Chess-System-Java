package application;

import Chess.ChessMatch;

public class Progam {

	public static void main(String[] args) {
		//Chess layer and printing the board		
		ChessMatch chessMatch = new ChessMatch();
		UI.printBoard(chessMatch.getPieces());

	}

}
