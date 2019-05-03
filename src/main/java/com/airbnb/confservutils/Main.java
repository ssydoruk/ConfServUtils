/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

import java.net.UnknownHostException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

/**
 *
 * @author stepan_sydoruk
 */
public class Main {
    
    private static Options options;
    private static Option optConfigProfile;
    private static Option optHelp;
    private static Option optLoaderLog;
    StoredSettings ds = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, UnknownHostException {
        
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WikiTeX");
        
        options = new Options();
        
        optConfigProfile = Option.builder("f")
                .hasArg(true)
                .required(false)
                .desc("Path to GUI configured storage (JSON)."
                        + "If specified, GUI configurator will be called")
                .longOpt("gui-profile")
                .build();
        options.addOption(optConfigProfile);
        
        optHelp = Option.builder("h")
                .hasArg(false)
                .required(false)
                .desc("Show help and exit")
                .longOpt("help")
                .build();
        options.addOption(optHelp);
        
        optLoaderLog = Option.builder()
                .hasArg(true)
                .required(false)
                .desc("Path to utility log directory and/or file")
                .longOpt("log-file")
                .build();
        options.addOption(optLoaderLog);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error(e);
            showHelpExit(e.getMessage(), options);
        }
        
        if (cmd.hasOption(optHelp.getLongOpt())) {
            showHelpExit(options);
        }
        
        String sLoaderLog = (String) cmd.getParsedOptionValue(optLoaderLog.getLongOpt());
        initLogger(sLoaderLog);
        
        String sGUIProfile = (String) cmd.getParsedOptionValue(optConfigProfile.getLongOpt());
        


        
        if (sGUIProfile != null && !sGUIProfile.isEmpty()) {
            try {
                AppForm frm = new AppForm();
                frm.setProfile(sGUIProfile);
                frm.runGui();
            } catch (Exception ex) {
                showHelpExit("Not able to run application: " + ex.getMessage(), options);
            }
        } else {
            showHelpExit("No config file specified", options);
        }
    }
    
    static void exitHelp(String string) {
        showHelpExit(string, options);
    }
    
    private static void showHelpExit(String msg, Options options) {
        if (msg != null && !msg.isEmpty()) {
            logger.error(msg);
        }
        showHelpExit(options);
    }
    
    private static void showHelpExit(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("utility-name", options);
        
        System.exit(0);
    }
    
    private static void initLogger(String sLoaderLog1) {
        if (sLoaderLog1 == null || sLoaderLog1.isEmpty()) {
            sLoaderLog1 = "./applog";
        } else {
            System.setProperty("logPath", sLoaderLog1);
            
        }
        System.setProperty("log4j2.saveDirectory", "true");
        String s = System.getProperty("log4j.configurationFile");
        if (s != null && !s.isEmpty()) {
            s = System.getProperty("program.name") + ".xml";
            logger = LogManager.getLogger();
        } else {
            
            ConfigurationBuilder<BuiltConfiguration> builder
                    = ConfigurationBuilderFactory.newConfigurationBuilder();
            
            builder.addProperty("LogFileName", sLoaderLog1);
            
            AppenderComponentBuilder console
                    = builder.newAppender("stdout", "Console");
            
            ComponentBuilder triggeringPolicies = builder.newComponent("Policies")
                    .addComponent(builder.newComponent("OnStartupTriggeringPolicy"))
                    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
                            .addAttribute("size", "20M"));
            
            AppenderComponentBuilder rollingFile
                    = builder.newAppender("rolling", "RollingFile");
            rollingFile.addAttribute("fileName", "${LogFileName}.log");
            rollingFile.addAttribute("filePattern", "${LogFileName}-%d{yyyyMMdd-HHmmss_SSS}.log");
            rollingFile.addComponent(triggeringPolicies);

//        FilterComponentBuilder flow = builder.newFilter(
//                "MarkerFilter",
//                Filter.Result.ACCEPT,
//                Filter.Result.DENY);
//        flow.addAttribute("marker", "FLOW");
//        console.add(flow);
            LayoutComponentBuilder standard
                    = builder.newLayout("PatternLayout");
            standard.addAttribute("pattern", "%d %5.5p %30.30C [%t] %m%n");
            
            console.add(standard);
            rollingFile.add(standard);
            
            builder.add(console);
            builder.add(rollingFile);
//        Appender appe = MyCustomAppenderImpl.createAppender("appe1", null, null, null);
//        AppenderComponentBuilder newAppender = builder.newAppender("appe", "appe1");
//        builder.add(appe);

            RootLoggerComponentBuilder rootLogger
                    = builder.newRootLogger(Level.INFO);
            rootLogger.add(builder.newAppenderRef("stdout"));
            rootLogger.add(builder.newAppenderRef("rolling"));
            builder.add(rootLogger);
            
            Configurator.initialize(builder.build());
//            System.out.println(builder.toXmlConfiguration());
            logger = LogManager.getLogger();
        }
        logger.info("log initialized");
    }
    
    static Logger logger;
    
}
