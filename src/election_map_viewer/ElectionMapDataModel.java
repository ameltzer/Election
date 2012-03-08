package election_map_viewer;

import java.awt.Color;
import java.awt.Image;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;
import java.util.TreeMap;

import dbf_framework.DBFFileIO;
import dbf_framework.DBFRecord;
import dbf_framework.DBFTable;

import shp_framework.SHPData;
import shp_framework.SHPMap;
import shp_framework.geometry.SHPPolygon;
import shp_framework.geometry.SHPShape;

import static election_map_viewer.ElectionMapRenderer.*;
/**
 * This class serves as the data manager for all our application's 
 * core data. In in addition to initializing data, it provides service
 * methods for event handlers to use in manipulating the maps.
 * 
 * @author Richard McKenna, Aaron Meltzer
 **/
public class ElectionMapDataModel 
{
	//the DBFTable of a DBF File
	private DBFTable sections;
	// HERE'S THE MAP'S RENDERER, WHICH WE NEED TO NOTIFY WHENEVER
	// THERE ARE CHANGES TO DATA SO THAT IT REPAINTS ITSELF\
	private ElectionMapRenderer renderer;
	
	// HERE'S THE FILE MANAGER, WHICH WILL HELP WITH LOADING OF
	// STATE MAPS WHEN NEEDED
	private ElectionMapFileManager fileManager;

	// HERE'S WHERE THE USA MAP'S DATA IS STORED
	private SHPMap usaSHP;
	
	private SHPMap currentSHP;
	
	// HERE'S INFO ABOUT THE CURRENT MAP BEING RENDERED
	private String currentMapName;
	private String currentMapAbbr;
	//abr of a state
	private String stateAbbr;
	
	private String currentOverallAbbr;
	
	// THIS IS FOR HIGHLIGHTING A PART OF A POLYGON (LIKE A COUNTY OR STATE)
	private SHPPolygon highlightedPolygon;
	
	// WE'LL RECYCLE THIS POLYGON SO WE DON'T HAVE TO
	// KEEP CONSTRUCTING IT FOR OUR TESTS
	private Polygon testPoly;
	
	// THIS WILL STORE ALL OF OUR STATE FLAGS
	private TreeMap<String, Image> flags;
	private TreeMap<String, Image> miniFlags;

	// USED FOR TITLES AND THE USA MAP
	public static final String MAP_TITLE = " 2008 Presidential Election Results";
	public static final String USA_MAP_NAME = "USA";
	public static final String USA_MAP_ABBR = "USA";
	
	// .gif IMAGES ARE THE MINI IMAGES
	public static final String MINI_FLAG_EXT = ".gif";
	
	// THIS HELPS US TO KNOW IF A MAP HAS BEEN
	// RENDERED AT LEAST ONCE OR NOT
	private boolean mapRendered;
	
	/**
	 * Note that the data model is not fully setup after this constructor. It
	 * still needs the renderer and the file manager, which should be loaded
	 * when ready via the init method.
	 **/
	public ElectionMapDataModel()
	{
		// AND INITIALIZE OUR DATA STRUCTURES
		testPoly = new Polygon();
		flags = new TreeMap<String, Image>();
		miniFlags = new TreeMap<String, Image>();
		stateAbbr = "USA";
		
		// THE MAP HAS NOT YET BEEN RENDERED
		mapRendered = false;
		sections = new DBFTable();
	}

	// SIMPLE ACCESSOR METHODS
	public String 		getCurrentMapName() 		{ return currentMapName; 	}
	public String 		getCurrentMapAbbr() 		{ return currentMapAbbr; 	}
	public String		getOverallMapAbbr()			{ return this.currentOverallAbbr; }
	public SHPPolygon	getHighlightedPolygon()		{ return highlightedPolygon;}
	public boolean 	isMapLoaded() 			{ return currentMapName != null; 		}
	public boolean	isMapRendered()			{ return mapRendered;					}
	public boolean 	isRegionHighlighted()	{ return highlightedPolygon != null; 	}
	public TreeMap<String, Image> getFlags()  { return flags;							}
	public TreeMap<String, Image> getMiniFlags() { return miniFlags;				}
	public ElectionMapRenderer getRenderer() { return renderer;					}
	public DBFTable getTable()				 { return sections;					}
	public void setCurrentStateAbbr(String abbr)		{ this.stateAbbr =abbr;}
	public String getStateAbbr()	{return this.stateAbbr;}
	// MORE COMPLEX ACCESSOR METHODS
	
	/**
	 * For accessing the large flag of the map currently being rendered.
	 **/
	public Image getCurrentFlag()
	{
		return flags.get(currentMapAbbr);
	}	
	
