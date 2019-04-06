package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import metaheuristics.grasp.AbstractGRASP;
import metaheuristics.grasp.OperacaoBuscaLocal;
import problems.qbf.QBF;
import problems.qbf.QBF_Inverse;
import problems.qbf.Triple;
import problems.qbf.TripleElement;
import solutions.Solution;

/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 *
 * @author ccavellucci, fusberti
 */
public class GRASP_QBF extends AbstractGRASP<Integer> {
    
    protected boolean firstImproving = false;

    /**
     * Constructor for the GRASP_QBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param alpha The GRASP greediness-randomness parameter (within the range
     * [0,1])
     * @param iterations The number of iterations which the GRASP will be
     * executed.
     * @param filename Name of the file for which the objective function
     * parameters should be read.
     * @throws IOException necessary for I/O operations.
     */
    public GRASP_QBF(Double alpha, Boolean firstImproving, Integer tempoExecucao, Integer iteraConvengencia, String filename) throws IOException {
        super(new QBF_Inverse(filename), alpha, tempoExecucao, iteraConvengencia);
        
        this.firstImproving = firstImproving;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
     */
    @Override
    public ArrayList<Integer> makeCL() {

        ArrayList<Integer> _CL = new ArrayList<Integer>();
        for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
            Integer cand = new Integer(i);
            _CL.add(cand);
        }

        return _CL;

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeRCL()
     */
    @Override
    public ArrayList<Integer> makeRCL() {

        ArrayList<Integer> _RCL = new ArrayList<Integer>();

        return _RCL;

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
     */
    @Override
    public void updateCL() {

        // do nothing since all elements off the solution are viable candidates.
    }

    /**
     * {@inheritDoc}
     *
     * This createEmptySol instantiates an empty solution and it attributes a
     * zero cost, since it is known that a QBF solution with all variables set
     * to zero has also zero cost.
     */
    @Override
    public Solution<Integer> createEmptySol() {
        Solution<Integer> sol = new Solution<Integer>();
        sol.cost = 0.0;
        return sol;
    }

    /**
     * {@inheritDoc}
     *
     * The local search operator developed for the QBF objective function is
     * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
     */
    @Override
    public Solution<Integer> localSearch() {

        Random rand = new Random();
        Double minDeltaCost;
        OperacaoBuscaLocal melhorVizinho = null;

        do {
            minDeltaCost = Double.POSITIVE_INFINITY;
            updateCL();
            
            ArrayList<OperacaoBuscaLocal> listaVizinhanca = new ArrayList<>();

            // Adicionando toda vizinhança de inclusão de elementos
            for (Integer candIn : CL) {
                listaVizinhanca.add(new OperacaoBuscaLocal(OperacaoBuscaLocal.INSERCAO, candIn));
            }
            
            // Adicionando toda vizinhança de remoção de elementos
            for (Integer candOut : incumbentSol) {
                listaVizinhanca.add(new OperacaoBuscaLocal(OperacaoBuscaLocal.REMOCAO, candOut));
            }
            
            // Adicionando toda vizinhança de troca de elementos
            for (Integer candIn : CL) {
                for (Integer candOut : incumbentSol) {
                    listaVizinhanca.add(new OperacaoBuscaLocal(candOut, candIn));
                }
            }
            
            // Com a lista de todos os vizinhos que devem ser visitados os visita de forma aleatória e salva o melhor
            while (!listaVizinhanca.isEmpty()) {
                // Escolhe de forma aleatória qual o próximo vizinho a ser visitado
                int ind = rand.nextInt(listaVizinhanca.size());
                
                double deltaCost = Double.POSITIVE_INFINITY;
                OperacaoBuscaLocal vizinho = listaVizinhanca.get(ind);
                if(vizinho.isInsercao()) {
                    deltaCost = ObjFunction.evaluateInsertionCost(vizinho.getElemento(), incumbentSol);
                }
                if (vizinho.isRemocao()) {
                    deltaCost = ObjFunction.evaluateRemovalCost(vizinho.getElemento(), incumbentSol);
                }
                if (vizinho.isTroca()) {
                    deltaCost = ObjFunction.evaluateExchangeCost(vizinho.getElementoEntra(), vizinho.getElementoSai(), incumbentSol);
                }
                
                // Removendo esse vizinho para não passar mais por ele
                listaVizinhanca.remove(ind);
                
                // Se esse vizinho é o melhor até agora
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    melhorVizinho = vizinho;
                    
                    // Se for fist improving basta dar um break aqui
                    if (this.firstImproving) {
                        break;
                    }
                }
            }
            
            // Implement the best move, if it reduces the solution cost.
            if (minDeltaCost < -Double.MIN_VALUE && melhorVizinho != null) {
                if (melhorVizinho.isInsercao()) {
                    incumbentSol.add(melhorVizinho.getElemento());
                    CL.remove(melhorVizinho.getElemento());
                }
                
                if (melhorVizinho.isRemocao()) {
                    incumbentSol.remove(melhorVizinho.getElemento());
                    CL.add(melhorVizinho.getElemento());
                }
                
                if (melhorVizinho.isTroca()) {
                    incumbentSol.add(melhorVizinho.getElementoEntra());
                    CL.remove(melhorVizinho.getElementoEntra());
                    
                    incumbentSol.remove(melhorVizinho.getElementoSai());
                    CL.add(melhorVizinho.getElementoSai());
                }
                
                
                ObjFunction.evaluate(incumbentSol);
            }
        } while (minDeltaCost < -Double.MIN_VALUE);

        return null;
    }
    
    /**
     * That method generates a list of objects that represents each binary variable
     * called Triple Element that could be inserted into a prohibited triple
     * @return A list of Triple Elements
     */
    public ArrayList<TripleElement> generateTripleElements(){
    	ArrayList<TripleElement> tripleElements = new ArrayList<TripleElement>();
    	int n = ObjFunction.getDomainSize();
    	for(int i = 0; i < n; i++)
    	{
    		tripleElements.add(new TripleElement(i));
    	}
		return tripleElements;
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
    public int l(int pi1, int pi2, int u, int n)
    {
    	return 1 + ((pi1 * u + pi2) % n);
    }
    
    /**
     * Method that generate the index of a element to be inserted
     * on a prohibited triple
     * 
     * @param u
     * @param n number of variables
     * @return a pseudo-random variable index
     */
    public int g(int u, int n) {
    	int pi1 = 131;
    	int pi2 = 1031;
    	int lU = l(pi1, pi2, u, n);
    	
    	if(lU != u)
    		return lU;
    	else
    		return 1 + (lU % n);
    }
    
    /**
     * Method that generate the index of a element to be inserted
     * on a prohibited triple
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
    	if(lU != u && lU != gU)
    		return lU;
    	else if( (1 + (lU % n)) != u && (1 + (lU % n)) != gU)
    		return 1 + (lU % n);
    	else
    		return 1 + ((lU + 1) % n);
    }
    
    /**
     * Method that generates a list of n prohibited triples using l g and h functions
     * 
     * @param tripleElements triple elements objects
     * @return a list of n prohibited triples
     */
    public ArrayList<Triple> generateTriples(ArrayList<TripleElement> tripleElements)
    {
    	ArrayList<Triple> triples = new ArrayList<Triple>();
    	int n = ObjFunction.getDomainSize() - 1;
    	
    	for(int u = 0; u < n; u++)
    	{
    		TripleElement te1, te2, te3;
    		te1 = tripleElements.get(u);
    		te2 = tripleElements.get(g(u, n));
    		te3 = tripleElements.get(h(u, n));
    		Triple novaTripla = new Triple(te1, te2, te3);
    		
    		Collections.sort(novaTripla.getElements(), new Comparator<TripleElement>(){
                public int compare(TripleElement te1, TripleElement te2) {
                  return te1.getIndex().compareTo(te2.getIndex());
               }
    		});
    		
    		triples.add(novaTripla);
    	}
		return triples;	
    }

    /**
     * A main method used for testing the GRASP metaheuristic.
     *
     */
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
<<<<<<< HEAD
        GRASP_QBF grasp = new GRASP_QBF(0.05, 1000, "instances/qbf020");
        
        ArrayList<TripleElement> tripleElements = grasp.generateTripleElements();
        ArrayList<Triple> triples = grasp.generateTriples(tripleElements);

        //Print Triples
        //for(int i = 0; i < triples.size(); i++) triples.get(i).printTriple();
        
        Solution<Integer> bestSol = grasp.solve(30, 100);
=======
        GRASP_QBF grasp = new GRASP_QBF(0.05, false, 30, 100, "instances/qbf020");
        Solution<Integer> bestSol = grasp.solve();
>>>>>>> 3fb6f3ebf8bfd1d65594d274925d7715762cdf9e
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

    }

}
