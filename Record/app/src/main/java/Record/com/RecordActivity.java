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
	private String temFile; //以日期時間做暫存檔名
	private File recFile, recPATH;
	private List<String> lstArray=new ArrayList<String>(); //檔名陣列
	private int cListItem=0; //目前播放錄音

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
    	recPATH=Environment.getExternalStorageDirectory(); //SD卡路徑
    	mediaplayer=new MediaPlayer();
		imgDisable(imgStop);
    	recList(); //錄音列表
	}

    private ImageView.OnClickListener listener=new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
          switch(v.getId())
          {
            case R.id.imgRecord:  //錄音
			try {
				Time t=new Time();
				t.setToNow(); //取得現在日期及時間
				temFile="R"+add0(t.year)+add0(t.month+1)+add0(t.monthDay)+add0(t.hour)+add0(t.minute)+add0(t.second);
				recFile=new File(recPATH + "/" + temFile + ".amr");
				mediarecorder= new MediaRecorder();
				mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				mediarecorder.setOutputFile(recFile.getAbsolutePath());
				mediarecorder.prepare();
				mediarecorder.start();
				txtRec.setText("正在錄音…………");
				imgDisable(imgRecord); //處理按鈕是否可按
				imgDisable(imgPlay);
				imgEnable(imgStop);
				} catch (IOException e) {
					e.printStackTrace();
				}
            	break;
            case R.id.imgPlay:  //播放
            	playSong(recPATH + "/" + lstArray.get(cListItem).toString());
            	break;
            case R.id.imgStop:  //停止
				if (mediaplayer.isPlaying()) { ////停止播放
					mediaplayer.reset();
				} else if(recFile!=null) { //停止錄音
            		mediarecorder.stop();
            		mediarecorder.release();
            		mediarecorder=null;
    				txtRec.setText("停止" + recFile.getName() + "錄音！");
    				recList();
            	}
				imgEnable(imgRecord);
				imgEnable(imgPlay);
				imgDisable(imgStop);
               	break;
            case R.id.imgEnd:  //結束
            	finish();
              	break;
           }
        }
 	};

    private ListView.OnItemClickListener lstListener=new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			cListItem = position; //取得點選位置
			playSong(recPATH + "/" + lstArray.get(cListItem).toString()); //播放錄音
		}
     };

 	private void playSong(String path) {
 		try
 		{
 			mediaplayer.reset();
  			mediaplayer.setDataSource(path); //播放錄音路徑
 			mediaplayer.prepare();
 			mediaplayer.start(); //開始播放
 			txtRec.setText("播放：" + lstArray.get(cListItem).toString());
 			imgDisable(imgRecord);
 			imgDisable(imgPlay);
 			imgEnable(imgStop);
 			mediaplayer.setOnCompletionListener(new OnCompletionListener() {
 				public void onCompletion(MediaPlayer arg0) {
 					txtRec.setText(lstArray.get(cListItem).toString() + "播完！");
 					imgEnable(imgRecord);
 					imgEnable(imgPlay);
 					imgDisable(imgStop);
 				}
 			});
  		} catch (IOException e) {}
 	}

 	//取得錄音檔案列表
 	public void recList() {
 		lstArray.clear(); //清除陣列
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

 	private void imgEnable(ImageView image) { //使按鈕有效
 		image.setEnabled(true);
 		image.setAlpha(255);
 	}

 	private void imgDisable(ImageView image) { //使按鈕失能
 		image.setEnabled(false);
 		image.setAlpha(50);
 	}

 	protected String add0(int n) { //個位數前面補零
 		if(n<10) return ("0" + n);
 		else return ("" + n);
 	}
}