	/**
	 * For accessing the SHPMap that corresponds to the the map that
	 * is currently being rendered.
	 **/
	public SHPMap getCurrentSHP()
	{
		// WE ONLY HAVE ONE FOR NOW, YOU MIGHT WANT TO CHANGE
		// HOW THIS WORKS SINCE YOU'LL HAVE OTHERS
		return currentSHP;
	}
	/*
	 * @params- map:SHPMap
	 */
	public void setCurrentSHP(SHPMap map){
		currentSHP=map;
	}
	/*
	 * @params- abbr:Sstring
	 */
	public void setCurrentMapAbbr(String abbr){
		this.currentMapAbbr = abbr;
	}
	/*
	 * @params- abbr:Sstring
	 */
	public void setOverallMapAbbr(String abbr){
		this.currentOverallAbbr = abbr;
	}
	/*@params- candidates:Candidate[], file:File
	 * @returns- String[]
	 * @throws- IOException
	 * This function takes an array of candidates and a file, extracts information from the file and adds them to the
	 * correct candidate. It then constructs the string for a specific candidate and stores it in an array.
	 * Finally it returns the array of Strings for future use
	 */
	public String[]  buildStrings(Candidate[] candidates, File file) throws IOException{
		String[] votes = new String[candidates.length];
		DBFRecord theRecord;
		DBFTable currentTable = (new DBFFileIO().loadDBF(file));
		//if a state is selected
		if(renderer.getPolyLocation()!=-1)
			theRecord = (currentTable.getRecord(renderer.getPolyLocation()));
		else
			theRecord = currentTable.getRecord(0);//default record, only to satisfy the arguments
		//for each candidate create a string
		for(int i=0; i<candidates.length; i++){
			BigDecimal divisor = totalVotes(file, theRecord);
			BigDecimal numerator = candidates[i].getVotes();
			if(divisor.intValue()==0){
				divisor=BigDecimal.ONE;
				numerator=BigDecimal.ZERO;
			}
			votes[i] = candidates[i].getName() +": " + addCommas(candidates[i].getVotes()) + " Votes ("
			+ (numerator.divide(divisor,new MathContext(2))).multiply(new BigDecimal(100)).intValue()
					+"%)";
		}
		return votes;
	}
	/*@params number:BigDecimal
	 * @returns- String
	 * this function takes in a number, makes it a string and adds in the commas
	 */
	public String addCommas(BigDecimal number){
		String numberString=number.toString();
		int[] commaPositions = new int[4];
		int inversePosition=0;
		int commaIterator=0;
		//find out where the commas should be placed
		for(int i= numberString.length()-1; i>=0; i--){
			if(inversePosition%3==0 && inversePosition!=0){
				commaPositions[commaIterator]=i;
				commaIterator++;
			}
			inversePosition++;
		}
		//since the above array will be done backward go to the last useful number int he array
		for(int i=0; i<commaPositions.length; i++){
			if(commaPositions[i]==0)
				break;
			commaIterator= i;
		}
		//if numberString.length()%3==1 then a comma needs to be added after the zero place, so move on to the next 0
		if(numberString.length() % 3==1){
			commaIterator++;
		}
		String temp = "";
		//add the commas
		for(int i=0; i<numberString.length(); i++){
			temp= temp.concat(Character.toString(numberString.charAt(i)));
			if(commaIterator>-1 && i == commaPositions[commaIterator]){
				temp=temp.concat(",");
				commaIterator--;
			}
		}
		return temp;
	}
	/*@params- candidate:int, file:File
	 * @returns- BigDecimal
	 */
	public BigDecimal candidateVotes(int candidate, File file) throws IOException{
		//start at 0
		BigDecimal candidateVotes = BigDecimal.ZERO;
		Iterator<DBFRecord> iterator = (new DBFFileIO()).loadDBF(file).getTree().iterator();
		//add the candidates votes together. A candidate has a specific point in the array, which the calling
		//function will be responsible for telling this function.
		while(iterator.hasNext()){
			DBFRecord next = iterator.next();
			candidateVotes = candidateVotes.add(new BigDecimal((Long)next.getData(candidate)));
		}
		return candidateVotes;
	}
	/*@params- array:Candidate[]
	 * @returns- Candidate[]
	 * @throws- IOException
	 * sorts the candidate array based on who has the most votes
	 */
	public Candidate[] sortArray(Candidate[] array) throws IOException{
		for(int i=0; i<array.length; i++){
			Candidate temp = new Candidate(2, "temp", Color.black);
			temp.setDefaultVotes(new BigDecimal(-1));
			int k=0;
			for(int j=i; j<array.length; j++){
				if(temp.getVotes().compareTo(array[j].getVotes())<0){
					temp = array[j];
					k=j;
				}
			}
			array[k]=array[i];
			array[i]=temp;
		}
		return array;
	}
	/*@params- file:File
	 * @returns- String
	 * @throws- IOException
	 * take info from the file, finds the total number of votes and constructs the string
	 */
	public String totalVotesString(File file) throws IOException{
		String votes ="";
		//the table
		DBFTable currentTable = (new DBFFileIO()).loadDBF(file);
		DBFRecord theRecord=null;
		//if not selecting a county/state get a certain record
		if(renderer.getPolyLocation()!=-1)
			theRecord = (currentTable.getRecord(renderer.getPolyLocation()));
		else
			theRecord = currentTable.getRecord(0);//default record, only to satisfy the arguments
		BigDecimal totalVotes = totalVotes(file, theRecord);
		//construct string
		votes = "Total: " + this.addCommas(totalVotes) + "Votes (100%)";
		return votes;
	}
	/*@params- theRecord:DBFRecord, file:File
	 * @returns- BigDecimal
	 * @throws- IOException
	 * A helper method for the method above this calculates the total votes.
	 */
	public BigDecimal totalVotes(File file, DBFRecord theRecord) throws IOException{
		BigDecimal totalVotes = BigDecimal.ZERO;
		if(renderer.getPolyLocation()==-1){
			Iterator<DBFRecord> record= (new DBFFileIO()).loadDBF(file).getTree().iterator();
			while(record.hasNext()){
				DBFRecord next = record.next();
				for(int i=0; i<3; i++){
					totalVotes = totalVotes.add(new BigDecimal((Long)next.getData(next.getNumFields()-(3-i))));
				}
			}
		}
		else{
			for(int i=0; i<3; i++)
				totalVotes = totalVotes.add(new BigDecimal((Long)theRecord.getData(theRecord.getNumFields()-(3-i))));
		}
			
		return totalVotes;
	}
	// MUTATOR METHODS
	
