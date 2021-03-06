package com.exportershouse.gajanandstudio;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.exportershouse.gajanandstudio.Fragment.AboutusFragment;
import com.exportershouse.gajanandstudio.Fragment.AlbumFragment;
import com.exportershouse.gajanandstudio.Fragment.ContactusFragment;
import com.exportershouse.gajanandstudio.Fragment.HomeFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Uri> arrayListapkFilepath; // define global
    //creating fragment object
    Fragment fragment = null;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (checkNavigationMenuItem() != 0)
            {
                navigationView.setCheckedItem(R.id.nav_home);
                fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
            else
                super.onBackPressed();
        }
    }

    private int checkNavigationMenuItem() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).isChecked())
                return i;
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {



        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_category:
                fragment = new AlbumFragment();
                break;
//            case R.id.nav_inquiry:
//                fragment = new InquiryFragment();
//                break;
            case R.id.nav_aboutus:
                fragment = new AboutusFragment();
                break;
            case R.id.nav_contactus:
                fragment = new ContactusFragment();
                break;
            case R.id.nav_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                // change the type of data you need to share,
                //for image use "image/*"
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.exportershouse.shivdharakitchenware");
                startActivity(Intent.createChooser(intent, "Share"));
                break;
            case R.id.nav_send:

                //put this code when you wants to share apk
                arrayListapkFilepath = new ArrayList<Uri>();

                shareAPK(getPackageName());
                // you can pass bundle id of installed app in your device instead of getPackageName()
                Intent intent1 = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent1.setType("application/vnd.android.package-archive");
                intent1.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        arrayListapkFilepath);
                startActivity(Intent.createChooser(intent1, "Send " + arrayListapkFilepath.size() + " Files Via"));

                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    public void shareAPK(String bundle_id) {
        File f1;
        File f2 = null;

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        int z = 0;
        for (Object object : pkgAppsList) {

            ResolveInfo info = (ResolveInfo) object;
            if (info.activityInfo.packageName.equals(bundle_id)) {

                f1 = new File(info.activityInfo.applicationInfo.publicSourceDir);

                Log.v("file--",
                        " " + f1.getName().toString() + "----" + info.loadLabel(getPackageManager()));
                try {

                    String file_name = info.loadLabel(getPackageManager()).toString();
                    Log.d("file_name--", " " + file_name);

                    f2 = new File(Environment.getExternalStorageDirectory().toString() + "/Folder");
                    f2.mkdirs();
                    f2 = new File(f2.getPath() + "/" + file_name + ".apk");
                    f2.createNewFile();

                    InputStream in = new FileInputStream(f1);

                    OutputStream out = new FileOutputStream(f2);

                    // byte[] buf = new byte[1024];
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    System.out.println("File copied.");
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage() + " in the specified directory.");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        arrayListapkFilepath.add(Uri.fromFile(new File(f2.getAbsolutePath())));

    }


}
