package chess;

import boardgame.Pe�a;
import boardgame.Posi��o;
import boardgame.Tabuleiro;

public abstract class Pe�aXadrez extends Pe�a{

	private Cor cor;
	private int contagemMovimentos;

	public Pe�aXadrez(Tabuleiro tabuleiro, Cor cor) {
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
	
	public Posi��oXadrez getPosi��oXadrez() {
		return Posi��oXadrez.fromPosicao(posicao);
	}
	
	protected boolean temPecaOponente(Posi��o posicao) {
		Pe�aXadrez p = (Pe�aXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
