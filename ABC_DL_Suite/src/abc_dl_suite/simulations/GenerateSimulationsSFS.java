/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.simulations;

import abc_deeplearning.model.Load_Model_Data;
import abc_dl_suite.project_information.ProjectInformation;
import fastsimcoal2.FastSimcoalModel;
import java.io.File;
import simulations.RunModelSimulationsSFS;

/**
 * Class to generate the simulations of the SFS
 * @author Oscar Lao
 */
public class GenerateSimulationsSFS {

    /**
     * Run the simulations for the model using the project information
     * @param project
     * @param number a number that codes which folder is going to be used and which model is going to be used. The fastSimcoal_folder is defined by number/number_of_models and the model to run by number%number_of_models
     * @throws Exception if something goes wrong
     */
    public static void runSimulations(ProjectInformation project, int number) throws Exception {
// Get the models
        Load_Model_Data lm = project.getLm();
// Which is the fastSimcoal to run the model?
        int fastSimcoal_folder = number / lm.getBmodel().length;
// Which is the model to run?        
        int model_to_run = number % lm.getBmodel().length;

        FastSimcoalModel bmodel = lm.getBmodel()[model_to_run];
        
        String fileWithFragments = project.getWorking_folder() + File.separator + "masked_regions.txt";

        String folder = project.getWorking_folder() + File.separator + "fastSimcoal" + fastSimcoal_folder + File.separator;

        RunModelSimulationsSFS rm = new RunModelSimulationsSFS(fileWithFragments, folder, project.getName_of_fastSimcoal(), project.getOs());

        rm.run_Model(bmodel, project.getNumber_of_simulations_by_model_and_fastSimcoal2_folder());
    }
}
