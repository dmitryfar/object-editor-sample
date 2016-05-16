import org.slf4j.LoggerFactory;
def log = LoggerFactory.getLogger("me.objecteditor.GROOVYSCRIPT")

GroovyShell shell = new GroovyShell( binding )

shell.evaluate( "map = " + fieldPath )

if (map == null) {
    log.debug "Map '${fieldPath}' is null. Will create new map."
    shell.evaluate( fieldPath + " = [:]" )
}

shell.evaluate( fieldPath + ".${key} = value" )


