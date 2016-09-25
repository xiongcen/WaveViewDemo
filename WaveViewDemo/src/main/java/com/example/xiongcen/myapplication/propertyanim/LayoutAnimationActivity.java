package com.example.xiongcen.myapplication.propertyanim;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xiongcen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongcen on 16/9/25.
 */
public class LayoutAnimationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_animation);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("this is :" +i);
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, list));
    }
}
