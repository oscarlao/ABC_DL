/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.DL.model;

import a_simple_implementation.suite.ProjectInformationOfThisImplementation;
import abc_dl_suite.model_dl.GenerateModelComparison;

/**
 *
 * @author olao
 */
public class GenerateDLOfModels {
    public static void main(String [] args) throws Exception
    {
        args = new String[1];
        args[0] = "9";                
        GenerateModelComparison.generateModelComparison(0.025, Integer.parseInt(args[0]), ProjectInformationOfThisImplementation.getProjectInformation(), 20); // Networks for model comparison
        
    }
}