	/**
	 * Called whenever a different territory is highlighted, this updates
	 * our data model so it knows what is highlighted for rendering
	 * purposes.
	 **/
	public void setHighlightedRegion(SHPPolygon poly)
	{
		if (highlightedPolygon != null)
			highlightedPolygon.setLineColor(DEFAULT_BORDER_COLOR);
		highlightedPolygon = poly;
		poly.setLineColor(DEFAULT_HIGHLIGHT_COLOR);		
	}
	
	/**
	 * Called to keep track of whether the map has been rendered
	 * at least once or not. The reason for this is so we don't
	 * keep doing our zoom to map function.
	 */
	public void setMapRendered(boolean initMapRendered)
	{
		mapRendered = initMapRendered;
	}
	
	// SERVICE METHODS - THESE METHODS PROVIDE ADDITIONAL DATA PROCESSING
	// SERVICES, IN PARTICULAR FOR THE EVENT HANDLERS.
	
	/**
	 * This method adds the flag argument to the proper data structure
	 * for storage. Note that this method properly filters the images
	 * into their proper container, with .png files going to flags, and
	 * .gif files going to miniFlags.
	 **/
	public void addFlag(String name, String flagAbbr, Image flag)
	{
		if (name.endsWith(MINI_FLAG_EXT))
			miniFlags.put(flagAbbr, flag);
		else
			flags.put(flagAbbr, flag);
	}

