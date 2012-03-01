package shp_framework.geometry;

import java.awt.Color;
import java.awt.Graphics2D;
/**
 * This class serves as the parent type for all types of shapes
 * that might be needed to render a shapefile. Note that we're 
 * really only using the polygon type (5), but in case we wanted
 * to add others, we could extend this class.
 * 
 * @author Richard McKenna
 */
public abstract class SHPShape
{
	// DATA FOR ALL SHAPES, THOUGH SOME MIGHT NOT USE ALL OF IT
	protected double[] boundingBox;
	protected int recordNumber;
	protected int recordLength;
	protected Color lineColor;
	protected Color fillColor;

	/**
	 * Init this shape with a loaded bounding box and some default colors.
	 */
	public SHPShape(double[] initBoundingBox)
	{
		boundingBox = initBoundingBox;
		lineColor = Color.black;
		fillColor = new Color(0, 150, 0);
	}

	/**
	 * This method would be implemented by all subclasses, since
	 * each may be rendered differently.
	 */
	public abstract void render(	Graphics2D g2, 
						double zoomScale, 
						double viewportX, double viewportY,
						int panelWidth, int panelHeight);

	// ACCESSOR METHODS
	public int getRecordNumber() { return recordNumber; }
	public int getRecordLength() { return recordLength; }
	public Color getLineColor()		{ return lineColor; }
	public Color getFillColor()		{ return fillColor;	}

	// MUTATOR METHODS
	public void setRecordNumber(int initRecordNumber)
	{
		recordNumber = initRecordNumber;
	}
	
	public void setRecordLength(int initRecordLength)
	{
		recordLength = initRecordLength;
	}
	
	public double[] getBoundingBox()
	{	
		return boundingBox;
	}
	
	public void setLineColor(Color initLineColor)
	{
		lineColor = initLineColor;
	}
	
	public void setFillColor(Color initFillColor)
	{
		fillColor = initFillColor;
	}
}