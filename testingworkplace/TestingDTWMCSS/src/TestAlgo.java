import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.musicg.wave.Wave;

public class TestAlgo {
	public static void main(String args[]) {
		try {
			String directory = "/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/dataFile/313/";
			for (int i = 1; i <= 1; i++) {
				List<Short> datas = readShortFromRandomAccessFile(directory + i
						+ ".txt");
				List<Integer> frequencies = calculateFrequency(datas, 16000);
				List<Integer> avgAmp = calculateAvgAmplitudes(datas, 16000);
				List<Integer> avgAmpWithout = calculateAvgAmplitudesWithClassify(
						datas, 4000);
				File frequenciesFile = new File("result/313/" + i + "Freq.txt");
				File amplitudesFile = new File("result/313/" + i + "Amp.txt");
				File avgAmpFile = new File("result/313/" + i + "AvgAmp.txt");
				File avgAmpWithoutAbsFile = new File("result/313/" + i
						+ "AvgAmpWithoutClassify.txt");
				System.out.println("The data sizes : " + datas.size());
				System.out.println("The frequencies size : "
						+ frequencies.size());

				writeDataToFile(frequenciesFile, frequencies);
				writeDataToFile(avgAmpFile, avgAmp);
				// writeDataToFile(avgAmpWithoutAbsFile,avgAmpWithout);
				writeAmplitudeToFile(amplitudesFile, datas);
				// getSampleAmplitudesOrderOfMagnitude(datas);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	// calculate the frequence
	public static List<Integer> calculateFrequency(List<Short> datas,
			int offsetSize) {
		int crossingZero = 0;
		List<Integer> frequencies = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i += 2) {
			if (datas.get(i) < -10 && datas.get(i + 1) >= 5)
				crossingZero++;
			else if (datas.get(i) > 5 && datas.get(i + 1) <= -5)
				crossingZero++;
			if (i % offsetSize == 0 && i != 0) {
				frequencies.add(Integer.valueOf((crossingZero)
						* (44100 / offsetSize)));
				crossingZero = 0;
			}
		}
		frequencies.add(Integer.valueOf((crossingZero)
				* (44100 / offsetSize)));
		return frequencies;
	}

	// calculate the amplitudes intensity with absolute values
	public static List<Integer> calculateAvgAmplitudes(List<Short> datas,
			int offsetSize) {
		int avgAmp = 0;
		List<Integer> avgAmplitudes = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i++) {
			avgAmp += Math.abs(datas.get(i));
			if (i % offsetSize == 0 && i != 0) {
				avgAmp = avgAmp / offsetSize;
				avgAmplitudes.add(Integer.valueOf(avgAmp));
				avgAmp = 0;
			}
		}
		avgAmp = avgAmp / offsetSize;
		avgAmplitudes.add(Integer.valueOf(avgAmp));
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
			if(datas.get(i) >= 0)
			{
				pveAvgAmp +=datas.get(i);
				pveCount ++;
			}
			else
			{
				nveAvgAmp +=datas.get(i);
				nveCount ++;
			}
			if (i % offsetSize == 0 && i != 0) {
				pveAvgAmp /=pveCount;
				nveAvgAmp /=nveCount;
				avgAmplitudes.add(Integer.valueOf(nveAvgAmp));
				avgAmplitudes.add(Integer.valueOf(pveAvgAmp));
				nveAvgAmp = 0;
				pveAvgAmp = 0;
				pveCount = 0;
				nveCount = 0;
			}
		}
		pveAvgAmp /=pveCount;
		nveAvgAmp /=nveCount;
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
