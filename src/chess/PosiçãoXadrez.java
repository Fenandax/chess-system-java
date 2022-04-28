package chess;

import boardgame.Posi��o;

public class Posi��oXadrez {
	
	private char coluna;
	private int linha;
	
	public Posi��oXadrez(char coluna, int linha) {
		if (coluna < 'a' || coluna > 'h' || linha < 1 || linha > 8) {
			throw new XadrezException("Erro ao instanciar a posi��o. Valores v�lidos s�o de a1 at� h8");
		}
		this.coluna = coluna;
		this.linha = linha;
	}

	public char getColuna() {
		return coluna;
	}

	public int getLinha() {
		return linha;
	}

	protected Posi��o toPosicao(){
		return new Posi��o(8 - linha, coluna - 'a');	
	}
	
	protected static Posi��oXadrez fromPosicao(Posi��o posicao) {
		return new Posi��oXadrez((char)('a' + posicao.getColuna()), 8 - posicao.getLinha());
	}
	
	@Override
	public String toString() {
		return "" + coluna + linha;
	}
}
