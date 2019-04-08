package metaheuristics.grasp;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 * @author Felipe de Carvalho Pereira [felipe.pereira@students.ic.unicamp.br]
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

    public int getQuantUsos() {
        return quantUsos;
    }

    public void setProb(double prob) {
        if (prob > 0) {
            this.prob = prob;
        }
    }

    public double calcQi(double bestCost) {
        double mediaSolucoes = bestCost;

        if (this.quantUsos > 0) {
            mediaSolucoes = this.totalCustos / this.quantUsos;
        } else {
            return 0D;
        }

        return bestCost / mediaSolucoes;
    }

}
