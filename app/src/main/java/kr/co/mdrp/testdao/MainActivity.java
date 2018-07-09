package kr.co.mdrp.testdao;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.greendao.DaoLog;

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
}
