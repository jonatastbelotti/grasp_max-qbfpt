package metaheuristics.grasp;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class AlphaReativo {

    private final double valor;
    private double prob;

    public AlphaReativo(double valor, double prob) {
        this.valor = valor;
        this.prob = prob;
    }

    public double getValor() {
        return valor;
    }

    public double getProb() {
        return prob;
    }

}
