package main;

import java.io.IOException;
import problems.qbf.solvers.GRASP_MAXQBFPT;
import solutions.Solution;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class Main {

    public static final double ALPHA = 0.2;
    public static final boolean FIRST_IMPROVING = false;
    public static final int TEMPO_EXEC = 30; // Em minutos
    public static final int ITERACOES_CONVERGENCIA = -1; // Iterações sem melhora para encerrar execução, números negativos para considerar apenas o tempo

    public static final String[] listaArquivos = new String[]{
        "instances/qbf020",
        "instances/qbf040",
        "instances/qbf060",
        "instances/qbf080",
        "instances/qbf100",
        "instances/qbf200",
        "instances/qbf400"
    };

    public static void main(String[] args) throws IOException {
        long tempTotalInicial = System.currentTimeMillis();

        // Passando por todos os arquivos
        for (String arquivo : listaArquivos) {
            // Imprimindo as configurações do GRASP para cada arquivo
            System.out.println("Executando GRASP para o arquivo: " + arquivo);
            System.out.println("Configuração:");
            System.out.printf(" ALPHA = %.2f\n", ALPHA);
            System.out.println(" FIRST_IMPROVING = " + FIRST_IMPROVING);

            // Executando GRASP
            System.out.println("Execução:");

            long tempoInicial = System.currentTimeMillis();

            GRASP_MAXQBFPT grasp = new GRASP_MAXQBFPT(ALPHA, FIRST_IMPROVING, TEMPO_EXEC, ITERACOES_CONVERGENCIA, arquivo);
            Solution<Integer> melhorSolucao = grasp.solve();
            System.out.println(" maxVal = " + melhorSolucao);

            long tempoFinal = System.currentTimeMillis();
            long tempoTotal = tempoFinal - tempoInicial;
            System.out.println("Tempo = " + (double) tempoTotal / (double) 1000 + " seg");

            // Ao final imprime duas linhas em branco
            System.out.println("\n");
        }

        // Calculando tempo total de todas as execuções
        long tempTotalFinal = System.currentTimeMillis() - tempTotalInicial;

        System.out.println("\nTempo execução todos arquivos: " + (tempTotalFinal / 1000D) + "seg");

    }

}
