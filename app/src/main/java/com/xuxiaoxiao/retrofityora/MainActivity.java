package com.xuxiaoxiao.retrofityora;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
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

    private class UserDetails{
        public String id;
        public String location;

        @Override
        public String toString(){
            return "UserDetails { id = " + id + "location = " + location;
        }
    }

    private interface GithubService{
        @GET("/gists/public")
        List<Gist> getPublicGists();

        @GET("/search/users")
        UsersSearchResult searchUsers(@Query("q") String query);

        @GET("/users/{username}")
        UserDetails getUser(@Path("username") String username);

        // 下面是演示在另一个线程执行的情况
        @GET("/users/{username}")
        void getUserAsync(@Path("username") String username , Callback<UserDetails> callback);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();

        final GithubService service = adapter.create(GithubService.class);

        final ArrayAdapter<Object> listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.activity_main_listView);
        listView.setAdapter(listAdapter);

        listAdapter.addAll(service.searchUsers("wuqiang5733").items);
//        listAdapter.addAll(service.searchUsers("wuqiang5733"));
//        listAdapter.addAll(service.getPublicGists());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                UserSummery summery = (UserSummery)listAdapter.getItem(position);
//                Toast.makeText(MainActivity.this,summery.login,Toast.LENGTH_SHORT).show();
                service.getUserAsync(summery.login, new Callback<UserDetails>() {
                    @Override
                    public void success(UserDetails userDetails, Response response) {
                        Toast.makeText(MainActivity.this,userDetails.id,Toast.LENGTH_SHORT).show();
//                        Toast.makeText(MainActivity.this,userDetails.location,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
    }
}
