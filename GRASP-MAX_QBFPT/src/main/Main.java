package main;

import java.io.IOException;
import problems.qbf.solvers.GRASP_MAXQBFPT;
import solutions.Solution;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class Main {

    public static final double ALPHA = 0.05;
    public static final int TIPO_CONSTRUCAO = GRASP_MAXQBFPT.CONSTRUCAO_PADRAO;
    public static final boolean FIRST_IMPROVING = false;
    public static final int TEMPO_EXEC = 30; // Em minutos
    public static final int ITERACOES_CONVERGENCIA = 1000; // Iterações sem melhora para encerrar execução, números negativos para considerar apenas o tempo

    public static final String[] LISTA_ARQUIVOS = new String[]{
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
        for (String arquivo : LISTA_ARQUIVOS) {
            // Imprimindo as configurações do GRASP para cada arquivo
            System.out.println("Executando GRASP para o arquivo: " + arquivo);
            System.out.println("Configuração:");
            imprimirTipoConstrucao();
            imprimirAlpha();
            imprimirEstrategiaBusca();
            imprimirCriterioParada();

            // Executando GRASP
            System.out.println("Execução:");

            long tempoInicial = System.currentTimeMillis();

            GRASP_MAXQBFPT grasp = new GRASP_MAXQBFPT(ALPHA, TIPO_CONSTRUCAO, FIRST_IMPROVING, TEMPO_EXEC, ITERACOES_CONVERGENCIA, arquivo);
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

    private static void imprimirTipoConstrucao() {
        String resp = " Tipo construção = ";
        
        if (TIPO_CONSTRUCAO == GRASP_MAXQBFPT.CONSTRUCAO_PADRAO) {
            resp += "Construção padrão";
        }
        if (TIPO_CONSTRUCAO == GRASP_MAXQBFPT.CONSTRUCAO_REATIVA) {
            resp += "Construção reativa";
        }
        
        
        System.out.println(resp);
    }

    private static void imprimirAlpha() {
        if (TIPO_CONSTRUCAO == GRASP_MAXQBFPT.CONSTRUCAO_PADRAO) {
            System.out.println(" Alpha = " + ALPHA);
        }
    }

    private static void imprimirEstrategiaBusca() {
        String resp = " Busca local = ";
        
        if (FIRST_IMPROVING) {
            resp += "First Improving";
        } else {
            resp += "Best Improving";
        }
        
        System.out.println(resp);
    }

    private static void imprimirCriterioParada() {
        String resp = " Critério de parada = ";
        
        resp += TEMPO_EXEC + " minutos";
        
        if (ITERACOES_CONVERGENCIA > 0) {
            resp += " ou " + ITERACOES_CONVERGENCIA + " iterações sem melhoria";
        }
        
        System.out.println(resp);
    }

}
