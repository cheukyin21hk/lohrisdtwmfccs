import com.musicg.wave.Wave;


public class TestAlgo {
	public static void main(String args[])
	{

		Wave bellRinging = new Wave("/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/sound/key2.wav");
		Wave bellRinging2 = new Wave("/Users/lohris/programming/FYP/Dev/testingworkplace/TestingDTWMCSS/src/sound/DKey1.wav");
		System.out.println(bellRinging);
		System.out.println("Score :"+bellRinging.getFingerprintSimilarity(bellRinging2).getScore());
		System.out.println("Similiarity :"+bellRinging.getFingerprintSimilarity(bellRinging2).getSimilarity());
	}
}
