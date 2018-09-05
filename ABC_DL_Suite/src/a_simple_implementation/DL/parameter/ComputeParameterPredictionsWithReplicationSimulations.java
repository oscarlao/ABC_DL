/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.DL.parameter;

import a_simple_implementation.suite.ProjectInformationOfThisImplementation;
import abc_dl_suite.parameter_dl.PredictParameters_DL;

/**
 *
 * @author olao
 */
public class ComputeParameterPredictionsWithReplicationSimulations {

    public static void main(String[] args) throws Exception {
        PredictParameters_DL.predictABCParameters(ProjectInformationOfThisImplementation.getProjectInformation(), 1, 1);
    }
}
