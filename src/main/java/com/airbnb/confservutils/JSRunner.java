/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.graalvm.polyglot.*;

/**
 *
 * @author stepan_sydoruk
 */
public class JSRunner {

    private OutReaderThread stdOutReader;

    private OutReaderThread getStdOutReader() {
        return stdOutReader;
    }

    private OutReaderThread getStdErrReader() {
        return stdErrReader;
    }
    private OutReaderThread stdErrReader;

    static boolean runFile(String fileName, ConfigServerManager csManager, IOutputHook stdOutHook, IOutputHook stdErrHook, boolean forceFile) {
        JSRunner inst = getInstance();
//        inst.resetContext();
        inst.getStdOutReader().setReaderHook(stdOutHook);
        inst.getStdErrReader().setReaderHook(stdErrHook);

        boolean ret = runFile(fileName, csManager, null, forceFile);

//        inst.getStdOutReader().setReaderHook(null);
//        inst.getStdErrReader().setReaderHook(null);
        return ret;

    }

    static boolean runFile(String fileName, ConfigServerManager csManager, String[] params, boolean forceFile) {
        logger.trace("runFile [" + fileName + "]");

        try {
            Source source = getSource(fileName, forceFile);
            return runScript(new IEvalMethod() {
                @Override
                public void theMethod(Context cont) {
                    cont.eval(source);
                }

            }, csManager, params);

        } catch (IOException ex) {
            Logger.getLogger(JSRunner.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        return false;
    }

    private static final HashMap<String, Source> sourceFiles = new HashMap();

    private static Source getSource(String fileName, boolean forceFile) throws IOException {
        if (Main.isDebug()) {
            return Source.newBuilder("js", new File(fileName)).cached(false).build();
        } else {

            Source source = sourceFiles.get(fileName);
            if (source != null && forceFile) {
                sourceFiles.remove(fileName);
                source = null;
            }
            if (source == null) {
                source = Source.newBuilder("js", new File(fileName)).build();
                try {
                    JSRunner.getInstance().getCondContext().parse(source);

                } catch (PolyglotException e) {
                    logger.error("e", e);
                }
                sourceFiles.put(fileName, source);
            }
            return source;
        }
    }

    static boolean runScript(String script, ConfigServerManager csManager, String[] params) {
        logger.trace("runScript anonymous script [" + script + "]");

        return runScript(new IEvalMethod() {
            @Override
            public void theMethod(Context cont) {
                cont.eval("js", script);
            }
        }, csManager, params);

    }

    private static boolean runScript(IEvalMethod method, ConfigServerManager csManager, String[] params) {
        Context cont = getInstance().getCondContext();
        Value bindings = cont.getBindings("js");
        bindings.putMember("CS", CStoJS.getInstance(csManager));
        bindings.putMember("PARAMS", params);
        bindings.putMember("TERMINATE", false);

        method.theMethod(cont);
        return bindings.getMember("TERMINATE").asBoolean();
    }

    private void resetContext() {
        condContext.close(true);
        stdErrReader.interrupt();
        stdOutReader.interrupt();
        try {
            init();
        } catch (IOException ex) {
            Logger.getLogger(JSRunner.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    interface IEvalMethod {

        void theMethod(Context cont);
    };

    private JSRunner() {
        try {
            init();
        } catch (IOException ex) {
            logger.error("exception in JSRunner: " + ex);
        }
    }

    private void init() throws IOException {
        PipedInputStream pipedStdOutReader = new PipedInputStream();
        PipedInputStream pipedStdErrReader = new PipedInputStream();

        stdOutReader = new OutReaderThread(pipedStdOutReader, Level.DEBUG);
        stdErrReader = new OutReaderThread(pipedStdErrReader, Level.ERROR);
        PipedOutputStream stdOut = new PipedOutputStream(pipedStdOutReader);
        PipedOutputStream stdErr = new PipedOutputStream(pipedStdErrReader);

        Handler logHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {

                System.out.println("-- publish: " + record.getMessage());
            }

            @Override
            public void flush() {
                System.out.println("--flush");
            }

            @Override
            public void close() throws SecurityException {
                System.out.println("--close");
            }
        };

        logHandler.setLevel(java.util.logging.Level.FINEST);

        Context.Builder builder = Context.newBuilder("js")
                .allowAllAccess(true)
                .err(stdErr)
                .out(stdOut);
        if (System.getProperties().containsKey("debug")) {
            // commented out because too much useless (so far) logs
//            builder.option("log.level", "FINEST");
//            builder.option("log.js.level", "FINEST");
//            builder.option("log.js.com.oracle.truffle.js.parser.JavaScriptLanguage.level", "FINEST");
//            builder.option("log.file", "poly.txt");
            builder.option("inspect", "9229");
        }
        condContext = builder.build();

        stdOutReader.start();
        stdErrReader.start();

    }

    public Context getCondContext() {
        return condContext;
    }

    public static JSRunner getInstance() {
        return JSRunnerHolder.INSTANCE;
    }

    private static class JSRunnerHolder {

        private static final JSRunner INSTANCE = new JSRunner();
    }

    private Context condContext = null;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    /**
     *
     * @param script
     * @param rec
     * @param scriptFields
     * @return true if record should be ignored (based on balue of boolean
     * IGNORE_RECORD calculated by the script
     */
    /*
    public static boolean evalFields(String script, ILogRecord rec, HashMap<String, Object> scriptFields) {
        Context cont = getInstance().getCondContext();
        Value bindings = cont.getBindings("js");
        bindings.putMember("RECORD", rec);
        bindings.putMember("FIELDS", scriptFields);
        bindings.putMember("IGNORE_RECORD", false);
        cont.eval("js", script);
        boolean ret = bindings.getMember("IGNORE_RECORD").asBoolean();
        logger.trace("evalFields [" + scriptFields + "], ignored:[" + ret + "] - result of [" + script + "]");
        return ret;
    }

    public static String execString(String script, ILogRecord rec) {
        Context cont = getInstance().getCondContext();
        cont.getBindings("js").putMember("RECORD", rec);
        Value eval = cont.eval("js", script);
        logger.trace("eval [" + eval + "] - result of [" + script + "]");
        return eval.asString();

    }

    public static boolean execBoolean(String script, ILogRecord rec) {
        Context cont = getInstance().getCondContext();
        cont.getBindings("js").putMember("RECORD", rec);
        Value eval = cont.eval("js", script);
        logger.trace("eval [" + eval + "] - result of [" + script + "]");
        return eval.asBoolean();

    }
     */
    class OutReaderThread extends Thread {

        private final PipedInputStream pipedIn;
        private final Level logLevel;
        private IOutputHook readerHook = null;

        synchronized public IOutputHook getReaderHook() {
            return readerHook;
        }

        synchronized public void setReaderHook(IOutputHook readerHook) {
            System.out.println("setReaderHook " + (readerHook != null));
            this.readerHook = readerHook;
        }

        private OutReaderThread(PipedInputStream pipedStdIn, Level level) {
            pipedIn = pipedStdIn;
            logLevel = level;
        }

        @Override
        public void run() {
            logger.debug("started JSreader for log level " + logLevel);
            try {
                String s;
                BufferedReader br = new BufferedReader(new InputStreamReader(pipedIn));
                while ((s = br.readLine()) != null) {
                    logger.log(logLevel, "<js> " + s);
                    IOutputHook h = getReaderHook();
                    if (h != null) {
                        h.processOut(s);
                        logger.log(logLevel, "<js> " + "h != null");
                    }
                }
            } catch (IOException ex) {
                logger.error("Exception while reading js output", ex);
            }
            logger.debug("JSreader thread done");
        }

    }

}
