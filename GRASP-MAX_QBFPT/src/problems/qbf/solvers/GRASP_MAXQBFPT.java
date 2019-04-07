/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.qbf.solvers;

import java.io.IOException;

/**
 *
 * @author jonatas
 */
public class GRASP_MAXQBFPT extends GRASP_QBF {

    public GRASP_MAXQBFPT(Double alpha, Boolean firstImproving, Integer tempoExecucao, Integer iteraConvengencia, String filename) throws IOException {
        super(alpha, firstImproving, tempoExecucao, iteraConvengencia, filename);
    }

}
