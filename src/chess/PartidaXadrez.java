package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Peça;
import boardgame.Posição;
import boardgame.Tabuleiro;
import chess.pieces.Bispo;
import chess.pieces.Cavalo;
import chess.pieces.Peão;
import chess.pieces.Rainha;
import chess.pieces.Rei;
import chess.pieces.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;

	private List<Peça> pecasNoTabuleiro = new ArrayList<>();
	private List<Peça> pecasCapturadas = new ArrayList<>();

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

	public PeçaXadrez[][] getPecas() {
		PeçaXadrez[][] mat = new PeçaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PeçaXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public boolean[][] movimentosPossiveis(PosiçãoXadrez posicaoOrigem) {
		Posição posicao = posicaoOrigem.toPosicao();
		validarPosicaoOriginal(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PeçaXadrez movimentarPecaXadrez(PosiçãoXadrez posiçãoOrigem, PosiçãoXadrez posicaoDestino) {
		Posição origem = posiçãoOrigem.toPosicao();
		Posição destino = posicaoDestino.toPosicao();
		validarPosicaoOriginal(origem);
		validarPosicaoDestino(origem, destino);
		Peça pecaCapturada = mover(origem, destino);

		if (testarCheck(jogadorAtual)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezException("Você não pode se colocar em Check");
		}

		check = (testarCheck(oponente(jogadorAtual))) ? true : false;

		if (testarCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		} else {
			proximoTurno();
		}
		
		return (PeçaXadrez) pecaCapturada;
	}

	private Peça mover(Posição origem, Posição destino) {
		PeçaXadrez p = (PeçaXadrez)tabuleiro.removerPeca(origem);
		p.aumentarMovimentos();
		Peça pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.posicionarPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);

		}

		return pecaCapturada;
	}

	private void desfazerMovimento(Posição origem, Posição destino, Peça pecaCapturada) {
		PeçaXadrez p = (PeçaXadrez)tabuleiro.removerPeca(destino);
		p.diminuirMovimentos();
		tabuleiro.posicionarPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.posicionarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
	}

	private void validarPosicaoOriginal(Posição posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadrezException("Não tem peça na posição de origem");
		}
		if (jogadorAtual != ((PeçaXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("A peça escolhida não é sua");
		}
		if (!tabuleiro.peca(posicao).existeMovimentosPossiveis()) {
			throw new XadrezException("Não existem movimentos possíveis para a peça");
		}
	}

	private void validarPosicaoDestino(Posição origem, Posição destino) {
		if (!tabuleiro.peca(origem).movimentosPossiveis(destino)) {
			throw new XadrezException("A peça escolhida não pode ser colocada na posição de destino");
		}
	}

	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private PeçaXadrez rei(Cor cor) {
		List<Peça> lista = pecasNoTabuleiro.stream().filter(x -> ((PeçaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peça p : lista) {
			if (p instanceof Rei) {
				return (PeçaXadrez) p;
			}
		}
		throw new IllegalStateException("Não existe rei da cor " + cor + "no tabuleiro");
	}

	private boolean testarCheck(Cor cor) {
		Posição posicaoRei = rei(cor).getPosiçãoXadrez().toPosicao();
		List<Peça> pecasOponentes = pecasNoTabuleiro.stream().filter(x -> ((PeçaXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peça p : pecasOponentes) {
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
		List<Peça> lista = pecasNoTabuleiro.stream().filter(x -> ((PeçaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peça p : lista) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posição origem = ((PeçaXadrez) p).getPosiçãoXadrez().toPosicao();
						Posição destino = new Posição(i, j);
						Peça pecaCapturada = mover(origem, destino);
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

	private void posicionarNovaPeca(char coluna, int linha, PeçaXadrez peca) {
		tabuleiro.posicionarPeca(peca, new PosiçãoXadrez(coluna, linha).toPosicao());
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
		posicionarNovaPeca('a', 2, new Peão(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('b', 2, new Peão(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('c', 2, new Peão(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('d', 2, new Peão(tabuleiro, Cor.BRANCO));
		posicionarNovaPeca('e', 2, new Peão(tabuleiro, Cor.BRANCO));
        posicionarNovaPeca('f', 2, new Peão(tabuleiro, Cor.BRANCO));
        posicionarNovaPeca('g', 2, new Peão(tabuleiro, Cor.BRANCO));
        posicionarNovaPeca('h', 2, new Peão(tabuleiro, Cor.BRANCO));

		posicionarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('a', 7, new Peão(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('b', 7, new Peão(tabuleiro, Cor.PRETO));
		posicionarNovaPeca('c', 7, new Peão(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('d', 7, new Peão(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('e', 7, new Peão(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('f', 7, new Peão(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('g', 7, new Peão(tabuleiro, Cor.PRETO));
        posicionarNovaPeca('h', 7, new Peão(tabuleiro, Cor.PRETO));

	}

}
