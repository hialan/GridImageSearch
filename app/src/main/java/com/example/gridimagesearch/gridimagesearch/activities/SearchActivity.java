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
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchActivity extends AppCompatActivity implements EditSearchSettingDialogListener {
    final private String sizeMap[] = {"icon", "medium", "xxlarge", "huge"};
    final private String typeMap[] = {"face", "photo", "clipart", "linart"};
    final private String STR_IMAGE_API="https://ajax.googleapis.com/ajax/services/search/images";

    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private SearchSetting searchSetting;
    private List<String> colors = null;

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
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int offset = page * 8;
                loadDataFromImageAPI(offset);
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
        searchSetting = new SearchSetting();
        colors = Arrays.asList(getResources().getStringArray(R.array.image_color_array));
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
        searchSetting.query = etQuery.getText().toString();
        aImageResults.clear();
        loadDataFromImageAPI(0);
    }

    private void loadDataFromImageAPI(int offset) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = getSearchParams(searchSetting, offset);
        client.get(STR_IMAGE_API, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultJson = null;

                try {
                    imageResultJson = response.getJSONObject("responseData").getJSONArray("results");
                    //imageResults.clear();
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
        EditSearchSettingDialog editSearchSettingDialog = EditSearchSettingDialog.newInstance(searchSetting);
        editSearchSettingDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditSeachSettingDialog(SearchSetting newSetting) {
        Toast.makeText(this, newSetting.toString(), Toast.LENGTH_SHORT);
        searchSetting.copy(newSetting);
    }

    private RequestParams getSearchParams(SearchSetting searchSetting, int start) {
        int count = 8;
        RequestParams params = new RequestParams();
        params.put("v", "1.0");
        params.put("q", searchSetting.query);
        params.put("imgcolor", colors.get(searchSetting.color));
        params.put("imgsz", sizeMap[searchSetting.size]);
        params.put("imgtype", typeMap[searchSetting.type]);
        params.put("as_sitesearch", searchSetting.site);
        params.put("rsz", count);
        params.put("start", start);

        Toast.makeText(this, searchSetting.toString(), Toast.LENGTH_SHORT);

        return params;
    }
}
