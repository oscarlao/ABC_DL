/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.DL.model;

import a_simple_implementation.suite.ProjectInformationOfThisImplementation;
import abc_dl_suite.model_dl.ComputeModelPredictionInReplicationSimulations;

/**
 *
 * @author olao
 */
public class ComputeModelPredictionsWithReplicationSimulations {
    public static void main(String [] args) throws Exception
    {
        ComputeModelPredictionInReplicationSimulations.predictModelPosteriorReplicationSimulations(ProjectInformationOfThisImplementation.getProjectInformation());
    }
}
