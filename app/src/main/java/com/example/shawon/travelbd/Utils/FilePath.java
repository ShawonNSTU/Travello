package com.example.shawon.travelbd.Utils;

import android.os.Environment;

/**
 * Created by SHAWON on 8/14/2018.
 */

public class FilePath {

    // " /storage/emulated/0 " is the root directory
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String DCIM = ROOT_DIR + "/DCIM";
    public String PICTURE = ROOT_DIR + "/Picture";
    public String SHAREit_PICTURES = ROOT_DIR + "/SHAREit/Pictures";
    public String DOWNLOAD = ROOT_DIR + "/Download";

}
