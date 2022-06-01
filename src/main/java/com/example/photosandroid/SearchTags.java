package com.example.photosandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchTags extends AppCompatActivity {

    ArrayList<Bitmap> bitmapList;
    GridView imagegrid;
    Bitmap selectedPhoto = null;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tags);

        imagegrid = (GridView) findViewById(R.id.imagegridview);
        bitmapList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        String tagName = bundle.getString("tag");
        String value = bundle.getString("value");
        ArrayList<Album> albumList = (ArrayList<Album>) bundle.get("albumList");

        adapter = new ImageAdapter(this, bitmapList);
        imagegrid.setAdapter(adapter);

        for (Album album: albumList) {
            for (Photo photo: album.photos) {
                for (Tag tag: photo.tags) {
                    if (tag.name.equalsIgnoreCase(tagName) && tag.value.length() >= value.length() && value.equalsIgnoreCase(tag.value.substring(0, value.length()))) {
                        Bitmap thumbnail = null;
                        thumbnail = decodeString(photo.location);
                        bitmapList.add(thumbnail);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        ImageView img = (ImageView) findViewById(R.id.selectedimageview);

        imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Bitmap selected = (Bitmap) adapter.getItem(i);
                Bitmap selected = (Bitmap) adapter.bitmapList.get(i);
                selectedPhoto = selected;
//                System.out.println("i: " + i);
//                System.out.println("list: " + adapter.bitmapList);
//                System.out.println(selected.describeContents());
//                Toast.makeText(OpenAlbum.this, "item clicked was " + selected, Toast.LENGTH_SHORT).show();

                img.setImageBitmap(selected);

            }
        });
        System.out.println(tagName);
        System.out.println(value);
    }

    public Bitmap decodeString(String s){
        byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}