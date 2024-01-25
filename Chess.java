//Created By: Veda Yakkali and Matt Eng

package chess;

import java.util.ArrayList;

import java.lang.Math;

class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank;  // 1..8
	public String toString() {
		return ""+pieceFile+pieceRank+":"+pieceType;
	}
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece)other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

public class Chess {
	
	enum Player { white, black }
	
	public static ReturnPlay board;
	public static Player currColor;
	public static boolean enPassantOccurred = false;
	public static ReturnPiece enPassantCapture;
	public static boolean wkingMoved = false;
	public static boolean bkingMoved = false;
    public static boolean rwrookMoved = false;
	public static boolean lwrookMoved = false;
	public static boolean rbrookMoved = false;
	public static boolean lbrookMoved = false;

	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
		
		ReturnPlay chesspy = new ReturnPlay();
		//chesspy.piecesOnBoard = board.piecesOnBoard;
		
		Player selectedColor = null;
		boolean invalid = true;
		boolean draw = false;
		boolean promotion = false;
		String[] strSplit = move.split(" "); 

		//Only input should be resign if 1
        if(strSplit.length == 1) {
			//To make sure user is resigning
			if(strSplit[0].equals("resign")){
				if(currColor == Player.white){
					chesspy = board;
					chesspy.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
					return chesspy;
				}
				else{
					chesspy = board;
					chesspy.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
					return chesspy;
				}
			}
			else{
				if(!(strSplit[0].equals("resign"))) {
					chesspy = board;
					chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
					return chesspy;
				}
			}
        }

		//Checks to see if input of 2, has 2 characters, should be a1 a2, not a 1
		if(strSplit[0].length() != 2 || strSplit[1].length() != 2) {
			chesspy = board;
			chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return chesspy;
		}
	
        char currFile = strSplit[0].charAt(0);
        int currRank = Character.getNumericValue(strSplit[0].charAt(1));

        char finalFile = strSplit[1].charAt(0);
        int finalRank = Character.getNumericValue(strSplit[1].charAt(1));

		//Get the selected piece
		ReturnPiece selected = getPiece(currFile, currRank);

		if(selected != null){
			if(selected.pieceType.ordinal() <= 5){
				selectedColor = Player.white;
			}
			else{
				selectedColor = Player.black;
			}
		}

		//Check to see if selected piece is the right color of turn
		if(selected == null || selectedColor != currColor){
			chesspy = board;
			chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return chesspy;
		}


		//En Passant
		int intCurrFile = currFile - 'a';
		int intFinalFile = finalFile - 'a';
		ReturnPiece attacked = getPiece(finalFile, finalRank);
		ReturnPiece templ = getPiece((char)(intCurrFile + 'a' - 1), currRank);
		ReturnPiece tempr = getPiece((char)(intCurrFile + 'a' + 1), currRank);
		
		if(selected.pieceType == ReturnPiece.PieceType.WP || selected.pieceType == ReturnPiece.PieceType.BP){
			if(Math.abs(currRank - finalRank) == 2){
				if(selected.pieceType == ReturnPiece.PieceType.WP && finalRank == 4){
					enPassantCapture = selected;
					enPassantOccurred = true;
				}
				if(selected.pieceType == ReturnPiece.PieceType.BP && finalRank == 5){
					enPassantCapture = selected;
					enPassantOccurred = true;
				}
			}
			else{
				if(enPassantOccurred && selected.pieceType == ReturnPiece.PieceType.WP && currRank == 5 && finalRank == 6 && Math.abs(finalFile - currFile) == 1|| 
				   enPassantOccurred && selected.pieceType == ReturnPiece.PieceType.BP && currRank == 4 && finalRank == 3 && Math.abs(finalFile - currFile) == 1){
					if(intCurrFile == 0){
						if(tempr != null){
							if((selected.pieceType == ReturnPiece.PieceType.WP && tempr.pieceType == ReturnPiece.PieceType.BP && tempr.equals(enPassantCapture)) || 
							(selected.pieceType == ReturnPiece.PieceType.BP && tempr.pieceType == ReturnPiece.PieceType.WP && tempr.equals(enPassantCapture))){
								if(strSplit.length == 3 && strSplit[2].equals("draw?")){
									chesspy.message = ReturnPlay.Message.DRAW;
								}
								else if(strSplit.length == 3 && !(strSplit[2].equals("draw?"))){
									chesspy = board;
									chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
									return chesspy;
								}

								board.piecesOnBoard.remove(tempr);
								selected.pieceFile = ReturnPiece.PieceFile.values()[intFinalFile];
								selected.pieceRank = finalRank;
								chesspy = board;
								
								if(currColor == Player.white){
									currColor = Player.black;
								}
								else{
									currColor = Player.white;
								}

								enPassantOccurred = false;
								return chesspy;
							}
							else{
								chesspy = board;
								chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
								return chesspy;
							}
						}
						else{
							chesspy = board;
							chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
							return chesspy;
						}
						
					}
					else if(intCurrFile == 7){
						if(templ != null){
							if((selected.pieceType == ReturnPiece.PieceType.WP && templ.pieceType == ReturnPiece.PieceType.BP && templ.equals(enPassantCapture)) ||
							(selected.pieceType == ReturnPiece.PieceType.BP && templ.pieceType == ReturnPiece.PieceType.WP && templ.equals(enPassantCapture))){
								if(strSplit.length == 3 && strSplit[2].equals("draw?")){
									chesspy.message = ReturnPlay.Message.DRAW;
								}
								else if(strSplit.length == 3 && !(strSplit[2].equals("draw?"))){
									chesspy = board;
									chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
									return chesspy;
								}

								board.piecesOnBoard.remove(templ);
								selected.pieceFile = ReturnPiece.PieceFile.values()[intFinalFile];
								selected.pieceRank = finalRank;
								chesspy = board;
								
								if(currColor == Player.white){
									currColor = Player.black;
								}
								else{
									currColor = Player.white;
								}

								enPassantOccurred = false;
								return chesspy;
							}
							else{
								chesspy = board;
								chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
								return chesspy;
							}
						}
						else{
							chesspy = board;
							chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
							return chesspy;
						}	
					}
					else {
						if(templ != null || tempr != null){
							if(templ != null){
								if( (selected.pieceType == ReturnPiece.PieceType.WP && templ.pieceType == ReturnPiece.PieceType.BP && templ.equals(enPassantCapture)) || 
									(selected.pieceType == ReturnPiece.PieceType.BP && templ.pieceType == ReturnPiece.PieceType.WP && templ.equals(enPassantCapture)) ){
										if(strSplit.length == 3 && strSplit[2].equals("draw?")){
											chesspy.message = ReturnPlay.Message.DRAW;
										}
										else if(strSplit.length == 3 && !(strSplit[2].equals("draw?"))){
											chesspy = board;
											chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
											return chesspy;
										}
										
										board.piecesOnBoard.remove(templ);
										selected.pieceFile = ReturnPiece.PieceFile.values()[intFinalFile];
										selected.pieceRank = finalRank;
										chesspy = board;
										
										if(currColor == Player.white){
											currColor = Player.black;
										}
										else{
											currColor = Player.white;
										}

										enPassantOccurred = false;
										return chesspy;
									}
								else{
									chesspy = board;
									chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
									return chesspy;
								}
							}
							else if(tempr != null){
								if( (selected.pieceType == ReturnPiece.PieceType.WP && tempr.pieceType == ReturnPiece.PieceType.BP && tempr.equals(enPassantCapture)) || 
									(selected.pieceType == ReturnPiece.PieceType.BP && tempr.pieceType == ReturnPiece.PieceType.WP && tempr.equals(enPassantCapture)) ){
										if(strSplit.length == 3 && strSplit[2].equals("draw?")){
											chesspy.message = ReturnPlay.Message.DRAW;
										}
										else if(strSplit.length == 3 && !(strSplit[2].equals("draw?"))){
											chesspy = board;
											chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
											return chesspy;
										}
										
										board.piecesOnBoard.remove(tempr);
										selected.pieceFile = ReturnPiece.PieceFile.values()[intFinalFile];
										selected.pieceRank = finalRank;
										chesspy = board;
										
										if(currColor == Player.white){
											currColor = Player.black;
										}
										else{
											currColor = Player.white;
										}

										enPassantOccurred = false;
										return chesspy;
								}
								
								else{
									chesspy = board;
									chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
									return chesspy;
								}
						
							}
						}
						else{
							chesspy = board;
							chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
							return chesspy;
						}
						
					}
				}
			}
		}




