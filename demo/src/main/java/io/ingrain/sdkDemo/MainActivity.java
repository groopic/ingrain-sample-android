package io.ingrain.sdkDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.exoplayer.demo.R;

/**
 * Created by wingoku on 3/30/15.
 */
public class MainActivity extends Activity{

    String[] myActivities = {"MediaPlayerDemo", "ExoPlayerDemo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myActivities));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0)
                    startActivity(new Intent(MainActivity.this, MediaPlayerDemo.class));
                else
                    startActivity(new Intent(MainActivity.this, ExoPlayerDemo.class));
            }
        });
    }
}
