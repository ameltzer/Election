package shp_framework.geometry;

import java.awt.Graphics2D;
/**
 * SHPPolygon - This class is used to represent a shapefile polygon type, which
 * means it stores a series of polygons, called parts. 
 * 
 * @author Richard McKenna
 */
public class SHPPolygon extends SHPPolyType
{
	/**
	 * This constructor fully initializes all data needed for use.
	 */
	public SHPPolygon(	double[] initBoundingBox,
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
	 * This method provides the implementation for rendering polygons. Note that
	 * it will go through and render each part.
	 */
	public void render(	Graphics2D g2, 
						double zoomScale, 
						double viewportCenterX, double viewportCenterY,
						int panelWidth, int panelHeight) 
	{
		// FOR ALL PARTS (POLYGONS)
		for (int a = 0; a < numParts; a++)
		{
			// DETERMINE HOW MANY POINTS ARE IN THIS PART
			int size = calculateSize(a);

			// AND FILL OUR POINTS ARRAYS WITH DATA
			this.fillData(a, size, xRenderData, yRenderData, zoomScale, viewportCenterX, viewportCenterY, panelWidth, panelHeight);

			// THEN USE THEM TO RENDER
			g2.setColor(fillColor);
			g2.fillPolygon(xRenderData, yRenderData, size);
			g2.setColor(lineColor);
			g2.drawPolygon(xRenderData, yRenderData, size);
		}
	}
}