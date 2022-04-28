package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Pe�a;
import boardgame.Posi��o;
import boardgame.Tabuleiro;
import chess.pieces.Bispo;
import chess.pieces.Cavalo;
import chess.pieces.Pe�o;
import chess.pieces.Rainha;
import chess.pieces.Rei;
import chess.pieces.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;

	private List<Pe�a> pecasNoTabuleiro = new ArrayList<>();
	private List<Pe�a> pecasCapturadas = new ArrayList<>();

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		setupInicial();
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorAtual() {
		return jogadorAtual;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public Pe�aXadrez[][] getPecas() {
		Pe�aXadrez[][] mat = new Pe�aXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (Pe�aXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public boolean[][] movimentosPossiveis(Posi��oXadrez posicaoOrigem) {
		Posi��o posicao = posicaoOrigem.toPosicao();
		validarPosicaoOriginal(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public Pe�aXadrez movimentarPecaXadrez(Posi��oXadrez posi��oOrigem, Posi��oXadrez posicaoDestino) {
		Posi��o origem = posi��oOrigem.toPosicao();
		Posi��o destino = posicaoDestino.toPosicao();
		validarPosicaoOriginal(origem);
		validarPosicaoDestino(origem, destino);
		Pe�a pecaCapturada = mover(origem, destino);

		if (testarCheck(jogadorAtual)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezException("Voc� n�o pode se colocar em Check");
		}

		check = (testarCheck(oponente(jogadorAtual))) ? true : false;

		if (testarCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		} else {
			proximoTurno();
		}
		
		return (Pe�aXadrez) pecaCapturada;
	}

	private Pe�a mover(Posi��o origem, Posi��o destino) {
		Pe�aXadrez p = (Pe�aXadrez)tabuleiro.removerPeca(origem);
		p.aumentarMovimentos();
		Pe�a pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.posicionarPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);

		}

		return pecaCapturada;
	}

	private void desfazerMovimento(Posi��o origem, Posi��o destino, Pe�a pecaCapturada) {
		Pe�aXadrez p = (Pe�aXadrez)tabuleiro.removerPeca(destino);
		p.diminuirMovimentos();
		tabuleiro.posicionarPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.posicionarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
	}

	private void validarPosicaoOriginal(Posi��o posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadrezException("N�o tem pe�a na posi��o de origem");
		}
		if (jogadorAtual != ((Pe�aXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("A pe�a escolhida n�o � sua");
		}
		if (!tabuleiro.peca(posicao).existeMovimentosPossiveis()) {
			throw new XadrezException("N�o existem movimentos poss�veis para a pe�a");
		}
	}

	private void validarPosicaoDestino(Posi��o origem, Posi��o destino) {
		if (!tabuleiro.peca(origem).movimentosPossiveis(destino)) {
			throw new XadrezException("A pe�a escolhida n�o pode ser colocada na posi��o de destino");
		}
	}

	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private Pe�aXadrez rei(Cor cor) {
		List<Pe�a> lista = pecasNoTabuleiro.stream().filter(x -> ((Pe�aXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Pe�a p : lista) {
			if (p instanceof Rei) {
				return (Pe�aXadrez) p;
			}
		}
		throw new IllegalStateException("N�o existe rei da cor " + cor + "no tabuleiro");
	}

	private boolean testarCheck(Cor cor) {
		Posi��o posicaoRei = rei(cor).getPosi��oXadrez().toPosicao();
		List<Pe�a> pecasOponentes = pecasNoTabuleiro.stream().filter(x -> ((Pe�aXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Pe�a p : pecasOponentes) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testarCheckMate(Cor cor) {
		if (!testarCheck(cor)) {
			return false;
		}
		List<Pe�a> lista = pecasNoTabuleiro.stream().filter(x -> ((Pe�aXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Pe�a p : lista) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posi��o origem = ((Pe�aXadrez) p).getPosi��oXadrez().toPosicao();
						Posi��o destino = new Posi��o(i, j);
						Pe�a pecaCapturada = mover(origem, destino);
						boolean testarCheck = testarCheck(cor);
						desfazerMovimento(origem, destino, pecaCapturada);
						if (!testarCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void posicionarNovaPeca(char coluna, int linha, Pe�aXadrez peca) {
		tabuleiro.posicionarPeca(peca, new Posi��oXadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void setupInicial() {
		
		posicionarNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('a', 2, new Pe�o(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('b', 2, new Pe�o(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('c', 2, new Pe�o(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('d', 2, new Pe�o(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('e', 2, new Pe�o(tabuleiro, Cor.BRANCO));
        posicionarNovaPeca('f', 2, new Pe�o(tabuleiro, Cor.BRANCO));
        posicionarNovaPeca('g', 2, new Pe�o(tabuleiro, Cor.BRANCO));
        posicionarNovaPeca('h', 2, new Pe�o(tabuleiro, Cor.BRANCO));

		posicionarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('a', 7, new Pe�o(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('b', 7, new Pe�o(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('c', 7, new Pe�o(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('d', 7, new Pe�o(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('e', 7, new Pe�o(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('f', 7, new Pe�o(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('g', 7, new Pe�o(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('h', 7, new Pe�o(tabuleiro, Cor.PRETO));

	}

}
