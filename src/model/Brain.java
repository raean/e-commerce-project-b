package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.xml.internal.txw2.Document;

import sun.net.www.protocol.http.HttpURLConnection;

public class Brain
{
	public static final double BITS_PER_DIGIT = 3.0;
	public static final Random RNG = new Random();	
	public static final String TCP_SERVER = "red.eecs.yorku.ca";
	public static final int TCP_PORT = 12345;
	public static final String DB_URL = "jdbc:derby://red.eecs.yorku.ca:64413/EECS;user=student;password=secret";
	public static final String HTTP_URL = "https://www.eecs.yorku.ca/~roumani/servers/4413/f18/World.cgi";
	public static final String ROSTER_URL = "https://www.eecs.yorku.ca/~roumani/servers/4413/f18/Roster.cgi";
	
	public Brain() {}
	
	public String doTime()
	{
		return (new Date()).toString();
	}
	
	public String doPrime(int length)
	{
		BigInteger result = BigInteger.probablePrime((int) Math.floor(3.3*length), new Random());
		return result.toString();
	}

	public String doTcp(int length) throws Exception {
		Socket socket = new Socket(TCP_SERVER, TCP_PORT);
		PrintStream pw = new PrintStream(socket.getOutputStream());
		pw.println("prime " + length);
		
	    Scanner br = new Scanner(socket.getInputStream());
		String result = br.nextLine();
		
		socket.close();
		return result;
	}
	
	public String doDb(String itemNo) throws Exception{
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
		  Connection con = DriverManager.getConnection(DB_URL);
		  Statement s = con.createStatement();
		  s.executeUpdate("set schema roumani");
		  String query = "SELECT Name, Price FROM item WHERE Number='"+itemNo+"'";
		  ResultSet r = s.executeQuery(query);
		  String result = "";
		  if (r.next())
		  {
		  	result = "$" + r.getDouble("PRICE") + " - " + r.getString("NAME");
		  }
		  else
		  {
		  	throw new Exception(itemNo + " not found!");
		  }
		  r.close(); s.close(); con.close();
		  return result;
	}
	
	public String doHttp(String country, String type) throws Exception {
		try {
			StringBuilder result = new StringBuilder();
			URL url = new URL("https://www.eecs.yorku.ca/~roumani/servers/4413/f18/World.cgi?country="
			+country+"&query="+type);
			URLConnection con = url.openConnection();
			((java.net.HttpURLConnection) con).setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
		    }
		    rd.close();
		    return result.toString();
		} catch (Exception e) {
			return "Error: Exception " + e.getMessage();
		}
	}

	public String doRoster(String courseName) {
		try {
			
			StringBuilder result = new StringBuilder();
			URL url = new URL("https://www.eecs.yorku.ca/~roumani/servers/4413/f18/Roster.cgi?course="+courseName);
			URLConnection con = url.openConnection();
			((java.net.HttpURLConnection) con).setRequestMethod("GET");
			((java.net.HttpURLConnection) con).setRequestProperty("Accept","application/xml");
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
		    }
		    rd.close();
		    
		    
		    org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		            .parse(new InputSource(new StringReader(result.toString())));
		    
		    StringBuffer htmlTableCode = new StringBuffer("<table border=\"1pt\">");
		    htmlTableCode.append("<tr>"
		    		+ "<td align=\"center\"><b>ID</b></td>"
		    		+ "<td align=\"center\"><b>Last Name</b></td>"
		    		+ "<td align=\"center\"><b>First Name</b></td>"
		    		+ "<td align=\"center\"><b>City</b></td>"
		    		+ "<td align=\"center\"><b>Program</b></td>"
		    		+ "<td align=\"center\"><b>Hours</b></td>"
		    		+ "<td align=\"center\"><b>GPA</b></td>"
		    		+ "</tr>");
		    NodeList nodes = doc.getChildNodes().item(0).getChildNodes();
		    for (int i = 2 ; i < nodes.getLength() ; i++) {
		    	NodeList students = nodes.item(i).getChildNodes();
		    	htmlTableCode.append("<tr>");
		    	for (int j = 0 ; j  < students.getLength() ; j++) {
		    		htmlTableCode.append("<td>" + students.item(j).getTextContent() + "</td>");
		    	}
		    	htmlTableCode.append("</tr>");
		    }
		    htmlTableCode.append("</table>");
		    System.out.println(htmlTableCode.toString());
		    
		    return htmlTableCode.toString();
//		    DOMSource source = new DOMSource(doc);
//		    FileWriter writer = new FileWriter(new File("/eecs/home/ragheban/eclipse-workspace/ProjB/WebContent/Roster.xml"));
//		    StreamResult resultFiled = new StreamResult(writer);
//
//		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		    Transformer transformer = transformerFactory.newTransformer();
//		    transformer.transform(source, resultFiled);
//		    
//		    File htmlTable = new File("/eecs/home/ragheban/eclipse-workspace/ProjB/WebContent/RosterTable.html");
//		    File rosterXSLFile = new File("/eecs/home/ragheban/eclipse-workspace/ProjB/WebContent/Roster.xsl");
//			rosterXSLFile.createNewFile();
//		    htmlTable.createNewFile();
//		    System.out.println("1 hello");
//		    Source xml = new StreamSource("/eecs/home/ragheban/eclipse-workspace/ProjB/WebContent/Roster.xml");
//		    Source xslt = new StreamSource(rosterXSLFile);
//		    System.out.println("2 hello");
//		    StringWriter sw = new StringWriter();
//		    FileWriter fw = new FileWriter(htmlTable);
//		    System.out.println("3 hello");
//		    TransformerFactory tFactory = TransformerFactory.newInstance(); 
//		    Transformer transform = tFactory.newTransformer(xslt);
//		    System.out.println("4 hello");
//		    transform.transform(xml, new StreamResult(sw));
//		    fw.write(sw.toString());
//		    fw.close();
//		    System.out.println("5 hello");
//			
		    
		} catch (Exception e) {
			return "Error " + e.getMessage();
		}
	}


}