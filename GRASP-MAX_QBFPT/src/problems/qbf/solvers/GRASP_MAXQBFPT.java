package problems.qbf.solvers;

import java.io.IOException;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class GRASP_MAXQBFPT extends GRASP_QBF {

    public GRASP_MAXQBFPT(Double alpha, Boolean firstImproving, Integer tempoExecucao, Integer iteraConvengencia, String filename) throws IOException {
        super(alpha, firstImproving, tempoExecucao, iteraConvengencia, filename);
    }

}
