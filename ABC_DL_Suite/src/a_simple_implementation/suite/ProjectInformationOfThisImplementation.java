/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.suite;

import a_simple_implementation.models.ModelsToRunABC_DL;
import abc_dl_suite.project_information.ProjectInformation;
import simulations.os.WINDOWS;

/**
 *
 * @author olao
 */
public class ProjectInformationOfThisImplementation {
    
    /**
     * Hard coded information of the test project
     * @return the ProjectInformation of this project
     * @throws Exception if something goes wrong
     */
    public static ProjectInformation getProjectInformation() throws Exception            
    {
        // Individuals for training
        String [] training = {"Ind_0", "Ind_2", "Ind_4","Ind_6"};
        // Individuals for replication
        String [] replication = {"Ind_1", "Ind_3", "Ind_5","Ind_7"};
        // pops to retrieve
        int [] pops_to_retrieve = {0,1,2,3};
        return new ProjectInformation("observed_fake_data", new ModelsToRunABC_DL(""), "C:\\Users\\olao\\OneDrive - CRG - Centre de Regulacio Genomica\\ABC_DL\\test_model\\","fsc26",4, new WINDOWS(), 10000, 0.5, training, replication, pops_to_retrieve);
    }    
}
