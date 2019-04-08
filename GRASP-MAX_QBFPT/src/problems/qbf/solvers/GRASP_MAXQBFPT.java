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

	// RLC construction mechanisms
    public static final int STANDARD = 1;
    public static final int REACTIVE = 2;
    public static final int SAMPLED_GREEDY = 3;
    private final int contructionMechanism;
    
    // List of alphas used in reactive construction
    private ArrayList<AlphaReativo> reactiveAlphas;
    
    // Parameter p used in sampleGreedy construction
    private int sampleGreedyP;
    
    // List of element objects used in prohibited triples
    // These objects represents the variables of the model
    private TripleElement[] tripleElements;
    
    // List of prohibited triples
    private Triple[] triples;

    public GRASP_MAXQBFPT(Double alpha, int contructionMechanism, Boolean firstImproving, Integer executionTime, Integer iterationsLimit, String filename) throws IOException {
        
    	super(alpha, firstImproving, executionTime, iterationsLimit, filename);
        this.contructionMechanism = contructionMechanism;
        
        if(contructionMechanism == REACTIVE)
        	gerarListaAlphas();
        else if(contructionMechanism == SAMPLED_GREEDY)
        	sampleGreedyP = (int) (0.05 * ObjFunction.getDomainSize());
        
        generateTripleElements();
        generateTriples();
    }
    
    /**
     * Linear congruent function l used to generate pseudo-random numbers
     */
    public int l(int pi1, int pi2, int u, int n) {
        return 1 + ((pi1 * u + pi2) % n);
    }

    /**
     * Function g used to generate pseudo-random numbers
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
     * Function h used to generate pseudo-random numbers
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
    
    /**
     * That method generates a list of objects (Triple Elements) that represents each binary
     * variable that could be inserted into a prohibited triple
     */
    private void generateTripleElements() {
        int n = ObjFunction.getDomainSize();
        this.tripleElements = new TripleElement[n];

        for (int i = 0; i < n; i++) {
            tripleElements[i] = new TripleElement(i);
        }
    }

    /**
     * Method that generates a list of n prohibited triples using l g and h functions
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
     * The GRASP constructive heuristic, which is responsible for building a
     * feasible solution by selecting in a greedy-random fashion, candidate
     * elements to enter the solution.
     *
     * @return A feasible solution to the problem being minimized.
     */
    @Override
    public Solution<Integer> constructiveHeuristic() {

        if (this.contructionMechanism == REACTIVE) {
            Solution<Integer> solucaoParcial;
            escolherAlpha();
            solucaoParcial = super.constructiveHeuristic();
            atualizarProbAlphas(solucaoParcial);
            
        } else if (this.contructionMechanism == SAMPLED_GREEDY) {
            return sampleGreedyConstruction();
        }

        // Standard construction 
        return super.constructiveHeuristic();
    }

    /**
     * A GRASP CL generator for MAXQBFPT problem
     * 
     * @return A list of candidates to partial solution
     */
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

    /**
     * The GRASP CL updater for MAXQBFPT problem
     * 
     * @return A new list of candidates to partial solution without elements that
     * turned infeasible because of a prohibited triple
     */
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



    /*
     * Method that implements sampled greedy construction for MAXQBFPT problem
     * First it selects min{sampleGreedyParamenter, |feasibleCandidates|} random candidates
     * from all feasible candidates to construct a sampleGreedyCL.
     * Then 
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

            /* Chose randomly min{sampleGreedyP,|CL|} candidates to constructi a new CL*/
            @SuppressWarnings("unchecked")
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

        this.reactiveAlphas = new ArrayList<>();

        for (double val = VAL_INICIAL; val <= VAL_FINAL; val += VAL_INCREMENTO) {
            AlphaReativo alphaReativo = new AlphaReativo(val, 1D / (VAL_FINAL / VAL_INCREMENTO));
            this.reactiveAlphas.add(alphaReativo);
        }
    }

    private void escolherAlpha() {
        Random rand = new Random();
        double probTotal = 0D;
        double sorteio, soma;

        for (AlphaReativo alp : this.reactiveAlphas) {
            if (alp.getQuantUsos() < 1) {
                this.alpha = alp.getValor();
                return;
            }

            probTotal += alp.getProb();
        }

        sorteio = rand.nextDouble() * probTotal;

        soma = 0D;
        for (AlphaReativo alp : embaralharLista(this.reactiveAlphas)) {
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
        for (AlphaReativo alp : this.reactiveAlphas) {
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
        for (AlphaReativo alp : this.reactiveAlphas) {
            somaQi += alp.calcQi(melhorCusto);
        }

        for (AlphaReativo alp : this.reactiveAlphas) {
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
