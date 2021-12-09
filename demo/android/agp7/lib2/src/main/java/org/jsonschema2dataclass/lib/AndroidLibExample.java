package org.jsonschema2dataclass.lib;

import com.oosocial.clarityn.rest.clarityn.model.Entry_schema;
import com.oosocial.clarityn.rest.clarityn.model.Storage;

public final class AndroidLibExample {

  public static void example() {
    Entry_schema schema = new Entry_schema();
    schema.setReadonly(false);
    schema.setFstype(Entry_schema.Fstype.EXT_4);
    schema.setStorage(new Storage());
  }
}
