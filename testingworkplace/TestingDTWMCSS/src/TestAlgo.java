import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.musicg.wave.Wave;

public class TestAlgo {
	public static void main(String args[]) {
		int dateNo = 327;
		int numberOfFile = 5;
		ArrayList<List<Integer>> freqLists = new ArrayList<List<Integer>>();
		String directory = "/Users/lohris/lohrisdtwmfccs/testingworkplace/TestingDTWMCSS/src/dataFile/"
				+ dateNo + "/";
		try {
			// prepareData(5512, dateNo, numberOfFile);
			List<Integer> datas = null;
			for (int i = 1; i <= numberOfFile; i++) {
				datas = readIntFromRandomAccessFile(directory + i + ".txt");
				// System.out.println(datas);
				// File amplitudesFile = new File("result/" + dateNo + "/" + i
				// + "Amp.txt");
				// writeDataToFile(amplitudesFile, datas);
				List<Integer> amp, freq = null;
				File ampFile = new File("result/" + dateNo + "/" + i
						+ "amp.txt");
				File freqFile = new File("result/" + dateNo + "/" + i
						+ "freq.txt");
				amp = new ArrayList<Integer>();
				freq = new ArrayList<Integer>();
				int avgFreq = 0, count = 0;
				for (int j = 0; j < datas.size(); j += 2) {
					if (datas.get(j) != 0) {
						if (datas.get(j + 1) >= 5)
							avgFreq = 3;
						else if (datas.get(j + 1) >= 3)
							avgFreq = 2;
						else
							avgFreq = 1;
						freq.add(avgFreq);
					}

				}
				writeDataToFile(freqFile, freq);
				freqLists.add(freq);
			}
			System.out.println(freqLists);
			List<Integer> longestCommonSequence = new ArrayList<Integer>();
			List<Integer> lcsResult = null;
			int[] input = { 0, 1, 2, 3, 4 };
			ArrayList<ArrayList<Integer>> result = permuteUnique(input);
			for (int i = 0; i < result.size(); i++) {
				lcsResult = lcs(freqLists.get(result.get(i).get(0)),
						freqLists.get(result.get(i).get(1)));
				lcsResult = lcs(lcsResult, freqLists.get(result.get(i).get(2)));
				lcsResult = lcs(lcsResult, freqLists.get(result.get(i).get(3)));
				lcsResult = lcs(lcsResult, freqLists.get(result.get(i).get(4)));

				if(lcsResult.size() > longestCommonSequence.size())
					longestCommonSequence = lcsResult;
			}
			System.out.println(longestCommonSequence);
			for(int i = 0;i < longestCommonSequence.size();i++)
			{
				System.out.println(longestCommonSequence.get(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public static List<Integer> lcs(List<Integer> a, List<Integer> b) {
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> x = a;
		List<Integer> y = b;
		int M = x.size();
		int N = y.size();

		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		int[][] opt = new int[M + 1][N + 1];

		// compute length of LCS and all subproblems via dynamic programming
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x.get(i) == y.get(j))
					opt[i][j] = opt[i + 1][j + 1] + 1;
				else
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
			}
		}

		// recover LCS itself and print it to standard output
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

	public static List<Integer> readIntFromRandomAccessFile(String filePath)
			throws IOException {

		RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
		System.out.println(raf.length() + "");
		List<Integer> datas = new ArrayList<Integer>();
		for (int i = 0; i < raf.length(); i += 4) {
			datas.add(raf.readInt());
		}
		raf.close();
		return datas;

	}

	public static List<Short> readShortFromRandomAccessFile(String filePath)
			throws IOException {

		RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
		List<Short> datas = new ArrayList<Short>();
		for (long i = 0; i < raf.length(); i += 2) {

			datas.add(new Short(raf.readShort()));
		}
		raf.close();
		return datas;

	}

	public static float[] normalizedData(List<Short> datas) {
		float[] nData = new float[1024];
		for (int i = 0; i < 1024; i++) {
			nData[i] = datas.get(i) * 1.0f / Short.MAX_VALUE;
		}
		return nData;
	}

	public static void prepareData(int offset, int dateNo, int noOfFiles)
			throws IOException {
		String directory = "/Users/lohris/lohrisdtwmfccs/testingworkplace/TestingDTWMCSS/src/dataFile/"
				+ dateNo + "/";
		for (int i = 1; i <= noOfFiles; i++) {
			List<Short> datas = readShortFromRandomAccessFile(directory + i
					+ ".txt");
			// float[] normalizedDatas = normalizedData(datas);
			// FFT fft = new FFT(normalizedDatas.length,44100);
			// fft.forward(normalizedDatas);
			// float[] fftResult = fft.getSpectrum();
			//
			List<Integer> frequencies = calculateFrequency(datas, offset);
			List<Integer> frequenciesWithFilter = calculateFrequencyWithFilter(
					datas, offset);
			List<Integer> avgSqAmp = calculateSquareAvgAmplitudes(datas, offset);
			List<Integer> avgAmp = calculateAvgAmplitudes(datas, offset);
			File frequenciesWithFilterFile = new File("result/" + dateNo + "/"
					+ i + "FreqWithFilter.txt");
			File frequenciesFile = new File("result/" + dateNo + "/" + i
					+ "Freq.txt");
			File avgAmpFile = new File("result/" + dateNo + "/" + i
					+ "avgAmp.txt");
			File avgSqAmpFile = new File("result/" + dateNo + "/" + i
					+ "avgSqAmp.txt");
			File amplitudesFile = new File("result/" + dateNo + "/" + i
					+ "Amp.txt");
			System.out.println("The data sizes : " + datas.size());
			System.out.println("The frequencies size : " + frequencies.size());
			writeDataToFile(avgAmpFile, avgAmp);
			writeDataToFile(frequenciesFile, frequencies);
			writeDataToFile(avgSqAmpFile, avgSqAmp);
			// writeDataToFile(frequenciesWithFilterFile,frequenciesWithFilter);
			// writeAmplitudeToFile(amplitudesFile, datas);
			// getSampleAmplitudesOrderOfMagnitude(datas);
		}
	}

	private static void writeDataToFile(File fftFile, float[] fftResult)
			throws IOException {
		// TODO Auto-generated method stub
		FileUtils.writeStringToFile(fftFile, "");
		for (int i = 0; i < fftResult.length; i++) {
			FileUtils.writeStringToFile(fftFile, fftResult[i] + "\n", true);
		}
		System.out.println("The data file " + fftFile + " is completed.");
	}

	public static int convertIntoDb(double d) {
		if (d <= 0.5) {
			return 0;
		}
		int decibel = 0;
		decibel = (int) (20 * Math.log10(Math.abs(d) / 32768.0));
		return 100 + decibel;
	}

	// calculate the frequence
	public static List<Integer> calculateFrequency(List<Short> datas,
			int offsetSize) {
		int crossingZero = 0;
		List<Integer> frequencies = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i += 2) {
			if (datas.get(i) < 0 && datas.get(i + 1) >= 0)
				crossingZero++;
			else if (datas.get(i) > 0 && datas.get(i + 1) <= 0)
				crossingZero++;
			if (i % offsetSize == 0 && i != 0) {
				frequencies.add(Integer.valueOf((crossingZero)
						* (44100 / offsetSize)));
				crossingZero = 0;
			}
		}
		frequencies.add(Integer.valueOf((crossingZero / 2)
				* (44100 / offsetSize)));
		return frequencies;
	}

	public static List<Integer> calculateFrequencyWithFilter(List<Short> datas,
			int offsetSize) {
		int crossingZero = 0;
		List<Integer> frequencies = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i += 2) {
			if (datas.get(i) < 0 && datas.get(i + 1) >= 0)
				crossingZero++;
			else if (datas.get(i) > 0 && datas.get(i + 1) <= 0)
				crossingZero++;
			if (i % offsetSize == 0 && i != 0) {
				frequencies.add(Integer.valueOf((crossingZero)
						* (44100 / offsetSize)));
				crossingZero = 0;
			}
		}
		frequencies.add(Integer.valueOf((crossingZero / 2)
				* (44100 / offsetSize)));
		return frequencies;
	}

	public static List<Integer> calculateAvgAmplitudes(List<Short> datas,
			int offsetSize) {
		int avgAmp = 0;
		List<Integer> avgAmplitudes = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i++) {
			avgAmp += Math.abs(datas.get(i));
			if (i % offsetSize == 0 && i != 0) {
				// avgAmp = avgAmp / offsetSize;
				avgAmp = (int) Math.round((avgAmp / offsetSize / 2000.0));
				avgAmplitudes.add(Integer.valueOf(avgAmp));
				avgAmp = 0;
			}
		}
		avgAmp = (int) Math.round((avgAmp / offsetSize / 2000.0));
		avgAmplitudes.add(Integer.valueOf(avgAmp));
		return avgAmplitudes;

	}

