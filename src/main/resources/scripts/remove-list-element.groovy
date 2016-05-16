import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
def log = LoggerFactory.getLogger("me.objecteditor.GROOVYSCRIPT")

GroovyShell shell = new GroovyShell( binding )

shell.evaluate( "list = " + fieldPath )

if (list == null) {
    log.debug "List '${fieldPath}' is null."
} else if (!(list instanceof java.util.List || list.getClass().isArray())) {
    log.debug "'${fieldPath}' is not an instance of java.util.List or not is array"
} else if (list.size() == 0) {
    log.debug "'${fieldPath}' is empty. Nothing to remove."
} else {
    if (list.getClass().isArray()) {
        shell.evaluate( "array = ${fieldPath} as " + list.getClass().getComponentType().getName() + "[]" )
        array = ArrayUtils.remove(array, index);
        shell.evaluate( fieldPath + " = array " )
    } else {
        shell.evaluate( fieldPath + ".remove(index)" )
    }
}
