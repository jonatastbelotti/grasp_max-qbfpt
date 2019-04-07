package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import metaheuristics.grasp.AlphaReativo;
import problems.qbf.Triple;
import problems.qbf.TripleElement;
import solutions.Solution;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class GRASP_MAXQBFPT extends GRASP_QBF {
    
    public static final int CONSTRUCAO_PADRAO = 1;
    public static final int CONSTRUCAO_REATIVA = 2;
    private ArrayList<AlphaReativo> listaAlphas;
    
    private final int tipoConstrucao;
    private TripleElement[] tripleElements;
    private ArrayList<Triple> triples;
    private ArrayList<Triple>[] prohibitedTriples; // vetor em que cada posição contém um ArrayList

    public GRASP_MAXQBFPT(Double alpha, int tipoConstrucao, Boolean firstImproving, Integer tempoExecucao, Integer iteraConvengencia, String filename) throws IOException {
        super(alpha, firstImproving, tempoExecucao, iteraConvengencia, filename);
        
        this.tipoConstrucao = tipoConstrucao;
        gerarListaAlphas();

        generateTripleElements();
        generateTriples();
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
        limparTriplasEmUso();
        
        if (this.tipoConstrucao == CONSTRUCAO_REATIVA) {
            Solution<Integer> solucao;
            
            escolherAlpha();
            solucao = super.constructiveHeuristic();
            atualizarProbAlphas();
        }

        return super.constructiveHeuristic();
    }

    @Override
    public ArrayList<Integer> makeCL() {
        ArrayList<Integer> _CL = new ArrayList<Integer>();

        // Marcando todos os elementos da solução atual como já usados nas triplas
        if (this.incumbentSol != null) {
            for (Integer e : this.incumbentSol) {
                this.tripleElements[e].setSelected(true);
            }
        }

        // Passando por todos os elementos e verificando se eles podem ser usados ainda
        for (TripleElement tripElem : this.tripleElements) {
            // Se o elemento já foi usado
            if (tripElem.getSelected()) {
                continue;
            }

            // Se adicionar esse elemento significa quebrar uma tripla
            boolean podeAdicionar = true;
            for (Triple triple : this.prohibitedTriples[tripElem.index]) {
                if (triple.elementosEmUso() >= 2) {
                    podeAdicionar = false;
                }
            }
            
            if (podeAdicionar) {
                _CL.add(tripElem.index);
            }
        }

        return _CL;
    }

    @Override
    public void updateCL() {
        this.CL = makeCL();
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
        this.triples = new ArrayList();
        this.prohibitedTriples = new ArrayList[ObjFunction.getDomainSize()];

        for (int i = 0; i < this.prohibitedTriples.length; i++) {
            this.prohibitedTriples[i] = new ArrayList<>();
        }

        for (int u = 0; u < n; u++) {
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

            this.triples.add(novaTripla);
            this.prohibitedTriples[te1.index].add(novaTripla);
            this.prohibitedTriples[te2.index].add(novaTripla);
            this.prohibitedTriples[te3.index].add(novaTripla);
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
    * Método que marca todos os elementos como não usados ainda
     */
    private void limparTriplasEmUso() {
        for (TripleElement ele : this.tripleElements) {
            ele.setSelected(false);
        }
    }

    /*
    * Método que implementa a construção reativa
    */
    private Solution<Integer> construcaoReativa() {
        CL = makeCL();
        RCL = makeRCL();
        incumbentSol = createEmptySol();
        incumbentCost = Double.POSITIVE_INFINITY;
        
        while (!CL.isEmpty()) {
        }
        
        return incumbentSol;
    }

    private void gerarListaAlphas() {
        this.listaAlphas = new ArrayList<>();
        
        for (double val = 0.05; val <= 0.2; val += 0.05) {
            AlphaReativo alphaReativo = new AlphaReativo(val, 1D / (0.2 / 0.05));
            this.listaAlphas.add(alphaReativo);
        }
    }

    private void escolherAlpha() {
        Random rand = new Random();
        double probTotal = 0D;
        
        for (AlphaReativo alpha : this.listaAlphas) {
            probTotal += alpha.getProb();
        }
    }

    private void atualizarProbAlphas() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
