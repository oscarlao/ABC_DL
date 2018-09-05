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
import fastsimcoal2.parameter.ParameterValue;
import fastsimcoal2.parameter.ParameterValueScalable;
import fastsimcoal2.parameter.Value;
import fastsimcoal2.parameter.distribution.DistributionUniformFromParameterValue;
import fastsimcoal2.parameter.distribution.DistributionUniformFromValue;

/**
 * A Model depicting the relationship ((pop1,pop2),(pop3,pop4)) pop3 and pop4
 * are archaic populations, and the individuals have been sampled 1800 and 1414
 * generations ago individuals in pop1 and pop2 have been sampled nowadays
 *
 * @author olao
 */
public class Model_A extends FastSimcoalModel {

    @Override
    protected void defineDemography() throws ParameterException {
        // Names of the populations to be used in fastSimcoal2
        String[] names = {"Pop1", "Pop2", "Pop3", "Pop4"};
        
        demography = new Demography(names);
        demography.addSampleWithTime("Pop3", 1800, 2); //Archaic population 1 is sampled 1800 generations ago
        demography.addSampleWithTime("Pop4", 1414, 2); //Archaic population 2 is sampled 1414 generations ago 

        // How many chromosomes are sampled from each population? In principle, only two chromosomes (one individual)
        for (int pop = 0; pop < 2; pop++) {
            demography.addSample(names[pop], 2);
        }
    }

    @Override
    protected void initializeModelParameters() throws ParameterException {
// PARAMETERS
// EFFECTIVE POPULATION SIZES
    ParameterValue Ne1, Ne2, Ne3, Ne4, Ne1_2, Ne3_4, Ne1_2_3_4;
// MIGRATION RATES
// Our model assumes no migration events. Check class Model_Real to see how to include migration events
// EFFECTIVE POPULATION SIZES
// The effective population size of pop 1 can range A PRIORI uniformly between 1000 chromosomes and 5000 chromosomes
        Ne1 = new ParameterValue("Ne1", new DistributionUniformFromValue(new Value(1000), new Value(5000)));
        parameters.add(Ne1);
        demography.addEffectivePopulationSize("Pop1", Ne1);
// The effective population size of pop 2 can range A PRIORI between 500 and 1000
        Ne2 = new ParameterValue("Ne2", new DistributionUniformFromValue(new Value(500), new Value(1000)));
        parameters.add(Ne2);
        demography.addEffectivePopulationSize("Pop2", Ne2);
// The effective population size of pop 3 can range between 10000 and 20000
        Ne3 = new ParameterValue("Ne3", new DistributionUniformFromValue(new Value(10000), new Value(20000)));
        parameters.add(Ne3);
        demography.addEffectivePopulationSize("Pop3", Ne3);                        
// The effective population size of pop 4 can range between 5000 and 10000
        Ne4 = new ParameterValue("Ne4", new DistributionUniformFromValue(new Value(5000), new Value(10000)));
        parameters.add(Ne4);
        demography.addEffectivePopulationSize("Pop4", Ne4);   


// TIME EVENTS
    ParameterValue t1_2, t3_4;
// TIME EVENTS THAT DEPEND ON ANOTHER EVENT.
// In this case, the time of split of the ancestors of (pop1,2) and (pop3,4) cannot be younger than the time of split of pop3,4    
    ParameterValueScalable t1_2_3_4;        
// EVENTS
// Event Split pop1 from pop2. It ranges between 500 generations and 1500 generations
        t1_2 = new ParameterValue("tSplitPop1_Pop2", new DistributionUniformFromValue(new Value(500), new Value(1500)));
        parameters.add(t1_2);
// The effective population size of this merged population can be between 100 and 200        
        Ne1_2 = new ParameterValue("Ne1_2", new DistributionUniformFromValue(new Value(100), new Value(200)));
        parameters.add(Ne1_2);
// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter etSplit1_2 = new EventParameter(t1_2, demography.getPosition("Pop1"), demography.getPosition("Pop2"), new Value(1.0), Ne2, Ne1_2, new Value(0), 1);
        demography.add_event(etSplit1_2);

// Event Split pop3 from pop4.        
        t3_4 = new ParameterValue("tSplitPop3_Pop4", new DistributionUniformFromValue(new Value(2000), new Value(4000)));
        parameters.add(t3_4);
// The effective population size of this merged population can be between 100 and 200        
        Ne3_4 = new ParameterValue("Ne3_4", new DistributionUniformFromValue(new Value(100), new Value(200)));
        parameters.add(Ne3_4);
// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter etSplit3_4 = new EventParameter(t3_4, demography.getPosition("Pop3"), demography.getPosition("Pop4"), new Value(1.0), Ne4, Ne3_4, new Value(0), 1);
        demography.add_event(etSplit3_4);
        
// Event split pop1_2 from pop3_4        
        t1_2_3_4 = new ParameterValueScalable("tSplitPop1_Pop2_Pop3_Pop4", new DistributionUniformFromParameterValue(t3_4, new Value(6000)));
        t1_2_3_4.setScalable(t3_4);
        parameters.add(t1_2_3_4);
        Ne1_2_3_4 = new ParameterValue("Ne1_2_3_4", new DistributionUniformFromValue(new Value(100), new Value(200)));
        parameters.add(Ne1_2_3_4);
        EventParameter etSplit1_2_3_4 = new EventParameter(t1_2_3_4, demography.getPosition("Pop2"), demography.getPosition("Pop4"), new Value(1.0), Ne3_4, Ne1_2_3_4, new Value(0), 1);
        demography.add_event(etSplit1_2_3_4);
    }
}
