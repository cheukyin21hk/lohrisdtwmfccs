package ars.fyp.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lohris on 29/3/15.
 */
//class generated for the Longest common subsequence generation
public class LCSHelper {

    public static ArrayList<Integer> findCommonLCS(ArrayList<ArrayList<Integer>> freqLists) {
        ArrayList<Integer> result = null;
        ArrayList<Integer> lcsResult = null;
        ArrayList<ArrayList<Integer>> allPermutation;
        int[] input = new int[freqLists.size()];
        for (int i = 0; i < freqLists.size(); i++) {
            input[i] = i;
        }
        allPermutation = permuteUnique(input);
        for (int i = 0; i < allPermutation.size(); i++) {
            ArrayList<Integer> tmp = allPermutation.get(i);
            lcsResult = lcs(freqLists.get(tmp.get(0)), freqLists.get(tmp.get(1)));
            lcsResult = lcs(lcsResult, freqLists.get(tmp.get(2)));
            lcsResult = lcs(lcsResult, freqLists.get(tmp.get(3)));
            lcsResult = lcs(lcsResult, freqLists.get(tmp.get(4)));

            if (lcsResult.size() > result.size())
                result = lcsResult;
        }
        return result;
    }


    public static ArrayList<ArrayList<Integer>> permuteUnique(int[] num) {
        ArrayList<ArrayList<Integer>> returnList = new ArrayList<ArrayList<Integer>>();
        returnList.add(new ArrayList<Integer>());

        for (int i = 0; i < num.length; i++) {
            Set<ArrayList<Integer>> currentSet = new HashSet<ArrayList<Integer>>();
            for (List<Integer> l : returnList) {
                for (int j = 0; j < l.size() + 1; j++) {
                    l.add(j, num[i]);
                    ArrayList<Integer> T = new ArrayList<Integer>(l);
                    l.remove(j);
                    currentSet.add(T);
                }
            }
            returnList = new ArrayList<ArrayList<Integer>>(currentSet);
        }
        return returnList;
    }

    public static ArrayList<Integer> skipLowAmplitude(ArrayList<Integer> data) {
        ArrayList<Integer> classifiedFreq = null;
        int avgFreq = 0;
        for (int j = 0; j < data.size(); j += 2) {
            if (data.get(j) != 0) {
                if (data.get(j + 1) >= 5)
                    avgFreq = 3;
                else if (data.get(j + 1) >= 3)
                    avgFreq = 2;
                else
                    avgFreq = 1;
                classifiedFreq.add(avgFreq);
            }

        }
        return classifiedFreq;
    }



    public static ArrayList<Integer> lcs(ArrayList<Integer> a, ArrayList<Integer> b) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        ArrayList<Integer> x = a;
        ArrayList<Integer> y = b;
        int M = x.size();
        int N = y.size();

        int[][] opt = new int[M + 1][N + 1];

        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (x.get(i) == y.get(j))
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        int i = 0, j = 0;
        while (i < M && j < N) {
            if (x.get(i) == y.get(j)) {
                result.add(x.get(i));
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1])
                i++;
            else
                j++;
        }
        return result;

    }
}
