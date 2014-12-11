package random.frank.segmenttab.test;

import java.util.Timer;
import java.util.TimerTask;

import random.frank.segmenttab.R;
import random.frank.segmenttab.SegmentTab;
import random.frank.segmenttab.SegmentTab.TabClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 
 * @author frank.random
 *
 */
public class MainActivity extends Activity {

	private SegmentTab mSegTab;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		mSegTab = (SegmentTab) findViewById(R.id.seg_t);
		
		mSegTab.setTabClickListener(new TabClickListener() {
			
			@Override
			public void onTabClick(int index) {
				Toast.makeText(MainActivity.this, "on click " + index, Toast.LENGTH_SHORT).show();
			}
		});
		
		TimerTask tt = new TimerTask() {
			
			@Override
			public void run() {
				MainActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mSegTab.select(1);
					}
				});
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(tt, 3000);
	}

	
}
