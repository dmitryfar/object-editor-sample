import org.slf4j.LoggerFactory;
def log = LoggerFactory.getLogger("me.objecteditor.GROOVYSCRIPT")

GroovyShell shell = new GroovyShell( binding )

shell.evaluate( "map = " + fieldPath )

if (map == null) {
    log.debug "Map '${fieldPath}' is null."
} else if (!(map instanceof java.util.Map)) {
    log.debug "'${fieldPath}' is not an instance of java.util.Map"
} else if (!map.containsKey(key)) {
    log.debug "Map '${fieldPath}' has no key '${key}'"
} else {
    shell.evaluate( fieldPath + ".remove(key)" )
}
