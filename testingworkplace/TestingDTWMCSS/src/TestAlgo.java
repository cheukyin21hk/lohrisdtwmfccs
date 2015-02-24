import java.util.Calendar;

import com.musicg.wave.Wave;

public class TestAlgo {
	public static void main(String args[]) {

		String time;
		String name = "sample_";
		
		System.out.println(Calendar.getInstance().getTime().getTime());
		// Wave bellRinging = new
		// Wave("/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/sound/key2.wav");
		// Wave bellRinging2 = new
		// Wave("/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/sound/DKey1.wav");
		// Wave testWav = new
		// Wave("/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/sound/test.wav");
		// bellRinging.getNormalizedAmplitudes();
		//
		// double[] normalizedA = testWav.getNormalizedAmplitudes();
		// short[] amplitiudes = testWav.getSampleAmplitudes();
		// float[] normalizedInF = new float[normalizedA.length];
		// float[] amplitutiesR = new float[normalizedA.length];
		// float[] resultA = new float[1024];
		// float[] result = new float[1024];
		// int a = 0;
		// for(double i : normalizedA)
		// {
		// amplitutiesR[a] = (float)amplitiudes[a];
		// normalizedInF[a] = (float)i;
		// a++;
		// }
		//
		// FFT fft = new FFT(1024);
		// fft.forwardTransform(normalizedInF);
		// fft.modulus(normalizedInF, result);
		// fft.modulus(amplitutiesR, resultA);
		// a = 0;
		// for(float value : result)
		// {
		// System.out.print(resultA[a] +"\t");
		// System.out.println(value);
		// a++;
		// }
		//
		// GraphicRender gr = new GraphicRender();
		// String filename = "amp";
		// String directoryName = "output/";
		// int fileIndex = 1;
		// File amplitiudeFile = new File(directoryName + filename + fileIndex
		// + ".txt");
		// Wave key1 = new Wave(
		// "/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/sound/wave3.wav");
		//
		// short a = 0;
		// splitting the data with zero
		// for(int i = 0; i < amplitiudes.length ; i++)
		// {
		// a = amplitiudes[i];
		// data = data.concat(a+"\n");
		// if(a==0 && amplitiudes[i+1] ==0)
		// {
		// try {
		// FileUtils.writeStringToFile(amplitiudeFile, data, false);
		// data = "";
		// System.out.println("File "+fileIndex+" output");
		// fileIndex++;
		// amplitiudeFile = new File(directoryName+filename+fileIndex+"d.txt");
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// }

		// reading amplitude into a file with skipping the second channel
		// for(int i = 0; i < amplitiudes.length; i+=2)
		// {
		// System.out.println(((double)i)/amplitiudes.length * 100);
		// try {
		// FileUtils.writeStringToFile(new
		// File(directoryName+"concatanated2.txt"),amplitiudes[i]+"\n",true);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		// gr.renderWaveform(key1, "key3.jpg");
		// gr.renderSpectrogramData(key1.getSpectrogram().getNormalizedSpectrogramData(),
		// "keySpectrogram.jpg");
		// TimeSeries a = new
		// TimeSeries("/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/dataFile/trace0.csv",false,false,'\n');
		// TimeSeries b = new
		// TimeSeries("/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/dataFile/trace1.csv",false,false,'\n');
		// System.out.println(a.toString());
		// final DistanceFunction distFn;
		// distFn =
		// DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
		// final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(a, b, 1,
		// distFn);
		// System.out.println(info.toString());
		// System.out.println(com.dtw.FastDTW.getWarpDistBetween(a, b, distFn));
		// System.out.println(com.dtw.FastDTW.getWarpPathBetween(a, b, distFn));
	}

	public static void getSampleAmplitudesOrderOfMagnitude(Wave wave) {
		short[] amplitiudes = wave.getSampleAmplitudes();
		int[] oOM = new int[10];
		for (int i = 0; i < amplitiudes.length; i++) {
			if (Math.abs((amplitiudes[i])) < 10
					&& Math.abs((amplitiudes[i])) > 0)
				oOM[0]++;
			else if (Math.abs((amplitiudes[i])) < 100
					&& Math.abs((amplitiudes[i])) > 0)
				oOM[1]++;
			else if (Math.abs((amplitiudes[i])) < 1000
					&& Math.abs((amplitiudes[i])) > 0)
				oOM[2]++;
			else if (Math.abs((amplitiudes[i])) < 10000
					&& Math.abs((amplitiudes[i])) > 0)
				oOM[3]++;
			else if (Math.abs((amplitiudes[i])) < 100000
					&& Math.abs((amplitiudes[i])) > 0)
				oOM[4]++;
			else if (Math.abs((amplitiudes[i])) == 0)
				oOM[5]++;
			else
				oOM[6]++;
		}
		System.out.println("< 10 : " + oOM[0]);
		System.out.println("< 100 : " + oOM[1]);
		System.out.println("< 1000 : " + oOM[2]);
		System.out.println("< 10000 : " + oOM[3]);
		System.out.println("< 100000 : " + oOM[4]);
		System.out.println("equal 0 : " + oOM[6]);
		System.out.println("others: " + oOM[6]);
	}

}
