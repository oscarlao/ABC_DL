/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.parameter_dl;

import abc_deeplearning.model.Load_Model_Data;
import abc_dl_suite.project_information.ProjectInformation;
import fastsimcoal2.FastSimcoalModel;
import java.io.File;
import network.NetworkGeneratorParameter;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.obj.SerializeObject;

/**
 *
 * @author olao@crg.es
 */
public class TrainNetworkOfParameter {


    /**
     * Train a network of a parameter of a model.
     * @param project the project information
     * @param model_to_run the model that we want to run
     * @param replica the replica (or id) of this NN
     * @param par the parameter in the model_to_run that we want to run
     * @param number_of_intermediate_neurons the number of neurons in intermediate layers
     * @param error_threshold The minimum error to stop the training
     * @throws Exception if something goes wrong.
     */
    public static void trainNetworkOfParameterFromModel(ProjectInformation project, int model_to_run, int replica, int par, int number_of_intermediate_neurons, double error_threshold) throws Exception {
        Load_Model_Data lm = project.getLm();
        FastSimcoalModel bmodel = lm.getBmodel()[model_to_run];
        bmodel.defineModel();
        System.out.println("Running parameter " + bmodel.getListParameters().get(par).getName());        
        long startTime = System.currentTimeMillis();
        String folder_network = project.getWorking_folder() + File.separator + "parameter" + File.separator;
        ParameterTrainingData ptd = new ParameterTrainingData();
        ptd.load(project, model_to_run, par);
        double[][] sfs_training = ptd.getSfs_training();
        double[][] output_training = ptd.getOutput_training();
        double[][] output_training_kk = new double[output_training.length][1];
        for (int r = 0; r < output_training.length; r++) {
            output_training_kk[r][0] = output_training[r][0];
        }

        NetworkGeneratorParameter network = new NetworkGeneratorParameter();

// Run the network
        BasicNetwork bn = network.runParameterWithInjection(error_threshold, startTime, sfs_training, output_training_kk, number_of_intermediate_neurons, 10000, ptd.getNif(), ptd.getIs(), 0.50);

        SerializeObject.save(new File(folder_network + bmodel.modelName() + File.separator + bmodel.getListParameters().get(par).getName() + "_" + replica + ".network"), bn);
    }
}
