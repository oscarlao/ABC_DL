/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.generate_fake_observed_data;

import a_simple_implementation.models.Model_R;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.data.IndividualMutationListDatabase;
import fastsimcoal2.data.MutationList;
import fastsimcoal2.data.SNP;
import plink.util.Allele;
import plink.util.SNPGenotypeByteArray;
import plink.util.SNPPlink;
import plink.write.WriteBED;
import plink.util.demography.Individual;
import simulations.RunModelSimulationsGetIndividuals;
import simulations.os.WINDOWS;

/**
 *
 * @author olao
 */
public class Generate_fake_data_from_Model_R {

    /**
     * This is a small script to generate fake data of Model_R that we will use
     * as observed It is not really a part from the ABC-DL pipeline.
     * @param args arguments to pass to the main. Of no use here.
     * @throws Exception if something goes wrong
     */
    public static void main(String[] args) throws Exception {
        FastSimcoalModel bmodel = new Model_R();

        String fileWithFragments = "C:\\ABC_DL\\ABC_DL_Example_Project\\test_model\\masked_regions.txt";

        String fastSimcoal2 = "C:\\ABC_DL\\ABC_DL_Example_Project\\test_model\\fastSimcoal0\\";

        RunModelSimulationsGetIndividuals rs = new RunModelSimulationsGetIndividuals(fileWithFragments, fastSimcoal2, "fsc26", new WINDOWS());

        IndividualMutationListDatabase inds = rs.getDatabase(fastSimcoal2, bmodel);

        int s = inds.size();

        Individual[] ind_names = new Individual[s + 1];

        for (int i = 0; i < ind_names.length - 1; i++) {
            ind_names[i] = new Individual("Ind_"+i, inds.get(i).getName());
        }

        ind_names[ind_names.length - 1] = new Individual("Ancestral", "Ancestral");

        WriteBED wb = new WriteBED();
        
        wb.open("C:\\Users\\olao\\OneDrive - CRG - Centre de Regulacio Genomica\\ABC_DL\\test_model\\observed_fake_data");        

        wb.addIndividuals(ind_names);

        MutationList[] ml = inds.getM();

        for (int chr = 0; chr < ml.length; chr++) {
            for (int sn = 0; sn < ml[chr].size(); sn++) {
                SNP snp = ml[chr].get(sn);
                SNPPlink snp_plink = new SNPPlink("rs_"+chr+"_"+sn, (byte)chr,snp.getPosition(),Allele.getAllele(snp.getA()),Allele.getAllele(snp.getB()));
                byte [] g = new byte[ind_names.length];
                for(int i=0;i<inds.size();i++)
                {
                    g[i] = inds.get(i).getSequence(chr).getGenotype(sn);
                }
                SNPGenotypeByteArray snpG = new SNPGenotypeByteArray(snp_plink, g);
                wb.add(snpG);
            }

        }

        wb.close();

        System.out.println("Chis pun!");

    }
}
