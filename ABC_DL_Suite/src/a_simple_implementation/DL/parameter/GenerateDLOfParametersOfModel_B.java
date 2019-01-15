/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.DL.parameter;

import a_simple_implementation.suite.ProjectInformationOfThisImplementation;
import abc_dl_suite.parameter_dl.TrainNetworkOfParameter;

/**
 *
 * @author olao
 */
public class GenerateDLOfParametersOfModel_B {
    public static void main(String [] args) throws Exception
    {
        TrainNetworkOfParameter.trainNetworkOfParameterFromModel(ProjectInformationOfThisImplementation.getProjectInformation(), 1, 0, 11, 200, 0.025, 0.5);        
    }    
}
