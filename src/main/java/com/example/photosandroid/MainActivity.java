package com.example.photosandroid;
// comment to see if repo works sharad

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView albumlist;
    ArrayAdapter<String> adapter;

    String selected;
    ArrayList<String> albumsfromfile;

    ArrayList<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String path = getFilesDir() + File.separator + "albums.txt";
        File file = new File(path);

        albums = new ArrayList<Album>();

        // use this to reset file
//        file.delete();

        // check if album list exists on user's phone
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }

        // populate albumlistview with list of album names
        albumsfromfile = new ArrayList<String>();

        try {
            FileInputStream fis = openFileInput("albums.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            StringBuffer datax = new StringBuffer("");
            while (line != null) {
                datax.append(line);
                String name = datax.toString();
                albumsfromfile.add(name);

                System.out.println("Deserializing " + name);
                FileInputStream fileIn = MainActivity.this.getApplicationContext().openFileInput(name + ".dat");
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                Album a = (Album) objectIn.readObject();
                albums.add(a);

                datax.delete(0, datax.length());
                line = bufferedReader.readLine();
            }
            inputStreamReader.close();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            System.out.println("error1");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("error2");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("error3");
        }


        for (String s : albumsfromfile) System.out.println("Album " + s + " present");

        System.out.println("size is " + albums.size());

        for (Album alb : albums) System.out.println(alb.toString());

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, albumsfromfile);
        albumlist = (ListView) findViewById(R.id.albumlistview);
        albumlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        albumlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selected = adapter.getItem(position);
//                Toast.makeText(MainActivity.this, "item clicked was " + selected, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void openAlbum(View view) {
        if (selected == null) {
            noAlbumSelected(MainActivity.this);
            return;
        }

        Intent intent = new Intent(this, OpenAlbum.class);

        System.out.println(albums.size());

        Album send = null;
        for (Album a : albums) {
//            System.out.println(a.name);
//            System.out.println(selected);
//            System.out.println(a.name.equals(selected));
            if (a.name.equals(selected))
                send = a;
        }

        System.out.println(send);
        System.out.println(send.photos.size());

        intent.putExtra("Selected Album", selected);
        intent.putExtra("Selected Album Object", send);
        intent.putStringArrayListExtra("Album List", albumsfromfile);
        startActivity(intent);
    }

    public void createAlbum(View view) {
        showAddItemDialog(MainActivity.this);
    }

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new album")
                .setMessage("Enter name of the album")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String addition = String.valueOf(taskEditText.getText());
                        String task = addition + "\n";

