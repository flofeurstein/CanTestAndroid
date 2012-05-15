package at.fhv.uc.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import at.fhv.uc.can.client.DataClient;
import at.fhv.uc.can.client.DataClientAsyncTask;

public class CanTestAndroidActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void startService(View view){
    	TextView tv = (TextView) findViewById(R.id.canInfoTextView);
    	tv.append("\n");
    	
    	new DataClientAsyncTask(tv).execute((Void)null);
    }
    
    public void appendCanInfoTextView(String str){
    	TextView tv = (TextView) findViewById(R.id.canInfoTextView);
    	tv.append(str);
    }
}