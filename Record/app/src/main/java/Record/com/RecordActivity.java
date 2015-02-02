package Record.com;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RecordActivity extends Activity {
	private ImageView imgRecord, imgStop, imgPlay, imgEnd;
	private ListView lstRec;
	private TextView txtRec;
	private MediaPlayer mediaplayer;
	private MediaRecorder mediarecorder;
	private String temFile; //�H����ɶ����Ȧs�ɦW
	private File recFile, recPATH;
	private List<String> lstArray=new ArrayList<String>(); //�ɦW�}�C
	private int cListItem=0; //�ثe�������

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        imgRecord=(ImageView)findViewById(R.id.imgRecord); 
		imgPlay=(ImageView)findViewById(R.id.imgPlay); 
		imgStop=(ImageView)findViewById(R.id.imgStop);
		imgEnd=(ImageView)findViewById(R.id.imgEnd); 
		lstRec=(ListView)findViewById(R.id.lstRec); 
		txtRec=(TextView)findViewById(R.id.txtRec); 
		imgRecord.setOnClickListener(listener);
		imgPlay.setOnClickListener(listener);
		imgStop.setOnClickListener(listener);
		imgEnd.setOnClickListener(listener);
    	lstRec.setOnItemClickListener(lstListener);
    	recPATH=Environment.getExternalStorageDirectory(); //SD�d���|
    	mediaplayer=new MediaPlayer();
		imgDisable(imgStop);
    	recList(); //�����C��
	}

    private ImageView.OnClickListener listener=new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
          switch(v.getId())
          {
            case R.id.imgRecord:  //����
			try {
				Time t=new Time();
				t.setToNow(); //���o�{�b����ήɶ�
				temFile="R"+add0(t.year)+add0(t.month+1)+add0(t.monthDay)+add0(t.hour)+add0(t.minute)+add0(t.second);
				recFile=new File(recPATH + "/" + temFile + ".amr");
				mediarecorder= new MediaRecorder();
				mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				mediarecorder.setOutputFile(recFile.getAbsolutePath());
				mediarecorder.prepare();
				mediarecorder.start();
				txtRec.setText("���b�����K�K�K�K");
				imgDisable(imgRecord); //�B�z���s�O�_�i��
				imgDisable(imgPlay);
				imgEnable(imgStop);
				} catch (IOException e) {
					e.printStackTrace();
				}
            	break;
            case R.id.imgPlay:  //����
            	playSong(recPATH + "/" + lstArray.get(cListItem).toString());
            	break;
            case R.id.imgStop:  //����
				if (mediaplayer.isPlaying()) { ////�����
					mediaplayer.reset();
				} else if(recFile!=null) { //�������
            		mediarecorder.stop();
            		mediarecorder.release();
            		mediarecorder=null;
    				txtRec.setText("����" + recFile.getName() + "�����I");
    				recList();
            	}
				imgEnable(imgRecord);
				imgEnable(imgPlay);
				imgDisable(imgStop);
               	break;
            case R.id.imgEnd:  //����
            	finish();
              	break;
           }
        }
 	};

    private ListView.OnItemClickListener lstListener=new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			cListItem = position; //���o�I���m
			playSong(recPATH + "/" + lstArray.get(cListItem).toString()); //�������
		}
     };

     	private void playSong(String path) {
 		try
 		{
 			mediaplayer.reset();
  			mediaplayer.setDataSource(path); //����������|
 			mediaplayer.prepare();
 			mediaplayer.start(); //�}�l����
 			txtRec.setText("����G" + lstArray.get(cListItem).toString());
 			imgDisable(imgRecord);
 			imgDisable(imgPlay);
 			imgEnable(imgStop);
 			mediaplayer.setOnCompletionListener(new OnCompletionListener() {
 				public void onCompletion(MediaPlayer arg0) {
 					txtRec.setText(lstArray.get(cListItem).toString() + "�����I");
 					imgEnable(imgRecord);
 					imgEnable(imgPlay);
 					imgDisable(imgStop);
 				}
 			});
  		} catch (IOException e) {}
 	}

 	//���o�����ɮצC��
 	public void recList() {
 		lstArray.clear(); //�M���}�C
		for(File file:recPATH.listFiles()) {
			if(file.getName().toLowerCase().endsWith(".amr")) {
				lstArray.add(file.getName());
			}
		}
		if(lstArray.size()>0) {
 			ArrayAdapter<String> adaRec=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstArray);
 			lstRec.setAdapter(adaRec);
		}
 	}

 	private void imgEnable(ImageView image) { //�ϫ��s����
 		image.setEnabled(true);
 		image.setAlpha(255);
 	}

 	private void imgDisable(ImageView image) { //�ϫ��s����
 		image.setEnabled(false);
 		image.setAlpha(50);
 	}

 	protected String add0(int n) { //�Ӧ�ƫe���ɹs
 		if(n<10) return ("0" + n);
 		else return ("" + n);
 	}
}