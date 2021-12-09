package org.jsonschema2dataclass.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.oosocial.clarityn.rest.clarityn.model.EntrySchema;
import com.oosocial.clarityn.rest.clarityn.model.Storage;

public class TestGenerator extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        EntrySchema schema = new EntrySchema();
        schema.setReadonly(false);
        schema.setFstype(EntrySchema.Fstype.EXT_4);
        schema.setStorage(new Storage());
    }
}
