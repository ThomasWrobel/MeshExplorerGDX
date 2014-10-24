//package com.darkflame.client;
//
////new----------
//import java.util.ArrayList;
//import java.util.logging.Logger;
//
//import javax.xml.bind.annotation.DomHandler;
//
//import com.google.gwt.dom.client.Touch;
//import com.google.gwt.dom.client.Style.Unit;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.dom.client.ContextMenuEvent;
//import com.google.gwt.event.dom.client.ContextMenuHandler;
//import com.google.gwt.event.dom.client.FocusEvent;
//import com.google.gwt.event.dom.client.FocusHandler;
//import com.google.gwt.event.dom.client.KeyDownEvent;
//import com.google.gwt.event.dom.client.KeyDownHandler;
//import com.google.gwt.event.dom.client.KeyPressEvent;
//import com.google.gwt.event.dom.client.KeyPressHandler;
//import com.google.gwt.event.dom.client.KeyUpEvent;
//import com.google.gwt.event.dom.client.KeyUpHandler;
//import com.google.gwt.event.dom.client.MouseDownEvent;
//import com.google.gwt.event.dom.client.MouseDownHandler;
//import com.google.gwt.event.dom.client.MouseMoveEvent;
//import com.google.gwt.event.dom.client.MouseMoveHandler;
//import com.google.gwt.event.dom.client.MouseOutEvent;
//import com.google.gwt.event.dom.client.MouseOutHandler;
//import com.google.gwt.event.dom.client.MouseOverEvent;
//import com.google.gwt.event.dom.client.MouseOverHandler;
//import com.google.gwt.event.dom.client.MouseUpEvent;
//import com.google.gwt.event.dom.client.MouseUpHandler;
//import com.google.gwt.event.dom.client.MouseWheelEvent;
//import com.google.gwt.event.dom.client.MouseWheelHandler;
//import com.google.gwt.event.dom.client.TouchCancelEvent;
//import com.google.gwt.event.dom.client.TouchCancelHandler;
//import com.google.gwt.event.dom.client.TouchEndEvent;
//import com.google.gwt.event.dom.client.TouchEndHandler;
//import com.google.gwt.event.dom.client.TouchMoveEvent;
//import com.google.gwt.event.dom.client.TouchMoveHandler;
//import com.google.gwt.event.dom.client.TouchStartEvent;
//import com.google.gwt.event.dom.client.TouchStartHandler;
//import com.google.gwt.user.client.DOM;
//import com.google.gwt.user.client.Event;
//import com.google.gwt.user.client.Event.NativePreviewEvent;
//import com.google.gwt.user.client.History;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.AbsolutePanel;
//import com.google.gwt.user.client.ui.FocusPanel;
//import com.google.gwt.user.client.ui.HasAlignment;
//import com.google.gwt.user.client.ui.HasHorizontalAlignment;
//import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
//import com.google.gwt.user.client.ui.HasVerticalAlignment;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.SimplePanel;
//import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.user.client.ui.Widget;
//
///**
// * Essentially this is an absolute panel that can be dragged about by the mouse,
// * and have elements added too it with full muse actions
// * 
// * @author Thomas Wrobel
// **/
//
//public class SpiffyDragPanel extends FocusPanel implements MouseWheelHandler,
//		MouseOverHandler, MouseOutHandler, FocusHandler, TouchCancelHandler {
//	
//	static boolean draging = false;
//	
//	static int EdgePaddingForRestrictToScreen = 20;
//	// frame update time
//	static final int FRAME_RATE_UPDATE_TIME = 50; // 50 is safe
//
//	static boolean justDragged= false;
//	static Logger Log = Logger.getLogger("SpiffyDragPanel");
//
//	public ArrayList<FocusPanel> allObjectsOnPanel = new ArrayList<FocusPanel>();
//	
//	AbsolutePanel Container = new AbsolutePanel();
//	
//	int ContainerSizeX = 0;
//	int ContainerSizeY = 0;
//
//	private Widget currentlyDraggingWidget = null;
//	// for debugging
//	DragPanelDatabar databar = new DragPanelDatabar();
//
//	public AbsolutePanel dragableContents = new AbsolutePanel();
//	int DragDisX=0,DragDisY=0;
//	private Widget dragOnlyThis = null;
//	long dragstart = System.currentTimeMillis();
//	// --
//	int dragStartX = 0, dragStartY = 0;
//	SpiffyOverlay dynamicOverlayContents = new SpiffyOverlay();
//
//	private boolean editMode = false;
//	private boolean hardStop = false;
//	Boolean isCoasting = false;
//	Boolean isMoving = false;
//	private SpiffyLoadingIcon LoadingIcon;
//
//	VerticalPanel loadingMessage = new VerticalPanel();
//
//
//	
//	
//	// loading widget overlay
//	SimplePanel loadingOverlay = new SimplePanel();
//	int locationdownx = 0;
//	int locationdowny = 0;
//	private int Max_Height = -10000;// default (these arnt used to really check
//									// the height, as the container size has to
//																	// be taken into account)
//									private int Max_Width = -10000;// default
//
//	private int MaxXMovement = 0;
//	private int MaxYMovement = 0;
//	Label messageLabel;
//	private int MinXMovement = 0;
//
//	private int MinYMovement = 0;
//	private double MotionDisX = 0;
//	private double MotionDisY = 0;
//
//	Timer motionflow;
//	private String OldWidgetBorder="";
//
//	//controls panning the view when not coasting (ie, for transitions)
//	float currentPanPosX = 0;
//	 float currentPanPosY = 0;
//	 float endPanPosX = 0 ;
//	 float endPanPosY = 0 ;
//	 float PanDisplacementX = 0;
//	 float PanDisplacementY = 0;
//	Timer panTimer;
//	//------------
//	
//	
//	// for editing stuff
//	private Runnable OnFinishedEditingWidget;
//	boolean progressLabelOn = false;
//
//	Timer quickfade;
//	SpiffyOverlay staticOverlayContents = new SpiffyOverlay();
//	SpiffyDragPanel thisDragPanel = this;
//	int top = 0, left = 0;
//    public boolean XMOVEMENTDISABLED = false;
//	public boolean YMOVEMENTDISABLED = false;
//	
//	int PIXAL_DRAG_MINIMUM = 15;
//	
//	
//	/**
//	 * Essentially this is an absolute panel that can be dragged about by the mouse,
//	 * and have elements added too it with full mouse actions.
//	 * Elements added can flag if they want browser native events disabled or not.
//	 * By default, they are disabled (preventing right click actions like "copy image"
//	 * but also preventing textboxs being selected to place the cursor there.
//	 * 
//	 * @author Thomas Wrobel
//	 **/
//	public SpiffyDragPanel() {
//
//		super.setWidget(Container);
//		Container.add(dragableContents, 0, 0);
//
//		// add the static overlay
//		// staticOverlayContents.setVisibility(false);
//		staticOverlayContents.setMoveOverEventsEnabled(true);
//		// Container.add(staticOverlayContents, 0, 0);
//
//		// add the dynamic overlay
//		// dynamicOverlayContents.setVisibility(false);
//		dynamicOverlayContents.setMoveOverEventsEnabled(true);
//		// has to be readded after any other add operation in order to keep it
//		// on top.
//		// (this could be done with a zindex, but we dont want to hardcore a
//		// topmost value)
//		// dragableContents.add(dynamicOverlayContents, 0, 0);
//
//		// add the moving overlay
//
//		// --
//
//		Container.setSize("100%", "100%");
//		dragableContents.setSize("100%", "100%");
//		
//		this.addMouseMoveHandler(new SpiffyDragMouseMoveHandler(false));
//		this.addMouseUpHandler(new SpiffyDragMouseUpHandler(false,true));
//		this.addMouseDownHandler(new SpiffyDragMouseDownHandler(false, null,true));
//		
//		this.addMouseOutHandler(this);
//		this.addMouseOverHandler(this);
//		this.addFocusHandler(this);
//		this.addMouseWheelHandler(this);
//		
//		this.addTouchEndHandler(new SpiffyDragTouchEndHandler(false));
//		this.addTouchMoveHandler(new SpiffyDragTouchMoveHandler(false));
//		this.addTouchStartHandler(new SpiffyDragTouchDownHandler(false, null));
//
//		//prevent right clicks
////		dragableContents.addDomHandler(new MouseUpHandler(){
////			@Override
////			public void onMouseUp(MouseUpEvent event) {
////				//prevent a right click if the debug is not open
////				if (!databar.isAttached()){
////					event.preventDefault();
////				} else {
////					Log.info("databar not attached");
////				}
////			}
////			
////		}, MouseUpEvent.getType());
//		
//		dragableContents.addDomHandler(new ContextMenuHandler() {
//
//			@Override public void onContextMenu(ContextMenuEvent event) {
//				
//			
//				if (!databar.isAttached()){
//					event.preventDefault();
//					event.stopPropagation();
//				} 
//				
//			}
//		}, ContextMenuEvent.getType());
//		
////		
////		dragableContents.addDomHandler(new ClickHandler(){
////			@Override
////			public void onClick(ClickEvent event) {
////				//prevent a right click if the debug is not open
////				if (!databar.isAttached()){
////					event.preventDefault();
////				} else {
////					Log.info("databar not attached 2");
////				}
////			}
////			
////		}, ClickEvent.getType());
////		
//		
//		// quick fadeout
//		quickfade = new Timer() {
//			int o = 100;
//
//			@Override
//			public void run() {
//				o = o - 10;
//
//				loadingOverlay.getElement().getStyle().setOpacity(o / 100.0);
//
//				if (o < 10) {
//					Container.remove(loadingOverlay);
//
//					loadingOverlay.clear();
//					loadingMessage.clear();
//					this.cancel();
//				}
//			}
//		};
//
//		//setup a timer for the pan
//		panTimer = new Timer(){
//			
//			
//			@Override
//			public void run() {
//				
//				currentPanPosX = currentPanPosX + PanDisplacementX;
//				currentPanPosY = currentPanPosY + PanDisplacementY;
//				
//				//Log.info(" pos is now  "+currentPanPosX+","+currentPanPosY); 
//				
//				Boolean atX = false;
//				Boolean atY=false;
//		
//				if (Math.abs(currentPanPosX-endPanPosX)<(Math.abs(PanDisplacementX)+2)){
//										
//					currentPanPosX=endPanPosX;
//					atX = true;
//				}
//				if (Math.abs(currentPanPosY-endPanPosY)<(Math.abs(PanDisplacementY)+2)){
//										
//					currentPanPosY=endPanPosY;
//					atY = true;
//				}
//				
//				left = (int)-currentPanPosX;
//				top =  (int)currentPanPosY;
//				setPositionInternalCoOrdinates(left,top);
//								
//				//cancel if at both correct X and correct Y location
//				if (atX && atY){
//					cancel();
//				}
//				
//			}
//			
//		};
//		
//		
//		//setup the motionflow timer
//		//This controls all the main "grab and move" mouse coasting
//		//we dont use pan as pan works absolutely, and this works relatively
//		motionflow = new MotionFlowTimer();
//		
//		//ensure feedback is at top
//		//clickFeedbackImage.getElement().getStyle().setZIndex(9999999);
//		
//
//	}
//
//	/** adds a widget to the top left of the drag panel 
//	 * **/
//	@Override	
//	public void add(Widget widgetToAddAtTopLeft){
//		addWidget(widgetToAddAtTopLeft, 0, 0);
//	}
//
//	/** adds a widget to the position x,y in the drag panel
//	 * the widget is first added to a container, however, to ensure clicks and drags are handled correctlu 
//	 * **/
//	public void addWidget(Widget widget, int x, int y) {
//		addWidget(widget, x, y,true);
//	}
//	
//	
//	/** adds a widget to the position x,y in the drag panel
//	 * the widget is first added to a container, however, to ensure clicks and drags are handled correctly 
//	 * You can specify if focus is disabled or not. By default its true, which means images <br>
//	 * wont be selected and textboxs wont work **/
//	public void addWidget(Widget widget, int x, int y,final boolean disableFocus) {
//
//		// container widget
//		FocusPanel containerWidget = new FocusPanel();
//		containerWidget.add(widget);
//
//		dragableContents.add(containerWidget, x, y);
//		
//		//Log.info("widget added at:"+x+","+y);
//		//Log.info("widget added at:"+containerWidget.getElement().getStyle().getLeft()+","+containerWidget.getElement().getStyle().getTop());
//
//		// we have to add our own handlers to the widget to deal with dragging
//		// correctly
//		containerWidget.addMouseDownHandler(new SpiffyDragMouseDownHandler(
//				true, widget, disableFocus));
//		containerWidget.addMouseMoveHandler(new SpiffyDragMouseMoveHandler(true));
//		containerWidget.addMouseUpHandler(new SpiffyDragMouseUpHandler(true, disableFocus));
//		containerWidget.addMouseOutHandler(this);
//
//		containerWidget.addMouseOverHandler(this);
//		containerWidget.addFocusHandler(this);
//		containerWidget.addMouseWheelHandler(this);
//
//		containerWidget.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				Log.info("onclick from container");
//				// This will stop the event from being
//				// propagated
//				event.stopPropagation();
//				
//				if (disableFocus){
//					event.preventDefault();
//				}
//			}
//
//		});
//		
//		containerWidget.addTouchEndHandler(new SpiffyDragTouchEndHandler(true));
//		containerWidget.addTouchMoveHandler(new SpiffyDragTouchMoveHandler(true));
//		containerWidget.addTouchStartHandler(new SpiffyDragTouchDownHandler(true, widget));
//		containerWidget.addTouchCancelHandler(this);
//		
//		allObjectsOnPanel.add(containerWidget);
//
//	}
//	@Override
//	/** for this panel to work, widgets go on a subpanel within it. We dont want this subpanel removed, hence the clear command
//	 * just redirects to the clearAllWidgets command, that removes the inner widgets only**/
//	public void clear(){
//		clearAllWidgets();
//	}
//	public void clearAllWidgets(){
//		
//		for (Widget widgetOnPanel : allObjectsOnPanel) {
//			widgetOnPanel.removeFromParent();	
//			
//		}
//		
//		allObjectsOnPanel.clear();
//		
//	}
//	
//	public void clearJustDraggedFlag() {
//		Log.info("just draged set to false.");
//		justDragged=false;
//	}
//	
//	
//	
//	private void displacebyInternalCoOrdinates(int disX, int disY) {
//
//		// make sure X/Y isn't outside boundary's
//		if ((left > -MinXMovement) && (!XMOVEMENTDISABLED)) {
//			left = -MinXMovement;
//		}
//		;
//		if ((top > -MinYMovement) && (!YMOVEMENTDISABLED)) {
//			top = -MinYMovement;
//		}
//		;
//
//		// stop movement if disabled
//		if (XMOVEMENTDISABLED) {
//			// Log.info("______movement disabled in X");
//			disX = 0; // no displacement
//		}
//		if (YMOVEMENTDISABLED) {
//			// Log.info("______movement disabled in Y");
//			disY = 0;// no displacement
//		}
//
//		// if both disabled then we just stop
//
//		// stop movement at bottom right limits
//		if ((top < -MaxYMovement) && (!YMOVEMENTDISABLED)) {
//			// Log.info("hit height:" + Max_Height);
//			top = -MaxYMovement;
//		}
//		;
//
//		if ((left < -MaxXMovement) && (!XMOVEMENTDISABLED)) {
//			// Log.info("hit width" + Max_Width);
//			left = -MaxXMovement;
//		}
//		;
//
//		// get new co-ordinates based on old ones
//		left = left + disX;
//		top = top + disY;
//
//		// Log.info("set co-ordinates to " + left + " " + top);
//		setPositionInternalCoOrdinates(left, top);
//
//	}
//	
//	public void DisplayDebugBar(boolean b) {
//
//		if (b) {
//			
//			Log.info("__________________>>>>>>>>>>>  adding debug ");
//			
//			Container.add(databar, 0, 0);
//
//		} else {
//			Container.remove(databar);
//		}
//
//	}
//	
//	
//	/**
//	 * sets the edit mode on or off Edit mode allows the objects on this
//	 * dragpanel to be moved around. When a object is released from being
//	 * dragged "OnFinishedEditingWidget" is triggered. This can be set from the
//	 * "setOnFinishedEditingWidget" function
//	 * 
//	 * It also only edits the specific widget, or null if you want all editable at once
//	 **/
//
//	public void EditMode(boolean state,Widget EditOnlyThis) {
//		
//		Log.info("setting edit mode:"+ Boolean.toString(state));
//		
//		if (state) {
//			editMode = true;
//			dragOnlyThis = EditOnlyThis;
//			//currentlyDraggingWidget = dragOnlyThis;
//		} else {
//			editMode = false;
//			dragOnlyThis= null;
//			
//			if (currentlyDraggingWidget!=null){
//				//unhighlight
//				currentlyDraggingWidget.getElement().getStyle().setBorderColor(OldWidgetBorder);
//				currentlyDraggingWidget = null;
//			}
//			
//		}
//	}
//
//	public int getCurrentPanelAbsoluteX(){
//		return dragableContents.getAbsoluteLeft();
//	}
//
//	public int getCurrentPanelAbsoluteY(){
//		return dragableContents.getAbsoluteTop();
//	}
//
//	public long getLoadingTime(){
//		return LoadingIcon.currentTime;
//	}
//
//	/** gets the LEFT setting on the CSS **/
//	public int getWidgetLeft(Widget widget){
//		
//		//first we get the parent, as all widgets added to the panel should be contained in a containerWidget
//		FocusPanel containerWidget = (FocusPanel) widget.getParent();
//		//get the left
//		String leftAsString = containerWidget.getElement().getStyle().getLeft();
//		
//		
//		//convert to number
//		return Integer.parseInt(leftAsString); 
//		
//	}
//	
//	
//
//	/** gets the TOP setting on the CSS **/
//	public int getWidgetTop(Widget widget){
//		
//		//first we get the parent, as all widgets added to the panel should be contained in a containerWidget
//		FocusPanel containerWidget = (FocusPanel) widget.getParent();
//		//get the top
//		String topAsString = containerWidget.getElement().getStyle().getTop();
//		//convert to number
//		return Integer.parseInt(topAsString); 
//		
//	}
//
//	
//	
//	public boolean isOnPanel(Widget widget){
//
//		//first we get the parent, as all widgets added to the panel should be contained in a containerWidget
//		FocusPanel containerWidget;
//		try {
//			//first we see if the widgets parent is a FocusPanel, as all the panels widgets are contained by them.
//			containerWidget = (FocusPanel) widget.getParent();
//			
//		} catch (Exception e) {
//			// if not, then we return false
//			return false;
//		}
//		
//		
//		int wi = dragableContents.getWidgetIndex(containerWidget);
//		
//		//if its greater then -1, then its contained on this panel
//		if (wi>-1){
//			return true;
//		}
//		//else it isnt
//		return false;
//
//		
//	}
//
//	private void motionflow() {
//
//		// Log.info("set motionflow");
//
//		int displacementX = left - locationdownx;
//		int displacementY = top - locationdowny;
//		long period = System.currentTimeMillis() - dragstart;
//		// System.out.print("\n drag displacement time:"+period);
//
//		// displacement per unit of time;
//		MotionDisX = ((double) displacementX / (double) period) * 50;
//		MotionDisY = ((double) displacementY / (double) period) * 50;
//
//		// Log.info("\n drag displacement:" + MotionDisX + " " + MotionDisY);
//
//		// motionflow.cancel();
//		// only start if not already running
//		if (!(isCoasting)) {
//			motionflow.scheduleRepeating(FRAME_RATE_UPDATE_TIME);
//		} else {
//			Log.info("\n already coasting, so no new motion flow needed!");
//		}
//
//	}
//
//	@Override
//	public void onFocus(FocusEvent event) {
//		event.preventDefault();
//	}
//	
//	@Override
//	public void onLoad() {
//
//		ContainerSizeX = Container.getOffsetWidth();
//		ContainerSizeY = Container.getOffsetHeight();
//
//		Log.info("_containersizeX = " + ContainerSizeX);
//		Log.info("_containersizeY = " + ContainerSizeY);
//
//		MaxXMovement = Max_Width - ContainerSizeX;
//		MaxYMovement = Max_Height - ContainerSizeY;
//
//		super.onLoad();
//
//	}
//
//	private void onMouseOrTouchDown(int x, int y, int dx, int dy,boolean fromItem,Widget sourceWidget) {
//		
//		//Log.info("just draged set to false");
//		justDragged=false; //false untill movement starts
//		
//		// test if anything is under mouse
//		 Log.info("drag start a " + x + " y=" + y);
//		 Log.info("mouse down..");
//
//		dragStartX = x - left;
//		dragStartY = y - top;
//
//		draging = true;
//
//
//		
//		
//		
//		if (fromItem) {
//			
//			//probably should cancel any click actions the item has if we enter a drag?? (how?)
//			//might have to wait for a mouseup to tell the difference
//			
//			Log.info("MouseDown came from item");
//
//			// if edit mode on, we start dragging this one about!
//			if ((editMode) && (sourceWidget != null)) {
//				
//				
//				
//				stuffToDoWhenTheMouseIsDownWhileEditingStuff(dx,dy,
//						sourceWidget);
//			}
//		}
//
//		locationdownx = left;
//		locationdowny = top;
//		dragstart = System.currentTimeMillis();
//	}
//
//	private void onMouseOrTouchMove(int x, int y) {
//		
//
//		
//		databar.setCurrentMousePositionLabel(x - left, y - top);
//
//		if (editMode) {
//			stuffToDoWhenTheMouseMoveWhileEditingStuff(x,y);
//			return;
//		}
//
//		if (draging == true) {
//			
//			//diff in pos of mouse 
//			int dfx = Math.abs((x - left)-dragStartX);
//			int dfy = Math.abs((y - top)-dragStartY);
//			
//			//if we have moved a bit, then its a real drag so we trigger this
//			if ((dfx+dfy)>PIXAL_DRAG_MINIMUM){
//									
//				if (!justDragged){
//					justDragged=true; //used to determine for other code if this mouseup was from a drag
//					Log.info("___________started to drag:"+justDragged);
//					
//					Log.info("dfx="+dfx);
//					Log.info("dfy="+dfy);
//				}
//				
//			};
//			
//			if (!XMOVEMENTDISABLED) {
//				left = x - dragStartX;
//			};
//			if (!YMOVEMENTDISABLED) {
//				top = y - dragStartY;
//			};
//
//			// make sure X/Y isn't outside boundaries
//			if ((left > 0) && (!XMOVEMENTDISABLED)) {
//
//				Log.info("___________left outside range"+left);
//
//				left = 0;
//			}
//			;
//			if ((top > 0) && (!YMOVEMENTDISABLED)) {
//				top = 0;
//			}
//			;
//
//			// make sure X/Y isn't outside boundaries
//			if ((left < -MaxXMovement) && (!XMOVEMENTDISABLED)) {
//
//				Log.info("____________left outside range 2");
//				left = -MaxXMovement;
//			}
//			;
//			if ((top < -MaxYMovement) && (!YMOVEMENTDISABLED)) {
//				top = -MaxYMovement;
//			}
//			;
//			// Log.info("setting co-ordinates");
//			setPositionInternalCoOrdinates(left, top);
//
//			// int x = event.getRelativeX(Container.getElement());
//			// int y = event.getRelativeY(Container.getElement());
//
//			// left = x - dragStartX;
//			// top = y - dragStartY;
//
//			// Log.info("setting co-ordinates");
//			// Container.setWidgetPosition(dragableContents, left, top);
//
//			// setPositionInternalCoOrdinates(left, top);
//		}
//	}
//
//	private void onMouseOrTouchUp() {
//				Log.info("mouse up..");
//
//		if (draging == true) {
//			draging = false;
//			//justDragged=false;
//			// motion flow
//			motionflow();
//		}
//				
//				
//				// regardless of where it came from, on mouseup, we stop dragging things
//				if (editMode) {
//					stuffToDoWhenTheMouseUpWhileEditingStuff();
//				}
//
//
//	}
//
//	@Override
//	public void onMouseOut(MouseOutEvent event) {
//		event.preventDefault();
//		// Log.info("mouse leaving");
//
//		if (draging == true) {
//			draging = false;
//			//justDragged=false;
//			// motion flow
//			motionflow();
//		}
//	}
//
//	@Override
//	public void onMouseOver(MouseOverEvent event) {
//		draging = false;
//		//justDragged=false;
//		event.preventDefault();
//
//	}
//	
//	@Override
//	public void onMouseWheel(MouseWheelEvent event) {
//		event.preventDefault();
//	}
//	
//	@Override
//	public void onTouchCancel(TouchCancelEvent event) {
//		event.preventDefault();
//		 Log.info("touch drag canceled");
//
//		onMouseOrTouchUp();
//		
//	}
//
//	
//	public void removeWidget(Widget widget){
//	
//		//first we get the parent, as all widgets added to the panel should be contained in a containerWidget
//		FocusPanel containerWidget = (FocusPanel) widget.getParent();
//		
//		Log.info("_removing object from spiffyDragPanel which currently has "+dragableContents.getWidgetCount());
//		Boolean success = dragableContents.remove(containerWidget);
//		Boolean success2 =allObjectsOnPanel.remove(containerWidget);
//		Log.info("_removing_"+success+"_"+success2);
//		
//		
//	}
//	
//	/** sets the overall background color **/
//	public void setBackgroundColour(String css) {
//		
//		
//		Log.info("css = "+css);
//		Container.getElement().getStyle().setBackgroundColor(css);
//		
//
//	}
//	
//	/** sets the css background **/
//	public void setDraggableBackground(String url) {
//		Log.info("url(\"" + url + "\")");
//		dragableContents.getElement().getStyle()
//				.setBackgroundImage("url(\"" + url + "\")");
//
//	}
//	/** sets the css background repeat
//	 * valid values
//	 * "repeat-x"
//	 * "repeat-y"
//	 * "no repeat"
//	 * "repeat" (both x and y)**/
//	public void setDraggableBackgroundRepeat(String repeatmode ) {
//		Log.info("repeatmode =" + repeatmode + "");
//		
//		dragableContents.getElement().getStyle().setProperty("backgroundRepeat", repeatmode);
//		
//
//	}
//	/**
//	 * change the css of the staticOverlayContents if the CSS string is called
//	 * "OFF" it will disable the contents
//	 **/
//	public void setDynamicOverlayCSS(String css) {
//
//		Log.info("adding overlay:" + css);
//
//		if (css != null) {
//			if (css.equalsIgnoreCase("OFF")) {
//
//				// staticOverlayContents.setVisibility(false);
//				dragableContents.remove(dynamicOverlayContents);
//
//			} else {
//				if (!dragableContents.isAttached()) {
//
//					Log.info("attaching dynamic overlay:");
//					dragableContents.add(dynamicOverlayContents, 0, 0);
//				}
//				// staticOverlayContents.setVisibility(true);
//				dynamicOverlayContents.setCSS(css);
//			}
//		}
//	}
//	
//	public void setInternalSize(int x, int y) {
//
//		dragableContents.setSize(x + "px", y + "px");
//
//		// if the container size is bigger then the contents, then centre and
//		// disable movement in that direction
//		updateDragableSize();
//		/*
//		 * Max_Height = y; Log.info("Max_Height= " + Max_Height + "\"");
//		 * 
//		 * Max_Width = x; Log.info("Max_Width= " + Max_Width + "\"");
//		 * 
//		 * ContainerSizeX = Container.getOffsetWidth(); ContainerSizeY =
//		 * Container.getOffsetHeight();
//		 * 
//		 * Log.info("_containersizeX = " + ContainerSizeX);
//		 * Log.info("_containersizeY = " + ContainerSizeY);
//		 * 
//		 * MaxXMovement = Max_Width - ContainerSizeX; MaxYMovement = Max_Height
//		 * - ContainerSizeY;
//		 */
//
//	}
//	
//	/** sets the loading overlay background **/
//	public void setLoadingBackground(String ImageURL) {
//				if (ImageURL.length()>2){
//					loadingOverlay.getElement().getStyle().setBackgroundImage(ImageURL);
//				}
//	}
//	
//	
//	/** sets the loading overlay on/off **/
//	public void setLoading(boolean status, String Message) {
//
//		messageLabel = new Label(Message);
//
//		if (status) {
//
//			Container.add(loadingOverlay, 0, 0);
//			loadingOverlay.setStylePrimaryName("loadingOverlay");
//			loadingOverlay.getElement().getStyle().setBackgroundColor("#000");
//			loadingOverlay.getElement().getStyle().setZIndex(99999);
//			loadingOverlay.getElement().getStyle().setColor("#FFF");
//
//			loadingOverlay.setSize("100%", "100%");
//			
//			
//			LoadingIcon = new SpiffyLoadingIcon(false);
//			LoadingIcon.setProgressLabelVisible(true);
//			
//			
//			loadingMessage.getElement().getStyle().setColor("#FFF");
//			loadingMessage.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
//			loadingMessage
//					.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//
//			loadingMessage.setSpacing(7);
//			loadingMessage.add(messageLabel);
//			loadingMessage.add(LoadingIcon);
//			
//			loadingMessage.setSize("100%", "100%");
//			loadingMessage.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
//			
//			loadingOverlay.add(loadingMessage);
//
//		} else {
//
//			LoadingIcon.stopAnimation();
//			LoadingIcon.clear();
//
//			quickfade.scheduleRepeating(50);
//
//		}
//
//	}
//	
//	public void setLoadingCounter(boolean status) {
//		// set loading data
//		progressLabelOn = status;
//
//		LoadingIcon.setProgressLabelVisible(progressLabelOn);
//
//	}	
//		
//		
//	public void setLoadingTotal(int T) {
//		LoadingIcon.setTotalUnits(T);
//	}
//	
//	
//	public void setLoadingMessages(String[] messages) {
//		LoadingIcon.setLoadingMessages(messages);
//	}
//	
//	
//	public void setMouseOverEventsOnOverlays(boolean status) {
//
//		staticOverlayContents.setMoveOverEventsEnabled(status);
//		dynamicOverlayContents.setMoveOverEventsEnabled(status);
//		Log.info("set overlay mouse movement event sensing to:" + status);
//	}
//	/**
//	 * sets the movement limits for the panning imagine it as the small box
//	 * inside the bigger box...the user wont be able to move outside the small
//	 * box, but might see a little outside it when the movement bounces at the
//	 * edges
//	 * **/
//	public void setMovementLimits(int StartX, int StartY, int endX, int endY) {
//
//		// set top left
//
//		// set bottom right
//		Max_Height = endY;
//		Log.info("Max_Height= " + Max_Height + "");
//
//		Max_Width = endX;
//		Log.info("Max_Width= " + Max_Width + "");
//
//		ContainerSizeX = Container.getOffsetWidth();
//		ContainerSizeY = Container.getOffsetHeight();
//
//		Log.info("_containersizeX = " + ContainerSizeX);
//		Log.info("_containersizeY = " + ContainerSizeY);
//
//		MaxXMovement = Max_Width - ContainerSizeX;
//		MaxYMovement = Max_Height - ContainerSizeY;
//	
//		
//		MinXMovement = StartX;
//		MinYMovement = StartY;
//
//		updateDragableSize();
//	}
//
//	public void setOnFinishedEditingWidget(Runnable OnFinishedEditingWidget) {
//
//		this.OnFinishedEditingWidget = OnFinishedEditingWidget;
//
//	}
//
//	private void setPositionInternalCoOrdinates(int setX, int setY) {
//
//		// NEW: Move only the layer
//		Container.setWidgetPosition(dragableContents, setX, setY);
//
//		// Log.info("coordinates set");
//		databar.setCurrentPositionLabel(-setX, -setY);
//
//	}
//
//	/**
//	 * change the css of the staticOverlayContents if the CSS string is called
//	 * "OFF" it will disable the contents
//	 **/
//	public void setStaticOverlayCSS(String css) {
//
//		Log.info("adding overlay:" + css);
//
//		if (css != null) {
//			if (css.equalsIgnoreCase("OFF")) {
//
//				// staticOverlayContents.setVisibility(false);
//				Container.remove(staticOverlayContents);
//
//			} else {
//
//				// add it if not attached
//				if (!staticOverlayContents.isAttached()) {
//					Container.add(staticOverlayContents, 0, 0);
//				}
//				// staticOverlayContents.setVisibility(true);
//				staticOverlayContents.setCSS(css);
//			}
//		}
//	}
//
//	/** sets the view to the bottom left position **/
//
//	public void setViewToBottomLeft() {
//
//		Log.info("::::::::::::setting view to bottom left");
//
//		if (!XMOVEMENTDISABLED) {
//			left = -MinXMovement;
//		}
//		if (!YMOVEMENTDISABLED) {
//			top = -MaxYMovement;
//		}
//		setPositionInternalCoOrdinates(left, top);
//	}
//
//
//	
//
//	public void setViewToCenter(Boolean overrideMOVEMENTDISABLED) {
//		Log.info("::::::::::::setting view to center x/y");
//
//		if (!XMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
//			left = -(MinXMovement + MaxXMovement) / 2;
//			Log.info("::::::::::::-("+MinXMovement+" + "+MaxXMovement+")/ 2");
//		}
//		if (!YMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
//			top = -(MaxYMovement + MinYMovement) / 2;
//
//		}
//
//		Log.info("::::::::::::setting view to " + left + ", " + top);
//
//		setPositionInternalCoOrdinates(left, top);
//	}
//	
//	public void setViewToPos(int X, int Y) {
//		setViewToPos(X, Y,false);
//	}
//	
//	/** animates a transition to the specified position **/
//	public void scrollViewToPos(int X, int Y,Boolean overrideMOVEMENTDISABLED){
//		
//		//stop any current movement
//		motionflow.cancel();
//		
//		//get top left position
//		currentPanPosX = -this.getCurrentPanelAbsoluteX();
//		currentPanPosY = this.getCurrentPanelAbsoluteY();
//				
//		//convert the requested center position to top left position
//		endPanPosX = X-(this.ContainerSizeX/2);
//		endPanPosY = -(Y-(this.ContainerSizeY/2));
//		
//		//calculate the movement per Timer update
//		PanDisplacementX = (endPanPosX-currentPanPosX)/10f;
//		PanDisplacementY = (endPanPosY-currentPanPosY)/10f;
//		
//		Log.info(" starting at "+currentPanPosX+","+currentPanPosY);
//		Log.info(" going to "+endPanPosX+","+endPanPosY);
//		Log.info(" displacing by "+PanDisplacementX+", "+PanDisplacementY);
//			
//		//ensure Timer panTimer is running
//		if (!panTimer.isRunning()){
//			panTimer.scheduleRepeating(30);
//		} else {
//			Log.info(" timer already running ");
//		}
//	}
//	
//
//	public void setViewToPos(int X, int Y,Boolean overrideMOVEMENTDISABLED) {
//		Log.info("::::::::::::setting view to pos x/y" + X + "," + Y);
//
//		if (!XMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
//			left = -X + (ContainerSizeX / 2);
//
//		}
//		if (!YMOVEMENTDISABLED || overrideMOVEMENTDISABLED) {
//			top = -(Y - (ContainerSizeY / 2));
//
//		}
//
//		Log.info("::::::::::::setting view to " + left + ", " + top);
//
//		Log.info("::::::::::::ContainerSizeY = " + ContainerSizeY);
//
//		Log.info("::::::::::::ContainerSizeX = " + ContainerSizeX);
//		
//		setPositionInternalCoOrdinates(left, top);
//	}
//	public void setViewToTopCenter() {
//		Log.info("::::::::::::setting view to center to center");
//
//		if (!XMOVEMENTDISABLED) {
//			left = -(MinXMovement + MaxXMovement) / 2;
//		}
//		if (!YMOVEMENTDISABLED) {
//			top = -MaxYMovement;
//
//		}
//
//		Log.info("::::::::::::setting view to " + left + ", " + top);
//
//		setPositionInternalCoOrdinates(left, top);
//	}
//
//	/** sets the view to the top left position **/
//
//	public void setViewToTopLeft() {
//
//		Log.info("::::::::::::setting view to bottom left");
//
//		if (!XMOVEMENTDISABLED) {
//			left = -MinXMovement;
//		}
//		if (!YMOVEMENTDISABLED) {
//			top = MaxYMovement;
//		}
//		setPositionInternalCoOrdinates(left, top);
//	}
//
//	public void setWidgetsPosition(Widget widget, int x, int y,boolean restrictToScreen) {
//		
//		//make sure new position of object is fully within the screen
//		if (restrictToScreen){
//			
//			//Log.info("within screen check");
//			
//			int objectSizeX = widget.getOffsetWidth();
//			int objectSizeY = widget.getOffsetHeight();
//			
//			int currentXlimit = Math.abs(left)  + ContainerSizeX;	
//			int currentYlimit = Math.abs(top)  + ContainerSizeY;
//			
//			//left out of screen check
//			if (x<Math.abs(left)){				
//				x=Math.abs(left)+EdgePaddingForRestrictToScreen;						
//			}
//			
//			//top out of screen check
//			if (y<Math.abs(top)){				
//				y=Math.abs(top)+EdgePaddingForRestrictToScreen ;				
//			}
//			
//			//right out of screen check
//			if ((x+objectSizeX)>currentXlimit){	
//
//				//Log.info("right out of screen");
//				x = currentXlimit-objectSizeX-EdgePaddingForRestrictToScreen;								
//			}
//			//bottom out of screen check
//			if ((y+objectSizeY)>currentYlimit){		
//
//				//Log.info("bottom out of screen");
//				y = currentYlimit-objectSizeY-EdgePaddingForRestrictToScreen;								
//			}
//			
//		}
//
//
//		//Log.info("_____setWidgetPosition object at "+x+","+y);
//		// we get the parent, as its in a container widget
//		dragableContents.setWidgetPosition(widget.getParent(), x, y);
//
//	}
//
//	public void stepLoading() {
//		LoadingIcon.stepClockForward();
//	}
//
//	/**
//	 * Is this function name clear?
//	 * 
//	 * @param event
//	 * @param sourceWidget
//	 **/
//	private void stuffToDoWhenTheMouseIsDownWhileEditingStuff(
//			int X, int Y, Widget sourceWidget) {
//		
//
//		// remember grab point
//		Log.info("mouse down..... while editing at :");
//
//		
//		if (dragOnlyThis!=null){
//			//only allow drag if matchs
//			if (dragOnlyThis==sourceWidget){
//			currentlyDraggingWidget = dragOnlyThis;
//			}			
//		} else {
//			currentlyDraggingWidget = sourceWidget;
//		}
//		
//		//highlight		
//		OldWidgetBorder = currentlyDraggingWidget.getElement().getStyle().getBorderColor();		
//		currentlyDraggingWidget.getElement().getStyle().setBorderColor("#00F");
//		
//		Log.info("mouse down..... while editing at :"+X+","+Y);
//		
//		DragDisX = X;
//		DragDisY = Y;
//		
//	}
//
//	/**
//	 * Is this function name clear?
//	 * 
//	 * @param event
//	 **/
//
//	private void stuffToDoWhenTheMouseMoveWhileEditingStuff(int eventx,int eventy) {
//
//		// current mouse location
//		if (currentlyDraggingWidget != null) {
//			
//			int x = eventx - left;
//			int y = eventy - top;
//			
//			// remember grab point
//		//	Log.info("mouse move..... while editing ");
//			//currentlyDraggingWidget.getElement().getStyle().setBorderWidth(10, Unit.PX);
//			
//			this.setWidgetsPosition(currentlyDraggingWidget, x-DragDisX, y-DragDisY,false);
//
//		}
//	}
//
//	/**
//	 * Is this function name clear?
//	 **/
//	private void stuffToDoWhenTheMouseUpWhileEditingStuff() {
//		
//		//unhighlight
//		currentlyDraggingWidget.getElement().getStyle().setBorderColor(OldWidgetBorder);
//				
//				
//		currentlyDraggingWidget = null;
//		
//		Log.info("mouse up..... while editing ");
//		
//		
//		//run post actions
//		if (OnFinishedEditingWidget!=null){
//		OnFinishedEditingWidget.run();
//		}
//		
//		
//	}
//
//	public void testForNudgeKeyPressed(NativePreviewEvent event) {
//		
//		int charcode=event.getNativeEvent().getCharCode();
//		Log.info("key up:"+charcode);
//		
//		//nudge if editing
//		//97=a
//		//100=d
//		//115=s
//		if (editMode&&(dragOnlyThis != null)){
//			Log.info("key down:"+event.getNativeEvent().getCharCode()+" while editing");
//		
//
//				int x = dragOnlyThis.getParent().getElement().getOffsetLeft();
//				int y = dragOnlyThis.getParent().getElement().getOffsetTop();
//							
//				//move based on asdw keys 
//				if (charcode==97){
//					thisDragPanel.setWidgetsPosition(dragOnlyThis, x-1,y,false);
//				} else if (charcode==100 ){
//					thisDragPanel.setWidgetsPosition(dragOnlyThis, x+1,y,false);
//				} else if (charcode==115 ){
//					thisDragPanel.setWidgetsPosition(dragOnlyThis, x,y+1,false);
//				} else if (charcode==119 ){
//					thisDragPanel.setWidgetsPosition(dragOnlyThis, x,y-1,false);
//				}
//				
//				//update data
//				if (OnFinishedEditingWidget!=null){
//				OnFinishedEditingWidget.run();
//				}
//				
//		}
//	}
//
//	public void updateDragableSize() {
//
//		Log.info("updateDragableSize");
//
//		ContainerSizeX = Container.getOffsetWidth();
//		ContainerSizeY = Container.getOffsetHeight();
//
//		MaxXMovement = Max_Width - ContainerSizeX;
//		MaxYMovement = Max_Height - ContainerSizeY;
//
//		int x = dragableContents.getOffsetWidth();
//		int y = dragableContents.getOffsetHeight();
//
//		
//		Log.info("::::::::::::::::::::::::draggable contents x size =" + x
//				+ " container x size =" + ContainerSizeX);
//		
//		Log.info("::::::::::::::::::::::::draggable contents y size =" + y
//				+ " container y size =" + ContainerSizeY);
//		
//		Log.info("________________maxX movement = " + MaxXMovement);
//		Log.info("________________maxY movement = " + MaxYMovement);
//		
//		if (ContainerSizeX > x) {
//
//			left = ((ContainerSizeX / 2) - (x / 2));
//
//			Log.info("centering x=" + left);
//
//			// Container.setWidgetPosition(dragableContents, left ,top);
//			XMOVEMENTDISABLED = true;
//
//		}
//
//		if (ContainerSizeY > y) {
//
//			top = ((ContainerSizeY / 2) - (y / 2));
//
//			Log.info("centering y " + top);
//			// Container.setWidgetPosition(dragableContents, left , top);
//			YMOVEMENTDISABLED = true;
//
//		}
//
//		setPositionInternalCoOrdinates(left, top);
//	}
//
//	public boolean wasJustDragged() {
//		if (justDragged){
//			
//			//justDragged=false;
//			
//			
//			return true;
//		}
//		return false;
//	}
//
//	/** gives usefull debugging data **/
//	static class DragPanelDatabar extends HorizontalPanel {
//
//		// current mouse position
//		Label lab_currentMouseX = new Label("-");
//		Label lab_currentMouseY = new Label("-");
//
//		// current canvas position
//		Label lab_currentXpos = new Label("-");
//		Label lab_currentYpos = new Label("-");
//
//		public DragPanelDatabar() {
//			// fill container horizontal
//			this.setWidth("20%");
//			// set background to blacks
//			// this.getElement().getStyle().setBackgroundColor("BLACK");
//			
//			this.getElement().getStyle().setColor("WHITE");
//			this.getElement().getStyle().setProperty("textShadow", "rgb(0, 0, 0) 0px 0px 3px");
//			this.getElement().getStyle().setZIndex(900000);
//
//			addMouseFeedback();
//		}
//
//		private void addMouseFeedback() {
//			// add mouse feedback
//
//			VerticalPanel mouseLoc = new VerticalPanel();
//				
//			mouseLoc.add(lab_currentMouseX);
//			mouseLoc.add(lab_currentMouseY);
//
//			lab_currentMouseX.setStylePrimaryName("FeedbackLabel");
//			lab_currentMouseY.setStylePrimaryName("FeedbackLabel");
//
//			mouseLoc.setWidth("80px");
//			super.add(mouseLoc);
//			this.setCellWidth(mouseLoc, "90px");
//
//			VerticalPanel canvasLoc = new VerticalPanel();
//			canvasLoc.add(lab_currentXpos);
//			canvasLoc.add(lab_currentYpos);
//			canvasLoc.setWidth("60px");
//
//			lab_currentXpos.setStylePrimaryName("FeedbackLabel");
//			lab_currentYpos.setStylePrimaryName("FeedbackLabel");
//
//			this.add(canvasLoc);
//			this.setCellWidth(canvasLoc, "90px");
//
//		}
//
//		public void setCurrentMousePositionLabel(int X, int Y) {
//
//			lab_currentMouseX.setText("mx = " + X + "");
//			lab_currentMouseY.setText("my = " + Y + "");
//		}
//
//		public void setCurrentPositionLabel(int X, int Y) {
//
//			lab_currentXpos.setText("x = " + X + "");
//			lab_currentYpos.setText("y = " + Y + "");
//		}
//	}
//
//	private final class MotionFlowTimer extends Timer {
//		@Override
//		public void run() {
//
//			if (hardStop) {
//				Log.info("hard stopping");
//				isCoasting = false;
//				motionflow.cancel();
//				return;
//			}
//
//			// set in motion flag;
//			isCoasting = true;
//
//			// Log.info("seting coordinates from timer:");
//
//			displacebyInternalCoOrdinates((int) Math.round(MotionDisX),
//					(int) Math.round(MotionDisY));
//
//			// Log.info("set coordinates from timer_0");
//
//			// slow down
//			MotionDisX = (MotionDisX / 1.2);
//			MotionDisY = (MotionDisY / 1.2);
//
//			// stop
//			isMoving = false;
//			if ((MotionDisX < 1) && (MotionDisX > -1)) {
//				MotionDisX = 0;
//			} else {
//				isMoving = true;
//			}
//			if ((MotionDisY < 1) && (MotionDisY > -1)) {
//				MotionDisY = 0;
//			} else {
//				isMoving = true;
//			}
//
//			if (!(isMoving)) {
//				this.cancel();
//				Log.info("\n stoped");
//				isCoasting = false;
//
//			}
//			// Log.info("set coordinates from timer_end");
//
//		}
//
//	}
//	
//	class SpiffyDragMouseDownHandler implements MouseDownHandler {
//
//		private boolean fromItem = false;
//		private boolean disableFocus = false;
//
//		private Widget sourceWidget = null;
//		
//
//		public SpiffyDragMouseDownHandler(boolean fromItem,
//				Widget widget, boolean disableFocus) {
//			
//			
//
//			this.fromItem = fromItem;
//			this.sourceWidget = widget;
//			this.disableFocus = disableFocus;
//		}
//
//		@Override
//		public void onMouseDown(MouseDownEvent event) {
//			
//			if (disableFocus){
//				event.preventDefault();
//			}
//			DOM.setCapture(((Widget) event.getSource()).getElement());
//			// This will stop the event from being
//			// propagated
//			event.stopPropagation();
//			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);
//			
//			
//			Log.info("______onMouseDown");
//			
//			//int x = event.getX();
//			//int y = event.getY();
//			//fix?
//			int x = event.getRelativeX(Container.getElement());
//			int y = event.getRelativeY(Container.getElement());
//			
//			int dx = 0;
//			int dy =0;
//			
//			if ((editMode) && (sourceWidget != null)) {
//				
//			 dx  = event.getRelativeX(sourceWidget.getElement());
//			 dy  = event.getRelativeY(sourceWidget.getElement());
//			 
//			}
//			
//			onMouseOrTouchDown(x, y, dx, dy,fromItem,sourceWidget);
//
//		}
//
//		
//	}
//	class SpiffyDragMouseMoveHandler implements MouseMoveHandler {
//
//		private boolean cameFromItem2 = false;
//
//		public SpiffyDragMouseMoveHandler(boolean fromItem) {
//
//			cameFromItem2 = fromItem;
//		}
//
//		@Override
//		public void onMouseMove(MouseMoveEvent event) {
//			event.preventDefault();
//			event.stopPropagation();
//			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);
//			
//			//int x = event.getX();
//			//int y = event.getY();
//			//fix?
//			int x = event.getRelativeX(Container.getElement());
//			int y = event.getRelativeY(Container.getElement());
//			
//			onMouseOrTouchMove(x, y);
//		}
//
//	}
//
//	class SpiffyDragMouseUpHandler implements MouseUpHandler {
//
//		private boolean sourceWasItem = false;
//		private boolean disableFocus = false;
//
//		public SpiffyDragMouseUpHandler(boolean fromItem, boolean disableFocus) {
//			
//			sourceWasItem = fromItem;
//			this.disableFocus=disableFocus;
//			
//			Log.info("set up_:" + sourceWasItem + " ");
//
//		}
//
//		@Override
//		public void onMouseUp(MouseUpEvent event) {
//			if (disableFocus){
//				event.preventDefault();
//			}
//			// This will stop the event from being
//			// propagated
//			event.stopPropagation();
//			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);
//			DOM.releaseCapture(((Widget) event.getSource()).getElement());
//
//			onMouseOrTouchUp();
//	
//
//	
//		}
//
//	}
//
//	class SpiffyDragTouchDownHandler implements TouchStartHandler {
//
//		private boolean fromItem = false;
//
//		private Widget sourceWidget = null;
//		
//
//		public SpiffyDragTouchDownHandler(boolean fromItem,
//				Widget widget) {
//			
//
//			this.fromItem = fromItem;
//			this.sourceWidget = widget;
//
//		}
//
//		@Override
//		public void onTouchStart(TouchStartEvent event) {
//
//			//History.newItem("Touch start Event Detected!");
//		//	Window.setStatus("Touch start Event");
//
//			// This will stop the event from being
//			// propagated
//			event.stopPropagation();
//			if (true){
//			event.preventDefault();
//			}
//			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);
//			DOM.setCapture(((Widget) event.getSource()).getElement());
//			
//			Log.info("______onTouchStart");
//			
//			int x = event.getTouches().get(0).getRelativeX(event.getRelativeElement());
//			int y = event.getTouches().get(0).getRelativeY(event.getRelativeElement());
//			
//			int dx = 0;
//			int dy =0;
//			
//			if ((editMode) && (sourceWidget != null)) {
//			 dx = event.getTouches().get(0).getRelativeX(sourceWidget.getElement());
//			 dy = event.getTouches().get(0).getRelativeY(sourceWidget.getElement());
//			}
//			
//			Log.info("______triggering onMouseOrTouchDown");
//			
//			onMouseOrTouchDown(x, y, dx, dy,fromItem,sourceWidget);
//
//		}
//
//		
//	}
//
//	
//	class SpiffyDragTouchEndHandler implements TouchEndHandler {
//
//		private boolean sourceWasItem = false;
//
//		public SpiffyDragTouchEndHandler(boolean fromItem) {
//			sourceWasItem = fromItem;
//			Log.info("set up_:" + sourceWasItem + " ");
//
//		}
//
//		@Override
//		public void onTouchEnd(TouchEndEvent event) {
//			event.preventDefault();
//			// This will stop the event from being
//			// propagated
//			event.stopPropagation();
//			
//			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);
//			DOM.releaseCapture(((Widget) event.getSource()).getElement());
//
//			onMouseOrTouchUp();
//		}
//
//	}
//
//	class SpiffyDragTouchMoveHandler implements TouchMoveHandler {
//
//		private boolean cameFromItem2 = false;
//
//		public SpiffyDragTouchMoveHandler(boolean fromItem) {
//
//			cameFromItem2 = fromItem;
//		}
//
//
//		@Override
//		public void onTouchMove(TouchMoveEvent event) {
//			
//		//	History.newItem("Touch Move Event Detected!");
//		//	Window.setStatus("Touch Move Event");
//		
//			event.stopPropagation();
//			event.preventDefault();
//			DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);
//			
//			int x = event.getTouches().get(0).getRelativeX(event.getRelativeElement());
//			int y = event.getTouches().get(0).getRelativeY(event.getRelativeElement());
//			
//			Log.info("_____Touch Move Event Detected");
//			onMouseOrTouchMove(x, y);
//		}
//		
//	}
//
//
//}
