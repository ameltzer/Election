package election_map_viewer.events;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import dbf_framework.DBFFileIO;

import election_map_viewer.ElectionMapDataModel;
/**
 * Used for managing the event handling for mouse-overs of states or 
 * counties that we can then hightlight.
 * 
 * @author Richard McKenna, Aaron Meltzer
 */
public class ElectionMapMouseOverShapeHandler implements MouseMotionListener
{
	// WE'LL NEED TO UPDATE THE DATA
	private ElectionMapDataModel dataModel;
	
	/**
	 * The constructor sets up everything for use.
	 */
	public ElectionMapMouseOverShapeHandler(ElectionMapDataModel initDataModel)
	{
		dataModel = initDataModel;
	}
	
	/**
	 * This method responds to mouse movement by testing to
	 * see if the mouse is currently over a shape. If it is,
	 * that shape is highlighted. Note that the data model
	 * provides this service, since it manages all the data.
	 */
	public void mouseMoved(MouseEvent me) 
	{
		// GET THE CURRENT MOUSE LOCATION
		int x = me.getX();
		int y = me.getY();
		dataModel.highlightMapRegion(x, y);
		DBFFileIO input = new DBFFileIO();
		if(dataModel.getCurrentMapAbbr()=="USA" && dataModel.getRenderer().getPolyLocation()!=-1){
			try {
				int location = dataModel.getRenderer().getPolyLocation();
				File currentFile = dataModel.getRenderer().getFile();
				dataModel.setCurrentStateAbbr((String)input.loadDBF(currentFile).getRecord(location).getData(1));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(dataModel.getCurrentMapAbbr()!="USA" && dataModel.getRenderer().getPolyLocation()!=-1){
			try {
				int location = dataModel.getRenderer().getPolyLocation();
				File currentFile = dataModel.getRenderer().getFile();
				dataModel.setCurrentStateAbbr((String)input.loadDBF(currentFile).getRecord(location).getData(0));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(dataModel.getCurrentMapAbbr()!="USA" && dataModel.getRenderer().getPolyLocation()==-1){
			String nameFile= dataModel.getRenderer().getFile().getName();
			int i=0;
			String name ="";
			while(nameFile.charAt(i)!='.'){
				name=name.concat(Character.toString(nameFile.charAt(i)));
				i++;
			}
			dataModel.setCurrentStateAbbr(name);
		}
		else if(dataModel.getCurrentMapAbbr()=="USA" && dataModel.getRenderer().getPolyLocation()==-1){
			dataModel.setCurrentStateAbbr("USA");
		}
		
	}

	// WE WON'T USE THIS ONE
	public void mouseDragged(MouseEvent me) {}
}