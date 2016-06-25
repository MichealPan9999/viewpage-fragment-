
package com.ktc.tvlauncher.vod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

public class MovieDescriptionActivity extends Activity implements OnClickListener {
    private Button watch;
    private Button back;
    private ImageView movieImage;
    private TextView director;
    private TextView actor;
    private TextView description;
    private TextView movieName;
    private int movieNumber;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.movies_description);
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
        movieName = (TextView) findViewById(R.id.imovie_description_movie_name);
        movieImage = (ImageView) findViewById(R.id.imovie_description_image);
    }

    public void Init() {
        
        movieNumber = getIntent().getIntExtra("movieNumber", 0);
        SharedPreferences sp = getSharedPreferences(Constant.SAVE_MOVIESINFO, MODE_PRIVATE);
        String tx_actor = sp.getString("movie_actor_" + movieNumber, "");
        String tx_director = sp.getString("movie_director_" + movieNumber, "");
        String tx_description = sp.getString("movie_description_" + movieNumber, "");
        String tx_movieName = sp.getString("movie_title_" + movieNumber, "");
        actor.setText(tx_actor);
        director.setText(tx_director);
        description.setText("     " + tx_description);
        movieName.setText(tx_movieName);
        new LoadImage().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imovie_description_back:
                finish();
                break;
            case R.id.imovie_description_watch:
                SharedPreferences sp = getSharedPreferences(Constant.SAVE_MOVIESINFO, MODE_PRIVATE);
                int id = sp.getInt("movie_id_" + movieNumber, 0);
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
			SharedPreferences sp = getSharedPreferences(Constant.SAVE_MOVIESINFO, MODE_PRIVATE);
			  String imageName = sp.getString("movie_pic_name_" + movieNumber, "");
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
			  String url = getApplicationContext().getFilesDir().toString() + "/moviesImages/" + imageName;
			 // File file = getApplicationContext().getFileStreamPath(imageName);
	            bitmap = BitmapFactory.decodeFile(url);
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			movieImage.setImageBitmap(result);
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
