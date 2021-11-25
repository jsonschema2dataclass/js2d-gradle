package org.jsonschema2dataclass.android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.oosocial.clarityn.rest.clarityn.model.Entry_schema
import com.oosocial.clarityn.rest.clarityn.model.Storage

class MainActivity : AppCompatActivity() {

    private lateinit var schema: Entry_schema

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        schema = Entry_schema()
        schema.readonly = false
        schema.fstype = Entry_schema.Fstype.EXT_4
        schema.storage = Storage()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else
            super.onOptionsItemSelected(item)
    }
}
