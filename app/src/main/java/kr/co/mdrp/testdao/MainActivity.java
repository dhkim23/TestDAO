package kr.co.mdrp.testdao;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.greenrobot.greendao.DaoLog;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.mdrp.testdao.dao.DaoMaster;
import kr.co.mdrp.testdao.dao.DaoSession;
import kr.co.mdrp.testdao.dao.Person;
import kr.co.mdrp.testdao.dao.PersonDao;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //GreendDAO
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity.this,"test.db",null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        PersonDao personDao = daoSession.getPersonDao();

        Person person = new Person(null,"kim","Manager");
        personDao.insert(person);

        Cursor cursor = db.query(personDao.getTablename(),personDao.getAllColumns(),null,null,null,null,null);
        DaoLog.d("sjkim - " + DatabaseUtils.dumpCursorToString(cursor));

        for(int i = 0; i< personDao.loadAll().size() ; i++){
            DaoLog.d("sjkim - " + personDao.loadAll().get(i).getName() + ", " + personDao.loadAll().get(i).getComment());
        }

        AppController appController = new AppController();
        DaoSession daoSession1 = appController.getDaoSession();


        checkTedPermission();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    //onClick
    public void btnStart(View view){
        startService(new Intent(MainActivity.this, AlwaysOnTopService.class));

    }
    // [퍼미션 체크] ==================================================================================
    private void checkTedPermission() {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();


                // ===============================================


                //html parsing
                //https://learn.dict.naver.com/m/endic/wordbook/mhs.nhn
                (new HttpPingAsyncTask()).execute("https://learn.dict.naver.com/m/endic/wordbook/mhs.nhn");

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한이 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("SD Write Read 권한이 필요해요")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check();
    }
    static class HttpPingAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                Connection.Response response = Jsoup.connect(urls[0])
                        .method(Connection.Method.GET)
                        .execute();
                Document document = response.parse();
                Elements div = document.select("div.book_bg2");
                Elements ul = document.select("div.book_bg2 > ul");
                Elements li = ul.select("li");
                for(int i=0; i<li.size(); i++){
                    String text = li.get(i).select("div.link_bock").text();
                    DaoLog.d(text);
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }


        }
    }
}


