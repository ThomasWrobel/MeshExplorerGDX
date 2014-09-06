package com.lostagain.nl.uti;

import java.util.logging.Logger;

import com.badlogic.gdx.math.Vector2;

/** a class providing utilities for the whole meshworld **/
public class MeshWorld {

	static Logger Log = Logger.getLogger("ME.MeshWorld");
	
	final static int TOTALX = 1000;
	final static int TOTALY = 1000;
	
	/** a method of putting things on a x/y map purely from a domain name.**/
	static public Vector2 locationFromDomain(String domain){
		
		//remove protocol
		domain = domain.substring(domain.indexOf("//")+1);
		
		
		String Xletters = "";
		String Yletters = "";
		int charnum=0;
		//divide letters
		for (char character : domain.toCharArray()) {
			
			if (charnum%2==0){
				Xletters = Xletters + character;
			} else {
				Yletters = Yletters + character;
			}
			
				charnum++;
		}
		
	Log.info("Xletters="+Xletters);
	Log.info("Yletters="+Yletters);
		
		int Xlex = (Xletters.compareTo("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")-10)*50;
		int Ylex = (Yletters.compareTo("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")-10)*50;
		/*
		int TestValue = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ".compareTo("AAAAAAAAAAAAAAAAAAAAAAA");		
		Log.info("TestValue="+TestValue); 25

		 TestValue = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".compareTo("AAAAAAAAAAAAAAAAAAAAAAA");		
		Log.info("TestValue="+TestValue); 14
		
		 TestValue = "AAAAAAAAAAAAAAAAAAA".compareTo("AAAAAAAAAAAAAAAAAAAAAAA");		
			Log.info("TestValue="+TestValue); -4
			*/
		return new Vector2(Xlex,Ylex);
		
	}
	
}
