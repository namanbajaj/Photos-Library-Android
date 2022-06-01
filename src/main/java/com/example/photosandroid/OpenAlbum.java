package com.example.photosandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class OpenAlbum extends AppCompatActivity {

    String albumname;
    Album album;

    GridView imagegrid;
    ArrayList<Bitmap> bitmapList;
    ImageAdapter adapter;
    Bitmap selectedPhoto = null;

    ArrayList<String> albumnames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_album);
        Bundle bundle = getIntent().getExtras();

        albumname = bundle.getString("Selected Album");
        album = (Album) bundle.getSerializable("Selected Album Object");
        albumnames = bundle.getStringArrayList("Album List");
//        System.out.println(albumname);
//        System.out.println("Album check " + album);
//        for(String aname : albumnames) System.out.println("From albumnames: " + aname);


        TextView albumnametext = (TextView) findViewById(R.id.albumnametextfield);
        albumnametext.setText(albumname);

        imagegrid = (GridView) findViewById(R.id.imagegridview);
        bitmapList = new ArrayList<Bitmap>();

        adapter = new ImageAdapter(this, bitmapList);
        imagegrid.setAdapter(adapter);

//        System.out.println(bitmapList);
        for (Photo p : album.photos) {
            Bitmap thumbnail = null;
            thumbnail = decodeString(p.location);
            bitmapList.add(thumbnail);
            adapter.notifyDataSetChanged();
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
    }


    public void displayPhoto(View view) {
        if (selectedPhoto == null) {
            noPhotoSelected(OpenAlbum.this);
            return;
        }

        Intent intent = new Intent(this, DisplayPhoto.class);

        Photo sendPhoto = null;
        int i;
        for (i = 0; i < album.photos.size(); i++) {
            String p = encodeBitmap(selectedPhoto);
            String p2 = encodeBitmap(decodeString(album.photos.get(i).location));
//                System.out.println(p.equals(p2));
//                System.out.println(encodeBitmap(selectedPhoto));
//                System.out.println(album.photos.get(i).location);
//                System.out.println(p.equals(album.photos.get(i).location));
            if (p.equals(p2)) {
                sendPhoto = album.photos.get(i);
                break;
            }
        }

        intent.putExtra("Photo", sendPhoto);
        intent.putExtra("Album", album);
        intent.putExtra("Starting Index", i);
        intent.putStringArrayListExtra("Album List", albumnames);

        startActivity(intent);
    }

    public String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();

        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public Bitmap decodeString(String s) {
        byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private void relaunch() {
        Intent intent = new Intent(OpenAlbum.this, OpenAlbum.class);
        intent.putExtra("Selected Album", albumname);
        intent.putExtra("Selected Album Object", album);
        intent.putStringArrayListExtra("Album List", albumnames);
        startActivity(intent);
    }

    private void noPhotoSelected(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("No Photo Selected")
                .setMessage("No photo selected. Click on one to select.").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    public void createPhoto(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri fullPhotoUri = data.getData();
            Bitmap thumbnail = null;
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), fullPhotoUri);
            } catch (IOException e) {
            }

            bitmapList.add(thumbnail);
//            System.out.println("Thumbnail image: " + thumbnail);
            adapter.notifyDataSetChanged();
//            System.out.println(data.getData());

            FileOutputStream fos = null;
            try {
                album.addPhoto(encodeBitmap(thumbnail));
//                System.out.println(album);
                // overwrite dat file
                FileOutputStream out = OpenAlbum.this.openFileOutput(album.name + ".dat", Activity.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(album);
                out.getFD().sync();
                out.close();
                oos.close();
                System.out.println(album);
                recreate();

            } catch (IOException e) {
                e.printStackTrace();
            }

            relaunch();
//            recreate();

        }

    }


    public void deletePhoto(View view) {
        if (selectedPhoto == null) {
            noPhotoSelected(OpenAlbum.this);
            return;
        }

        FileOutputStream fos = null;
        try {
            System.out.println("in method");
            for (int i = 0; i < album.photos.size(); i++) {
                String p = encodeBitmap(selectedPhoto);
                String p2 = encodeBitmap(decodeString(album.photos.get(i).location));
//                System.out.println(p.equals(p2));
//                System.out.println(encodeBitmap(selectedPhoto));
//                System.out.println(album.photos.get(i).location);
//                System.out.println(p.equals(album.photos.get(i).location));
                if (p.equals(p2)) {
                    album.photos.remove(i);
                    album.size--;
                    System.out.println("found");
                    break;
                }
            }

            System.out.println(album);
            // overwrite dat file
            FileOutputStream out = OpenAlbum.this.openFileOutput(album.name + ".dat", Activity.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(album);
            out.getFD().sync();
            out.close();
            oos.close();

        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }

        bitmapList.remove(selectedPhoto);
        selectedPhoto = null;
        adapter.notifyDataSetChanged();
        System.out.println(album);
//        recreate();

        relaunch();
    }

    public void movePhoto(View view) {
        moveHelper(OpenAlbum.this);
    }

    public void moveHelper(Context c) {
        if (selectedPhoto == null) {
            noPhotoSelected(OpenAlbum.this);
            return;
        }

        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new album")
                .setMessage("Enter name of the album")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String moveto = String.valueOf(taskEditText.getText());

//                        System.out.println("addition is " + addition);

                        if (moveto.equals(""))
                            invalidName(OpenAlbum.this);

                        else if (!albumnames.contains(moveto))
                            invalidLocation(OpenAlbum.this);

                        else {
                            // find picture and remove from current album
                            String movePic = encodeBitmap(selectedPhoto);
                            Photo p = null;
                            for (int i = 0; i < album.photos.size(); i++) {
                                String p2 = encodeBitmap(decodeString(album.photos.get(i).location));
//                                System.out.println(p2);
                                if (movePic.equals(p2)) {
                                    p = album.photos.get(i);
                                    album.photos.remove(i);
                                    album.size--;
                                    System.out.println("found");
                                    try {
                                        overwriteAlbum();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }

                            // load new album
                            Album a = null;
                            try {
                                FileInputStream fileIn = OpenAlbum.this.getApplicationContext().openFileInput(moveto + ".dat");
                                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                                a = (Album) objectIn.readObject();
                                a.photos.add(p);
                                a.size++;
                                objectIn.close();
                                fileIn.close();
                            }catch(IOException | ClassNotFoundException e){}

                            // overwrite new album
                            try {
                                FileOutputStream out = OpenAlbum.this.openFileOutput(moveto + ".dat", Activity.MODE_PRIVATE);
                                ObjectOutputStream oos = new ObjectOutputStream(out);
                                oos.writeObject(a);
                                out.getFD().sync();
                                oos.close();
                                out.close();
                            } catch(IOException e){}

                            bitmapList.remove(selectedPhoto);
                            adapter.notifyDataSetChanged();

//                            recreate();
                            relaunch();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }

    private void overwriteAlbum() throws IOException {
        FileOutputStream out = OpenAlbum.this.openFileOutput(album.name + ".dat", Activity.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(album);
        out.getFD().sync();
        out.close();
        oos.close();
    }

    private void invalidLocation(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Album not found")
                .setMessage("Album with entered name not found.").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    private void invalidName(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Invalid Name")
                .setMessage("Album with invalid name detected. Please enter a different name").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}