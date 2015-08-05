package com.example.gridimagesearch.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
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

    private StaggeredGridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private SearchSetting searchSetting;
    private List<String> colors = null;

    private void setupViews() {
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                newQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //newQuery(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
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

    private void newQuery(String query) {
        searchSetting.query = query;
        aImageResults.clear();
        loadDataFromImageAPI(0);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void loadDataFromImageAPI(int offset) {
        if(isNetworkAvailable() == false) {
            Toast.makeText(this, "Internet is unavailable", Toast.LENGTH_SHORT).show();
            return ;
        }

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
        String oldQeury = searchSetting.query;
        searchSetting.copy(newSetting);
        if(oldQeury != null && oldQeury.length() > 0) {
            newQuery(oldQeury);
        }
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

//        Toast.makeText(this, searchSetting.toString(), Toast.LENGTH_SHORT).show();

        return params;
    }
}
