/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pissottagenerator;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author paolo
 */
public class UniqueSequenceGenerator extends SequenceGenerator {
    
    HashSet<String> memoryMap;
    int CLASH_LIMIT = 10000;
    
    StringBuilder builder = new StringBuilder();
    
    public UniqueSequenceGenerator(int numSimbols) {
        super(numSimbols);
        memoryMap = new HashSet<String>();
    }
    
    @Override
     public Integer[] generateSequence(int length) {
       Integer[] seq = null;
       boolean unique_found=false; 
       int num_clash=0;
       while(!unique_found&&num_clash<CLASH_LIMIT){ 
        seq = super.generateSequence(length);
        for(int i:seq) {
           builder.append(i).append(":");
        }
        if(!memoryMap.add(builder.toString())) {
            System.out.println("Sequence already done - skipping");
            num_clash++;
        } else {
            unique_found = true;
        }
       };
       if(num_clash==CLASH_LIMIT) throw new IllegalStateException("Number of clush while looking for a new sequence reached - aborting");
       return seq;
     }
    
}
