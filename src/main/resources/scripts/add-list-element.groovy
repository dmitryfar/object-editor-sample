import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
def log = LoggerFactory.getLogger("me.objecteditor.GROOVYSCRIPT")

GroovyShell shell = new GroovyShell( binding )

shell.evaluate( "list = " + fieldPath )

if (list == null) {
    log.debug "List '${fieldPath}' is null. Will create new list."
    shell.evaluate( "list = " + fieldPath + " = []" )
}

shell.evaluate("isArray = " + fieldPath + ".getClass().isArray()");
log.debug "isarray: ${isArray}"

if (isArray) {
    shell.evaluate("type = " + fieldPath + ".getClass().getComponentType().getName()");
    log.debug "type of ${fieldPath}: ${type}[]"
    shell.evaluate( "array = list as ${type}[]" )
    array = ArrayUtils.add(array, value);
    shell.evaluate( fieldPath + " = array " )
} else {
    try {
        shell.evaluate( fieldPath + " << value" )
    } catch (groovy.lang.MissingMethodException e) {
        // try is as an array
        // array = ArrayUtils.add(array, value);
        // shell.evaluate( fieldPath + " = array " )
        log.error ">>>>" + e.message
    }
}

