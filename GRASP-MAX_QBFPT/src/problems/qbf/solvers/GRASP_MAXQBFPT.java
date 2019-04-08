package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import alternativeConstruction.AlphaReativo;
import solutions.Solution;
import triple.Triple;
import triple.TripleElement;

/**
 * 
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 * @author Felipe de Carvalho Pereira [felipe.pereira@students.ic.unicamp.br]
 */
public class GRASP_MAXQBFPT extends GRASP_QBF {

    public static final int CONSTRUCAO_PADRAO = 1;
    public static final int CONSTRUCAO_REATIVA = 2;
    public static final int SAMPLED_GREEDY = 3;
    private ArrayList<AlphaReativo> listaAlphas;

    private final int tipoConstrucao;
    private TripleElement[] tripleElements;
    private Triple[] triples;
    private final int sampleGreedyP;

    public GRASP_MAXQBFPT(Double alpha, int tipoConstrucao, Boolean firstImproving, Integer tempoExecucao, Integer iteraConvengencia, String filename) throws IOException {
        super(alpha, firstImproving, tempoExecucao, iteraConvengencia, filename);

        this.tipoConstrucao = tipoConstrucao;
        gerarListaAlphas();

        generateTripleElements();
        generateTriples();
        sampleGreedyP = (int) (0.05 * ObjFunction.getDomainSize());
    }

    /**
     * The GRASP constructive heuristic, which is responsible for building a
     * feasible solution by selecting in a greedy-random fashion, candidate
     * elements to enter the solution.
     *
     * @return A feasible solution to the problem being minimized.
     */
    @Override
    public Solution<Integer> constructiveHeuristic() {

        if (this.tipoConstrucao == CONSTRUCAO_REATIVA) {
            Solution<Integer> solucaoParcial;

            escolherAlpha();
            solucaoParcial = super.constructiveHeuristic();
            atualizarProbAlphas(solucaoParcial);
        } else if (this.tipoConstrucao == SAMPLED_GREEDY) {
            return sampleGreedyConstruction();
        }

        // Standard construction 
        return super.constructiveHeuristic();
    }

    @Override
    public ArrayList<Integer> makeCL() {
        int n = ObjFunction.getDomainSize();
        ArrayList<Integer> _CL = new ArrayList<Integer>(n);

        for (TripleElement tripElem : this.tripleElements) {
            tripElem.setAvailable(true);
            tripElem.setSelected(false);
            _CL.add(tripElem.getIndex());
        }

        return _CL;
    }

    @Override
    public void updateCL() {
        ArrayList<Integer> _CL = new ArrayList<Integer>();

        if (this.incumbentSol != null) {
            for (Integer e : this.incumbentSol) {
                this.tripleElements[e].setSelected(true);
                this.tripleElements[e].setAvailable(false);
            }
        }

        for (Triple trip : this.triples) {
            TripleElement te0, te1, te2;
            te0 = trip.getElements().get(0);
            te1 = trip.getElements().get(1);
            te2 = trip.getElements().get(2);

            if (te0.getSelected() && te1.getSelected()) {
                te2.setAvailable(false);
            } else if (te0.getSelected() && te2.getSelected()) {
                te1.setAvailable(false);
            } else if (te1.getSelected() && te2.getSelected()) {
                te0.setAvailable(false);
            }
        }

        for (TripleElement tripElem : this.tripleElements) {
            if (!tripElem.getSelected() && tripElem.getAvailable()) {
                _CL.add(tripElem.getIndex());
            }
        }

        this.CL = _CL;
    }

    /**
     * That method generates a list of objects that represents each binary
     * variable called Triple Element that could be inserted into a prohibited
     * triple
     *
     * @return A list of Triple Elements
     */
    private void generateTripleElements() {
        int n = ObjFunction.getDomainSize();
        this.tripleElements = new TripleElement[n];

        for (int i = 0; i < n; i++) {
            tripleElements[i] = new TripleElement(i);
        }
    }

    /**
     * Method that generates a list of n prohibited triples using l g and h
     * functions
     */
    private void generateTriples() {
        int n = ObjFunction.getDomainSize() - 1;
        this.triples = new Triple[ObjFunction.getDomainSize()];

        for (int u = 0; u <= n; u++) {
            TripleElement te1, te2, te3;
            Triple novaTripla;

            te1 = tripleElements[u];
            te2 = tripleElements[g(u, n)];
            te3 = tripleElements[h(u, n)];
            novaTripla = new Triple(te1, te2, te3);

            Collections.sort(novaTripla.getElements(), new Comparator<TripleElement>() {
                public int compare(TripleElement te1, TripleElement te2) {
                    return te1.getIndex().compareTo(te2.getIndex());
                }
            });

            this.triples[u] = novaTripla;
        }
    }

    /**
     * Linear function congruent used to generate pseudo-random numbers
     *
     * @param pi1
     * @param pi2
     * @param u
     * @param n number of variables
     * @return a pseudo-random variable index
     */
    public int l(int pi1, int pi2, int u, int n) {
        return 1 + ((pi1 * u + pi2) % n);
    }

