package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import problems.qbf.Triple;
import problems.qbf.TripleElement;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class GRASP_MAXQBFPT extends GRASP_QBF {

    private TripleElement[] tripleElements;
    private ArrayList<Triple> triples;
    private ArrayList<Triple>[] prohibitedTriples; // vetor em que cada posição contém um ArrayList

    public GRASP_MAXQBFPT(Double alpha, Boolean firstImproving, Integer tempoExecucao, Integer iteraConvengencia, String filename) throws IOException {
        super(alpha, firstImproving, tempoExecucao, iteraConvengencia, filename);

        generateTripleElements();
        generateTriples();
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
        int n = ObjFunction.getDomainSize();
        this.triples = new ArrayList();
        this.prohibitedTriples = new ArrayList[n];
        
        for (int i = 0; i < n; i++) {
            this.prohibitedTriples[i] = new ArrayList<>();
        }

        n = n - 1;

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

}
