package com.lostagain.nl.uti;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.logging.Logger;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.darkflame.client.interfaces.SSSGenericFileManager;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;

//NOTE: No save implemented on this SSSGenericFileManager
public class FileManager implements SSSGenericFileManager{
	
	static Logger Log = Logger.getLogger("sss.JavaFileManager");
	Boolean useTextFetcher=false;
	
	@Override
	public void getText(String location,
			FileCallbackRunnable runoncomplete, FileCallbackError runonerror,
			boolean forcePost, boolean useCache) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getText(String location,
			FileCallbackRunnable runoncomplete, FileCallbackError runonerror,
			boolean forcePost) {
		
		if (!location.contains("://")){
		   getLocalFile(location, runoncomplete, runonerror); //Note; Currently all relative path game files need to be compiled in
		} else {
		   getFromURL(location, runoncomplete, runonerror,forcePost);
		}
		
		

	}
	
	class MEFileResponseListener implements HttpResponseListener {
		private FileCallbackRunnable runoncomplete;
		private FileCallbackError runonerror;
	    
		String url="";
		
	    public MEFileResponseListener(String url,FileCallbackRunnable runoncomplete,
				FileCallbackError runonerror) {
	    	
	    	this.runoncomplete = runoncomplete;
	    	this.runonerror = runonerror;
	    	this.url = url;
	    	
		}

		public void handleHttpResponse(HttpResponse httpResponse) {
			
			if (httpResponse.getStatus().getStatusCode()!=HttpStatus.SC_OK){
				
				Log.info("status code="+httpResponse.getStatus().getStatusCode());
				
			}
			runoncomplete.run(httpResponse.getResultAsString(), httpResponse.getStatus().getStatusCode());
			
			
	    }

	    public void failed(Throwable t) {
	    	
	    	runonerror.run("failed to get:"+url, t);
	    	
	    }

	   
		@Override
		public void cancelled() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private void getFromURL(String location,
			FileCallbackRunnable runoncomplete, FileCallbackError runonerror, boolean forcePost) {
		
		HttpRequest newrequest;
		
		if (!useTextFetcher){
			forcePost = false; //temp override - we should only use POST if we are using textFetcher and its requested to be used 
		}
		
		if (!forcePost) {
			newrequest = new HttpRequest(HttpMethods.GET);
		} else {
			newrequest = new HttpRequest(HttpMethods.POST);
		}
		
		newrequest.setUrl(location);
		
		//newrequest.setHeader(name, value);
		
		
		//followed used somehow to send variables, we use this if using the textfetcher.php system
		//to bypass SOP issues
		//newrequest.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		
		HttpResponseListener httpResponseListener = new MEFileResponseListener(location,runoncomplete,runonerror);
		
		Log.info("getting file at:"+location+" with  a "+newrequest.getMethod());
		
		
		Gdx.net.sendHttpRequest(newrequest, httpResponseListener);
		
		/*
		
		
		URL myUrl;
		
		
		
		try {
			myUrl = new URL(location);
			
		} catch (MalformedURLException e1) {
			Log.info(" couldn't get remote file;"+e1.getLocalizedMessage());
			runonerror.run(e1.getLocalizedMessage(), e1);
			return;
		}
		


	    try {
	    	
		    BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    myUrl.openStream()));

		    String line="";
	    	String file="";
	    	
			while ((line = in.readLine()) != null){
				
				file=file+"\n"+line;
			    
			}
			  in.close();
			  
			  runoncomplete.run(file, 200);
			  
		} catch (IOException e) {
			Log.info(" couldn't get remote file;"+e.getLocalizedMessage());
			runonerror.run(e.getLocalizedMessage(), e);
		}
*/
	  
		
	}
	
	
	private void getLocalFile(String location,
			FileCallbackRunnable runoncomplete, FileCallbackError runonerror) {
				
		Log.info(" getting file :"+location);
		
		FileHandle handle = Gdx.files.internal(location);
		
		if (handle.exists()){
			
			String text = handle.readString();
			
			runoncomplete.run(text, 200);
			
			
		} else {
			
			runonerror.run(" file:"+location+" does not exist", new Throwable());
			
		}
		
		/*
		//make absolute if needed

		Path fileloc =Paths.get(location);	
		
		if (fileloc.isAbsolute()){
				
			
		} else {
			Path currentRelativePath = Paths.get("");		
			Log.info(" getting file at currentRelativePath="+currentRelativePath.toAbsolutePath().toString());
			
			//not sure if slash is needed here
			String filepath = currentRelativePath.toAbsolutePath().toString() +"/"+ location;		
			
			fileloc = currentRelativePath.toAbsolutePath().resolve(filepath);
			
			
		}

		Log.info(" getting file at="+fileloc.toString());
		
		try {
			

			String contents = new String(Files.readAllBytes(fileloc));
		
			
			
			Log.info(" got contents="+contents);
			
			//fire runnable
			runoncomplete.run(contents, 200);
			
		} catch (IOException e) {
			
			
			Log.info(" couldn't get file;"+e.getLocalizedMessage());
			//Log.info("in "+currentRelativePath.toAbsolutePath().toString());
			//Log.info("filepath was = "+filepath);
		
			runonerror.run(e.getLocalizedMessage(), e);
			
		}*/
	}
	

	@Override
	public String getAbsolutePath(String relativepath) {
		
		//if its already absolute we just return it unchanged
		//eg c:\blahblah\blah.ntlist
		//or http:\\www.blahblah.com\blah\blah.ntlist
		//or http:\\blahblah.com\blah\blah.ntlist
		Log.info("get canonical name of:"+relativepath);
		
		//if its a web address ignore it
		if (relativepath.contains("://")||relativepath.startsWith("www.")){
						
			Log.info("path is likely a web address so we ignore");
			
			return relativepath;
			
		}
		
		if(Gdx.app.getType()==ApplicationType.WebGL) {

			Log.info("running in webmode so we ignore");
			//FileHandle test1 = Gdx.app.getFiles().absolute(relativepath);
			FileHandle test2 = Gdx.app.getFiles().getFileHandle(relativepath, FileType.Internal);
		//	FileHandle test3 = Gdx.app.getFiles().getFileHandle(relativepath, FileType.Classpath); //doesnt work in gwt
		//	FileHandle test4 = Gdx.app.getFiles().getFileHandle(relativepath, FileType.External);
		//	FileHandle test5 = Gdx.app.getFiles().getFileHandle(relativepath, FileType.Local);

			//Log.info("paths:"+test1.path());
			Log.info("paths:"+test2.path());
		//	Log.info("paths:"+test3.path());
			//Log.info("paths:"+test4.path());
		//	Log.info("paths:"+test5.path());
			
			
			Log.info("paths:"+Gdx.files.classpath(relativepath));
			
			return relativepath;
		}
		
		//if not then, as this is a Java and thus local we use File to get the absolute
		//path:		
		File f = new File(relativepath);
		String can =  "ERROR_GETTING_CANONICAL PATHNAME";
		try {
			
			can = f.getCanonicalPath();
			
			Log.info("path now:"+can);
			if (can==null){	
				Log.info("path null");
			}
			
		} catch (Exception e) {

			Log.severe("can not get canonical pathname of :"+relativepath+" "+e.getLocalizedMessage());
			can =  "ERROR_GETTING_CANONICAL PATHNAME";
		}
		
		return can;
		
	}

	@Override
	public boolean saveTextToFile(String location, String contents, FileCallbackRunnable runoncomplete,
			FileCallbackError runonerror) {
		// TODO Auto-generated method stub
		return false;
	}
	
}