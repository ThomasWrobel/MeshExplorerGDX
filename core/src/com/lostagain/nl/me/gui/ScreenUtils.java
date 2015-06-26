package com.lostagain.nl.me.gui;

/** 
 * A class of static fields and methods to help deal with various screen/game field sizes.
 * The intention is to use this to, for example, get a appropriate font size, or camera distance.
 * We do this via this class so it can all be adjusted from one place. 
 * **/
public class ScreenUtils {
	
	
		
	/** enum that reflects various vertical pixel sizes of the stage.
	 * The number in brackets represents their upper size limit in pixels.
	 * So "tiny(320,#,#) can be read as "less then or equal to 320 pixels vertical
	 * The # refers to the other params which is the font size used for that screen size, default cam height etc **/
	enum StageVerticalSize
	{
		tiny(320,15,410),small(600,15,410),medium(1100,20,700),large(2000,20,850),insane(4000,30,900),OMGTOLARGETOCOMPREHENDITMUSTBETHEFUTURE(10000,50,1100);
		
		private int  MaxYRes;		
		private int  GUISpriteFontSize;
		private int  DefaultCameraHeight;
		
		StageVerticalSize(int MaxYRes,int GUISpriteFontSize,int DefaultCameraHeight){			
			this.MaxYRes = MaxYRes;
			this.GUISpriteFontSize = GUISpriteFontSize;
			this.DefaultCameraHeight = DefaultCameraHeight;
		}
		
		static StageVerticalSize getEnumForSize(float heightInPixels){
			
			
			//work out the correct enum for the size
			for (StageVerticalSize sizeEnum : StageVerticalSize.values()) {
				
				if (heightInPixels<=sizeEnum.MaxYRes){
					return sizeEnum;
				}
				
			}
			
			//default to OMGTOLARGETOCOMPREHENDITMUSTBETHEFUTURE if nothing else.
			return OMGTOLARGETOCOMPREHENDITMUSTBETHEFUTURE;			
		}


		public float getDefaultCameraHeight() {
			return DefaultCameraHeight;
		}
		public int getGUISpriteFontSize() {
			return GUISpriteFontSize;
		}
	}
   //--------------------------------------------------
	
	private static StageVerticalSize currentVerticalSize = StageVerticalSize.medium; //default to medium
	
	
	/** Should be called every time the stage size changes **/
	static public void setup(float WidthInPixels,float HeightInPixels){		
		//get the new enums
		currentVerticalSize = StageVerticalSize.getEnumForSize(HeightInPixels);
				
		
	}
	/** 
	 * Gets the appropriate font size for the current screen size.
	 * Only useful once we have vector fonts supported (or multi-res bitmap)
	 * /
	static public int getGUIFontSize(){
		return currentVerticalSize.getGUISpriteFontSize();
	}
	
	
	/**
	 * Gets appropriate camera height for the current screen size
	 * @return float for cam z
	 */
	static public float getSuitableDefaultCameraHeight(){		
		return currentVerticalSize.getDefaultCameraHeight();			
	}
	

}
