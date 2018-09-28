package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

public class Brain
{
	public static final double BITS_PER_DIGIT = 3.0;
	public static final Random RNG = new Random();	
	public static final String TCP_SERVER = "red.eecs.yorku.ca";
	public static final int TCP_PORT = 12345;
	public static final String DB_URL = "jdbc:derby://red.eecs.yorku.ca:64413/EECS;user=student;password=secret";
	public static final String HTTP_URL = "https://www.eecs.yorku.ca/~roumani/servers/4413/f18/World.cgi";
	public static final String ROSTER_URL = "https://www.eecs.yorku.ca/~roumani/servers/4413/f18/Roster.cgi";
	
	public Brain()
	{
	}
	
	public String doTime()
	{
		return (new Date()).toString();
	}
	
	public String doPrime(int length)
	{
		BigInteger result = BigInteger.probablePrime((int) Math.floor(3.3*length), new Random());
		return result.toString();
	}

	public String doTcp(int length) throws UnknownHostException, IOException {
		try {
			Socket socket = new Socket(TCP_SERVER, TCP_PORT);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			pw.write("prime " + length);
		    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
			br.read();
			String result = br.readLine();
			br.close();
			return result;
		} catch (Exception e) {
			return "Error";
		} 
	}
	
}