package com.example.contatodeemergencia;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyAdapter mAdapter;
    private MatrixCursor mMatrixCursor;
    private AppBarConfiguration mAppBarConfiguration;
    private ArrayList<Contato> mContatoArrayList = new ArrayList<Contato>();
    private SearchView svSearch;
    private ListView lvContatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        initialize();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        // The contacts from the contacts content provider is stored in this
        // cursor
        mMatrixCursor = new MatrixCursor(new String[] { "_id", "name", "details" });



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            }
        }

        // Creating an AsyncTask object to retrieve and load listview with
        // contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

        // Starting the AsyncTask process to retrieve and load listview with
        // contacts
        listViewContactsLoader.execute();
    }

    private void initialize() {
        lvContatos = (ListView)findViewById(R.id.lst_contacts);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        svSearch =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        svSearch.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        svSearch.setOnQueryTextListener(queryListener);
        return true;
    }

    final private SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String s) {
            mAdapter.getFilter().filter(s);
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.lst_contacts);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    /** An AsyncTask class to retrieve and load listview with contacts */
    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        /** progress dialog to show user that the backup is processing. */
        /** application context. */
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Carregando Contatos...");
            this.dialog.show();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            // contatos de emergencia
            mContatoArrayList.add(new Contato("Contato de Emergencia 1","Livre"));
            mContatoArrayList.add(new Contato("Contato de Emergencia 2","Livre"));
            mContatoArrayList.add(new Contato("Contato de Emergencia 3","Livre"));
            mContatoArrayList.add(new Contato("Contato de Emergencia 4","Livre"));
            mContatoArrayList.add(new Contato("Contato de Emergencia 5","Livre"));

            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            // Querying the table ContactsContract.Contacts to retrieve all the
            // contacts
            Cursor contactsCursor = getContentResolver().query(contactsUri,
                    null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

            int limit = 0;
            if (contactsCursor.moveToFirst()) {
                do {
                    long contactId = contactsCursor.getLong(contactsCursor
                            .getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    // Querying the table ContactsContract.Data to retrieve
                    // individual items like
                    // home phone, mobile phone, work email etc corresponding to
                    // each contact
                    Cursor dataCursor = getContentResolver().query(dataUri,
                            null,
                            ContactsContract.Data.CONTACT_ID + "=" + contactId,
                            null, null);

                    String displayName = "";
                    String nickName = "";
                    String homePhone = "";
                    String mobilePhone = "";
                    String workPhone = "";


                    if (dataCursor.moveToFirst()) {
                        limit++;
                        // Getting Display Name
                        displayName = dataCursor
                                .getString(dataCursor
                                        .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

                        do {
                            // Getting NickName
                            if (dataCursor
                                    .getString(
                                            dataCursor
                                                    .getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
                                nickName = dataCursor.getString(dataCursor
                                        .getColumnIndex("data1"));

                            // Getting Phone numbers
                            if (dataCursor
                                    .getString(
                                            dataCursor
                                                    .getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                switch (dataCursor.getInt(dataCursor
                                        .getColumnIndex("data2"))) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        homePhone = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        mobilePhone = dataCursor
                                                .getString(dataCursor
                                                        .getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        workPhone = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"));
                                        break;
                                }
                            }
                        } while (dataCursor.moveToNext());

                        String details = "";

                        // Concatenating various information to single string
                        if (homePhone != null && !homePhone.equals(""))
                            details = "Residencial : " + homePhone + "\n";
                        if (mobilePhone != null && !mobilePhone.equals(""))
                            details += "Celular : " + mobilePhone + "\n";
                        if (workPhone != null && !workPhone.equals(""))
                            details += "Trabalho : " + workPhone + "\n";
                        if (nickName != null && !nickName.equals(""))
                            details += "Apelido : " + nickName + "\n";

                        if(displayName == null || displayName.equals("")){

                        }else{
                            mContatoArrayList.add(new Contato(displayName,details));
                        }

                    }

                } while (contactsCursor.moveToNext() && limit < 500);
            }
            return mMatrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            // Adapter to set data in the listview
            mAdapter = new MyAdapter(getBaseContext(), mContatoArrayList);
            // Getting reference to listview
            ListView lstContacts = (ListView) findViewById(R.id.lst_contacts);
            // Setting the adapter to listview
            lstContacts.setAdapter(mAdapter);
        }
    }

}