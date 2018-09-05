/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.run_simulations;

import a_simple_implementation.suite.ProjectInformationOfThisImplementation;
import abc_dl_suite.simulations.GenerateSimulationsSFS;

/**
 *
 * @author olao
 */
public class RunSimulations {
    
    /**
     * Generate the simulations of a given model in a given folder.
     * @param args It only requires one argument, which is decomposed in two numbers using the total number of models:
     * fastSimcoal folder that is going to be used = args[0]/ number_of_models
     * model that is going to be generated = args[0] % number_of_models
     * for example, if we have two models and 4 folders fastSimcoal in our working directory, then args[0] can range between 0 and 7
     * @throws Exception 
     */
    public static void main(String [] args) throws Exception
    {        
        // Run the simulations.
        args = new String[1];
        args[0] = "8";
        GenerateSimulationsSFS.runSimulations(ProjectInformationOfThisImplementation.getProjectInformation(), Integer.parseInt(args[0]));
    }    
}
