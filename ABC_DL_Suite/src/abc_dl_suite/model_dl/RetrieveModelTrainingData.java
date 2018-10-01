/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.model_dl;

import abc_deeplearning.model.Load_Model_Data;
import abc_dl_suite.project_information.ProjectInformation;
import data_management.DataManagement;
import data_management.RetrieveDataByRow;
import data_management.RetrieveFragmentList;
import data_management.RetrieveObservedData;
import data_transformation.InputDataTransformation_Standardize;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.data.FragmentList;
import java.io.File;
import encog.noise.NoiseInjectionObservedSFS;
import java.util.ArrayList;
import java.util.Random;
import plink.read.ReadBEDFile;
import util.NoiseRange;
import util.Parameters_SummaryStatistics;

/**
 *
 * @author olao@crg.es
 */
public class RetrieveModelTrainingData {
    public RetrieveModelTrainingData(ProjectInformation pi) throws Exception
    {
        load(pi);                
    }
    
    private InputDataTransformation_Standardize stdT;
    private ArrayList<Integer> variable_columns = new ArrayList<>();
    
    private double[][] sfs_training;
    private double[][] output_training;
    private NoiseInjectionObservedSFS nif;

    public NoiseInjectionObservedSFS getNif() {
        return nif;
    }    
    
    public double[][] getSfs_training() {
        return sfs_training;
    }

    public double[][] getOutput_training() {
        return output_training;
    }
    
    /**
     * Get the input data transformation that was used for this model training
     * @return the object to standardize the input data
     */
    public InputDataTransformation_Standardize getStdT() {
        return stdT;
    }
    
    /**
     * Load everything using the project information
     * @param pi the ProjectInformation associated to this project
     * @throws Exception if something goes wrong
     */
    protected final void load(ProjectInformation pi) throws Exception
    {
        Load_Model_Data lm = pi.getLm();
        String [] individuals = pi.getIndividuals_Training();
        int [] pops_to_retrieve = pi.getPops_to_retrieve();
// Observed data          
        String folder = pi.getWorking_folder();        

// Simulations
      
        FastSimcoalModel [] bmodels = lm.getBmodel();

        int training_samples = (int)(pi.getPercentage_of_training()*pi.getNumber_of_fastSimcoal2_folders()*pi.getNumber_of_simulations_by_model_and_fastSimcoal2_folder());
        
        sfs_training = new double[training_samples * bmodels.length][];
        output_training = new double[training_samples * bmodels.length][bmodels.length];        

        Random r = new Random();
        
        int count_training = 0;
        for(int i = 0; i<bmodels.length;i++)
        {
            FastSimcoalModel m = bmodels[i];
            RetrieveDataByRow rdb = new RetrieveDataByRow(new File(folder + "output_"+m.modelName()+"_training.txt"));
            for(int row=0;row<training_samples;row++)
            {
                Parameters_SummaryStatistics pss = rdb.next();
                sfs_training[count_training] = DataManagement.extract_populations(individuals.length, pops_to_retrieve, pss.getSummaryStatistics());
                for(int e=0;e<sfs_training[count_training].length;e++)
                {
                    sfs_training[count_training][e] += r.nextGaussian()*0.0001;
                }
                output_training[count_training][i] = 1;
                count_training++;
            }
        }
//         
        stdT = new InputDataTransformation_Standardize(individuals.length);
// load the mean and std from the training data. BUT DO NOT MAKE ANY CHANGE TO THE DATA ITSELF!                
        stdT.load(sfs_training);    
        
// Observed data        
        String fileWithFragments = folder + "masked_regions.txt";

        FragmentList[] fr = RetrieveFragmentList.retrieveTheFragments(fileWithFragments);       
        
        String plink = pi.getWorking_folder() + File.separator + pi.getPlink_file_with_observed_data();
        RetrieveObservedData ro = new RetrieveObservedData(new ReadBEDFile(plink), fr);
        String[] reference = {"Ancestral"};

        int[] observed_sfs = ro.getSFS(individuals, reference);
        int not_excluded = ro.getIncluded_SNPs();
        int excluded = ro.getExcluded_SNPs();
        double [] sfs_observed = new double[observed_sfs.length];
        for (int e = 0; e < observed_sfs.length; e++) {
            sfs_observed[e] = observed_sfs[e] + (excluded * observed_sfs[e] / (double) not_excluded);
        }
        
        sfs_observed = DataManagement.extract_populations(individuals.length, pops_to_retrieve, sfs_observed);           
        
        nif = new NoiseInjectionObservedSFS(new NoiseRange(0.0,0.2), sfs_observed);                
    }    
}
