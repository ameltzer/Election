package shp_framework.geometry;

import java.awt.Graphics2D;
/**
 * SHPPolyline - This class is used to represent a shapefile polyline type, which
 * means it stores a series of polylines, called parts. Note that this is commonly
 * used for rendering roads in a map.
 * 
 * @author Richard McKenna
 */
public class SHPPolyline extends SHPPolyType
{
	/**
	 * This constructor fully initialize the object for use.
	 */
	public SHPPolyline(	double[] initBoundingBox,
								int initNumBytes,
								int initNumParts,
								int initNumPoints,
								int[] initParts,
								double[] initXPointsData,
								double[] initYPointsData)
	{
		super(initBoundingBox, initNumBytes, initNumParts, initNumPoints, initParts, initXPointsData, initYPointsData);
	}

	/**
	 * This renders the polyine, including all lines within.
	 */
	public void render(	Graphics2D g2, 
						double zoomScale, 
						double viewportCenterX, double viewportCenterY,
						int panelWidth, int panelHeight)
	{
		// GO THROUGH ALL THE LINES
		for (int a = 0; a < numParts; a++)
		{
			// FIGURE OUT HOW MANY POINTS THIS PART HAS
			int size = calculateSize(a);
			
			// AND LOAD THE DATA INTO OUR ARRAYS
			int[] xData = new int[size];
			int[] yData = new int[size];
			fillData(a, size, xData, yData, zoomScale, viewportCenterX, viewportCenterY, panelWidth, panelHeight);
			
			// AND USE THEM FOR RENDERING
			g2.setColor(fillColor);
			g2.fillPolygon(xData, yData, size);
			g2.setColor(lineColor);
			g2.drawPolygon(xData, yData, size);
		}
	}
}