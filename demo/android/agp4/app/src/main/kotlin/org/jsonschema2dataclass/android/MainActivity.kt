package org.jsonschema2dataclass.android

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import com.oosocial.clarityn.rest.clarityn.model.EntrySchema
import com.oosocial.clarityn.rest.clarityn.model.Storage

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val schema = EntrySchema()
        schema.setReadonly(false)
        schema.setFstype(EntrySchema.Fstype.EXT_4)
        schema.setStorage(Storage())
    }
}
