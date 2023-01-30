package org.jsonschema2dataclass.lib;

import com.oosocial.clarityn.rest.clarityn.model.EntrySchema;
import com.oosocial.clarityn.rest.clarityn.model.Storage;

public final class AndroidLibExample {

    public static void example() {
        EntrySchema schema = new EntrySchema();
        schema.setReadonly(false);
        schema.setFstype(EntrySchema.Fstype.EXT_4);
        schema.setStorage(new Storage());
    }
}
