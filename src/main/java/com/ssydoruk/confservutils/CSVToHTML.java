/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.ssydoruk.confservutils;

import Utils.*;
import Utils.Pair;
import Utils.swing.*;
import com.genesyslab.platform.applicationblocks.com.*;
import com.genesyslab.platform.applicationblocks.com.objects.*;
import com.genesyslab.platform.applicationblocks.com.queries.*;
import com.genesyslab.platform.commons.collections.*;
import com.genesyslab.platform.commons.protocol.Message;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.confserver.events.*;
import com.genesyslab.platform.configuration.protocol.types.*;
import com.google.gson.*;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.ssydoruk.confservutils.ConfigConnection;
import com.ssydoruk.confservutils.ConfigServerManager;
import confserverbatch.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.*;
import org.xbill.DNS.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 *
 * @author ssydoruk
 */
public class CSVToHTML {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		String file = "D:\\archive\\uhg\\ssydoruk\\Downloads\\_work.csv";
//        openCSV(file);
		apacheCSV(file);
		System.out.println("-1-");
		System.exit(0);
	}

	public static void openCSV(String file) {
		try {

			final CSVReader reader = new CSVReaderBuilder(new FileReader(file))
				.withCSVParser(new CSVParserBuilder()
					.withSeparator('\t')
					.withSeparator(',')
					.withIgnoreQuotations(true)
					.build())
				.build();
			java.util.List<String[]> readAll = reader.readAll();
			System.out.println(":");

		} catch (FileNotFoundException ex) {
			java.util.logging.Logger.getLogger(CStoJS.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
		} catch (CsvException ex) {
			java.util.logging.Logger.getLogger(AppForm.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public static Document createShell(String baseUri) {
		Validate.notNull(baseUri);
		Document doc = new Document(baseUri);
		Element html = doc.appendElement("html");
		html.appendElement("head");
		html.appendElement("body");
		return doc;
	}

	private static void apacheCSV(String file) {
		File jsFormatFile = new File("I:\\src\\src\\logbrowser\\bin\\etc\\logbrowser\\ORS_CSV_HTML.js");
		String script = null;
		if (jsFormatFile.canRead()) {
			script = FileUtils.loadFile(jsFormatFile);
//            JSRunner.getInstance().setDebugPort("4889");
		}

		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
//            CSVFormat fmt = CSVFormat.Builder.create().setDelimiter(",").setSkipHeaderRecord(true).setQuoteMode(QuoteMode.MINIMAL).build();
//            CSVParser records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
			CSVParser records = CSVFormat.RFC4180
				.builder()
				.setDelimiter(",")
				.setHeader()
				.setSkipHeaderRecord(true)
				.setQuoteMode(QuoteMode.MINIMAL)
				.build()
				.parse(in);
			Document html = createShell("aaa.html");
			html.title("This is title");
			html.body().appendElement("h1").attr("id", "header").text("Welcome");
			File ccsFile = new File("D:\\archive\\uhg\\ssydoruk\\Downloads\\out.css");
			if (ccsFile.canRead()) {
				html.body().appendElement("style").text(FileUtils.loadFile(ccsFile));
			}

			Element tab = html.body().appendElement("table").attr("id", "tab").attr("border", "1");

			Element colgroup = tab.appendElement("colgroup");
			java.util.List<String> headerNames = records.getHeaderNames();
			HashMap<String, String> row = new HashMap<>();
			for (int i = 0; i < headerNames.size(); i++) {
				colgroup.appendElement("col").attr("class", headerNames.get(i));
			}

			Element tabHead = tab.appendElement("thead");
			Element tabRow = tabHead.appendElement("tr");
			headerNames = records.getHeaderNames();
			int timeIdx = -1;
			row = new HashMap<>();
			for (int i = 0; i < headerNames.size(); i++) {
				if (headerNames.get(i).equals("_time")) {
					timeIdx = i;
				}
				tabRow.appendElement("th").attr("class", headerNames.get(i)).text(headerNames.get(i));
			}

			Element tabBody = tab.appendElement("tbody");
			for (CSVRecord record : records.getRecords()) {
				for (int i = 0; i < record.size(); i++) {
					row.put(headerNames.get(i), record.get(i));
				}

				if (script != null) {
					JSRunner.runCSVFormatScript(script, row);
				}
				tabRow = tabBody.appendElement("tr");
				for (int i = 0; i < record.size(); i++) {
					Element cell = tabRow.appendElement("td");
					if (i != timeIdx) {
						cell.attr("style", "word-break:break-all;");
					}
//                    cell.html(row.get(headerNames.get(i)).replaceAll("\n", "<br>"));
					cell.html(row.get(headerNames.get(i)));
				}
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\archive\\uhg\\ssydoruk\\Downloads\\out.html"))) {
				writer.write(html.outerHtml());
			} catch (IOException ex) {
				java.util.logging.Logger.getLogger(CSVToHTML.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (FileNotFoundException ex) {
			java.util.logging.Logger.getLogger(CSVToHTML.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(CSVToHTML.class.getName()).log(Level.SEVERE, null, ex);
		}
//        System.out.println(html.outerHtml());

	}

}
