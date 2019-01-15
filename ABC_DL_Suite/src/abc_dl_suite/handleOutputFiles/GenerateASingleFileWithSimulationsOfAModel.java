/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.handleOutputFiles;

import abc_dl_suite.project_information.ProjectInformation;
import fastsimcoal2.FastSimcoalModel;
import java.io.File;
import java.io.IOException;
import read.ReadFile;
import write.WriteFile;

/**
 *
 * @author Oscar Lao
 */
public class GenerateASingleFileWithSimulationsOfAModel {

    /**
     * Generate a single file of the model
     *
     * @param es general information of the project
     * @param model_to_run the output of which model is going to be merged?
     * @throws IOException if there is a problem writing the file or reading the
     * output from the fastSimcoal folder
     */
    public static void generateASingleFileOfModel(ProjectInformation es, int model_to_run) throws IOException {
        FastSimcoalModel bmodel = es.getLm().getBmodel()[model_to_run];

        WriteFile wf = new WriteFile(es.getWorking_folder() + File.separator + "output_" + bmodel.modelName() + "_training.txt");

        double percentage_of_training = es.getPercentage_of_training();

        int training_folders = (int) (es.getNumber_of_fastSimcoal2_folders() * percentage_of_training);

//File.separator + "scratch" + File.separator + "devel" + File.separator + "olao" + File.separator + "jaume" + File.separator + "abcSimulations" + File.separator
        for (int f = 0; f < training_folders; f++) {
            ReadFile fastSimcoal = new ReadFile(es.getWorking_folder() + File.separator + "fastSimcoal" + f + File.separator + "output_" + bmodel.modelName() + ".txt");
            if (f == 0) {
                wf.println(fastSimcoal.readRow());
            } else {
                fastSimcoal.readRow();
            }
            String line = fastSimcoal.readRow();
            if (line != null) {
                int cells = line.split("\\s").length;
                while (line != null) {
                    if (line.split("\\s").length == cells) {
                        wf.println(line);
                    } else {
                        System.out.println(f + " " + bmodel.modelName());
                    }
                    line = fastSimcoal.readRow();
                }
            }

            fastSimcoal.close();
        }
        wf.close();

        WriteFile wff = new WriteFile(es.getWorking_folder() + File.separator + "output_" + bmodel.modelName() + "_replication.txt");

        for (int f = training_folders; f < es.getNumber_of_fastSimcoal2_folders(); f++) {
            ReadFile fastSimcoal = new ReadFile(es.getWorking_folder() + File.separator + "fastSimcoal" + f + File.separator + "output_" + bmodel.modelName() + ".txt");
// header
            if (f == training_folders) {
                wff.println(fastSimcoal.readRow());
            } else {
                fastSimcoal.readRow();
            }
// Remaining lines            
            String line = fastSimcoal.readRow();
            if (line != null) {
                int cells = line.split("\\s").length;
                while (line != null) {
// Check that the length of the cells is the one from the first line (not truncated line or something else!)
                    if (line.split("\\s").length == cells) {
                        wff.println(line);
                    } else {
                        System.out.println(f + " " + bmodel.modelName());
                    }
                    line = fastSimcoal.readRow();
                }
            }
            fastSimcoal.close();
        }
        wff.close();
    }
}
