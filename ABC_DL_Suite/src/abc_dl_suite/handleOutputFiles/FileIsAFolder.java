/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.handleOutputFiles;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Oscar Lao
 */
public class FileIsAFolder implements FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory();
    }  
}
