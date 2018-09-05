///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package abc_dl_suite;
//
//import abc_dl_suite.model_dl.GenerateModelComparison;
//import abc_dl_suite.project_information.ProjectInformation;
//import abc_dl_suite.simulations.GenerateSimulationsSFS;
//
//
//
///**
// * This is the main class. From here, you can run all the steps to generate the output to run ABC
// * @author Oscar Lao
// */
//public class ABC_DL_Suite {
//
//    
//    /**
//     * 
//     * @param project
//     * @param thing_to_run 
//     */
//    public static void run(ProjectInformation project, int thing_to_run) {
//        int thing_to_run = Integer.parseInt(args[0]);        
//        switch(thing_to_run)
//        {
//            // Generate the simulations
//            case 1: GenerateSimulationsSFS.runSimulations(Integer.parseInt(args[1])); break; // Simulations
//            // Generate a single file out of the simulations
//            case 2: GenerateASingleFile.generateASingleFile(Integer.parseInt(args[1]));break; // Merge simulations from models
//            // Generate the DL for model comparison
//            case 3: GenerateModelComparison.generateModelComparison(Integer.parseInt(args[1]));break; // Networks for model comparison
//            // Compute the prediction in observed data
//            case 4: ComputeModelPosteriorObservedData.computeModelPosteriorObservedData(Integer.parseInt(args[1])); break;
//            // Generate the forded three pairwise population SFS
//            case 5: GenerateFoldedPairwiseSFS.generateFoldedPairwiseSFS(Integer.parseInt(args[1]));break;
//            // Estimate the parameters of Jaume
//            case 6: EstimateNetworkParameterJaume.estimateModelParametersIndependent(Integer.parseInt(args[1])); break;
//            // Compute the DL in the replication simulations
//            case 9: ComputeABCParameters.computeABCParameters(Integer.parseInt(args[1]));break;
//            // Compute the DL in the observed data
//            case 10: ComputeObservedParameters.computeObservedParameters(Integer.parseInt(args[1]));break;
//            // Generate simulations from the posterior mean
//            case 11: GenerateSimulationsFromPosteriorDistributions.generateSimulationsFromPosteriorDistributions(Integer.parseInt(args[1]));break;
//            default:break;
//        }
//    }    
//}
