//"{->${entity.child = childObject}}";

GroovyShell shell = new GroovyShell( binding )
shell.evaluate( fieldPath + " = value" )
