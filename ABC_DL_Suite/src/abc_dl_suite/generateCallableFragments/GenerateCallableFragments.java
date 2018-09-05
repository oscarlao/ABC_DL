/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.generateCallableFragments;

import data_masking.CheckCallableFragmentsV2;
import fastsimcoal2.data.FragmentsWithinFragment;
import java.util.ArrayList;
import write.WriteFile;

/**
 *
 * @author Oscar Lao
 */
public class GenerateCallableFragments {

    public static void generateFragments(String file_with_callable_no_genes_no_cpg_islands, String output_file_to_store_fragments, int withinFragmentDistance, int amongFragmentDistance, int maxFragmentSize) throws Exception {
        
        WriteFile wf = new WriteFile(output_file_to_store_fragments);
        
        CheckCallableFragmentsV2 callable = new CheckCallableFragmentsV2(file_with_callable_no_genes_no_cpg_islands);
        
        ArrayList<FragmentsWithinFragment> ff = callable.getFragments(withinFragmentDistance, amongFragmentDistance, maxFragmentSize);      
        
        ff.forEach((f) -> {
            wf.println(f);
        });
        wf.close();
    }
}