//                        System.out.println("addition is " + addition);

                        if (addition.equals(""))
                            invalidName(MainActivity.this);

                        else if (adapter.getPosition(addition) != -1)
                            duplicateAlbum(MainActivity.this);

                        else {
                            try {
                                FileOutputStream fos = openFileOutput("albums.txt", Context.MODE_APPEND);
                                fos.write(task.getBytes());
                                fos.close();

//                                System.out.println(albums.size());

                                Album a = new Album(addition);
                                albums.add(a);
//                                albumsfromfile.add(a.name);

//                                System.out.println(albums.size());
//                                System.out.println(a);

                                // create dat file
                                FileOutputStream out = MainActivity.this.openFileOutput(a.name + ".dat", Activity.MODE_PRIVATE);
                                ObjectOutputStream oos = new ObjectOutputStream(out);
                                oos.writeObject(a);
                                out.getFD().sync();
                                oos.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("error4");
                            }
                            albumsfromfile.add(task);
                            adapter.notifyDataSetChanged();
                            recreate();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }


    private void duplicateAlbum(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Duplicate Album")
                .setMessage("Album with same name detected. Please enter a different name").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    private void noAlbumSelected(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("No Album Selected")
                .setMessage("No album selected. Click on one to select.").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    private void invalidName(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Invalid Name")
                .setMessage("Album with invalid name detected. Please enter a different name").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    private void invalidTag(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Invalid Tag")
                .setMessage("Tag with invalid values detected. Please enter different values.").setPositiveButton("Ok", null).create();
        dialog.show();
    }

    public void deleteAlbum(View view) {
        try {
            if (selected == null) {
                noAlbumSelected(MainActivity.this);
                return;
            }

            FileOutputStream fos = openFileOutput("albums.txt", Context.MODE_PRIVATE);

            for (int i = 0; i < albumsfromfile.size(); i++) {
                System.out.println("Current element is " + albumsfromfile.get(i));
                if (albumsfromfile.get(i).equals(selected)) {
                    albumsfromfile.remove(i);
                    break;
                }
            }

            for (int i = 0; i < albums.size(); i++) {
                if (albums.get(i).name.equals(selected)) {
                    albums.remove(i);
                    break;
                }
            }

            for (int i = 0; i < albumsfromfile.size(); i++) {
                fos.write((albumsfromfile.get(i) + "\n").getBytes());
            }

            String path = getFilesDir() + File.separator + selected + ".dat";
            File file = new File(path);
            file.delete();

            selected = null;

            adapter.notifyDataSetChanged();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renameAlbum(View view) {
        showRenameItemDialog(MainActivity.this);
    }

    private void showRenameItemDialog(Context c) {
        if (selected == null) {
            noAlbumSelected(MainActivity.this);
            return;
        }

        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Rename album")
                .setMessage("Enter new name for Album " + selected)
                .setView(taskEditText)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String addition = String.valueOf(taskEditText.getText());

                        if (addition.equals(""))
                            invalidName(MainActivity.this);

                        else if (adapter.getPosition(addition) != -1)
                            duplicateAlbum(MainActivity.this);

                        else {
                            try {
//                                FileOutputStream fos = openFileOutput("albums.txt", Context.MODE_APPEND);
//                                fos.write(task.getBytes());
//                                fos.close();

                                FileOutputStream fos = openFileOutput("albums.txt", Context.MODE_PRIVATE);

                                for (int i = 0; i < albumsfromfile.size(); i++) {
                                    if (albumsfromfile.get(i).equals(selected)) {
//                                        System.out.println("found");
                                        albumsfromfile.remove(i);
                                        albumsfromfile.add(addition);
                                        break;
                                    }
                                }
                                Album toRemove = null;
                                for (int i = 0; i < albums.size(); i++) {
                                    if (albums.get(i).name.equals(selected)) {
                                        toRemove = albums.get(i);
                                        toRemove.name = addition;
                                        albums.remove(i);
                                        albums.add(toRemove);
                                        break;
                                    }
                                }

                                for (int i = 0; i < albumsfromfile.size(); i++) {
                                    fos.write((albumsfromfile.get(i) + "\n").getBytes());
                                }

                                FileOutputStream out = MainActivity.this.openFileOutput(addition + ".dat", Activity.MODE_PRIVATE);
                                ObjectOutputStream oos = new ObjectOutputStream(out);
                                oos.writeObject(toRemove);
                                out.getFD().sync();
                                oos.close();

                                String path = getFilesDir() + File.separator + selected + ".dat";
                                File file = new File(path);
                                file.delete();

                                adapter.notifyDataSetChanged();
                                fos.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }


    public void searchTags(View view) {
        showSearchDialog(MainActivity.this);
    }


    private void showSearchDialog(Context c) {
        final EditText taskEditTextTag = new EditText(c);
        final EditText taskEditTextValue = new EditText(c);
        taskEditTextTag.setHint("Tag");
        taskEditTextValue.setHint("Hint");
        LinearLayout container = new LinearLayout(c);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(taskEditTextTag);
        container.addView(taskEditTextValue);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Search Photos")
                .setMessage("Enter Tag-Value Pair")
                .setView(container)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = String.valueOf(taskEditTextTag.getText());
                        String value = String.valueOf(taskEditTextValue.getText());

                        if (tag.equals("") || !(tag.equalsIgnoreCase("person") ||
                                tag.equalsIgnoreCase("location")) || value.equals("")) {
                            invalidTag(MainActivity.this);
                        } else {
                            Intent intent = new Intent(MainActivity.this, SearchTags.class);
                            intent.putExtra("tag", tag);
                            intent.putExtra("value", value);
                            intent.putExtra("albumList", albums);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }

}