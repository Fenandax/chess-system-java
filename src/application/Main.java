package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.PartidaXadrez;
import chess.Pe�aXadrez;
import chess.Posi��oXadrez;
import chess.XadrezException;

public class Main {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		List<Pe�aXadrez> capturadas = new ArrayList<>();

		while (!partidaXadrez.getCheckMate()) {
			try {
				UI.limparTela();
				UI.imprimirPartida(partidaXadrez, capturadas);
				System.out.println();
				System.out.print("Origem: ");
				Posi��oXadrez origem = UI.lerPosicao(sc);
				
				boolean[][] movimentosPossiveis = partidaXadrez.movimentosPossiveis(origem);
				UI.limparTela();
				UI.imprimirTabuleiro(partidaXadrez.getPecas(), movimentosPossiveis);
				
				System.out.println();
				System.out.print("Destino: ");
				Posi��oXadrez destino = UI.lerPosicao(sc);

				Pe�aXadrez pecaCapturada = partidaXadrez.movimentarPecaXadrez(origem, destino);
				
				if (pecaCapturada != null) {
					capturadas.add(pecaCapturada);
				}
				
			} catch (XadrezException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.limparTela();
		UI.imprimirPartida(partidaXadrez, capturadas);
	}

}
