/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.logging.Logger;
import org.apache.commons.lang3.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.graalvm.polyglot.*;

/**
 *
 * @author stepan_sydoruk
 */
public class JSRunner {

    private OutReaderThread stdOutReader;
    private String port;
    private IOutputHook errHook = null;
    private IOutputHook outHook = null;

    public IOutputHook getErrHook() {
        return errHook;
    }

    public void setErrHook(IOutputHook errHook) {
        this.errHook = errHook;
        if (stdErrReader != null && errHook != null) {
            stdErrReader.setReaderHook(errHook);
        }
    }

    public IOutputHook getOutHook() {
        return outHook;
    }

    public void setOutHook(IOutputHook outHook) {
        this.outHook = outHook;
        if (stdOutReader != null && outHook != null) {
            stdOutReader.setReaderHook(outHook);
        }
    }

    private OutReaderThread getStdOutReader() {
        return stdOutReader;
    }

    public ArrayList<String> setDebugPort(String _port) {
        if ((port == null && _port == null) || (port != null && _port != null && port.equals(_port))) {
            return null;
        }
        IOutputHook _errHook = errHook;
        IOutputHook _outHook = outHook;
        close();
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<String> buf = new ArrayList();

        if (_port != null) {
            setErrHook(new IOutputHook() {
                int linesRead = 0;

                @Override
                public void processOut(String str) {
                    buf.add(str);
                    // logger.debug("l:" + linesRead);
                    if (++linesRead > 2) { // reading 3 lines
                        latch.countDown(); // the procedure will be called from separate thread of output/error reader
                        // so no locking
                    }
                }
            });
        }
        this.port = _port;
        try {
            init();
            if (_port != null) {
                latch.await(2, TimeUnit.SECONDS);
            }
            logger.info("done with stdout");

        } catch (IOException | InterruptedException ex) {
            logger.error("ex ", ex);
        } finally {
            setErrHook(_errHook);
            setOutHook(_outHook);
        }
        logger.info("buf: " + StringUtils.join(buf, '\n'));
        return (port != null) ? buf : null;
    }

    private OutReaderThread getStdErrReader() {
        return stdErrReader;
    }

    private OutReaderThread stdErrReader;

    static boolean runFile(String fileName, ConfigServerManager csManager, IOutputHook stdOutHook,
            IOutputHook stdErrHook, boolean forceFile) {
        JSRunner inst = getInstance();
        IOutputHook errHook = inst.getStdErrReader().getReaderHook();
        IOutputHook outHook = inst.getStdOutReader().getReaderHook();
        inst.getStdOutReader().setReaderHook(stdOutHook);
        inst.getStdErrReader().setReaderHook(stdErrHook);

        boolean ret = false;
        try {
            ret = runFile(fileName, csManager, null, forceFile);
        } catch (Exception e) {
            stdErrHook.processOut("Exception executing:\n" + e.getMessage() + "\n--------------\n");
            logger.error("", e);
        } finally {
            inst.getStdOutReader().setReaderHook(outHook);
            inst.getStdErrReader().setReaderHook(errHook);
        }

        // inst.getStdOutReader().setReaderHook(null);
        // inst.getStdErrReader().setReaderHook(null);
        return ret;

    }

    static boolean runFile(String fileName, ConfigServerManager csManager, String[] params, boolean forceFile) {
        logger.trace("runFile [" + fileName + "]");

        try {
            Source source = getSource(fileName, forceFile);
            return runScript(new IEvalMethod() {
                @Override
                public void theMethod(Context cont) {
//                    try {
                    cont.eval(source);
//                    }
//                    catch (Exception e){
//                        logger.error("Error executing ", e);
//                    }
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

    public String getPort() {
        return port;
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
        ExistingObjectDecider eod = ExistingObjectDecider.getInstance();
//        eod.init(ObjectExistAction.FAIL, theForm);
        bindings.putMember("CS", CStoJS.getInstance(csManager));
        bindings.putMember("PARAMS", params);
        bindings.putMember("TERMINATE", false);

        method.theMethod(cont);
        return bindings.getMember("TERMINATE").asBoolean();
    }

    static boolean runCSVFormatScript(String script, HashMap<String, String> record) {
        logger.trace("runScript anonymous script [" + script + "]");

        return runCSVFormatScript(new IEvalMethod() {
            @Override
            public void theMethod(Context cont) {
                cont.eval("js", script);
            }
        }, record);

    }

    private static boolean runCSVFormatScript(IEvalMethod method, HashMap<String, String> record) {
        Context cont = getInstance().getCondContext();
        Value bindings = cont.getBindings("js");
        ExistingObjectDecider eod = ExistingObjectDecider.getInstance();
//        eod.init(ObjectExistAction.FAIL, theForm);
        bindings.putMember("RECORD", record);
        bindings.putMember("IGNORE_RECORD", false);

        method.theMethod(cont);
        return bindings.getMember("IGNORE_RECORD").asBoolean();
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

    private void close() {
        if (condContext != null) {
            condContext.close(true);
        }
        if (stdErrReader != null) {
            stdErrReader.interrupt();
            stdErrReader = null;
        }
        if (stdOutReader != null) {
            stdOutReader.interrupt();
            stdOutReader = null;
        }
    }

    interface IEvalMethod {

        void theMethod(Context cont) throws PolyglotException;
    }

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
        stdOutReader.setReaderHook(outHook);

        stdErrReader = new OutReaderThread(pipedStdErrReader, Level.ERROR);
        stdErrReader.setReaderHook(errHook);
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

        Context.Builder builder = Context.newBuilder("js").allowAllAccess(true).err(stdErr).out(stdOut);
        if (port != null) {
            builder.option("inspect", port);
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


    /*
     * public static boolean evalFields(String script, ILogRecord rec,
     * HashMap<String, Object> scriptFields) { Context cont =
     * getInstance().getCondContext(); Value bindings = cont.getBindings("js");
     * bindings.putMember("RECORD", rec); bindings.putMember("FIELDS",
     * scriptFields); bindings.putMember("IGNORE_RECORD", false); cont.eval("js",
     * script); boolean ret = bindings.getMember("IGNORE_RECORD").asBoolean();
     * logger.trace("evalFields [" + scriptFields + "], ignored:[" + ret +
     * "] - result of [" + script + "]"); return ret; }
     * 
     * public static String execString(String script, ILogRecord rec) { Context cont
     * = getInstance().getCondContext(); cont.getBindings("js").putMember("RECORD",
     * rec); Value eval = cont.eval("js", script); logger.trace("eval [" + eval +
     * "] - result of [" + script + "]"); return eval.asString();
     * 
     * }
     * 
     * public static boolean execBoolean(String script, ILogRecord rec) { Context
     * cont = getInstance().getCondContext();
     * cont.getBindings("js").putMember("RECORD", rec); Value eval = cont.eval("js",
     * script); logger.trace("eval [" + eval + "] - result of [" + script + "]");
     * return eval.asBoolean();
     * 
     * }
     */
    class OutReaderThread extends Thread {

        private final PipedInputStream pipedIn;
        private final Level logLevel;
        private IOutputHook readerHook = null;

        synchronized public IOutputHook getReaderHook() {
            return readerHook;
        }

        synchronized public void setReaderHook(IOutputHook readerHook) {
//            System.out.println("setReaderHook " + (readerHook != null));
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
