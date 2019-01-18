/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.parameter_dl;

import abc_deeplearning.model.Load_Model_Data;
import abc_dl_suite.project_information.ProjectInformation;
import data_management.PruneNotVariableVariables;
import data_management.RetrieveDataByRow;
import data_management.RetrieveFragmentList;
import data_management.RetrieveObservedData;
import data_transformation.InputDataTransformation_Standardize;
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
 * Divide the datasets in 1000, so we can parallelize the prediction of the
 * parameters in the simulations
 *
 * @author Oscar Lao
 */
public class PredictParameters_DL {

    /**
     * Compute the abc parameters in the observed data and the simulated data
     * using the first NN_replicas of each parameter of model_to_run
     *
     * @param project the project information of this project
     * @param model_to_run which model to run
     * @param how_many_replicas_by_parameter how many replicas by parameter of
     * the model to run
     * @throws Exception if something goes wrong.
     */
    public static void predictABCParameters(ProjectInformation project, int model_to_run, int how_many_replicas_by_parameter) throws Exception {

        Load_Model_Data lm = project.getLm();

        FastSimcoalModel bmodel = lm.getBmodel()[model_to_run];

        bmodel.defineModel();

        String folder_network = project.getWorking_folder() + File.separator + "parameter" + File.separator + bmodel.modelName() + File.separator;
        String folder_data = project.getWorking_folder() + File.separator;

        ParameterTrainingData ptd = new ParameterTrainingData();

        ptd.load(project, model_to_run);

        File files_r = new File(folder_data + File.separator + "output_" + bmodel.modelName() + "_replication.txt");
        RetrieveDataByRow rdb = new RetrieveDataByRow(files_r);

        WriteFile wf = new WriteFile(folder_data + File.separator + bmodel.modelName() + "_replication_parameter_for_abc.txt");
// Set the names of the variables.

        int printable_parameters = 0;

        for (int p = 0; p < bmodel.getListParameters().size(); p++) {
            if (bmodel.getListParameters().get(p).getPrint_parameter()) {
                String name = bmodel.getListParameters().get(p).getName();
                wf.print(" " + name);
                for (int rep = 0; rep < how_many_replicas_by_parameter; rep++) {
                    wf.print(" " + name + "_p_" + rep);
                }
                printable_parameters++;
            }
        }
        wf.println("");

// Load the networks        
        NetworkLoader[][] nc = new NetworkLoader[how_many_replicas_by_parameter][printable_parameters];

        for (int rep = 0; rep < how_many_replicas_by_parameter; rep++) {
            int k = 0;
            for (int par = 0; par < bmodel.getListParameters().size(); par++) {
// Consider only the parameters that are not nuisance
                if (bmodel.getListParameters().get(par).getPrint_parameter()) {
                    nc[rep][k] = new NetworkLoader((BasicNetwork) SerializeObject.load(new File(folder_network + bmodel.getListParameters().get(par).getName() + "_" + rep + ".network")));
                    k++;
                }
            }
        }

// now compute the predictions with the replication of the observed data
        String[] reference = {"Ancestral"};

        PruneNotVariableVariables pnvv = ptd.getPnvv();

        InputDataTransformation_Standardize standardize_t = ptd.getIs();

// Retrieve Observed data NOT USED for training
        String[] individuals_replication = project.getIndividuals_replication();

        String fileWithFragments = folder_data + "masked_regions.txt";

        FragmentList[] fr = RetrieveFragmentList.retrieveTheFragments(fileWithFragments);

        String plink = folder_data + File.separator + project.getPlink_file_with_observed_data();

        RetrieveObservedData ro = new RetrieveObservedData(new ReadBEDFile(plink), fr);

        int[] observed_sfs = ro.getSFS(individuals_replication, reference);
        int not_excluded = ro.getIncluded_SNPs();
        int excluded = ro.getExcluded_SNPs();
        double[] f = new double[observed_sfs.length];
        for (int e = 0; e < observed_sfs.length; e++) {
            f[e] += observed_sfs[e] + (excluded * observed_sfs[e] / (double) not_excluded);
        }
// Prune
        f = pnvv.prune(f);
// Standardize
        f = standardize_t.transform(f);
        for (int param = 0; param < printable_parameters; param++) {
            wf.print(" NA");
            for (int rep = 0; rep < how_many_replicas_by_parameter; rep++) {
                wf.print(" " + nc[rep][param].predict(f)[0] + " ");
            }
        }
        wf.println("");

        Parameters_SummaryStatistics pss = rdb.next();

// Do the other rows
        while (pss != null) {
            double[] pa = pss.getParameters();
            double[] sfs_replication = ptd.getIs().transform(ptd.getPnvv().prune(pss.getSummaryStatistics()));

            bmodel.reset();
            bmodel.defineModel();
            bmodel.loadParameters(pa);
            double[] pp = new double[pa.length];
            int k = 0;
            for (int p = 0; p < bmodel.getListParameters().size(); p++) {
                if (bmodel.getListParameters().get(p).getPrint_parameter()) {
                    if (bmodel.getListParameters().get(p).number_of_composited_parameters() > 0) {
                        pp[k] = bmodel.getListParameters().get(p).get_composite_value().doubleValue();
                    } else {
                        pp[k] = bmodel.getListParameters().get(p).getValue().doubleValue();
                    }
                    k++;
                }
            }
            
            for (int par = 0; par < pss.getParameters().length; par++) {
                wf.print(" " + pp[par]);
                for (int rep = 0; rep < how_many_replicas_by_parameter; rep++) {
                    wf.print(" " + nc[rep][par].predict(sfs_replication)[0]);
                }
            }
            
            wf.println("");
            pss = rdb.next();
        }
        rdb.close();
        wf.close();
    }
}
