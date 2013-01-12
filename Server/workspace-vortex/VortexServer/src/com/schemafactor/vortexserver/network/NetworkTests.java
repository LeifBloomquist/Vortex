package com.schemafactor.vortexserver.network;

import java.net.*;
import java.io.*;

public class NetworkTests 
{
    public static void networkTests()
    {
        try {
			//URL oracle = new URL("http://api.thingspeak.com/"); //channels/1417/field/1/last.txt");
			URL oracle = new URL("http://184.106.153.149");	
			//URL oracle = new URL("http://www.example.com");
        	//URL oracle = new URL("http://www.google.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
			    System.out.println(inputLine);
			in.close();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
