package com.example.photosandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    Album album;
    Photo photo;
    ArrayList<Photo> photos;
    ArrayList<String> tags; // used for listview
    int index;

    Button right;
    Button left;

    ImageView img;

    ListView taglist;
    ArrayAdapter<String> adapter;

    String tagSelected;

    ArrayList<String> albumnames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        Bundle bundle = getIntent().getExtras();

        album = (Album) bundle.getSerializable("Album");
        photo = (Photo) bundle.getSerializable("Photo");
        photos = album.photos;
        index = bundle.getInt("Starting Index");

        albumnames = bundle.getStringArrayList("Album List");

        img = (ImageView) findViewById(R.id.imageView);
        taglist = (ListView) findViewById(R.id.taglistview);

        tags = new ArrayList<String>();
        for(Tag t : photo.tags)
            tags.add(t.toString());

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tags);
        taglist.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        img.setImageBitmap(decodeString(photo.location));

        right = (Button) findViewById(R.id.gorightslideshow);
        left = (Button) findViewById(R.id.goleftslideshow);

        taglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                tagSelected = adapter.getItem(position);
//                Toast.makeText(DisplayPhoto.this, "item clicked was " + tagSelected, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTagList(){
        tags.clear();
        for(Tag t : photo.tags)
            tags.add(t.toString());

        adapter.notifyDataSetChanged();
    }

    private void overwriteAlbum() throws IOException {
        FileOutputStream out = DisplayPhoto.this.openFileOutput(album.name + ".dat", Activity.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(album);
        out.getFD().sync();
        oos.close();
    }

    public Bitmap decodeString(String s){
        byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void activateSlideshow(View view) {
        if(left.getVisibility() == View.INVISIBLE) {
            right.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
        }

        else if(left.getVisibility() == View.VISIBLE) {
            right.setVisibility(View.INVISIBLE);
            left.setVisibility(View.INVISIBLE);
        }
    }

    public void goLeft(View view){
        index--;
        if(index < 0)
            index = photos.size()-1;

        photo = photos.get(index);

        img.setImageBitmap(decodeString(photo.location));
        setTagList();
    }

    public void goRight(View view){
        index++;
        if(index == photos.size())
            index = 0;

        photo = photos.get(index);

        img.setImageBitmap(decodeString(photo.location));
        setTagList();
    }

    public void addTag(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Tag Key and Value");

        final EditText one = new EditText(this);
        final EditText two = new EditText(this);

        one.setHint("Key");
        two.setHint("Value");

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(one);
        lay.addView(two);
        builder.setView(lay);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String key = one.getText().toString();
                String value = two.getText().toString();

                if(!key.equalsIgnoreCase("person") && !key.equalsIgnoreCase("location") || value.equals("")){
                    invalidKey(DisplayPhoto.this);
                }

                else{
                    Tag newTag = new Tag(key, value);
                    photos.get(index).tags.add(newTag);
                    tags.add(newTag.toString());
                    adapter.notifyDataSetChanged();
                    try {
                        overwriteAlbum();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void invalidKey(Context c){
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Invalid Key")
                .setMessage("Keys must be \'person\' or \'location\' with a set value").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    public void deleteTag(View view){
        String tString = "";
        for(int i = 0; i < tags.size(); i++){
            tString = tags.get(i);
            if(tString.equals(tagSelected)){
                tags.remove(i);
                break;
            }
        }

        for(int i = 0; i < photos.get(index).tags.size(); i++){
//            System.out.println(photos.get(index).tags.get(i));
            if(photos.get(index).tags.get(i).toString().equals(tagSelected)){
                System.out.println(photos.get(index).tags.get(i));
                photos.get(index).tags.remove(i);
                break;
            }
        }
        try {
            overwriteAlbum();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, OpenAlbum.class);
        intent.putExtra("Selected Album", album.name);
        intent.putExtra("Selected Album Object", album);
        intent.putStringArrayListExtra("Album List", albumnames);
        startActivity(intent);
    }

}
