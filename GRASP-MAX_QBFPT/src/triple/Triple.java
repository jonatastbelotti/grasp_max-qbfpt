package triple;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 * @author Felipe de Carvalho Pereira [felipe.pereira@students.ic.unicamp.br]
 */
public class Triple {

    public final ArrayList<TripleElement> elements;

    public Triple(TripleElement te1, TripleElement te2, TripleElement te3) {
        this.elements = new ArrayList<TripleElement>(Arrays.asList(te1, te2, te3));
    }

    public ArrayList<TripleElement> getElements() {
        return elements;
    }

    public int elementosEmUso() {
        int resp = 0;

        for (TripleElement e : elements) {
            if (e.getSelected()) {
                resp++;
            }
        }

        return resp;
    }

    public void printTriple() {
        System.out.print("[" + elements.get(0).getIndex() + ", ");
        System.out.print(elements.get(1).getIndex() + ", ");
        System.out.println(elements.get(2).getIndex() + "]");
    }
}
