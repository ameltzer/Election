package shp_framework.geometry;

/**
 * SHPPolyType - This class stores the data associated with a poly type,
 * which means a shape with a variable series of points. It can be extended
 * to represent polygons or polylines, and perhaps other types.
 * 
 * @author Richard McKenna
 */
public abstract class SHPPolyType extends SHPShape
{
	// HERE'S COMMON DATA FOR POLY TYPES
	protected int numParts;			// NUMBER OF PIECES
	protected int numPoints;		// TOTAL NUMBER OF POINTS FOR ALL PIECES
	protected int[] parts;			// START INDICES IN POINTS DATA ARRAYS FOR EACH PIECE
	protected double[] xPointsData;	// X COORDINATES FOR ALL POINTS
	protected double[] yPointsData;	// Y COORDINATES FOR ALL POINTS
	protected int numBytes;			// SIZE 
	protected int[] xRenderData;	// 
	protected int[] yRenderData;

	/**
	 * This constructor initializes all data needed to render.
	 */
	public SHPPolyType(	double[] initBoundingBox,
								int initNumBytes,
								int initNumParts,
								int initNumPoints,
								int[] initParts,
								double[] initXPointsData,
								double[] initYPointsData)
	{
		super(initBoundingBox);
		numBytes = initNumBytes;
		numParts = initNumParts;
		numPoints = initNumPoints;
		parts = initParts;
		xPointsData = initXPointsData;
		yPointsData = initYPointsData;
		xRenderData = new int[xPointsData.length];
		yRenderData = new int[yPointsData.length];
	}

	// ACCESSOR METHODS
	public int 		getNumBytes()		{ return numBytes;		}
	public double[] getBoundingBox()	{ return boundingBox; 	}
	public int 		getNumParts() 		{ return numParts; 		}
	public int 		getNumPoints() 		{ return numPoints; 	}
	public int[] 	getParts() 			{ return parts; 		}
	public double[] getXPointsData() 	{ return xPointsData; 	}
	public double[] getYPointsData()	{ return yPointsData;	}
	public int[]	getXRenderData()	{ return xRenderData;	}
	public int[]	getYRenderData()	{ return yRenderData;	}
	
	/**
	 * This method calculates and returns the number of points in the poly
	 * type at partsIndex.
	 */
	public int calculateSize(int partsIndex)
	{
		// IF IT'S ANY PART BUT THE LAS
		if (partsIndex < (numParts-1))
			return parts[partsIndex+1] - parts[partsIndex];
		// IF IT'S THE LAST ONE
		else
			return numPoints - parts[partsIndex];
	}

	/**
	 * We add bytes to keep track of the full file size.
	 */
	public void addBytes(int bytesToAdd)
	{
		numBytes += bytesToAdd;
	}
	
	/**
	 * This method fills in the xData and yData arrays with the necessary
	 * points data for rendering.
	 */
	public void fillData(	int partsIndex,
							int size,
							int[] xData, int[] yData,
							double zoomScale,
							double viewportCenterX, double viewportCenterY,
							int panelWidth, int panelHeight)
	{
		// GET SOME RENDERING CONVERSION INFO
		double longWidth = 360/zoomScale;
		double latHeight = 180/zoomScale;
		double minLong = viewportCenterX - (longWidth/2);
		double minLat = viewportCenterY - (latHeight/2);
		int index = 0;

		// GET THE DATA
		for (int i = parts[partsIndex]; i < parts[partsIndex] + size; i++)
		{
			double coordX = xPointsData[i];
			double coordY = yPointsData[i];
		
			// SCALE THE COORDINATES
			double percentX = (coordX - minLong)/longWidth;
			double percentY = (coordY - minLat)/latHeight;

			// CONVERT IT
			coordX = panelWidth * percentX;
			coordY = panelHeight * percentY;
		
			try
			{
				// AND ADD IT
				xData[index] = (int)(Math.round(coordX));
				yData[index] = panelHeight - (int)(Math.round(coordY));
				index++;
			}
			catch(Exception e)
			{
				System.out.println(i);
			}
		}		
	}
}