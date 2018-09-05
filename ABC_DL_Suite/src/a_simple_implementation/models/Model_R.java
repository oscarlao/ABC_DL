/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.models;

import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.ParameterException;
import fastsimcoal2.event.demography.Demography;
import fastsimcoal2.event.demography.EventParameter;
import fastsimcoal2.event.demography.MigrationMatrix;
import fastsimcoal2.parameter.ParameterValue;
import fastsimcoal2.parameter.Value;
import fastsimcoal2.parameter.distribution.DistributionUniformFromValue;

/**
 * Model R. This is the "real" model that generated the observed data. This model is going to be used only to generate the observed data.
 * @author olao
 */
public class Model_R extends FastSimcoalModel {

    @Override
    protected void defineDemography() throws ParameterException {
        // Names of the populations to be used in fastSimcoal2
        String[] names = {"Pop1", "Pop2", "Pop3", "Pop4", "G3"};
        
        demography = new Demography(names);
        demography.addSampleWithTime("Pop3", 1800, 4); //Archaic population 1 is sampled 1800 generations ago
        demography.addSampleWithTime("Pop4", 1414, 4); //Archaic population 2 is sampled 1414 generations ago 

        // How many chromosomes are sampled from each population? In principle, only two chromosomes (one individual)
        for (int pop = 0; pop < 2; pop++) {
            demography.addSample(names[pop], 4);
        }
        demography.addSample("G3",0);
    }

    @Override
    protected void initializeModelParameters() throws ParameterException {
        // Migrations
        MigrationMatrix migrationMatrix = demography.getMigrationMatrix();
// MIGRATION RATES
        // Between European and African
        ParameterValue migrationPop1_to_Pop2 = new ParameterValue("migration_Pop1_to_Pop2", new DistributionUniformFromValue(new Value(0.0), new Value(5 * Math.pow(10, -4))));
        parameters.add(migrationPop1_to_Pop2);
        migrationMatrix.addMigration("Pop1", "Pop2", migrationPop1_to_Pop2);        
        // Demography
        demography.addEffectivePopulationSize("Pop1", new Value(3000));
        demography.addEffectivePopulationSize("Pop2", new Value(600));
        demography.addEffectivePopulationSize("Pop3", new Value(12000));                        
        demography.addEffectivePopulationSize("Pop4", new Value(6000));   
        demography.addEffectivePopulationSize("G3", new Value(4000));
        // Introgression G3 -> Pop2
        EventParameter eIntrogression3_to_2 = new EventParameter(new Value(300), demography.getPosition("Pop2"), demography.getPosition("G3"), new Value(0.05), new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eIntrogression3_to_2);
        // Split G3 with Pop3
        EventParameter eSplitG3_3 = new EventParameter(new Value(3000), demography.getPosition("G3"), demography.getPosition("Pop3"), new Value(1), new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eSplitG3_3);

// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter etSplit1_2 = new EventParameter(new Value(750), demography.getPosition("Pop1"), demography.getPosition("Pop2"), new Value(1.0), new Value(600), new Value(120), new Value(0), 1);
        demography.add_event(etSplit1_2);

// Event Split pop3 from pop4.        
// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter etSplit3_4 = new EventParameter(new Value(3800), demography.getPosition("Pop3"), demography.getPosition("Pop4"), new Value(1.0), new Value(6000), new Value(60), new Value(0), 1);
        demography.add_event(etSplit3_4);
        
// Event split pop1_2 from pop3_4        
        EventParameter etSplit1_2_3_4 = new EventParameter(new Value(5000), demography.getPosition("Pop2"), demography.getPosition("Pop4"), new Value(1.0), new Value(60), new Value(400), new Value(0), 1);
        demography.add_event(etSplit1_2_3_4);
    }
}