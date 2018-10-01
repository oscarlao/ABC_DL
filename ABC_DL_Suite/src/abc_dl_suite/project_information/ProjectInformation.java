/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.project_information;

import abc_deeplearning.model.Load_Model_Data;
import simulations.os.OS;

/**
 * This is a class that contains all the information required to run the ABC-DL approach.
 * @author olao@crg.es
 */
public class ProjectInformation {
/**
 * All the information of the project required to run the ABC-DL approach
 * @param plink_file_with_observed_data the plink file with the observed data
 * @param lm the models that are considered
 * @param working_folder for example File.separator + "scratch" + File.separator + "devel" + File.separator + "olao" + File.separator + "jaume" + File.separator + "abcSimulations" + File.separator
 * @param name_of_fastSimcoal name of the fastSimcoal executable file, which has been copied to each of the fastSimcoalx folders. for example fsc26
 * @param number_of_fastSimcoal2_folders the number of fastSimcoal2 folders within the working_folder where we can generate simulations in parallel. Each fastSimcoal2 folder is named as fastSimcoal followed by a number that goes from 0 to (number_of_fastSimcoal2_folders-1)
 * @param os the operative system that we are considering. It can be LINUX or WINDOWS
 * @param number_of_simulations_by_model_and_fastSimcoal2_folder the number of simulations to be run in each model at each fastSimcoal2 folder
 * @param percentage_of_training the percentage of simulations from each model that are going to be considered as training
 * @param individuals_training names of the individuals from the real data for training. They will use in the noise injection algorithm
 * @param individuals_replication names of the individuals for replication. They will use to perform the final ABC-DL analyses (model prediction, parameter estimation)
 * @param pops_to_retrieve the populations that we are going to use for some analyses.
 * @throws Exception if something goes wrong.
 */
    
    public ProjectInformation(String plink_file_with_observed_data, Load_Model_Data lm, String working_folder, String name_of_fastSimcoal, int number_of_fastSimcoal2_folders, OS os, int number_of_simulations_by_model_and_fastSimcoal2_folder, double percentage_of_training, String [] individuals_training, String [] individuals_replication, int [] pops_to_retrieve) throws Exception
    {
        this.plink_file_with_observed_data = plink_file_with_observed_data;
        this.lm = lm;
        this.individuals_training = individuals_training;
        this.working_folder = working_folder;
        this.name_of_fastSimcoal = name_of_fastSimcoal;
        this.number_of_fastSimcoal2_folders = number_of_fastSimcoal2_folders;
        this.os = os;
        this.number_of_simulations_by_model_and_fastSimcoal2_folder = number_of_simulations_by_model_and_fastSimcoal2_folder;
        this.individuals_replication = individuals_replication;
        this.pops_to_retrieve = pops_to_retrieve;
        this.percentage_of_training = percentage_of_training;
    }   
    // The plink file
    private final String plink_file_with_observed_data;
    // The models of this project
    private final Load_Model_Data lm;
    // The name of the individuals in the real data to do the training
    private final String [] individuals_training;
    // The working folder where fastSimcoal2 and Plink data is stored
    private final String working_folder, name_of_fastSimcoal;
    // The individuals to do the replication in the real data
    private final String [] individuals_replication;
    // Populations to be used
    private final int[] pops_to_retrieve;
    // number of fastSimcoal2 folders to be used
    private final int number_of_fastSimcoal2_folders, number_of_simulations_by_model_and_fastSimcoal2_folder;
    // Operative system to run FastSimcoal2
    private final OS os;
    // The percentage of training
    private final double percentage_of_training;

    /**
     * Get the proportion of simulations that are going to be used as training in the DL
     * @return 
     */
    public double getPercentage_of_training() {
        return percentage_of_training;
    }

    
    
    /**
     * Get the models
     * @return the list of models
     */
    public Load_Model_Data getLm() {
        return lm;
    }
    
    /**
     * Get the individuals from the observed data for noise injection
     * @return the training individuals
     */
    public String[] getIndividuals_Training() {
        return individuals_training;
    }

    /**
     * Get the individuals from the observed data for the replication
     * @return the replication individuals
     */
    public String[] getIndividuals_replication() {
        return individuals_replication;
    }

    /**
     * Working folder where all the data and fastSimcoal2 folders is available
     * @return the folder that contains the info of the project
     */
    public String getWorking_folder() {
        return working_folder;
    }

    /**
     * get the ids of the populations to retrieve
     * @return the populations that we are going to use
     */
    public int[] getPops_to_retrieve() {
        return pops_to_retrieve;
    }

    /**
     * Get the number of fastSimcoal2 folders that are available in the working directory
     * @return the number of fastSimcoal2 folders
     */
    public int getNumber_of_fastSimcoal2_folders() {
        return number_of_fastSimcoal2_folders;
    }

    /**
     * Get the operative System to run fastSimcoal2
     * @return the operative system
     */
    public OS getOs() {
        return os;
    }

    /**
     * Get the name of fastSimcoal executable. For example, fsc26
     * @return the name of the fastSimcoal2 executable
     */
    public String getName_of_fastSimcoal() {
        return name_of_fastSimcoal;
    }

    /**
     * Get the number of simulations to be run at each model and fastSimcoal2 folder
     * @return the number of simulations by model and fastSimcoal2 folder
     */
    public int getNumber_of_simulations_by_model_and_fastSimcoal2_folder() {
        return number_of_simulations_by_model_and_fastSimcoal2_folder;
    }

    /**
     * Get the plink file with the observed data
     * @return the name of the plink file with the observed data
     */
    public String getPlink_file_with_observed_data() {
        return plink_file_with_observed_data;
    }
    
    
}
