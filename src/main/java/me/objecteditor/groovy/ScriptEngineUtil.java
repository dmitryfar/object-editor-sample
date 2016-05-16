package me.objecteditor.groovy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

import me.objecteditor.util.ObjectEditorMetrics;

public class ScriptEngineUtil {
  private static final Logger logger = LoggerFactory.getLogger(ScriptEngineUtil.class);

  private static final Counter evaluationCounter = ObjectEditorMetrics.METRIC_REGISTRY.counter("script-engine-evaluations");

  protected static final Set<String> UNSTORED_KEYS = new HashSet<String>(
      Arrays.asList("out", "out:print", "lang:import", "context", "elcontext", "print", "println"));

  protected Map<String, ScriptEngine> cachedEngines = new HashMap<String, ScriptEngine>();

  public static final String GROOVY_SCRIPTING_LANGUAGE = "groovy";
  ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

  private volatile static ScriptEngineUtil instance;

  public static ScriptEngineUtil getInstance() {
    ScriptEngineUtil localInstance = instance;
    if (localInstance == null) {
      synchronized (ScriptEngineUtil.class) {
        localInstance = instance;
        if (localInstance == null) {
          instance = localInstance = new ScriptEngineUtil();
        }
      }
    }
    return localInstance;
  }

  protected static Object evaluateFile(String filename, String language, Map<String, Object> bindings) {
    String script = load(filename);
    return evaluate(script, language, bindings);
  }

  protected static Object evaluateFile(String filename, String language, Map<String, Object> bindings,
      boolean storeScriptVariables) {
    String script = load(filename);
    return evaluate(script, language, bindings, storeScriptVariables);
  }

  protected static Object evaluate(String script, String language, Map<String, Object> bindings) {
    return evaluate(script, language, bindings, true);
  }

  protected static Object evaluate(String script, String language, Map<String, Object> bindings,
      boolean storeScriptVariables) {
    Bindings defaultBindings = new SimpleScriptContext().getBindings(SimpleScriptContext.ENGINE_SCOPE);
    if (bindings != null) {
      defaultBindings.putAll(bindings);
    }
    Object result = evaluate(script, language, defaultBindings);

    if (storeScriptVariables) {
      for (String name : defaultBindings.keySet()) {
        if (!UNSTORED_KEYS.contains(name)) {
          bindings.put(name, defaultBindings.get(name));
        }
      }
    }
    return result;
  }

  protected static Object evaluate(String script, String language, Bindings bindings) {
    ScriptEngine scriptEngine = getInstance().getEngineByName(language);
    try {
      // ObjectEditorMetrics.getInstance().incCounter();
      evaluationCounter.inc();
      return scriptEngine.eval(script, bindings);
    } catch (ScriptException e) {
      throw new RuntimeException("problem evaluating script: " + e.getMessage(), e);
    }
  }

  protected ScriptEngine getEngineByName(String language) {
    ScriptEngine scriptEngine = null;

    scriptEngine = cachedEngines.get(language);
    if (scriptEngine == null) {
      scriptEngine = scriptEngineManager.getEngineByName(language);

      if (scriptEngine != null) {
        try {
          scriptEngine.getContext().setAttribute("#jsr223.groovy.engine.keep.globals", "weak",
              ScriptContext.ENGINE_SCOPE);
        } catch (Exception ignore) {
          // ignore this, in case engine doesn't support the passed attribute
        }

        // Check if script-engine allows caching, using "THREADING" parameter as
        // defined in spec
        Object threadingParameter = scriptEngine.getFactory().getParameter("THREADING");
        if (threadingParameter != null) {
          // Add engine to cache as any non-null result from the
          // threading-parameter indicates at least MT-access
          cachedEngines.put(language, scriptEngine);
        }
      }
    }
    if (scriptEngine == null) {
      throw new RuntimeException("Can't find scripting engine for '" + language + "'");
    }
    return scriptEngine;
  }

  private static String load(String filename) throws RuntimeException {
    try {
      // XmlEntitiesHelper.class.getClassLoader().getResourceAsStream(filename);
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
      String s = IOUtils.toString(is);
      return s;
      // return new Scanner(new File(is).g).useDelimiter("\\Z").next();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
