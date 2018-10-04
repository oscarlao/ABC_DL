/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.parameter_dl;

import abc_dl_suite.project_information.ProjectInformation;
import data_management.PruneNotVariableVariables;
import data_management.RetrieveFragmentList;
import data_management.RetrieveObservedData;
import data_management.RetrieveSimulatedDataLargeDataset;
import data_transformation.InputDataTransformation_ByColumn;
import data_transformation.InputDataTransformation_Standardize;
import encog.noise.NoiseInjectionObservedSFS;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.data.FragmentList;
import java.io.File;
import java.util.Random;
import plink.read.ReadBEDFile;
import util.NoiseRange;

/**
 *
 * @author olao
 */
public class ParameterTrainingData {

    /**
     * Create a new parameter training data object.
     * @throws Exception when something goes wrong.
     */
    public ParameterTrainingData() throws Exception {
    }

    private InputDataTransformation_ByColumn bc;
    private InputDataTransformation_Standardize stdT;
    private PruneNotVariableVariables pnvv;
    private NoiseInjectionObservedSFS nif;
    private double[][] sfs_training;
    private double[][] output_training;

    public InputDataTransformation_ByColumn getBc() {
        return bc;
    }

    public double[][] getSfs_training() {
        return sfs_training;
    }

    public double[][] getOutput_training() {
        return output_training;
    }

    public NoiseInjectionObservedSFS getNif() {
        return nif;
    }

    public InputDataTransformation_Standardize getIs() {
        return stdT;
    }

    public PruneNotVariableVariables getPnvv() {
        return pnvv;
    }


    /**
     * Load the information for the model and parameter
     * @param pi the project information
     * @param model the model id according to the position of list in LoadModel
     * @param param the parameter of the model
     * @throws Exception if something goes wrong
     */
    public void load(ProjectInformation pi, int model, int param) throws Exception {
        Random r = new Random();
        String[] reference = {"Ancestral"};        
        File[] files = new File[1];
        FastSimcoalModel bmodel = pi.getLm().getBmodel()[model];
        files[0] = new File(pi.getWorking_folder() + "output_" + bmodel.modelName() + "_training.txt");
        RetrieveSimulatedDataLargeDataset rmodels = new RetrieveSimulatedDataLargeDataset(files, 0);     
        double[][] sfs = rmodels.getSfs();
// parameters are transformed to be between 0 and 1
        double[][] params = rmodels.getParams();
        double[][] params_selected = new double[params.length][1];
        for (int row = 0; row < params.length; row++) {
            params_selected[row][0] = params[row][param];
            for(int e=0;e<sfs[0].length;e++)
            {
                sfs[row][e] = sfs[row][e] + r.nextGaussian()*0.00001;
            }
        }        
        bc = new InputDataTransformation_ByColumn(-1);
        params = bc.transform(params_selected);
// Remove the sfs variables that are fixed        
        pnvv = new PruneNotVariableVariables(sfs);
        sfs = pnvv.prune(sfs);
        sfs_training = new double[params.length][sfs[0].length];
        output_training = new double[params.length][params[0].length];

        for (int row = 0; row < params.length; row++) {
            sfs_training[row] = sfs[row];
            output_training[row] = params[row];
        }

        stdT = new InputDataTransformation_Standardize(-1);

        stdT.load(sfs);
// Observed data    
        String fileWithFragments = pi.getWorking_folder() + File.separator + "masked_regions.txt";

        FragmentList[] fr = RetrieveFragmentList.retrieveTheFragments(fileWithFragments);
        String plink = pi.getWorking_folder() +File.separator + pi.getPlink_file_with_observed_data() ;
        RetrieveObservedData ro = new RetrieveObservedData(new ReadBEDFile(plink), fr);

        int[] observed_sfs = ro.getSFS(pi.getIndividuals_Training(), reference);
        int not_excluded = ro.getIncluded_SNPs();
        int excluded = ro.getExcluded_SNPs();
        double[] sfs_observed = new double[observed_sfs.length];
        for (int e = 0; e < observed_sfs.length; e++) {
            sfs_observed[e] = observed_sfs[e] + (excluded * observed_sfs[e] / (double) not_excluded);
        }
// Remove the sfs variables that are fixed in the folded       
        sfs_observed = pnvv.prune(sfs_observed);
// Compute the noise injection        
        nif = new NoiseInjectionObservedSFS(new NoiseRange(0.0, 0.2), sfs_observed);
    }
    
    /**
     * Load the information for the model and parameter
     * @param pi the project information
     * @param model the model
     * @throws Exception if something goes wrong
     */
    public void load(ProjectInformation pi, int model) throws Exception {
        Random r = new Random();
        String[] reference = {"Ancestral"};        
        File[] files = new File[1];
        FastSimcoalModel bmodel = pi.getLm().getBmodel()[model];
        files[0] = new File(pi.getWorking_folder() + "output_" + bmodel.modelName() + "_training.txt");
        RetrieveSimulatedDataLargeDataset rmodels = new RetrieveSimulatedDataLargeDataset(files, 0);     
        double[][] sfs = rmodels.getSfs();
// parameters are transformed to be between 0 and 1
        double[][] params_selected = rmodels.getParams();
        for (int row = 0; row < params_selected.length; row++) {
            for(int e=0;e<sfs[0].length;e++)
            {
                sfs[row][e] = sfs[row][e] + r.nextGaussian()*0.00001;
            }
        }        
        bc = new InputDataTransformation_ByColumn(-1);
        params_selected = bc.transform(params_selected);
// Remove the sfs variables that are fixed        
        pnvv = new PruneNotVariableVariables(sfs);
        sfs = pnvv.prune(sfs);
        sfs_training = new double[params_selected.length][sfs[0].length];
        output_training = new double[params_selected.length][params_selected[0].length];

        for (int row = 0; row < params_selected.length; row++) {
            sfs_training[row] = sfs[row];
            output_training[row] = params_selected[row];
        }

        stdT = new InputDataTransformation_Standardize(-1);

        stdT.load(sfs);
// Observed data    
        String fileWithFragments = pi.getWorking_folder() + File.separator + "masked_regions.txt";

        FragmentList[] fr = RetrieveFragmentList.retrieveTheFragments(fileWithFragments);
        String plink = pi.getWorking_folder() +File.separator + pi.getPlink_file_with_observed_data() ;
        RetrieveObservedData ro = new RetrieveObservedData(new ReadBEDFile(plink), fr);

        int[] observed_sfs = ro.getSFS(pi.getIndividuals_Training(), reference);
        int not_excluded = ro.getIncluded_SNPs();
        int excluded = ro.getExcluded_SNPs();
        double[] sfs_observed = new double[observed_sfs.length];
        for (int e = 0; e < observed_sfs.length; e++) {
            sfs_observed[e] = observed_sfs[e] + (excluded * observed_sfs[e] / (double) not_excluded);
        }
// Remove the sfs variables that are fixed in the folded       
        sfs_observed = pnvv.prune(sfs_observed);
// Compute the noise injection        
        nif = new NoiseInjectionObservedSFS(new NoiseRange(0.0, 0.2), sfs_observed);
    }    
}
