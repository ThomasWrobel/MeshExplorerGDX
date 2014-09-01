package com.lostagain.nl.uti;

import java.util.logging.Logger;

import com.badlogic.gdx.math.Vector2;

public class MeshWorld {

	static Logger Log = Logger.getLogger("MeshWorld");
	
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
		
		int Xlex = Xletters.compareTo("AAAAAAAAAAAAAAAAAAAAAAA")*50;
		int Ylex = Yletters.compareTo("AAAAAAAAAAAAAAAAAAAAAAA")*50;
		
			
		return new Vector2(Xlex,Ylex);
		
	}
	
}