		if(strSplit.length >= 3){

            if(strSplit[2].equals("draw?")){
                draw = true;
            }
            else if( (strSplit[2].equals("R")) || (strSplit[2].equals("N")) || (strSplit[2].equals("B")) || (strSplit[2].equals("Q"))){
                // check pawn is in correct rank (row) to allow promotion
                if( !(selectedColor == Player.white && currRank == 7) && !(selectedColor == Player.black && currRank == 2) ) {
                        chesspy = board;
                        chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
                        return chesspy;
                }
                //If there is a case example of:    g7 g8 N draw?
                if(strSplit.length == 4){
                    if(strSplit[3].equals("draw?")){
                        draw = true;
                    }
                    else{
                        chesspy = board;
                        chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
                        return chesspy;
                    }
                }
            }
            else{
                // check, if input length = 3, valid promotion or draw
                if( !(strSplit[2].equals("draw?")) || !(strSplit[2].equals("R")) ||
                    !(strSplit[2].equals("N")) || !(strSplit[2].equals("B")) || !(strSplit[2].equals("Q"))){
                    chesspy = board;
                    chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
                    return chesspy;
                }
            }
                //Implement the promotion only if player is attempting to promote
				if( (selectedColor == Player.white && currRank == 7 && selected.pieceType == ReturnPiece.PieceType.WP) ||
					(selectedColor == Player.black && currRank == 2 && selected.pieceType == ReturnPiece.PieceType.BP) ) {
					chesspy = board;
					promotion = pawnMove(selected, currFile, currRank, finalFile, finalRank, false);

					if(promotion && ((selectedColor == Player.white && currRank == 7) || (selectedColor == Player.black && currRank == 2)) ){
						// not sure if we need this as we already chedk if length >= 3
						if(strSplit.length >= 3) {
							ReturnPiece temp = new ReturnPiece();
							if(strSplit[2].equals("R")){
								if(selectedColor == Player.white){
									selected.pieceType = ReturnPiece.PieceType.WR;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('w', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
								else{
									selected.pieceType = ReturnPiece.PieceType.BR;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('b', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
							}
							else if(strSplit[2].equals("N")){
								if(selectedColor == Player.white){
									selected.pieceType = ReturnPiece.PieceType.WN;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('w', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
								else{
									selected.pieceType = ReturnPiece.PieceType.BN;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('b', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
							}
							else if(strSplit[2].equals("B")){
								if(selectedColor == Player.white){
									selected.pieceType = ReturnPiece.PieceType.WB;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('w', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
								else{
									selected.pieceType = ReturnPiece.PieceType.BB;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('b', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
							}
							else if(strSplit[2].equals("Q")){
								if(selectedColor == Player.white){
									selected.pieceType = ReturnPiece.PieceType.WQ;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('w', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
								else{
									selected.pieceType = ReturnPiece.PieceType.BQ;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('b', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
							}
							else {
								if(selectedColor == Player.white){
									selected.pieceType = ReturnPiece.PieceType.WQ;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('w', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
								else{
									selected.pieceType = ReturnPiece.PieceType.BQ;
									if(!draw) {
										// finding opponent's king
										for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
											temp = board.piecesOnBoard.get(i);
										}
										char kFile = temp.toString().charAt(0);
										int kRank = temp.pieceRank;
										boolean inCheck = check('b', kFile, kRank);
										if(inCheck) {
											if(currColor == Player.white){
												currColor = Player.black;
											}
											else{
												currColor = Player.white;
											}
											chesspy.message = ReturnPlay.Message.CHECK;
											return chesspy;
										}
									}
								}
							}
						}
						if(currColor == Player.white){
							currColor = Player.black;
						}
						else{
							currColor = Player.white;
						}
						if(draw){
						chesspy.message = ReturnPlay.Message.DRAW;
						}
						else{
							chesspy.message = null;
						}
						return chesspy;
					}
					else if(promotion) {
						if(currColor == Player.white){
							currColor = Player.black;
						}
						else{
							currColor = Player.white;
						}
						if(draw){
						chesspy.message = ReturnPlay.Message.DRAW;
						}
						else{
							chesspy.message = null;
						}
						return chesspy;
					}
					else{
						chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
						return chesspy;
					}
				}
           
        }
		//Implement the promotion only if player is attempting to promote
        else if( (selectedColor == Player.white && currRank == 7 && selected.pieceType == ReturnPiece.PieceType.WP) ||
					(selectedColor == Player.black && currRank == 2 && selected.pieceType == ReturnPiece.PieceType.BP) ){
            chesspy = board;
            promotion = pawnMove(selected, currFile, currRank, finalFile, finalRank, false);
            // if player is promoting to queen without explicitly saying "Q" when strSplit.length == 2
            if(promotion && ((selectedColor == Player.white && currRank == 7) || (selectedColor == Player.black && currRank == 2)) ) {
				ReturnPiece temp = new ReturnPiece();
                if(selectedColor == Player.white){
                    selected.pieceType = ReturnPiece.PieceType.WQ;
						// finding opponent's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						boolean inCheck = check('w', kFile, kRank);
						if(inCheck) {
							if(currColor == Player.white){
								currColor = Player.black;
							}
							else{
								currColor = Player.white;
							}
							chesspy.message = ReturnPlay.Message.CHECK;
							return chesspy;
						}
                }
                else{
                    selected.pieceType = ReturnPiece.PieceType.BQ;
						// finding opponent's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						boolean inCheck = check('b', kFile, kRank);
						if(inCheck) {
							if(currColor == Player.white){
								currColor = Player.black;
							}
							else{
								currColor = Player.white;
							}
							chesspy.message = ReturnPlay.Message.CHECK;
							return chesspy;
						}
                }
				if(currColor == Player.white){
                    currColor = Player.black;
                }
                else{
                    currColor = Player.white;
            	}
                chesspy.message = null;
                return chesspy;
            }
			else if(promotion) {
				if(currColor == Player.white){
                    currColor = Player.black;
                }
                else{
                    currColor = Player.white;
            	}
                chesspy.message = null;
                return chesspy;
			}
            else {
				chesspy = board;
                chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return chesspy;
			}
        }



		//Switch cases for each piece:
		boolean mov = false;
		if(currColor == Player.white){
			switch(selected.pieceType){
				case WP:
					mov = pawnMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case WR:
					mov = rookMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case WN:
					mov = knightMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case WB:
					mov = bishopMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case WQ:
					mov = queenMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case WK:
					mov = kingMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
			}
		}
		else{
			switch(selected.pieceType){
				case BP:
					mov = pawnMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case BR:
					mov = rookMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case BN:
					mov = knightMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case BB:
					mov = bishopMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case BQ:
					mov = queenMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
				case BK:
					mov = kingMove(selected, currFile, currRank, finalFile, finalRank, false);
					break;
			}
		}

		if(!mov) {
			chesspy = board;
			chesspy.message = ReturnPlay.Message.ILLEGAL_MOVE;
		}
		else if(draw){
			chesspy = board;
			chesspy.message = ReturnPlay.Message.DRAW;

			if(currColor == Player.white){
				currColor = Player.black;
			}
			else{
				currColor = Player.white;
			}
		}
		else{
			chesspy = board;

			// this checks to make sure board's message isn't anything else (check, checkmate, etc.)
			if(board.message == null) {
				chesspy.message = null;
			}

			if(currColor == Player.white){
				currColor = Player.black;
			}
			else{
				currColor = Player.white;
			}
		}
		
		return chesspy;
	}
	

	private static ReturnPiece getPiece(char file, int rank) {
        for (ReturnPiece p : board.piecesOnBoard) {
            if (p.pieceFile.name().equalsIgnoreCase(String.valueOf(file)) && p.pieceRank == rank) {
                return p;
            }
        }
        return null;
    }

	/**
	 * Checks if the opponent is in check.
	 * @param turn Represents current player checking if opponent is in check
	 * 				Also used to see if you put yourself in check when turn is opponent
	 * @param targFile Opponent's (or self) king's file location
	 * @param targRank Opponent's (or self) king's file location
	 * @return Returns true if opponent in check, false if not
	 */
	private static boolean check(char turn, char targFile, int targRank) {
		// int fileInt = targFile - 'a';

		// iterate through all pieces still left on board
		for(int i = 0; i < board.piecesOnBoard.size(); i++) {
			ReturnPiece piece = board.piecesOnBoard.get(i);
			// checks if piece's color matches player
			if( (turn == 'w' && piece.pieceType.ordinal() <= 5) || (turn == 'b' && piece.pieceType.ordinal() > 5) ) {
					boolean mov = false;
					char currFile = piece.toString().charAt(0);
					int currRank = piece.pieceRank;
					if(piece.pieceType.ordinal() <= 5){
						// System.out.println("white");
						switch(piece.pieceType){
							case WP:
								mov = pawnMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case WR:
								// System.out.println("checking WR");
								mov = rookMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case WN:
								mov = knightMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case WB:
								mov = bishopMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case WQ:
								// System.out.println("checking WQ");
								mov = queenMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case WK:
								mov = kingMove(piece, currFile, currRank, targFile, targRank, true);
								break;
						}
					}
					else{
						// System.out.println("black");
						switch(piece.pieceType){
							case BP:
								mov = pawnMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case BR:
								mov = rookMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case BN:
								mov = knightMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case BB:
								mov = bishopMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case BQ:
								mov = queenMove(piece, currFile, currRank, targFile, targRank, true);
								break;
							case BK:
								mov = kingMove(piece, currFile, currRank, targFile, targRank, true);
								break;
						}
					}
					// if one of the pieces of player's color was able to reach king -> check
					if(mov)
					return true;
			}
		}
		return false;
	}


	private static boolean pawnMove(ReturnPiece pawn, char startFile, int startRank, char targFile, int targRank, boolean checking){
		int sfileInt = startFile - 'a';
		int fileInt = targFile - 'a';
		boolean deleted = false;
		// System.out.println("fileInt: " + fileInt);
		
		boolean oneStep = ((pawn.pieceType == ReturnPiece.PieceType.WP) && (targRank == startRank + 1)) ||
                          ((pawn.pieceType == ReturnPiece.PieceType.BP) && (targRank == startRank - 1));
		boolean twoStep = ((pawn.pieceType == ReturnPiece.PieceType.WP) && (startRank == 2) && (targRank == 4)) ||
                          ((pawn.pieceType == ReturnPiece.PieceType.BP) && (startRank == 7) && (targRank == 5));
		boolean acrossStep = (Math.abs(targFile - startFile) == 1) &&
                             (((pawn.pieceType == ReturnPiece.PieceType.WP) && (targRank == startRank + 1)) ||
                             ((pawn.pieceType == ReturnPiece.PieceType.BP) && (targRank == startRank - 1)));

		if(oneStep || twoStep || acrossStep){
			ReturnPiece somePiece = getPiece(targFile, targRank);
			ReturnPiece removeTemp = new ReturnPiece();
			// only attempt to remove piece if not confirming either player is in check
			if(somePiece != null && acrossStep && !checking){
				if((somePiece.pieceType.ordinal() <= 5 && pawn.pieceType.ordinal() > 5) ||
                    (somePiece.pieceType.ordinal() > 5 && pawn.pieceType.ordinal() <= 5)){
						// assigning deleted piece's attributes to removeTemp
						int delType = somePiece.pieceType.ordinal();
						int delFile = somePiece.pieceFile.ordinal();
						int delRank = somePiece.pieceRank;
						removeTemp.pieceType = ReturnPiece.PieceType.values()[delType];
						removeTemp.pieceFile = ReturnPiece.PieceFile.values()[delFile];
						removeTemp.pieceRank = delRank;
						deleted = true;
						board.piecesOnBoard.remove(somePiece);
						// System.out.println("deleting piece");
				}
				else{
					return false;
				}
			}

			else if(somePiece == null && acrossStep || somePiece != null){
				return false;
			}

			// Check Implementation
			if(!checking) {
				// ReturnPiece currTemp = new ReturnPiece();
				// currTemp = pawn;
				// System.out.println("currTemp (initial): " + currTemp.toString());
				pawn.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
				pawn.pieceRank = targRank;
				// white's turn
				if(pawn.pieceType == ReturnPiece.PieceType.WP) {
					ReturnPiece temp = new ReturnPiece();
					// finding player's king
					for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
						temp = board.piecesOnBoard.get(i);
					}
					char kFile = temp.toString().charAt(0);
					int kRank = temp.pieceRank;
					// check if you're now in check(put yourself in check or were already in check)
					boolean inCheck = check('b', kFile, kRank);
					if(inCheck) {
						if(deleted) {
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
						}
						// setting piece's location to its original location
						pawn.pieceRank = startRank;
						pawn.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
						return false;
						// pawn = currTemp;
					}
					// check if you put opponent in check
					else {
						// finding opponent's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						kFile = temp.toString().charAt(0);
						kRank = temp.pieceRank;
						inCheck = check('w', kFile, kRank);
						if(inCheck) {
							board.message = ReturnPlay.Message.CHECK;
						}
						else {
								board.message = null;
							}
					}
				}
				// black's turn
				else {
					ReturnPiece temp = new ReturnPiece();
					// finding player's king
					for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
						temp = board.piecesOnBoard.get(i);
					}
					char kFile = temp.toString().charAt(0);
					int kRank = temp.pieceRank;
					// System.out.println(temp.toString());
					// System.out.println("bk calculated: " + kFile + kRank);
					// check if you're now in check(put yourself in check or were already in check)
					boolean inCheck = check('w', kFile, kRank);
					if(inCheck) {
						// System.out.println("put yourself in check");
						if(deleted) {
							// System.out.println("re-adding deleted piece");
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
						}
						// System.out.println("currTemp (final): " + currTemp.toString());
						// setting piece's location to its original location
						pawn.pieceRank = startRank;
						pawn.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
						// System.out.println("pawn's final location: " + pawn.toString());
						// pawn = currTemp;
						return false;
					}
					// check if you put opponent in check
					else {
						// finding opponent's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						kFile = temp.toString().charAt(0);
						kRank = temp.pieceRank;
						inCheck = check('b', kFile, kRank);
						if(inCheck) {
							board.message = ReturnPlay.Message.CHECK;
						}
						else {
								board.message = null;
							}
					}
				}
		}
			//enPassantOccurred = false;
			return true;
		}
		return false;	
	}

	private static boolean rookMove(ReturnPiece rook, char startFile, int startRank, char targFile, int targRank, boolean checking){
		boolean deleted = false;
		ReturnPiece somePiece = getPiece(targFile, targRank);		
		int sfileInt = startFile - 'a';
		int tfileInt = targFile - 'a'; 
	
		char color;

		if (sfileInt < 0 || sfileInt > 7 || tfileInt < 0 || tfileInt > 7 || targRank < 1 || targRank > 8) {
			return false;
		} 

		if(rook.pieceType.ordinal() <= 5) {
			color = 'w';
			// check if trying to move to target with piece of same color
			if(somePiece != null && somePiece.pieceType.ordinal() <= 5) {
				return false;
			}
		}
		else {
			color = 'b';
			// check if trying to move to target with piece of same color
			if(somePiece != null && somePiece.pieceType.ordinal() > 5) {
				return false;
			}
		}

		//moving vertically
		if(sfileInt == tfileInt){
			int st = (targRank > startRank) ? 1 : -1;

			for(int i = startRank + st; i != targRank; i += st){
				if(getPiece((char) ('a' + sfileInt), i) != null){
					return false;
				}	
			}	
		}//moving horizontal
		else if(startRank == targRank){
			int st = (tfileInt > sfileInt) ? 1 : -1;

			for(int i = sfileInt + st; i != tfileInt; i += st){
				if(getPiece((char) ('a' + i), startRank) != null){
					return false;
				}	
			}
		}
		else{
			return false;
		}

		if((color == 'w' && somePiece != null && somePiece.pieceType.ordinal() > 5) || 
			   (color == 'b' && somePiece != null && somePiece.pieceType.ordinal() <= 5)){
				// Check Implementation
				if(!checking) {
					ReturnPiece removeTemp = somePiece;
					int delType = somePiece.pieceType.ordinal();
					int delFile = somePiece.pieceFile.ordinal();
					int delRank = somePiece.pieceRank;
					removeTemp.pieceType = ReturnPiece.PieceType.values()[delType];
					removeTemp.pieceFile = ReturnPiece.PieceFile.values()[delFile];
					removeTemp.pieceRank = delRank;
					deleted = true;
					board.piecesOnBoard.remove(somePiece);
					ReturnPiece currTep = rook;
					rook.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
					rook.pieceRank = targRank;

					// white's turn
					if(color == 'w') {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('b', kFile, kRank);
						if(inCheck) {
							if(deleted) {
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
							}
							// setting piece's location to its original location
							rook.pieceRank = startRank;
							rook.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('w', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					// black's turn
					else {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('w', kFile, kRank);
						if(inCheck) {
							if(deleted) {
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
							}
							// setting piece's location to its original location
							rook.pieceRank = startRank;
							rook.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('b', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					enPassantOccurred = false;

					if(color == 'w'){
						if(startFile == 'h' && startRank == 1){
							rwrookMoved = true;
						}
						else if(startFile == 'a' && startRank == 1){
							lwrookMoved = true;
						}
					}
					else if(color == 'b'){
						if(startFile == 'h' && startRank == 8){
							rbrookMoved = true;
						}
						else if(startFile == 'a' && startRank == 8){
							lbrookMoved = true;
						}
					}
					return true;
				}
				
				if(color == 'w'){
                    if(startFile == 'h' && startRank == 1){
                        rwrookMoved = true;
                    }
                    else if(startFile == 'a' && startRank == 1){
                        lwrookMoved = true;
                    }
                }
                else if(color == 'b'){
                    if(startFile == 'h' && startRank == 8){
                        rbrookMoved = true;
                    }
                    else if(startFile == 'a' && startRank == 8){
                        lbrookMoved = true;
                    }
                }
				enPassantOccurred = false;
				return true;
		}
		else{
				// Check Implementation
				if(!checking) {
					ReturnPiece currTep = rook;
					rook.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
					rook.pieceRank = targRank;
					// white's turn
					if(color == 'w') {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						// System.out.println("check if white put itself in check");
						boolean inCheck = check('b', kFile, kRank);
						if(inCheck) {
							// setting piece's location to its original location
							rook.pieceRank = startRank;
							rook.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							// System.out.println("put yourself in check WHITE");
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('w', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					// black's turn
					else {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						// System.out.println(rook.toString());
						// System.out.println("check if black put itself in check");
						boolean inCheck = check('w', kFile, kRank);
						if(inCheck) {
							// setting piece's location to its original location
							rook.pieceRank = startRank;
							rook.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							// System.out.println("put yourself in check BLACK");
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('b', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					enPassantOccurred = false;
					if(color == 'w'){
						if(startFile == 'h' && startRank == 1){
							rwrookMoved = true;
						}
						else if(startFile == 'a' && startRank == 1){
							lwrookMoved = true;
						}
					}
					else if(color == 'b'){
						if(startFile == 'h' && startRank == 8){
							rbrookMoved = true;
						}
						else if(startFile == 'a' && startRank == 8){
							lbrookMoved = true;
						}
					}
					return true;
				}
				if(color == 'w'){
                    if(startFile == 'h' && startRank == 1){
                        rwrookMoved = true;
                    }
                    else if(startFile == 'a' && startRank == 1){
                        lwrookMoved = true;
                    }
                }
                else if(color == 'b'){
                    if(startFile == 'h' && startRank == 8){
                        rbrookMoved = true;
                    }
                    else if(startFile == 'a' && startRank == 8){
                        lbrookMoved = true;
                    }
                }
				enPassantOccurred = false;
				return true;
		}
		// return true;
	}

	private static boolean knightMove(ReturnPiece knight, char startFile, int startRank, char targFile, int targRank, boolean checking){
		boolean deleted = false;
		int fileInt = targFile - 'a';
		int rankInt = targRank - 1;

		int fileDiff = Math.abs(targFile - startFile);
        int rankDiff = Math.abs(targRank - startRank);

		ReturnPiece somePiece = getPiece(targFile, targRank);

		if (fileInt < 0 || fileInt > 7 || targRank < 1 || targRank > 8) {
			return false;
		} 

		char color;
		if(knight.pieceType.ordinal() <= 5) {
			color = 'w';
			// check if trying to move to target with piece of same color
			if(somePiece != null && somePiece.pieceType.ordinal() <= 5) {
				return false;
			}
		}
		else {
			color = 'b';
			// check if trying to move to target with piece of same color
			if(somePiece != null && somePiece.pieceType.ordinal() > 5) {
				return false;
			}
		}


		if ((fileDiff == 2 && rankDiff == 1) || (fileDiff == 1 && rankDiff == 2)){
			if((color == 'w' && somePiece != null && somePiece.pieceType.ordinal() > 5) || 
			   (color == 'b' && somePiece != null && somePiece.pieceType.ordinal() <= 5)){
				// Check Implementation		
				if(!checking) {
					ReturnPiece removeTemp = somePiece;
					int delType = somePiece.pieceType.ordinal();
					int delFile = somePiece.pieceFile.ordinal();
					int delRank = somePiece.pieceRank;
					removeTemp.pieceType = ReturnPiece.PieceType.values()[delType];
					removeTemp.pieceFile = ReturnPiece.PieceFile.values()[delFile];
					removeTemp.pieceRank = delRank;
					deleted = true;
					board.piecesOnBoard.remove(somePiece);
					ReturnPiece currTemp = knight;
					knight.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
					knight.pieceRank = targRank;

					// white's turn
					if(knight.pieceType == ReturnPiece.PieceType.WN) {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('b', kFile, kRank);
						if(inCheck) {
							if(deleted) {
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
							}
							// setting piece's location to its original location
							knight.pieceRank = startRank;
							knight.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('w', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					// black's turn
					else {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('w', kFile, kRank);
						if(inCheck) {
							if(deleted) {
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
							}
							// setting piece's location to its original location
							knight.pieceRank = startRank;
							knight.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('b', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					enPassantOccurred = false;
					return true;
				}
				return true;
			}
			if(!checking) {
					ReturnPiece currTemp = knight;
					knight.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
					knight.pieceRank = targRank;
					// white's turn
					if(knight.pieceType == ReturnPiece.PieceType.WN) {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('b', kFile, kRank);
						if(inCheck) {
							// setting piece's location to its original location
							knight.pieceRank = startRank;
							knight.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('w', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					// black's turn
					else {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('w', kFile, kRank);
						if(inCheck) {
							// setting piece's location to its original location
							knight.pieceRank = startRank;
							knight.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('b', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					enPassantOccurred = false;
					return true;
			}
			return true;
		}

        return false;
	}

	private static boolean bishopMove(ReturnPiece bishop, char startFile, int startRank, char targFile, int targRank, boolean checking) {
		boolean deleted = false;
        ReturnPiece somePiece = getPiece(targFile, targRank);
		int sfileInt = startFile - 'a';
		int tfileInt = targFile - 'a'; 

		if (Math.abs(tfileInt - sfileInt) != Math.abs(targRank - startRank)) {
            return false;
        }

		if (sfileInt < 0 || sfileInt > 7 || tfileInt < 0 || tfileInt > 7 || targRank < 1 || targRank > 8) {
			return false;
		} 

        int fileDir = (tfileInt > sfileInt) ? 1 : -1;
        int rankDir = (targRank > startRank) ? 1 : -1;

        int posFile = sfileInt + fileDir;
        int posRank = startRank + rankDir;

        char color;
        if(bishop.pieceType.ordinal() <= 5) {
            color = 'w';
            // check if trying to move to target with piece of same color
            if(somePiece != null && somePiece.pieceType.ordinal() <= 5) {
                return false;
            }
        }
        else {
            color = 'b';
            // check if trying to move to target with piece of same color
            if(somePiece != null && somePiece.pieceType.ordinal() > 5) {
                return false;
            }
        }

		while(posFile != tfileInt && posRank != targRank){
			if(getPiece((char) ('a' + posFile), posRank) != null){
				return false;
			}

			posFile += fileDir;
			posRank += rankDir;
		}

		if((color == 'w' && somePiece != null && somePiece.pieceType.ordinal() > 5) || 
			   (color == 'b' && somePiece != null && somePiece.pieceType.ordinal() <= 5)){
				// Implementing Check
				if(!checking) {
					ReturnPiece removeTemp = somePiece;
					int delType = somePiece.pieceType.ordinal();
					int delFile = somePiece.pieceFile.ordinal();
					int delRank = somePiece.pieceRank;
					removeTemp.pieceType = ReturnPiece.PieceType.values()[delType];
					removeTemp.pieceFile = ReturnPiece.PieceFile.values()[delFile];
					removeTemp.pieceRank = delRank;
					deleted = true;
					board.piecesOnBoard.remove(somePiece);
					ReturnPiece currTemp = bishop;
					bishop.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
					bishop.pieceRank = targRank;
					// white's turn
					if(color == 'w') {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('b', kFile, kRank);
						if(inCheck) {
							if(deleted) {
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
							}
							// setting piece's location to its original location
							bishop.pieceRank = startRank;
							bishop.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('w', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					// black's turn
					else {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('w', kFile, kRank);
						if(inCheck) {
							if(deleted) {
							// re-adding previously deleted piece to board
							board.piecesOnBoard.add(removeTemp);
							}
							// setting piece's location to its original location
							bishop.pieceRank = startRank;
							bishop.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('b', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					enPassantOccurred = false;
					return true;
				}
				return true;
		}
		else{
			if(!checking) {
					ReturnPiece currTemp = bishop;
					bishop.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
					bishop.pieceRank = targRank;
					// white's turn
					if(color == 'w') {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('b', kFile, kRank);
						if(inCheck) {
							// setting piece's location to its original location
							bishop.pieceRank = startRank;
							bishop.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('w', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
					// black's turn
					else {
						ReturnPiece temp = new ReturnPiece();
						// finding player's king
						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck = check('w', kFile, kRank);
						if(inCheck) {
							// setting piece's location to its original location
							bishop.pieceRank = startRank;
							bishop.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							return false;
						}
						// check if you put opponent in check
						else {
							// finding opponent's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							kFile = temp.toString().charAt(0);
							kRank = temp.pieceRank;
							inCheck = check('b', kFile, kRank);
							if(inCheck) {
								board.message = ReturnPlay.Message.CHECK;
							}
							else {
								board.message = null;
							}
						}
					}
				enPassantOccurred = false;
				return true;
			}
			return true;
		}
    }

	private static boolean queenMove(ReturnPiece queen, char startFile, int startRank, char targFile, int targRank, boolean checking) {
        if(rookMove(queen, startFile, startRank, targFile, targRank, checking)) {
			enPassantOccurred = false;
            return true;
        }
        else if(bishopMove(queen, startFile, startRank, targFile, targRank, checking)) {
			enPassantOccurred = false;
            return true;
        }
        return false;
    }

	private static boolean kingMove(ReturnPiece king, char startFile, int startRank, char targFile, int targRank, boolean checking) {
		boolean deleted = false;
		int sfileInt = startFile - 'a';
		int tfileInt = targFile - 'a';
		int fileInt = targFile - 'a';
		ReturnPiece somePiece = getPiece(targFile, targRank);
		// set color

		
		if (fileInt < 0 || fileInt > 7 || targRank < 1 || targRank > 8) {
			return false;
		} 
		char color;
		if(king.pieceType.ordinal() <= 5) {
			color = 'w';
			// check if trying to move to target with piece of same color
			if(somePiece != null && somePiece.pieceType.ordinal() <= 5) {
				return false;
			}
		}
		else {
			color = 'b';
			// check if trying to move to target with piece of same color
			if(somePiece != null && somePiece.pieceType.ordinal() > 5) {
				return false;
			}
		}
		
		// check move is legal
        if(Math.abs(sfileInt - tfileInt) == 2 && startRank == targRank){
            if(!wkingMoved || !bkingMoved){
                if(color == 'w' && king.pieceType.ordinal() <= 5){
                    if(sfileInt < tfileInt && !rwrookMoved){//right moving
                        for(int file = sfileInt + 1; file > tfileInt; file++){
                            if(getPiece((char) (file + 'a'), startRank) != null){
                                return false;
                            }
                        }

						ReturnPiece temp = new ReturnPiece();
						
						boolean inCheck1 = check('b', startFile, startRank);
						
						if(inCheck1) {
							return false;
						}
						
                        ReturnPiece rwrook = getPiece('h', 1);
                       
                        king.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
                        king.pieceRank = targRank;
                       
                        rwrook.pieceFile = ReturnPiece.PieceFile.values()[fileInt-1];
                        rwrook.pieceRank = targRank;

						boolean inCheck2 = check('b', targFile, targRank);
						
						if(inCheck2){
							king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							king.pieceRank = startRank;
						
							rwrook.pieceFile = ReturnPiece.PieceFile.values()[7];
							rwrook.pieceRank = startRank;
							return false;
						}
                       
                        wkingMoved = true;
                        rwrookMoved = true;
                        enPassantOccurred = false;

						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck3 = check('w', kFile, kRank);
						if(inCheck3) {
							board.message = ReturnPlay.Message.CHECK;
						}

                        return true;
                    }
                    else if(sfileInt > tfileInt && !lwrookMoved){//left moving
                        for(int file = tfileInt + 1; file > sfileInt; file++){
                            if(getPiece((char) (file + 'a'), startRank) != null){
                                return false;
                            }
                        }
						
						ReturnPiece temp = new ReturnPiece();
						
						boolean inCheck1 = check('b', startFile, startRank);
						
						if(inCheck1) {
							return false;
						}


                        ReturnPiece lwrook = getPiece('a', 1);
                       
                        king.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
                        king.pieceRank = targRank;
                       
                        lwrook.pieceFile = ReturnPiece.PieceFile.values()[fileInt+1];
                        lwrook.pieceRank = targRank;
                       
						boolean inCheck2 = check('b', targFile, targRank);
						
						if(inCheck2){
							king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							king.pieceRank = startRank;
						
							lwrook.pieceFile = ReturnPiece.PieceFile.values()[0];
							lwrook.pieceRank = startRank;
							return false;
						}
					   
                        wkingMoved = true;
                        lwrookMoved = true;
                        enPassantOccurred = false;

						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck3 = check('w', kFile, kRank);
						if(inCheck3) {
							board.message = ReturnPlay.Message.CHECK;
						}

                        return true;
                    }
                }
                else if(color == 'b' && king.pieceType.ordinal() > 5){
                    if(sfileInt < tfileInt && !rbrookMoved){//right
                        for(int file = sfileInt + 1; file > tfileInt; file++){
                            if(getPiece((char) (file + 'a'), startRank) != null){
                                return false;
                            }
                        }
                       
						ReturnPiece temp = new ReturnPiece();
						
						boolean inCheck1 = check('w', startFile, startRank);
						
						if(inCheck1) {
							return false;
						}
						
                        ReturnPiece rbrook = getPiece('h', 8);
                       
                        king.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
                        king.pieceRank = targRank;
                       
                        rbrook.pieceFile = ReturnPiece.PieceFile.values()[fileInt-1];
                        rbrook.pieceRank = targRank;
                       
						boolean inCheck2 = check('w', targFile, targRank);
						
						if(inCheck2){
							king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							king.pieceRank = startRank;
						
							rbrook.pieceFile = ReturnPiece.PieceFile.values()[7];
							rbrook.pieceRank = startRank;
							return false;
						}

                        bkingMoved = true;
                        rbrookMoved = true;
                        enPassantOccurred = false;

						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck3 = check('b', kFile, kRank);
						if(inCheck3) {
							board.message = ReturnPlay.Message.CHECK;
						}

                        return true;
                    }
                    else if(sfileInt > tfileInt && !lbrookMoved){//left
                        for(int file = tfileInt + 1; file > sfileInt; file++){
                            if(getPiece((char) (file + 'a'), startRank) != null){
                                return false;
                            }
                        }
						ReturnPiece temp = new ReturnPiece();
						
						boolean inCheck1 = check('w', startFile, startRank);
						
						if(inCheck1) {
							return false;
						}


                        ReturnPiece lbrook = getPiece('a', 8);
                       
                        king.pieceFile = ReturnPiece.PieceFile.values()[tfileInt];
                        king.pieceRank = targRank;
                       
                        lbrook.pieceFile = ReturnPiece.PieceFile.values()[fileInt+1];
                        lbrook.pieceRank = targRank;
                       
						boolean inCheck2 = check('w', targFile, targRank);
						
						if(inCheck2){
							king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
							king.pieceRank = startRank;
						
							lbrook.pieceFile = ReturnPiece.PieceFile.values()[0];
							lbrook.pieceRank = startRank;
							return false;
						}

                        bkingMoved = true;
                        lbrookMoved = true;
                        enPassantOccurred = false;

						for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
							temp = board.piecesOnBoard.get(i);
						}
						char kFile = temp.toString().charAt(0);
						int kRank = temp.pieceRank;
						// check if you're now in check(put yourself in check or were already in check)
						boolean inCheck3 = check('b', kFile, kRank);
						if(inCheck3) {
							board.message = ReturnPlay.Message.CHECK;
						}

                        return true;
                    }
                }
            }
            return false;
        }

		else if(targFile >= startFile - 1 && targFile <= startFile + 1 && targRank >= startRank - 1 && targRank <= startRank + 1) {
			if((color == 'w' && somePiece != null && somePiece.pieceType.ordinal() > 5) || 
				(color == 'b' && somePiece != null && somePiece.pieceType.ordinal() <= 5)){
					if(!checking) {	
						ReturnPiece removeTemp = somePiece;
						int delType = somePiece.pieceType.ordinal();
						int delFile = somePiece.pieceFile.ordinal();
						int delRank = somePiece.pieceRank;
						removeTemp.pieceType = ReturnPiece.PieceType.values()[delType];
						removeTemp.pieceFile = ReturnPiece.PieceFile.values()[delFile];
						removeTemp.pieceRank = delRank;
						deleted = true;
						board.piecesOnBoard.remove(somePiece);
						ReturnPiece currTemp = king;
						king.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
						king.pieceRank = targRank;
						// white's turn
						if(king.pieceType == ReturnPiece.PieceType.WK) {
							ReturnPiece temp = new ReturnPiece();
							// finding player's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							char kFile = temp.toString().charAt(0);
							int kRank = temp.pieceRank;
							// check if you're now in check(put yourself in check or were already in check)
							boolean inCheck = check('b', kFile, kRank);
							if(inCheck) {
								if(deleted) {
								// re-adding previously deleted piece to board
								board.piecesOnBoard.add(removeTemp);
								}
								// setting piece's location to its original location
								king.pieceRank = startRank;
								king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
								return false;
							}
							// check if you put opponent in check
							else {
								// finding opponent's king
								for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
									temp = board.piecesOnBoard.get(i);
								}
								kFile = temp.toString().charAt(0);
								kRank = temp.pieceRank;
								inCheck = check('w', kFile, kRank);
								if(inCheck) {
									board.message = ReturnPlay.Message.CHECK;
								}
								else {
								board.message = null;
								}
							}
						}
						// black's turn
						else {
							ReturnPiece temp = new ReturnPiece();
							// finding player's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							char kFile = temp.toString().charAt(0);
							int kRank = temp.pieceRank;
							// check if you're now in check(put yourself in check or were already in check)
							boolean inCheck = check('w', kFile, kRank);
							if(inCheck) {
								if(deleted) {
								// re-adding previously deleted piece to board
								board.piecesOnBoard.add(removeTemp);
								}
								// setting piece's location to its original location
								king.pieceRank = startRank;
								king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
								return false;
							}
							// check if you put opponent in check
							else {
								// finding opponent's king
								for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
									temp = board.piecesOnBoard.get(i);
								}
								kFile = temp.toString().charAt(0);
								kRank = temp.pieceRank;
								inCheck = check('b', kFile, kRank);
								if(inCheck) {
									board.message = ReturnPlay.Message.CHECK;
								}
								else {
								board.message = null;
								}
							}
						}
						enPassantOccurred = false;
							if(color == 'w'){
								wkingMoved = true;
							}
							else{
								bkingMoved = true;
							}
						return true;
					}
				if(color == 'w'){
                    wkingMoved = true;
                }
				else{
                    bkingMoved = true;
                }
				return true;
			}
			else{
				if(!checking) {
					ReturnPiece currTemp = king;
					king.pieceFile = ReturnPiece.PieceFile.values()[fileInt];
					king.pieceRank = targRank;
					// white's turn
						if(king.pieceType == ReturnPiece.PieceType.WK) {
							ReturnPiece temp = new ReturnPiece();
							// finding player's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							char kFile = temp.toString().charAt(0);
							int kRank = temp.pieceRank;
							// check if you're now in check(put yourself in check or were already in check)
							boolean inCheck = check('b', kFile, kRank);
							if(inCheck) {
								// setting piece's location to its original location
								king.pieceRank = startRank;
								king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
								return false;
							}
							// check if you put opponent in check
							else {
								// finding opponent's king
								for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
									temp = board.piecesOnBoard.get(i);
								}
								kFile = temp.toString().charAt(0);
								kRank = temp.pieceRank;
								inCheck = check('w', kFile, kRank);
								if(inCheck) {
									board.message = ReturnPlay.Message.CHECK;
								}
								else {
								board.message = null;
								}
							}
						}
						// black's turn
						else {
							ReturnPiece temp = new ReturnPiece();
							// finding player's king
							for(int i = 0; temp.pieceType != ReturnPiece.PieceType.BK && i < board.piecesOnBoard.size(); i++) {
								temp = board.piecesOnBoard.get(i);
							}
							char kFile = temp.toString().charAt(0);
							int kRank = temp.pieceRank;
							// check if you're now in check(put yourself in check or were already in check)
							boolean inCheck = check('w', kFile, kRank);
							if(inCheck) {
								// setting piece's location to its original location
								king.pieceRank = startRank;
								king.pieceFile = ReturnPiece.PieceFile.values()[sfileInt];
								return false;
							}
							// check if you put opponent in check
							else {
								// finding opponent's king
								for(int i = 0; temp.pieceType != ReturnPiece.PieceType.WK && i < board.piecesOnBoard.size(); i++) {
									temp = board.piecesOnBoard.get(i);
								}
								kFile = temp.toString().charAt(0);
								kRank = temp.pieceRank;
								inCheck = check('b', kFile, kRank);
								if(inCheck) {
									board.message = ReturnPlay.Message.CHECK;
								}
								else {
								board.message = null;
								}
							}
						}
					enPassantOccurred = false;
					if(color == 'w'){
						wkingMoved = true;
					}
					else{
						bkingMoved = true;
					}
					return true;
				}
				enPassantOccurred = false;
				if(color == 'w'){
                    wkingMoved = true;
                }
				else{
                    bkingMoved = true;
                }
				return true;
			}
		}
		
		return false;
	}


	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		/* FILL IN THIS METHOD */

		//Starting Color
		currColor = Player.white;
		
		board = new ReturnPlay();
		board.piecesOnBoard = new ArrayList<>();
		//play.piecesOnBoard = all;
		
		
		//B & W Pieces
		//Rook
		ReturnPiece wrooka = new ReturnPiece();
		wrooka.pieceType = ReturnPiece.PieceType.WR;
		wrooka.pieceFile = ReturnPiece.PieceFile.a;
		wrooka.pieceRank = 1;
		board.piecesOnBoard.add(wrooka);

		ReturnPiece wrookh = new ReturnPiece();
		wrookh.pieceType = ReturnPiece.PieceType.WR;
		wrookh.pieceFile = ReturnPiece.PieceFile.h;
		wrookh.pieceRank = 1;
		board.piecesOnBoard.add(wrookh);

		ReturnPiece brooka = new ReturnPiece();
		brooka.pieceType = ReturnPiece.PieceType.BR;
		brooka.pieceFile = ReturnPiece.PieceFile.a;
		brooka.pieceRank = 8;
		board.piecesOnBoard.add(brooka);

		ReturnPiece brookh = new ReturnPiece();
		brookh.pieceType = ReturnPiece.PieceType.BR;
		brookh.pieceFile = ReturnPiece.PieceFile.h;
		brookh.pieceRank = 8;
		board.piecesOnBoard.add(brookh);


		//Knight
		ReturnPiece wnighb = new ReturnPiece();
		wnighb.pieceType = ReturnPiece.PieceType.WN;
		wnighb.pieceFile = ReturnPiece.PieceFile.b;
		wnighb.pieceRank = 1;
		board.piecesOnBoard.add(wnighb);

		ReturnPiece wnighg = new ReturnPiece();
		wnighg.pieceType = ReturnPiece.PieceType.WN;
		wnighg.pieceFile = ReturnPiece.PieceFile.g;
		wnighg.pieceRank = 1;
		board.piecesOnBoard.add(wnighg);

		ReturnPiece bnighb = new ReturnPiece();
		bnighb.pieceType = ReturnPiece.PieceType.BN;
		bnighb.pieceFile = ReturnPiece.PieceFile.b;
		bnighb.pieceRank = 8;
		board.piecesOnBoard.add(bnighb);

		ReturnPiece bnighg = new ReturnPiece();
		bnighg.pieceType = ReturnPiece.PieceType.BN;
		bnighg.pieceFile = ReturnPiece.PieceFile.g;
		bnighg.pieceRank = 8;
		board.piecesOnBoard.add(bnighg);
		

		//Bishop
		ReturnPiece wbishc = new ReturnPiece();
		wbishc.pieceType = ReturnPiece.PieceType.WB;
		wbishc.pieceFile = ReturnPiece.PieceFile.c;
		wbishc.pieceRank = 1;
		board.piecesOnBoard.add(wbishc);

		ReturnPiece wbishf = new ReturnPiece();
		wbishf.pieceType = ReturnPiece.PieceType.WB;
		wbishf.pieceFile = ReturnPiece.PieceFile.f;
		wbishf.pieceRank = 1;
		board.piecesOnBoard.add(wbishf);

		ReturnPiece bbishc = new ReturnPiece();
		bbishc.pieceType = ReturnPiece.PieceType.BB;
		bbishc.pieceFile = ReturnPiece.PieceFile.c;
		bbishc.pieceRank = 8;
		board.piecesOnBoard.add(bbishc);

		ReturnPiece bbishf = new ReturnPiece();
		bbishf.pieceType = ReturnPiece.PieceType.BB;
		bbishf.pieceFile = ReturnPiece.PieceFile.f;
		bbishf.pieceRank = 8;
		board.piecesOnBoard.add(bbishf);


		//King & Queen
		ReturnPiece wking = new ReturnPiece();
		wking.pieceType = ReturnPiece.PieceType.WK;
		wking.pieceFile = ReturnPiece.PieceFile.e;
		wking.pieceRank = 1;
		board.piecesOnBoard.add(wking);

		ReturnPiece wqueen = new ReturnPiece();
		wqueen.pieceType = ReturnPiece.PieceType.WQ;
		wqueen.pieceFile = ReturnPiece.PieceFile.d;
		wqueen.pieceRank = 1;
		board.piecesOnBoard.add(wqueen);

		ReturnPiece bking = new ReturnPiece();
		bking.pieceType = ReturnPiece.PieceType.BK;
		bking.pieceFile = ReturnPiece.PieceFile.e;
		bking.pieceRank = 8;
		board.piecesOnBoard.add(bking);

		ReturnPiece bqueen = new ReturnPiece();
		bqueen.pieceType = ReturnPiece.PieceType.BQ;
		bqueen.pieceFile = ReturnPiece.PieceFile.d;
		bqueen.pieceRank = 8;
		board.piecesOnBoard.add(bqueen);


		//Pawn Pieces of Both White & Black
		ReturnPiece wpawna = new ReturnPiece();
		wpawna.pieceType = ReturnPiece.PieceType.WP;
		wpawna.pieceFile = ReturnPiece.PieceFile.a;
		wpawna.pieceRank = 2;
		board.piecesOnBoard.add(wpawna);

		ReturnPiece wpawnb = new ReturnPiece();
		wpawnb.pieceType = ReturnPiece.PieceType.WP;
		wpawnb.pieceFile = ReturnPiece.PieceFile.b;
		wpawnb.pieceRank = 2;
		board.piecesOnBoard.add(wpawnb);

		ReturnPiece wpawnc = new ReturnPiece();
		wpawnc.pieceType = ReturnPiece.PieceType.WP;
		wpawnc.pieceFile = ReturnPiece.PieceFile.c;
		wpawnc.pieceRank = 2;
		board.piecesOnBoard.add(wpawnc);

		ReturnPiece wpawnd = new ReturnPiece();
		wpawnd.pieceType = ReturnPiece.PieceType.WP;
		wpawnd.pieceFile = ReturnPiece.PieceFile.d;
		wpawnd.pieceRank = 2;
		board.piecesOnBoard.add(wpawnd);

		ReturnPiece wpawne = new ReturnPiece();
		wpawne.pieceType = ReturnPiece.PieceType.WP;
		wpawne.pieceFile = ReturnPiece.PieceFile.e;
		wpawne.pieceRank = 2;
		board.piecesOnBoard.add(wpawne);

		ReturnPiece wpawnf = new ReturnPiece();
		wpawnf.pieceType = ReturnPiece.PieceType.WP;
		wpawnf.pieceFile = ReturnPiece.PieceFile.f;
		wpawnf.pieceRank = 2;
		board.piecesOnBoard.add(wpawnf);

		ReturnPiece wpawng = new ReturnPiece();
		wpawng.pieceType = ReturnPiece.PieceType.WP;
		wpawng.pieceFile = ReturnPiece.PieceFile.g;
		wpawng.pieceRank = 2;
		board.piecesOnBoard.add(wpawng);

		ReturnPiece wpawnh = new ReturnPiece();
		wpawnh.pieceType = ReturnPiece.PieceType.WP;
		wpawnh.pieceFile = ReturnPiece.PieceFile.h;
		wpawnh.pieceRank = 2;
		board.piecesOnBoard.add(wpawnh);



		ReturnPiece bpawna = new ReturnPiece();
		bpawna.pieceType = ReturnPiece.PieceType.BP;
		bpawna.pieceFile = ReturnPiece.PieceFile.a;
		bpawna.pieceRank = 7;
		board.piecesOnBoard.add(bpawna);

		ReturnPiece bpawnb = new ReturnPiece();
		bpawnb.pieceType = ReturnPiece.PieceType.BP;
		bpawnb.pieceFile = ReturnPiece.PieceFile.b;
		bpawnb.pieceRank = 7;
		board.piecesOnBoard.add(bpawnb);

		ReturnPiece bpawnc = new ReturnPiece();
		bpawnc.pieceType = ReturnPiece.PieceType.BP;
		bpawnc.pieceFile = ReturnPiece.PieceFile.c;
		bpawnc.pieceRank = 7;
		board.piecesOnBoard.add(bpawnc);

		ReturnPiece bpawnd = new ReturnPiece();
		bpawnd.pieceType = ReturnPiece.PieceType.BP;
		bpawnd.pieceFile = ReturnPiece.PieceFile.d;
		bpawnd.pieceRank = 7;
		board.piecesOnBoard.add(bpawnd);

		ReturnPiece bpawne = new ReturnPiece();
		bpawne.pieceType = ReturnPiece.PieceType.BP;
		bpawne.pieceFile = ReturnPiece.PieceFile.e;
		bpawne.pieceRank = 7;
		board.piecesOnBoard.add(bpawne);

		ReturnPiece bpawnf = new ReturnPiece();
		bpawnf.pieceType = ReturnPiece.PieceType.BP;
		bpawnf.pieceFile = ReturnPiece.PieceFile.f;
		bpawnf.pieceRank = 7;
		board.piecesOnBoard.add(bpawnf);

		ReturnPiece bpawng = new ReturnPiece();
		bpawng.pieceType = ReturnPiece.PieceType.BP;
		bpawng.pieceFile = ReturnPiece.PieceFile.g;
		bpawng.pieceRank = 7;
		board.piecesOnBoard.add(bpawng);

		ReturnPiece bpawnh = new ReturnPiece();
		bpawnh.pieceType = ReturnPiece.PieceType.BP;
		bpawnh.pieceFile = ReturnPiece.PieceFile.h;
		bpawnh.pieceRank = 7;
		board.piecesOnBoard.add(bpawnh);
	}

}
