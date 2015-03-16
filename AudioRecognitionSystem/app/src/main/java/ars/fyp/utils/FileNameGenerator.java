package ars.fyp.utils;


import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Class for generate the Name of the file
public class FileNameGenerator {
    private static FileNameGenerator instance = null;

    public static FileNameGenerator getInstance() {
        if (instance == null) {

            instance = new FileNameGenerator();
        }
        return instance;
    }

    //return the name of sample by time
    public String getWaveSampleName()
    {

        //part for generating the file name with time based on it
        String time;
        String name = "sample_";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
        time = formatter.format(date);
        name+=time;
        name = name.concat(".wav");
        return name;
    }

    public String getDirectory()
    {
        //part for generating the directory for the file
        File directoryPath;
        directoryPath = Environment.getExternalStorageDirectory();
        return directoryPath.toString();
    }
    public String getTextSampleName()
    {

        //part for generating the file name with time based on it
        String time;
        String name = "sample_";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
        time = formatter.format(date);
        name+=time;
        name = name.concat(".txt");
        return name;
    }

}
