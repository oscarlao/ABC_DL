/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.model_dl;

import abc_dl_suite.project_information.ProjectInformation;
import data_management.DataManagement;
import data_management.RetrieveDataByRow;
import data_management.RetrieveFragmentList;
import data_management.RetrieveObservedData;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.data.FragmentList;
import java.io.File;
import network.NetworkLoader;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.obj.SerializeObject;
import plink.read.ReadBEDFile;
import util.Parameters_SummaryStatistics;
import write.WriteFile;

/**
 *
 * @author Oscar Lao
 */
public class ComputeModelPredictionInReplicationSimulations {

    public static void predictModelPosteriorReplicationSimulations(ProjectInformation project) throws Exception {
// Model Network things used for training the model Neural Network
        RetrieveModelTrainingData mt = new RetrieveModelTrainingData(project);
// Predicted assignation to each model

        WriteFile wf = new WriteFile(project.getWorking_folder() + File.separator + "model_predictions.txt");

        String network_folder = project.getWorking_folder() + File.separator + "model" + File.separator;

        File[] files = new File(network_folder).listFiles();
// Networks        
        NetworkLoader[] networks = new NetworkLoader[files.length];
// Individuals to use to the replication
        String[] individuals = project.getIndividuals_replication();

        for (int l = 0; l < networks.length; l++) {
            networks[l] = new NetworkLoader((BasicNetwork) SerializeObject.load(files[l]));
        }

// Observed data
        String[] reference = {"Ancestral"};

        String fileWithFragments = project.getWorking_folder() + File.separator + "masked_regions.txt";

        FragmentList[] fr = RetrieveFragmentList.retrieveTheFragments(fileWithFragments);

        String plink = project.getWorking_folder() + File.separator + project.getPlink_file_with_observed_data();

        RetrieveObservedData ro = new RetrieveObservedData(new ReadBEDFile(plink), fr);

        int[] observed_sfs = ro.getSFS(individuals, reference);
        int not_excluded = ro.getIncluded_SNPs();
        int excluded = ro.getExcluded_SNPs();

        double[] fsfs = new double[observed_sfs.length];
        for (int e = 0; e < observed_sfs.length; e++) {
            fsfs[e] += observed_sfs[e] + (excluded * observed_sfs[e] / (double) not_excluded);
        }

        fsfs = DataManagement.extract_populations(individuals.length, project.getPops_to_retrieve(), fsfs);

        fsfs = mt.getStdT().transform(fsfs);

// Predicted assignation to each model
        wf.print("observed");
        for (int l = 0; l < networks.length; l++) {
            double[] model_prediction_observed = networks[l].predict(fsfs);
            for (double m : model_prediction_observed) {
                wf.print(" " + m);
            }
        }
        wf.println("");

// Now the replication data        
        FastSimcoalModel[] lm = project.getLm().getBmodel();

        for (int model = 0; model < lm.length; model++) {
            FastSimcoalModel bmodel = lm[model];
            RetrieveDataByRow rdr = new RetrieveDataByRow(new File(project.getWorking_folder() + File.separator + "output_" + bmodel.modelName() + "_replication.txt"));
// We use 150000 sims per model
                Parameters_SummaryStatistics ps = rdr.next();
                while(ps!=null) {
// Transform the sfs after extracting the pops of interest
                double[] sfs = mt.getStdT().transform(DataManagement.extract_populations(individuals.length, project.getPops_to_retrieve(), ps.getSummaryStatistics()));
                wf.print(bmodel.modelName());
                for (int l = 0; l < networks.length; l++) {
                    double[] model_prediction_simulated = networks[l].predict(sfs);
                    for (double m : model_prediction_simulated) {
                        wf.print(" " + m);
                    }
                }
                wf.println("");
                ps = rdr.next();
            }
            rdr.close();
        }
    }
}
