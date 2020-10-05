package com.example.photomanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String ROOT_DIRECTORY = "";
    private File currentDirectory = null;

    private LinearLayout mainLayout = null;

    private final String[] imagesFileExtensions = new String[] {
            "jpg",
            "png",
            "gif",
            "jpeg"
    };

    public boolean ifFileHaveImageExt(String fileName) {
        for (String ext : this.imagesFileExtensions) {
            if (fileName.toLowerCase().endsWith(ext))
                return true;
        }

        return false;
    }

    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
        setContentView(R.layout.activity_main);
    }

    public void printDirectory(String directoryPath) {
        // R.id.directory_layout

        if (this.mainLayout == null) this.mainLayout = (LinearLayout) findViewById(R.id.directory_layout);
        else this.mainLayout.removeAllViews();

        File directory = new File(directoryPath);
        this.currentDirectory = directory;

        LinearLayout.LayoutParams generalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        generalParams.setMargins((int) convertDpToPixel(5), (int) convertDpToPixel(5), (int) convertDpToPixel(5), (int) convertDpToPixel(10));

        for (final File i : directory.listFiles()) {
            Log.d("MYTAG", (i.isFile() ? "File: " : "Dir: ") + '/' + i.getName());
            TextView nTextView = new TextView(getApplicationContext());

            nTextView.setText((i.isFile() ? "File: " : "Dir: ") + i.getName());
            nTextView.setBackground(getDrawable((i.isFile() ? R.drawable.layout_element_file : R.drawable.layout_element_directory)));
            nTextView.setPadding((int) convertDpToPixel(10), (int) convertDpToPixel(10), (int) convertDpToPixel(10), (int) convertDpToPixel(10));
            nTextView.setLayoutParams(generalParams);

            nTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(getApplicationContext(), "You click on " + i.getName(), Toast.LENGTH_SHORT).show();
                    if (i.isDirectory()) {
                        printDirectory(i.getAbsolutePath());
                    } else if (i.isFile() && ifFileHaveImageExt(i.getName())) {
                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                        intent.putExtra("file_name", i.getAbsolutePath());

                        startActivity(intent);
//                        startActivityForResult(intent, 1);
                    }
                }
            });

            this.mainLayout.addView(nTextView);
        }

    }

    public void pressStart(View view) {

        // let get info about directories
        // check permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Access Granded", Toast.LENGTH_SHORT).show();

            findViewById(R.id.textView).setVisibility(View.INVISIBLE);
            findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
            findViewById(R.id.button).setVisibility(View.INVISIBLE);

            findViewById(R.id.manager_layout).setVisibility(View.VISIBLE);

            File extStorage = Environment.getExternalStorageDirectory();

            if (extStorage.exists()) {
                Toast.makeText(getApplicationContext(), "External storage are exist " + extStorage.getAbsolutePath(), Toast.LENGTH_LONG).show();
                // should print storage/emulated/0 directory
                this.printDirectory(extStorage.getAbsolutePath());
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please give us permission to READ & WRITE external storage", Toast.LENGTH_LONG).show();
        }

    }

    public void returnToPreviusDir(View view) {
        if (this.currentDirectory != null && !this.currentDirectory.getAbsolutePath().equals(this.ROOT_DIRECTORY))
            printDirectory(this.currentDirectory.getParentFile().getAbsolutePath());
        else Toast.makeText(getApplicationContext(), "You are in the root directory", Toast.LENGTH_SHORT).show();
    }
}