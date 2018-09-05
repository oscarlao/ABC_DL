/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.models;

import abc_deeplearning.model.Load_Model_Data;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.ParameterException;


/**
 * The models that we are going to run in this implementation of the ABC-DL
 * @author olao
 */
public class ModelsToRunABC_DL extends Load_Model_Data{
    
    public ModelsToRunABC_DL(String folder) throws ParameterException{
        super(folder);
    }       
    
    @Override
    protected void defineModels() throws ParameterException {
        bmodel = new FastSimcoalModel[2];
        bmodel[0] = new Model_A();
        bmodel[1] = new Model_B();      
    }
}
