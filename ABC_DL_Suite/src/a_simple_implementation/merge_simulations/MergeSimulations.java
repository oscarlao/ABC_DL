/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.merge_simulations;

import a_simple_implementation.suite.ProjectInformationOfThisImplementation;
import abc_dl_suite.handleOutputFiles.GenerateASingleFileWithSimulationsOfAModel;

/**
 *
 * @author olao
 */
public class MergeSimulations {
    public static void main(String [] args) throws Exception
    {
        args = new String[1];
        args[0] = "1";
        GenerateASingleFileWithSimulationsOfAModel.generateASingleFileOfModel(ProjectInformationOfThisImplementation.getProjectInformation(),Integer.parseInt(args[0]),0.5);
    }    
}
