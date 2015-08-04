package com.example.gridimagesearch.gridimagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.gridimagesearch.gridimagesearch.R;
import com.example.gridimagesearch.gridimagesearch.adapters.ImageResultsAdapter;
import com.example.gridimagesearch.gridimagesearch.fragments.EditSearchSettingDialog;
import com.example.gridimagesearch.gridimagesearch.fragments.EditSearchSettingDialog.EditSearchSettingDialogListener;
import com.example.gridimagesearch.gridimagesearch.models.ImageResult;
import com.example.gridimagesearch.gridimagesearch.models.SearchSetting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements EditSearchSettingDialogListener {
    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;

    final private String STR_IMAGE_API="https://ajax.googleapis.com/ajax/services/search/images?v=1.0&";

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                i.putExtra("result", result);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        imageResults = new ArrayList<>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
            showEditSettingDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSearch(View v) {
        String query = etQuery.getText().toString();
        Toast.makeText(this, "Search for: " + query, Toast.LENGTH_SHORT).show();

        String requestQuery = STR_IMAGE_API + "q=" + query;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(requestQuery, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultJson = null;

                try {
                    imageResultJson = response.getJSONObject("responseData").getJSONArray("results");
                    imageResults.clear();
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("INFO", imageResults.toString());
            }
        });

    }

    private void showEditSettingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SearchSetting searchSetting = new SearchSetting();
        EditSearchSettingDialog editSearchSettingDialog = EditSearchSettingDialog.newInstance(searchSetting);
        editSearchSettingDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditSeachSettingDialog(SearchSetting setting) {
        Toast.makeText(this, setting.toString(), Toast.LENGTH_SHORT);
    }
}
