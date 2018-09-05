/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.parameter_dl;

import abc_deeplearning.model.Load_Model_Data;
import abc_dl_suite.project_information.ProjectInformation;
import data_management.DataManagement;
import data_management.PruneNotVariableVariables;
import data_management.RetrieveFragmentList;
import data_management.RetrieveObservedData;
import data_transformation.InputDataTransformation_Standardize;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.data.FragmentList;
import fastsimcoal2.parameter.ParameterDistribution;
import java.io.File;
import java.util.ArrayList;
import network.NetworkLoader;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.obj.SerializeObject;
import plink.read.ReadBEDFile;
import write.WriteFile;
import suite.parameter.ParameterTrainingData;

/**
 * Class to predict the parameters in the observed data
 * @author Oscar Lao
 */
public class ComputeObservedParameters {

    /**
     * Compute the observed parameters
     * @param project the project information
     * @param model_to_run the model where the parameters are going to be estimated
     * @throws Exception 
     */
    public static void computeObservedParameters(ProjectInformation project, int model_to_run) throws Exception {
        
        String folder_data = project.getWorking_folder() + File.separator;
  
        Load_Model_Data lm = project.getLm();     
        
        FastSimcoalModel bmodel = lm.getBmodel()[model_to_run];

        String folder_network = File.separator + "scratch" + File.separator + "devel" + File.separator + "olao" + File.separator + "jaume" + File.separator + "network" + File.separator + "parameters" + File.separator + "unknown";

        String[] individuals = project.getIndividuals_Training();
        String[] reference = {"Ancestral"};        
        
        ParameterTrainingData ptd = new ParameterTrainingData(individuals, reference, folder_data);
        
        bmodel.defineModel();        
        
        ptd.load(bmodel, 10000);
        
        WriteFile wf = new WriteFile(folder_network + "/" +  bmodel.modelName() + "/" + bmodel.modelName()+"_observed_for_abc.txt");

        PruneNotVariableVariables pnvv = ptd.getPnvv();

        InputDataTransformation_Standardize standardize_t = ptd.getIs();

// Retrieve Observed data NOT USED for training

        String[] individualNames = project.getIndividuals_replication();

        String fileWithFragments = folder_data + "masked_regions.txt";

        FragmentList[] fr = RetrieveFragmentList.retrieveTheFragments(fileWithFragments);
        
        String plink = folder_data + "Hominin_ANC";
        
        RetrieveObservedData ro = new RetrieveObservedData(new ReadBEDFile(plink), fr);

        int [] observed_sfs = ro.getSFS(individualNames, reference);
        int not_excluded = ro.getIncluded_SNPs();
        int excluded = ro.getExcluded_SNPs();
        double [] f = new double[observed_sfs.length];
        for (int e = 0; e < observed_sfs.length; e++) {
            f[e] += observed_sfs[e] + (excluded * observed_sfs[e] / (double) not_excluded);
        }
// compute the folded        
        f = DataManagement.getSFSTriplets(individualNames.length, f); 
// Prune
        f = pnvv.prune(f); 
// Standardize
        f = standardize_t.transform(f);
        ArrayList<ParameterDistribution> parameters = bmodel.getListParameters();
        wf.println(bmodel.getLabelOfParameters());
            for (ParameterDistribution parameter : parameters) {
                NetworkLoader nc = new NetworkLoader((BasicNetwork) SerializeObject.load(new File(folder_network +File.separator + bmodel.modelName() + File.separator + parameter.getName() + ".network")));
                wf.print(" " + nc.predict(f)[0] + " ");
            }
        wf.println("");
        wf.close();
    }
}
