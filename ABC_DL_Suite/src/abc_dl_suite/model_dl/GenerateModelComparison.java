/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.model_dl;

import abc_dl_suite.project_information.ProjectInformation;
import data_transformation.InputDataTransformation_Standardize;
import network.NetworkGenerator;
import encog.noise.NoiseInjectionObservedSFS;
import java.io.File;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.obj.SerializeObject;


/**
 *
 * @author Oscar Lao
 */
public class GenerateModelComparison {

    /**
     * Generate the model comparisons
     * @param error_threshold the minimum error in the classification that we allow
     * @param rep the id of the network
     * @param pi the project information
     * @param neuronsIntermediateLayers the number of neurons in the intermediate layers
     * @param max_time_running the maximum amount of time you can train this neural network
     * @throws Exception if something goes wrong.
     */
    public static void generateModelComparison(double error_threshold, int rep, ProjectInformation pi, int neuronsIntermediateLayers, double max_time_running) throws Exception {

        long startTime = System.currentTimeMillis();
       
        // Now run the Network
        NetworkGenerator network = new NetworkGenerator();
        
        RetrieveModelTrainingData mt = new RetrieveModelTrainingData(pi);
        
        double [][] sfs_training = mt.getSfs_training();
        double [][] output_training = mt.getOutput_training();        
        
        NoiseInjectionObservedSFS nif = mt.getNif();
        
        InputDataTransformation_Standardize idt = mt.getStdT();
        
        BasicNetwork bn = network.runANNGenerateModelComparisonNoiseInjection(error_threshold, startTime, sfs_training, output_training, null, null, neuronsIntermediateLayers, 10000, nif, idt, max_time_running);

        SerializeObject.save(new File(pi.getWorking_folder() + File.separator + "model" + File.separator + "ABC_Models_comparison_10000_ThreeLayers" + neuronsIntermediateLayers + "_" + rep + ".network"), bn);

        System.exit(0);
    }
}
