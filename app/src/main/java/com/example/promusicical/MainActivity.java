package com.example.promusicical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String []items;
    private static final String COMMON_TAG ="Orientationchange" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listviewsong);

        // runtime permission
        runtimepermission();
    }
    public void runtimepermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaysongs();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> fetchsongs(File file){
        ArrayList arraylist = new ArrayList();
        File songs[] = file.listFiles();
        if(songs != null){
            for(File myfile: songs){
                if(myfile.isDirectory() && !myfile.isHidden()){
                    arraylist.addAll(fetchsongs(myfile));
                }
                else{
                    if(myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arraylist.add(myfile);
                    }
                }
            }
        }
        return arraylist;
    }

    void displaysongs(){
        final ArrayList<File> mysong = fetchsongs(Environment.getExternalStorageDirectory());
        items = new String[mysong.size()];
        for(int i=0;i< mysong.size();i++){
            items[i] = mysong.get(i).getName().toString().replace(".mp3","");
        }
        Customadapter customadapter = new Customadapter();
        listView.setAdapter(customadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname = (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(), Playsong.class)
                        .putExtra("song", mysong)
                        .putExtra("songname", songname)
                        .putExtra("pos", position));

            }
        });
    }

    public class Customadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
//            convertView = getLayoutInflater().inflate(R.layout.list_item,parent,null);
            TextView txtsongname = convertView.findViewById(R.id.txtsongname);
            txtsongname.setText(items[position]);
            txtsongname.setSelected(true);
            return convertView;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.i(COMMON_TAG,"landscape");
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i(COMMON_TAG,"portrait");
        }
    }

}