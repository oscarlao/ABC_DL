/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.parameter_dl;

import abc_dl_suite.project_information.ProjectInformation;
import data_management.DataManagement;
import data_management.PruneNotVariableVariables;
import data_management.RetrieveDataByRow;
import data_management.RetrieveFragmentList;
import data_management.RetrieveObservedData;
import data_transformation.InputDataTransformation_ByColumn;
import data_transformation.InputDataTransformation_Standardize;
import encog.noise.NoiseInjectionObservedSFS;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.data.FragmentList;
import java.io.File;
import java.util.Random;
import plink.read.ReadBEDFile;
import read.ReadFile;
import util.NoiseRange;
import util.Parameters_SummaryStatistics;

/**
 *
 * @author olao
 */
public class ParameterTrainingData {

    /**
     * Create a new parameter training data object.
     *
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
     *
     * @param pi the project information
     * @param model the model id according to the position of list in LoadModel
     * @param param the parameter of the model
     * @throws Exception if something goes wrong
     */
    public void load(ProjectInformation pi, int model, int param) throws Exception {
        String[] individuals = pi.getIndividuals_Training();
        int[] pops_to_retrieve = pi.getPops_to_retrieve();        
        Random r = new Random();
        String[] reference = {"Ancestral"};
        File[] files = new File[1];
        FastSimcoalModel bmodel = pi.getLm().getBmodel()[model];
        files[0] = new File(pi.getWorking_folder() + File.separator + "output_" + bmodel.modelName() + "_training.txt");

        ReadFile rf = new ReadFile(files[0]);
        
        rf.readRow();
        
        int rows = 0;
        while(rf.readRow()!=null) rows++;
        
        rf.close();
        
        int training_samples = rows;
        
        System.out.println("Using " + training_samples + " as training");       
        
        double[][] sfs = new double[training_samples][];
// parameters are transformed to be between 0 and 1
        double[][] params = new double[training_samples][];        
        
        RetrieveDataByRow rdb = new RetrieveDataByRow(files[0]);
        for (int row = 0; row < training_samples; row++) {
            try {
                Parameters_SummaryStatistics pss = rdb.next();
                sfs[row] = DataManagement.extract_populations(individuals.length, pops_to_retrieve, pss.getSummaryStatistics());
                for (int e = 0; e < sfs[row].length; e++) {
                    sfs[row][e] += r.nextGaussian() * 0.0001;
                }
                params[row] = pss.getParameters();
            } catch (Exception tok) {
                System.out.println("Problem reading line " + row + " in " + pi.getWorking_folder() + File.separator + "output_" + bmodel.modelName() + "_training.txt");
                throw tok;
            }
        }
        
        rdb.close();

        double[][] params_selected = new double[params.length][1];
        for (int row = 0; row < params.length; row++) {
            params_selected[row][0] = params[row][param];
            for (int e = 0; e < sfs[0].length; e++) {
                sfs[row][e] = sfs[row][e] + r.nextGaussian() * 0.00001;
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
        String plink = pi.getWorking_folder() + File.separator + pi.getPlink_file_with_observed_data();
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
     *
     * @param pi the project information
     * @param model the model
     * @throws Exception if something goes wrong
     */
    public void load(ProjectInformation pi, int model) throws Exception {
        String[] individuals = pi.getIndividuals_Training();
        int[] pops_to_retrieve = pi.getPops_to_retrieve();   
        Random r = new Random();
        String[] reference = {"Ancestral"};
        File[] files = new File[1];
        FastSimcoalModel bmodel = pi.getLm().getBmodel()[model];
        files[0] = new File(pi.getWorking_folder() + File.separator + "output_" + bmodel.modelName() + "_training.txt");
        ReadFile rf = new ReadFile(files[0]);
        
        rf.readRow();
        
        int rows = 0;
        while(rf.readRow()!=null) rows++;
        
        rf.close();
        
        int training_samples = (int)(rows*pi.getPercentage_of_training());
        
        System.out.println("Using " + training_samples + " as training");       
        
        double[][] sfs = new double[training_samples][];
// parameters are transformed to be between 0 and 1
        double[][] params_selected = new double[training_samples][];        
        
        RetrieveDataByRow rdb = new RetrieveDataByRow(files[0]);
        for (int row = 0; row < training_samples; row++) {
            try {
                Parameters_SummaryStatistics pss = rdb.next();
                sfs[row] = DataManagement.extract_populations(individuals.length, pops_to_retrieve, pss.getSummaryStatistics());
                for (int e = 0; e < sfs[row].length; e++) {
                    sfs[row][e] += r.nextGaussian() * 0.0001;
                }
                params_selected[row] = pss.getParameters();
            } catch (Exception tok) {
                System.out.println("Problem reading line " + row + " in " + pi.getWorking_folder() + File.separator + "output_" + bmodel.modelName() + "_training.txt");
                throw tok;
            }
        }
        
        rdb.close();
        
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
        String plink = pi.getWorking_folder() + File.separator + pi.getPlink_file_with_observed_data();
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
