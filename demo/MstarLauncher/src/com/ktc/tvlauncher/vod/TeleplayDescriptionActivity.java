package com.ktc.tvlauncher.vod;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktc.tvlauncher.R;
import com.ktc.tvlauncher.utils.Constant;
import com.ktc.tvlauncher.utils.Logger;

public class TeleplayDescriptionActivity extends Activity implements OnClickListener{
    private Button watch;
    private Button back;
    private ImageView teleplayImage;
    private TextView director;
    private TextView actor;
    private TextView description;
    private TextView teleplayName;
    private int teleplayNumber;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.teleplays_description);
        findView();
        Init();
        setListener();
    }
    
    public void setListener(){
        watch.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void findView() {
        watch = (Button) findViewById(R.id.imovie_description_watch);
        back = (Button) findViewById(R.id.imovie_description_back);
        director = (TextView) findViewById(R.id.imovie_description_director_content);
        actor = (TextView) findViewById(R.id.imovie_description_actor_content);
        description = (TextView) findViewById(R.id.imovie_description_description_content);
        teleplayName = (TextView) findViewById(R.id.imovie_description_movie_name);
        teleplayImage = (ImageView) findViewById(R.id.imovie_description_image);
    }

    public void Init() {
        
    	teleplayNumber = getIntent().getIntExtra("teleplayNumber", 0);
        SharedPreferences sp = getSharedPreferences(Constant.SAVE_TELEPLAYSINFO, MODE_PRIVATE);
        String tx_actor = sp.getString("teleplay_actor_" + teleplayNumber, "");
        String tx_director = sp.getString("teleplay_director_" + teleplayNumber, "");
        String tx_description = sp.getString("teleplay_description_" + teleplayNumber, "");
        String tx_movieName = sp.getString("teleplay_title_" + teleplayNumber, "");
        actor.setText(tx_actor);
        director.setText(tx_director);
        description.setText("     " + tx_description);
        teleplayName.setText(tx_movieName);
        new LoadImage().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imovie_description_back:
                finish();
                break;
            case R.id.imovie_description_watch:
                SharedPreferences sp = getSharedPreferences(Constant.SAVE_TELEPLAYSINFO, MODE_PRIVATE);
                int id = sp.getInt("teleplay_id_" + teleplayNumber, 0);
                Log.i("test","id:"+id);
                Intent intent = new Intent("com.wasuali.action.programinfo");
            	intent.putExtra("Id", id);
            	intent.putExtra("Domain", "bs3-auth.sdk.wasu.tv");
            	intent.putExtra("IsFavorite", false);
            	startActivity(intent);
                break;
            default:
                break;
        }

    }
    class LoadImage extends AsyncTask<Void,Bitmap,Bitmap>{

		@Override
		protected Bitmap doInBackground(Void... arg0) {
			SharedPreferences sp = getSharedPreferences(Constant.SAVE_TELEPLAYSINFO, MODE_PRIVATE);
			  String imageName = sp.getString("teleplay_pic_name_" + teleplayNumber, "");
			  Bitmap bitmap =null;  
			  /*			  try {
		            FileInputStream fis = openFileInput(imageName);
		            bitmap = BitmapFactory.decodeStream(fis);
		            fis.close();
		          
		            
		            
		        } catch (FileNotFoundException e) {
		            //Log.i(Constants.TAG, "load image fail FileNotFoundException");
		        } catch (IOException e) {
		           // Log.i(Constants.TAG, "load image fail IOException");
		        }*/
			  String url = getApplicationContext().getFilesDir().toString() + "/teleplaysImages/" + imageName;
			 // File file = getApplicationContext().getFileStreamPath(imageName);
	            bitmap = BitmapFactory.decodeFile(url);
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			teleplayImage.setImageBitmap(result);
		}

    	
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		watch.setFocusable(true);
		watch.requestFocus();
	}
}
