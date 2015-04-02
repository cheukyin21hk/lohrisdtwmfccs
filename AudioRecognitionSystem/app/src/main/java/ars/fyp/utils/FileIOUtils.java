package ars.fyp.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

import ars.fyp.audiorecognitionsystem.MainPage;

/**
 * Created by lohris on 1/4/15.
 */
public class FileIOUtils {

    public static ArrayList<Integer> readIntFromRandomAccessFile(String filePath) {
        try {
            RandomAccessFile raf = null;
            raf = new RandomAccessFile(filePath, "rw");
            ArrayList<Integer> datas = new ArrayList<Integer>();
            for (int i = 0; i < raf.length(); i += 4) {
                datas.add(raf.readInt());
            }
            raf.close();
            return datas;
        } catch (FileNotFoundException e) {
            Log.e(MainPage.class.toString(), e.toString());
        } catch (IOException e) {
            Log.e(MainPage.class.toString(), e.toString());
        }
        return null;
    }

    public static void writeDataToFile(File filename, ArrayList<Integer> datas)
    {
        try {
            FileUtils.writeStringToFile(filename, "");
            for (int i = 0; i < datas.size(); i++) {
                FileUtils.writeStringToFile(filename, datas.get(i) + "\n", true);
            }
            Log.i(MainPage.class.toString(),"The data file " + filename + " is completed.");
        } catch (IOException e) {
            Log.e(MainPage.class.toString(), e.toString());
        }

    }

}
