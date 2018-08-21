package com.example.shawon.travelbd.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by SHAWON on 8/14/2018.
 */

public class FileSearch {

    /**
     * return a list of all **directories** contained inside a directory
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> mListOfDirectories = new ArrayList<>();
        File file = new File(directory);
        File[] list = file.listFiles();
        try {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    mListOfDirectories.add(list[i].getAbsolutePath());
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return mListOfDirectories;
    }

    /**
     * return a list of all **files** contained inside a directory
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> mListOfFiles = new ArrayList<>();
        File file = new File(directory);
        File[] list = file.listFiles();
        try {
            for (int i=0; i<list.length; i++) {
                if (list[i].isFile()){
                    mListOfFiles.add(list[i].getAbsolutePath());
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return mListOfFiles;
    }

}