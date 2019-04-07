package metaheuristics.grasp;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class AlphaReativo {

    private final double valor;
    private double prob;
    private int quantUsos;
    private double totalCustos;

    public AlphaReativo(double valor, double prob) {
        this.valor = valor;
        this.prob = prob;
        this.quantUsos = 0;
        this.totalCustos = 0D;
    }

    public double getValor() {
        return valor;
    }

    public double getProb() {
        return prob;
    }

    public void addUso(double custo) {
        this.quantUsos++;
        this.totalCustos += custo;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public double calcQi(double bestCost) {
        double mediaSolucoes = 10D;

        if (this.quantUsos > 0) {
            mediaSolucoes = this.totalCustos / this.quantUsos;
        }

        return bestCost / mediaSolucoes;
    }

}
