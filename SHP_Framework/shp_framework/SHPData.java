package shp_framework;

import java.util.Iterator;
import java.util.Vector;
import shp_framework.geometry.SHPShape;
/**
 * SHPData - This class stores all of the geometric data for a given map. Note
 * that a map is made up of shapes, each of which may have many parts. Note that
 * a part is a single polygon. Note that all data stored inside this class is
 * in geographic coordinates, meaning latitude and longitude, where east is
 * positive and west is negative, north is positive and south is negative.
 * 
 * @author Richard McKenna
 */
public class SHPData 
{
	// HERE'S ALL THE GEOMETRY FOR THE MAP
	private Vector<SHPShape> shapes;

	// HERE'S SHAPEFILE DATA
	private int fileCode;
	private int[] unusedBytes;
	private int fileLength;
	private int version;
	private int shapeType;
	private double[] mbr;
	private double[] zBounds;
	private double[] mBounds;

	/**
	 * This constructor just sets up our shapes data structure. The geometric
	 * data will be loaded and unloaded as needed.
	 */
	public SHPData()
	{
		shapes = new Vector<SHPShape>();
	}

	// ACCESSOR METHODS
	public int					getFileCode()		{ return fileCode;			}
	public int					getFileLength()		{ return fileLength;		}
	public double[]				getMBounds()		{ return mBounds;			}
	public double[]				getMBR()			{ return mbr;				}
	public SHPShape 			getShape(int index)	{ return shapes.get(index);	}
	public int					getShapeType()		{ return shapeType;			}
	public Vector<SHPShape> 	getShapes()			{ return shapes; 			}
	public int[]				getUnusedBytes()	{ return unusedBytes;		}
	public int					getVersion()		{ return version;			}
	public double[]				getZBounds()		{ return zBounds;			}

	// ITERATOR - FOR GOING THROUGH ALL THE SHAPES ONE AT A TIME	
	public Iterator<SHPShape> shapesIterator()
	{
		return shapes.iterator();
	}

	// MUTATOR METHODS
	public void addShape(SHPShape shapeToAdd)
	{
		shapes.add(shapeToAdd);
	}

	public void setShapes(Vector<SHPShape> initShapes)
	{
		shapes = initShapes;
	}
	
	public void setFileCode(int initFileCode)
	{
		fileCode = initFileCode;
	}
	
	public void setUnusedBytes(int[] initUnusedBytes)
	{
		unusedBytes = initUnusedBytes;
	}
	
	public void setFileLength(int initFileLength)
	{
		fileLength = initFileLength;		
	}
	
	public void setVersion(int initVersion)
	{
		version = initVersion;
	}
	
	public void setShapeType(int initShapeType)
	{
		shapeType = initShapeType;
	}
	
	public void setMBR(double[] initMBR)
	{
		mbr = initMBR;
	}
	
	public void setZBounds(double[] initZBounds)
	{
		zBounds = initZBounds;
	}
	
	public void setMBounds(double[] initMBounds)
	{
		mBounds = initMBounds;
	}
}