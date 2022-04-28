package boardgame;

public class Tabuleiro {
	
	private int linhas;
	private int colunas;
	private Pe�a[][] pecas;
	
	public Tabuleiro(int linhas, int colunas) {
		if (linhas < 1 || colunas < 1) {
			throw new TabuleiroException("Erro criando o tabuleiro, � necess�rio ao menos uma linha");
		}
		this.linhas = linhas;
		this.colunas = colunas;
		pecas = new Pe�a[linhas][colunas];
	}

	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}

	public Pe�a peca(int linha, int coluna) {
		if (!posicaoExiste(linha, coluna)) {
			throw new TabuleiroException("Posi��o n�o existe no tabuleiro");
		}
		return pecas[linha][coluna];
	}
	
	public Pe�a peca(Posi��o posicao) {
		if (!posicaoExiste(posicao)) {
			throw new TabuleiroException("Posi��o n�o existe no tabuleiro");
		}
		return pecas[posicao.getLinha()][posicao.getColuna()];
	}
	
	public void posicionarPeca(Pe�a peca, Posi��o posicao) {
		if (temUmaPeca(posicao)) {
			throw new TabuleiroException("J� existe uma pe�a nessa posi��o do tabuleiro");
		}
		pecas[posicao.getLinha()][posicao.getColuna()] = peca;
		peca.posicao = posicao;
	}
	
	public Pe�a removerPeca(Posi��o posicao) {
		if (!posicaoExiste(posicao)) {
			throw new TabuleiroException("Posi��o n�o existe no tabuleiro");
		}
		if (peca(posicao) == null) {
			return null;
		}
		Pe�a aux = peca(posicao);
		aux.posicao = null;
		pecas[posicao.getLinha()][posicao.getColuna()] = null;
		return aux;
	}
	
	public boolean posicaoExiste(int linha, int coluna) {
		return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
	}
	
	public boolean posicaoExiste(Posi��o posicao) {
		return posicaoExiste(posicao.getLinha(), posicao.getColuna());
	}
	
	public boolean temUmaPeca(Posi��o posicao) {
		if (!posicaoExiste(posicao)) {
			throw new TabuleiroException("Posi��o n�o existe no tabuleiro");
		}
		return peca(posicao) != null;
	}
}

