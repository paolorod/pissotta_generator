/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pissottagenerator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author paolo
 */
public class SequenceGenerator {
    
    
    private final int numSimbols;
    private final LinkedList<Integer> simbolList;

    public SequenceGenerator(int numSimbols) {
        this.numSimbols = numSimbols;
        this.simbolList = new LinkedList();
        for(int i=0;i<numSimbols;i++) {
            simbolList.add(i);
        }
    }

    
    
    /**
     * Genera una sequenza lunga length di numeri compresi tra 0 e numSimbols-1, senza ripetizioni e ordinata
     * @param length
     * @return 
     */
    public Integer[] generateSequence(int length) {
        Collections.shuffle(simbolList); 
        LinkedList<Integer> sequence = new LinkedList<Integer>();
        for(int i=0; i<length; i++) {
            sequence.addLast(simbolList.get(i));  
        }
        Collections.sort(sequence);
        System.out.println(printSequence(sequence));
        return sequence.toArray(new Integer[sequence.size()]);
    }
    
         
    private String printSequence(List<Integer> sequence) {
        StringBuilder builder = new StringBuilder();
        for(Integer i : sequence) builder.append(i).append(" ");
        return builder.toString();
    }
    
}
