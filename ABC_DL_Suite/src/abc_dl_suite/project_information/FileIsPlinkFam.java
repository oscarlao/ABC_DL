/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.project_information;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Oscar Lao
 */
public class FileIsPlinkFam implements FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isFile()) {
            String name = f.getName();
            return name.substring(name.length()-4).equals(".fam");
        }
        return false;
    }
}
