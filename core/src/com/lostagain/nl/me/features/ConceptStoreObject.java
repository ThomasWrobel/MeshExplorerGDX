package com.lostagain.nl.me.features;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.SuperSimpleSemantics;
import com.darkflame.client.interfaces.GenericProgressMonitor;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.me.features.ConceptObjectSlot.SlotMode;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.gui.ScanManager;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.models.objectType;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.MySorter.ZIndexAttribute;

/**
 * This is a panel designed to store concept objects
 * They are locked by default untill scanned, whereupon copys can be taken
 * 
 * @author Tom
 *  
 */
public class ConceptStoreObject extends VerticalPanel implements GenericMeshFeature {

	final static String logstag = "ME.ConceptStoreObject";


	private final HashSet<SSSNode> StoredObjects = new HashSet<SSSNode>();
	


	LocationHub parentLocation;
	Label TitleLabel;
	
	public ConceptStoreObject(LocationHub locationHub) {
		parentLocation = locationHub;
		super.setSpaceing(5f);
		super.setPadding(15f); //padding around border
		
		super.getStyle().clearBackgroundColor();
		super.getStyle().clearBorderColor();
		
		TitleLabel = new Label("Concept Store Object");
		TitleLabel.setToScale(new Vector3(0.6f,0.6f,0.6f)); 
		TitleLabel.setLabelBackColor(Color.CLEAR);
		
		this.add(TitleLabel);
		
		
	}
	
	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}
	@Override
	public void updateApperance(float alpha, FeatureState currentState) {

		setOpacity(alpha);
	}

	
	
	public void addConceptObject(ConceptObject newConceptObject) {
		if (StoredObjects.contains(newConceptObject.itemsnode)){
			Gdx.app.log(logstag,"already has object:"+newConceptObject.itemsnode);			
			return;
		}

		ConceptObjectContainerBar newBar = new ConceptObjectContainerBar(newConceptObject);
		StoredObjects.add(newConceptObject.itemsnode);
		this.add(newBar);
		
	}
	
	static class ConceptObjectContainerBar extends HorizontalPanel implements GenericProgressMonitor {
		
		
		float StandardWidth = 350;	
		ProgressBar scanbar = new ProgressBar(20,5,StandardWidth-ConceptObjectSlot.WIDTH);
		
		ConceptObjectSlot slot = new ConceptObjectSlot();
		
		enum scanState {
			unstarted,scanning,finnished
		}
		scanState currentScanState = scanState.unstarted;
		SSSNode containsNode = null;
		
		public ConceptObjectContainerBar(ConceptObject newConceptObject){
			super.setMinSize(StandardWidth+30, 30);
			super.setAsHitable(true);
			containsNode=newConceptObject.itemsnode;
			
			this.getStyle().setBackgroundColor(Color.CLEAR);
			scanbar.setValue(5);
			Gdx.app.log(logstag,"adding scan bar widget.");
		
			
			
			add(scanbar);
			
			Label testLabelLala = new Label("|");
			testLabelLala.setLabelBackColor(Color.CLEAR);				
			add(testLabelLala);
			
			slot.setPivotAlignment(MODELALIGNMENT.TOPLEFT);
			add(slot);
			
			//locked  slot untill scanned
			slot.setCurrentMode(SlotMode.Locked);
			//set the concept in the slot 
			slot.setAsCointaining(newConceptObject);
			
			
			
		}
		
		private void setAsOpen(){
			this.getStyle().setBackgroundColor(Color.GREEN);
		}
		
		

		@Override
		public void fireTouchDown() {
				
				Gdx.app.log(logstag,"___touchdown on ConceptObjectContainerBar at position="+this.getLocalCollisionBox());
			
			if (currentScanState == scanState.unstarted){
				
				currentScanState = scanState.scanning;
				scanbar.setValue(0);			
				boolean successfullyStarted = ScanManager.addNewScan(this);
				
			}
			
			
		}

		@Override
		public void setTotalProgressUnits(int i) {
			scanbar.setTotalProgressUnits(i);			
			
		}

		@Override
		public void addToTotalProgressUnits(int i) {
			scanbar.addToTotalProgressUnits(i);			
		}

		@Override
		public void setCurrentProcess(String message) {
			scanbar.setCurrentProcess(message);			
		}

		@Override
		public void stepProgressForward() {
			scanbar.stepProgressForward();
		}

		@Override
		public void setCurrentProgress(int i) {
			scanbar.setCurrentProgress(i);
			
			//detect 100% here for done then unlock slot
			if (i>99){
				scanbar.setValue(100);
				currentScanState = scanState.finnished;
				//unlock slot
				slot.setCurrentMode(SlotMode.OutOnly);
				
				//add to datastore
				PlayersData.addItemToDatabase(containsNode, "local",true);
				
				//set style as open
				setAsOpen();
				
			}
			
		}

		/**
		 * does this object block whats behind it?
		 * @return
		 */
		@Override
		public objectType getInteractionType() {
			return objectType.Blocker;
		}

		
	}

	MeshIcon parentIcon = null;
	@Override
	public void setParentMeshIcon(MeshIcon icon) {
		parentIcon = icon;
		return;
	}

	@Override
	public MeshIcon getParentMeshIcon() {
		return parentIcon;
	}

	@Override
	public void clear() {	
		super.clear();
		StoredObjects.clear();
	}
	
	@Override
	public Vector3 getDefaultCameraPosition() {
		//gets the center of this email on the stage
		Vector3 center = getCenterOnStage();
		center.z = ScreenUtils.getSuitableDefaultCameraHeight();
		
		return center;
	}

	
	@Override
	public void setZIndex(int index, String group) {
		//set zindex of back material
		super.getStyle().addAttributeToShader(new ZIndexAttribute(index,group));
	
		//but we also need to apply it to all subobjects (only a little higher!)
		for (Widget childwidget : super.getChildren()) {
			childwidget.getStyle().addAttributeToShader(new ZIndexAttribute(index+1,group)); //NOTE; wont work for sub-sub objects as zindex isnt part of gwtish
		}
		
		
	}

}
