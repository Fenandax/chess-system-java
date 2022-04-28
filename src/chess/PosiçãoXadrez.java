package chess;

import boardgame.Posição;

public class PosiçãoXadrez {
	
	private char coluna;
	private int linha;
	
	public PosiçãoXadrez(char coluna, int linha) {
		if (coluna < 'a' || coluna > 'h' || linha < 1 || linha > 8) {
			throw new XadrezException("Erro ao instanciar a posição. Valores válidos são de a1 até h8");
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

	protected Posição toPosicao(){
		return new Posição(8 - linha, coluna - 'a');	
	}
	
	protected static PosiçãoXadrez fromPosicao(Posição posicao) {
		return new PosiçãoXadrez((char)('a' + posicao.getColuna()), 8 - posicao.getLinha());
	}
	
	@Override
	public String toString() {
		return "" + coluna + linha;
	}
}
