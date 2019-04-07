package metaheuristics.grasp;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class OperacaoBuscaLocal {

    public static int INSERCAO = 1;
    public static int REMOCAO = 2;
    public static int TROCA = 3;

    private int tipoOperacao;
    private Integer elemento;
    private Integer elementoSai;
    private Integer elementoEntra;

    public OperacaoBuscaLocal(int tipoOperacao, Integer elemento) {
        this.tipoOperacao = tipoOperacao;
        this.elemento = elemento;
    }

    public OperacaoBuscaLocal(Integer elementoSai, Integer elementoEntra) {
        this.tipoOperacao = TROCA;
        this.elementoSai = elementoSai;
        this.elementoEntra = elementoEntra;
    }

    public boolean isInsercao() {
        return this.tipoOperacao == INSERCAO;
    }

    public boolean isRemocao() {
        return this.tipoOperacao == REMOCAO;
    }

    public boolean isTroca() {
        return this.tipoOperacao == TROCA;
    }

    public Integer getElemento() {
        return elemento;
    }

    public Integer getElementoSai() {
        return elementoSai;
    }

    public Integer getElementoEntra() {
        return elementoEntra;
    }

}
