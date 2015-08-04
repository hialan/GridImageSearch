package com.example.gridimagesearch.gridimagesearch.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gridimagesearch.gridimagesearch.R;
import com.example.gridimagesearch.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ImageDisplayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

//        getActionBar().hide();

        final ImageResult result = (ImageResult) getIntent().getSerializableExtra("result");
        Toast.makeText(this, result.fullUrl, Toast.LENGTH_SHORT).show();
        final ImageView ivImageResult = (ImageView) findViewById(R.id.ivImageResult);
        Picasso.with(this)
                .load(result.thumbUrl)
                .fit()
                .centerInside()
                .into(ivImageResult, new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.with(ImageDisplayActivity.this)
                                .load(result.fullUrl)
                                .placeholder(ivImageResult.getDrawable())
                                .fit()
                                .centerInside()
                                .into(ivImageResult);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
