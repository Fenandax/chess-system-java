package chess;

import boardgame.Peça;
import boardgame.Posição;
import boardgame.Tabuleiro;

public abstract class PeçaXadrez extends Peça{

	private Cor cor;
	private int contagemMovimentos;

	public PeçaXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}
	
	public int getContagemMovimentos() {
		return contagemMovimentos;
	}
	
	public void aumentarMovimentos(){
		contagemMovimentos++;
	}
	
	public void diminuirMovimentos(){
		contagemMovimentos--;
	}
	
	public PosiçãoXadrez getPosiçãoXadrez() {
		return PosiçãoXadrez.fromPosicao(posicao);
	}
	
	protected boolean temPecaOponente(Posição posicao) {
		PeçaXadrez p = (PeçaXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