	// calculate the amplitudes intensity with absolute values
	public static List<Integer> calculateSquareAvgAmplitudes(List<Short> datas,
			int offsetSize) {
		long avgAmp = 0;
		List<Integer> avgAmplitudes = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i++) {
			avgAmp += (datas.get(i) * datas.get(i));
			if (i % offsetSize == 0 && i != 0) {
				avgAmp = avgAmp / offsetSize;
				avgAmp = (int) Math.sqrt(avgAmp);
				avgAmplitudes.add(Integer.valueOf(avgAmp + ""));
				avgAmp = 0;
			}
		}
		avgAmp = avgAmp / offsetSize;
		avgAmp = (int) Math.sqrt(avgAmp);
		avgAmplitudes.add(Integer.valueOf(avgAmp + ""));
		return avgAmplitudes;
	}

	public static List<Integer> calculateAvgAmplitudesWithClassify(
			List<Short> datas, int offsetSize) {
		int nveAvgAmp = 0;
		int pveAvgAmp = 0;
		int pveCount = 0;
		int nveCount = 0;
		List<Integer> avgAmplitudes = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i++) {
			if (datas.get(i) >= 0) {
				pveAvgAmp += datas.get(i);
				pveCount++;
			} else {
				nveAvgAmp += datas.get(i);
				nveCount++;
			}
			if (i % offsetSize == 0 && i != 0) {
				if (pveCount != 0)
					pveAvgAmp /= pveCount;
				if (nveCount != 0)
					nveAvgAmp /= nveCount;
				avgAmplitudes.add(Integer.valueOf(nveAvgAmp));
				avgAmplitudes.add(Integer.valueOf(pveAvgAmp));
				nveAvgAmp = 0;
				pveAvgAmp = 0;
				pveCount = 0;
				nveCount = 0;
			}
		}
		if (pveCount != 0)
			pveAvgAmp /= pveCount;
		if (nveCount != 0)
			nveAvgAmp /= nveCount;
		avgAmplitudes.add(Integer.valueOf(nveAvgAmp));
		avgAmplitudes.add(Integer.valueOf(pveAvgAmp));
		return avgAmplitudes;
	}

	public static void printSA(Wave wave) {
		short[] amplitudes = wave.getSampleAmplitudes();
		System.out.println("Total Number of Amplitudes " + amplitudes.length);
		for (int i = 0; i < amplitudes.length; i++) {
			System.out.println("Amplitudes : " + amplitudes);
		}
	}

	public static void getSampleAmplitudesOrderOfMagnitude(List<Short> datas) {

		int[] oOM = new int[10];
		for (int i = 0; i < datas.size(); i++) {
			if (Math.abs((datas.get(i))) < 10 && Math.abs((datas.get(i))) > 0)
				oOM[0]++;
			else if (Math.abs((datas.get(i))) < 100
					&& Math.abs((datas.get(i))) > 0)
				oOM[1]++;
			else if (Math.abs((datas.get(i))) < 1000
					&& Math.abs((datas.get(i))) > 0)
				oOM[2]++;
			else if (Math.abs((datas.get(i))) < 10000
					&& Math.abs((datas.get(i))) > 0)
				oOM[3]++;
			else if (Math.abs((datas.get(i))) < 100000
					&& Math.abs((datas.get(i))) > 0)
				oOM[4]++;
			else if (Math.abs((datas.get(i))) == 0)
				oOM[5]++;
			else
				oOM[6]++;
		}
		System.out.println("< 10 : " + oOM[0]);
		System.out.println("< 100 : " + oOM[1]);
		System.out.println("< 1000 : " + oOM[2]);
		System.out.println("< 10000 : " + oOM[3]);
		System.out.println("< 100000 : " + oOM[4]);
		System.out.println("equal 0 : " + oOM[5]);
		System.out.println("others: " + oOM[6]);
	}

	public static void writeDataToFile(File filename, List<Integer> datas)
			throws IOException {
		FileUtils.writeStringToFile(filename, "");
		for (int i = 0; i < datas.size(); i++) {
			FileUtils.writeStringToFile(filename, datas.get(i) + "\n", true);
		}
		System.out.println("The data file " + filename + " is completed.");
	}

	public static void writeAmplitudeToFile(File filename, List<Short> datas)
			throws IOException {
		FileUtils.writeStringToFile(filename, "");
		for (int i = 0; i < datas.size(); i++) {
			FileUtils.writeStringToFile(filename, datas.get(i) + "\n", true);
		}
		System.out.println("The data file " + filename + " is completed.");
	}
}
