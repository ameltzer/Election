package dbf_framework;
/**
 * This type of object would store information about a particular
 * column, like what type of data will be stored there.
 * 
 * @author Richard McKenna
 */
public class DBFField 
{
	// THIS IS ALL THE DATA FOR A GIVEN COLUMN HEADER
	private String name;
	private DBFFieldType type;
	private int displacement;
	private int length;
	private int numberOfDecimalPlaces;
	private byte flags;
	private int next;
	private int step;
	private long reservedData;

	// ACCESSOR METHODS
	public int				getDisplacement()			{ return displacement; 					}
	public byte				getFlags()					{ return flags;							}
	public int				getLength()					{ return length;						}
	public String 			getName() 					{ return name; 							}
	public int				getNext()					{ return next;							}
	public int				getNumberOfDecimalPlaces()	{ return numberOfDecimalPlaces;			}
	public long				getReservedData()			{ return reservedData;					}
	public int				getStep()					{ return step;							}
	public DBFFieldType		getType()					{ return type; 							}

	// MUTATOR METHODS
	public void setDisplacement(int initDisplacement)	{ displacement = initDisplacement; 	}
	public void setFlags(byte initFlags)				{ flags = initFlags; 				}
	public void setLength(int initLength)				{ length = initLength;				}
	public void setName(String initName) 				{ name = initName; 					}
	public void setNext(int initNext)					{ next = initNext;					}
	public void setNumberOfDecimalPlaces(int initNODP)	{ numberOfDecimalPlaces = initNODP;	}
	public void setReservedData(long initRD)			{ reservedData = initRD;			}
	public void setStep(int initStep)					{ step = initStep;					}
	public void setType(DBFFieldType initType)			{ type = initType; 					}
}