    /**
     * Method that generate the index of a element to be inserted on a
     * prohibited triple
     *
     * @param u
     * @param n number of variables
     * @return a pseudo-random variable index
     */
    public int g(int u, int n) {
        int pi1 = 131;
        int pi2 = 1031;
        int lU = l(pi1, pi2, u, n);

        if (lU != u) {
            return lU;
        } else {
            return 1 + (lU % n);
        }
    }

    /**
     * Method that generate the index of a element to be inserted on a
     * prohibited triple
     *
     * @param u
     * @param n number of variables
     * @return a pseudo-random variable index
     */
    public int h(int u, int n) {
        int pi1 = 193;
        int pi2 = 1093;
        int lU = l(pi1, pi2, u, n);
        int gU = g(u, n);

        if (lU != u && lU != gU) {
            return lU;
        } else if ((1 + (lU % n)) != u && (1 + (lU % n)) != gU) {
            return 1 + (lU % n);
        } else {
            return 1 + ((lU + 1) % n);
        }
    }

    /*
     * Method that implements SAMPLED GREEDY CONSTRUCTION
     */
    private Solution<Integer> sampleGreedyConstruction() {

        incumbentSol = createEmptySol();
        incumbentCost = Double.POSITIVE_INFINITY;
        CL = makeCL();
        RCL = makeRCL();

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!constructiveStopCriteria()) {
            double minCost = Double.POSITIVE_INFINITY;
            Integer bestCandidate = -1;

            incumbentCost = ObjFunction.evaluate(incumbentSol);
            updateCL();

            if (CL.isEmpty()) {
                break;
            }

            /* Chose randomly min{p,|CL|} candidates to constructi a new CL*/
            ArrayList<Integer> sampledGreedyCL = (ArrayList<Integer>) CL.clone();
            Collections.shuffle(sampledGreedyCL);
            int minimumP = Math.min(this.sampleGreedyP, CL.size());
            for (int i = 0; i < minimumP; i++) {
                RCL.add(sampledGreedyCL.get(i));
            }

            /* Choose the best candidate from the RCL */
            for (Integer c : sampledGreedyCL) {
                Double newCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
                if (newCost < minCost) {
                    minCost = newCost;
                    bestCandidate = c;
                }
            }

            // Insert best candidate in partial solution
            CL.remove(bestCandidate);
            incumbentSol.add(bestCandidate);
            ObjFunction.evaluate(incumbentSol);
            RCL.clear();

        }

        return incumbentSol;
    }

    private void gerarListaAlphas() {
        double VAL_INICIAL = 0.1;
        double VAL_INCREMENTO = 0.01;
        double VAL_FINAL = 1;

        this.listaAlphas = new ArrayList<>();

        for (double val = VAL_INICIAL; val <= VAL_FINAL; val += VAL_INCREMENTO) {
            AlphaReativo alphaReativo = new AlphaReativo(val, 1D / (VAL_FINAL / VAL_INCREMENTO));
            this.listaAlphas.add(alphaReativo);
        }
    }

    private void escolherAlpha() {
        Random rand = new Random();
        double probTotal = 0D;
        double sorteio, soma;

        for (AlphaReativo alp : this.listaAlphas) {
            if (alp.getQuantUsos() < 1) {
                this.alpha = alp.getValor();
                return;
            }

            probTotal += alp.getProb();
        }

        sorteio = rand.nextDouble() * probTotal;

        soma = 0D;
        for (AlphaReativo alp : embaralharLista(this.listaAlphas)) {
            soma += alp.getProb();

            if (soma >= sorteio) {
                this.alpha = alp.getValor();
                break;
            }
        }

//        System.out.println("Alpha = " + this.alpha);
    }

    private void atualizarProbAlphas(Solution<Integer> solucaoParcia) {
        // Atualizando alpha usado agora
        for (AlphaReativo alp : this.listaAlphas) {
            if (alp.getValor() == this.alpha) {
                alp.addUso(solucaoParcia.cost);
                break;
            }
        }

        // Calculando novas probabilidades
        double somaQi = 0D;
        double melhorCusto = solucaoParcia.cost;
        if (this.bestCost != null) {
            melhorCusto = this.bestCost;
        }
        for (AlphaReativo alp : this.listaAlphas) {
            somaQi += alp.calcQi(melhorCusto);
        }

        for (AlphaReativo alp : this.listaAlphas) {
            alp.setProb(alp.calcQi(melhorCusto) / somaQi);
        }
    }

    private ArrayList<AlphaReativo> embaralharLista(ArrayList<AlphaReativo> listaAlphas) {
        Random rand = new Random();
        ArrayList<AlphaReativo> resp = new ArrayList<>();
        ArrayList<Integer> listaIndices = new ArrayList<>();

        for (int i = 0; i < listaAlphas.size(); i++) {
            listaIndices.add(i);
        }

        while (!listaIndices.isEmpty()) {
            int n = rand.nextInt(listaIndices.size());
            resp.add(listaAlphas.get(listaIndices.get(n)));
            listaIndices.remove(n);
        }

        return resp;
    }

}
