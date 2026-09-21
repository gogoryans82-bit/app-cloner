package com.example.cloner;

import android.app.ListActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private List<ApplicationInfo> apps;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = getPackageManager();
        apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        List<String> labels = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            labels.add(app.loadLabel(pm).toString());
        }

        setListAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                labels));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int pos, long id) {
        ApplicationInfo app = apps.get(pos);
        String label = app.loadLabel(pm).toString();

        try {
            File outDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS),
                    "ClonedApps");
            if (!outDir.exists()) outDir.mkdirs();

            File outFile = new File(outDir, label + ".apk");
            copyFile(app.sourceDir, outFile.getAbsolutePath());

            Toast.makeText(this,
                    "Saved: " + outFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void copyFile(String src, String dst) throws Exception {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }
}
