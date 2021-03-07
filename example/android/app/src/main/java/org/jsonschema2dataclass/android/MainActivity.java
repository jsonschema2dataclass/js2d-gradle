package org.jsonschema2dataclass.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.oosocial.clarityn.rest.clarityn.model.Entry_schema;
import com.oosocial.clarityn.rest.clarityn.model.Storage;


public class MainActivity extends AppCompatActivity {

    Entry_schema schema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schema = new Entry_schema();
        schema.setReadonly(false);
        schema.setFstype(Entry_schema.Fstype.EXT_4);
        schema.setStorage(new Storage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
