import org.slf4j.LoggerFactory;
def log = LoggerFactory.getLogger("me.objecteditor.GROOVYSCRIPT")

GroovyShell shell = new GroovyShell( binding )
shell.evaluate( fieldPath + " = value" )