	/**
	 * Called in reponse to mouse motion, this method tests to see
	 * if the current mouse's x,y position overlaps any of the current
	 * map's polygons, and if it does, makes that the highlighted
	 * map region. Note that this method forces a renderer repaint.
	 **/
	public void highlightMapRegion(int x, int y)
	{
		boolean polySelected = selectPolygonAt(x, y);
		if (!polySelected)
		{
			// THE MOUSE ISN'T CURRENTLY OVER ANY SHAPES, SO
			// DON'T HIGHLIGHT ANY OF THEM
			resetHighlightedRegion();
		}
		
		// UPDATE THE VIEW
		renderer.repaint();		
	}
	public void recallPaint(){
		renderer.repaint();
	}
	/**
	 * This method completes the setup of this data model. We'll need the 
	 * renderer and fileManager to properly process event responses.
	 **/
	public void init(	ElectionMapRenderer initRenderer,
						ElectionMapFileManager initFileManager)
	{
		// SAVE THESE GUYS FOR LATER
		renderer = initRenderer;
		fileManager = initFileManager;
	}
	public void colorSections(SHPMap map, File file){
		try {
			//set sections to the relevant DBFTable
			sections = (new DBFFileIO()).loadDBF(file);
			initShapeColors(map);//color in sections
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * For the provided map and table arguments, this method initializes
	 * all the shape regions in the map with the colors of the winner
	 * in the table's election results.
	 **/
	public void initShapeColors(SHPMap map)
	{
		// INITIALIZE THE COLORS
		Iterator<SHPShape> shapesIt = map.shapesIterator();
		for (int i=0; shapesIt.hasNext(); i++)
		{
			//get the next shape
			SHPShape shape = shapesIt.next();
			//find the number of fields
			int numFields = this.sections.getNumFields();
			DBFRecord record = this.sections.getTree().get(i);
			//if Obama has more votes, set it to blue. if McCain has more votes, set it to Red. Otherwise, set it to yellow
			if((Long)record.getData(numFields-3)>(Long)record.getData(numFields-2)){
				shape.setFillColor(Color.BLUE);
			}
			else if((Long)record.getData(numFields-3)<(Long)record.getData(numFields-2)){
				shape.setFillColor(Color.RED);
			}
			else{
				shape.setFillColor(Color.YELLOW);
			}
		}
	}	

	
	/**
	 * This method sets the USA map, including the shp and
	 * dbf data. Note that it does not force a repaint.
	 **/
	public void initUSAMap(SHPMap initUSAshp)
	{
		currentMapName = USA_MAP_NAME;
		currentMapAbbr = USA_MAP_ABBR;
		this.currentOverallAbbr = USA_MAP_ABBR;
		usaSHP = initUSAshp;
		currentSHP= usaSHP;
	}
	
	/**
	 * This method tests to see if the (x,y) point is inside one of the parts (polygons)
	 * of the poly argument. If it is, true is returned, else false.
	 **/
	public boolean pointIsInPoly(ElectionMapRenderer renderer, SHPPolygon poly, int x, int y)
	{
		// GO THROUGH ALL THE PARTS (POLYGONS) OF THIS SHPPolygon. REMEMBER, IN
		// AN SHP FILE, A POLYGON IS MAKE UP OF OTHER PARTS, WHICH ARE EACH THEIR
		// OWN POLYGONS
		for (int i = 0; i < poly.getNumParts(); i++)
		{
			// CLEAR OUR RECYCLED POLYGON OBJECT
			testPoly.reset();
		
			// DETERMINE WHERE IN THE ARRAY WE'LL GET OUR POINTS FROM
			int partStart = poly.getParts()[i];
			int partEnd = poly.getNumPoints()-1;
			if (i < poly.getNumParts()-1)
			{
				partEnd = poly.getParts()[i+1]-1;
			}
		
			// NOW FILL OUR testPoly WITH PARTS
			for (int j = partStart; j <= partEnd; j++)
			{
				double lat = poly.getXPointsData()[j];
				double lon = poly.getYPointsData()[j];
				int pX = renderer.xCoordinateToPixel(lat);
				int pY = renderer.yCoordinateToPixel(lon);
				testPoly.addPoint(pX,pY);
			}
			// AND LET OUR testPoly DO THE TEST
			if (testPoly.contains(x, y))
				return true;
		}
		return false;
	}

	/**
	 * Undoes all map highlighting, this method should be called
	 * when the mouse is determined to not be overlapping any map
	 * regions.
	 **/
	public void resetHighlightedRegion()
	{
		highlightedPolygon = null;
	}	
	
	/**
	 * Used for testing to see if the x,y location is within the
	 * bounds of a map region's polygon. If it is, that region is
	 * highlighted and true is returned. If no map region is found
	 * to contain the point, all regions are unhighlighted and 
	 * false is returned. 
	 **/
	public boolean selectPolygonAt(int x, int y)
	{
		SHPMap currentMap = getCurrentSHP();
		SHPData mapData = currentMap.getShapefileData();	
		Iterator<SHPShape> polyIt = (mapData.getShapes().iterator());
		SHPPolygon poly;
		boolean polyFound = false;
		boolean stopSearching = false;
		
		// GO THROUGH ALL THE POLYGONS IN THE MAP
		while (polyIt.hasNext())
		{
			// TEST EACH ONE
			poly = (SHPPolygon)polyIt.next();

			if (!stopSearching)
			{
				// ONCE ONE IS FOUND TO BE TRUE, NONE OTHERS CAN
				polyFound = pointIsInPoly(renderer, poly, x, y);
			}

			if (polyFound)
			{
				// MARK THIS ONE FOR HIGHLIGHTING
				setHighlightedRegion(poly);
				renderer.setPolyNumber(poly.getRecordNumber()-1);
				return true;
			}				
			else
			{
				// ALL OTHERS WILL KEEP THE STANDARD BLACK
				poly.setLineColor(Color.black);
			}
		}
		renderer.setPolyNumber(-1);
		return false;
	}	
	
	/**
	 * A helper method for switching the map in use to the original
	 * USA map.
	 **/
	private void switchToUSAMap()
	{
		currentMapName = USA_MAP_NAME;
		currentMapAbbr = USA_MAP_ABBR;
	}
}