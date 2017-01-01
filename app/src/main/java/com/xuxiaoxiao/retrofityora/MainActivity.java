package com.xuxiaoxiao.retrofityora;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

public class MainActivity extends AppCompatActivity {
    private class GistFile{
        public String type;
        public String filename;
    }
    private class Gist{
        public String id;
        public HashMap<String,GistFile> files;

        @Override
        public String toString(){
            String output = id + ": ";

            for(Map.Entry<String,GistFile> file:files.entrySet()){
                output += file.getKey() + "=" + file.getValue().type + ", ";
            }
            return output;
        }
    }

    private class UserSummery{
        public String login;
        public String id;

        @Override
        public String toString(){
            return "UserSummery{" + "login = " + login + "id = " + id;
        }
    }

    private class UsersSearchResult{
        public int total_count;
        public boolean incomplete_resule;
        public List<UserSummery> items;
    }

    private interface GithubService{
        @GET("/gists/public")
        List<Gist> getPublicGists();

        @GET("/search/users")
        UsersSearchResult searchUsers(@Query("q") String query);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();

        GithubService service = adapter.create(GithubService.class);

        ArrayAdapter<Object> listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.activity_main_listView);
        listView.setAdapter(listAdapter);

        listAdapter.addAll(service.searchUsers("wuqiang5733").items);
//        listAdapter.addAll(service.searchUsers("wuqiang5733"));
//        listAdapter.addAll(service.getPublicGists());
    }
}
