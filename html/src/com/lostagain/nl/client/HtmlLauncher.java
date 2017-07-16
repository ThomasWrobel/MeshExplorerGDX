package com.lostagain.nl.client;

import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.user.client.Window;
import com.lostagain.nl.ME;

public class HtmlLauncher extends GwtApplication {
	static Logger Log = Logger.getLogger("HtmlLauncher");
	
//        @Override
//        public GwtApplicationConfiguration getConfig () {
//        //	Gdx.app.setLogLevel(Application.LOG_INFO);
//        	Log.info("GwtApplicationConfiguration0");
//    		System.out.print("GwtApplicationConfiguration");
//    		
//    		
//    		return new GwtApplicationConfiguration(640, 480);
//                
//                
//        }
//
//        @Override
//        public ApplicationListener getApplicationListener () {
//        	//	Gdx.app.setLogLevel(Application.LOG_INFO);
//        		Log.info("test, returning class ME()d ");
//        		System.out.print("test, returning class ME() ");
//                return new ME();
//        }

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Window.getClientWidth(), Window.getClientHeight()); //480,320 is default
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new ME();
        }
}