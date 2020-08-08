package application;

import Chess.ChessPiece;

public class UI {

	public static void printBoard(ChessPiece[][] pieces) {
		
		for(int i = 0; i < pieces.length;i++) {
			System.out.print ((8-i) + " ");
			for(int j =0;j < pieces.length;j++) {
				PrintPiece(pieces[i][j]);
			}
			System.out.println();
		}
		System.out.println("  A B C D E F G H");
	}
	
	public static void PrintPiece(ChessPiece piece) {
		
		if (piece == null) {
			System.out.print("-");
		}
		else {
			System.out.print(piece);
		}
		System.out.print(" ");
	}
}
