package org.jsonschema2dataclass.internal.task

/** Plugin ID to refer in messages */
const val PLUGIN_ID = "org.jsonschema2dataclass"

/** Configuration name for tools */
const val JS2D_CONFIGURATION_NAME = "jsonschema2dataclass"

/** Configuration to put plugin dependencies to */
const val JS2D_PLUGINS_CONFIGURATION_NAME = "jsonschema2dataclassPlugins"

/** Default schema locations under project name. Should be changed */
const val DEFAULT_SCHEMA_PATH = "src/main/json"

/** Default folder to store generated files under build folder */
const val DEFAULT_TARGET_FOLDER_BASE = "generated/sources/js2d"

/** Extension name for jsonschema2pojo processor */
const val JS2P_EXTENSION_NAME = "jsonSchema2Pojo"

/** Base task name for jsonschema2pojo processor */
const val JS2D_TASK_NAME = "generateJsonSchema2DataClass"

/** Processor name to generate task names */
const val JS2P_TOOL_NAME = "Js2p"
