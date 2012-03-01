package dbf_framework;

/**
 * An object of this class stores all of the data
 * for a single row in a .dbf file for our table.
 * 
 * @author Richard McKenna
 */
public class DBFRecord
{
	// WHAT IS THIS BYTE OF DATA FROM THE FILE BEFORE EACH RECORD?
	private byte mystery;
	
	// HERE'S ALL THE DATA FOR THIS ROW
	private Object[] fieldData;

	/**
	 * This constructor initializes our array for all the data depending on how
	 * many columns we will have.
	 * 
	 * @param numFields The number of fields in the table, thus the number
	 * of pieces of data for each record.
	 */
	public DBFRecord(int numFields)
	{
		fieldData = new Object[numFields];
		for (int i = 0; i < fieldData.length; i++)
			fieldData[i] = null;
	}
	
	// ACCESSOR METHODS
	public int 		getNumFields() 		{ return fieldData.length;	}	
	public Object[] getAllData()		{ return fieldData; 		}
	public byte 	getMystery()		{ return mystery;			}
	public Object 	getData(int index) 	{ return fieldData[index];	}
	public Long 	getEV()				{ return (Long)fieldData[2];}

	// MUTATOR METHODS
	public void setData(Object data, int index) { fieldData[index] = data;	}
	public void setMystery(byte initMystery)	{ mystery = initMystery;	}

	/**
	 * This method is used for updating this record whenever a field
	 * is added to the table. When that happens, we have to increase our
	 * array of data by one to accommodate it.
	 */
	public void addField()
	{
		Object[] updatedArray = new Object[fieldData.length + 1];
		for (int i = 0; i < fieldData.length; i++)
			updatedArray[i] = fieldData[i];
		fieldData = updatedArray;
	}

	/**
	 * This method is used for updating this record whenever a field
	 * is removed from the table. When that happens, we have to decrease our
	 * array of data by one.
	 * 
	 * @param index The index of the field to be removed from this record.
	 */
	public void removeField(int index)
	{
		// MAKE SURE IT'S A VALID FIELD INDEX
		if ((fieldData.length > 0) && (index >= 0) && (index < fieldData.length))
		{
			Object[] updatedArray = new Object[fieldData.length-1];
			
			// IS IT THE FIRST FIELD?
			if (index == 0)
			{
				for (int i = 1; i < fieldData.length; i++)
					updatedArray[i-1] = fieldData[i];
			}
			// THE LAST FIELD?
			else if (index == (fieldData.length-1))
			{
				for (int i = 0; i < fieldData.length-1; i++)
					updatedArray[i] = fieldData[i];
			}
			// OR A MIDDLE FIELD?
			else
			{
				for (int i = 0; i < index; i++)
					updatedArray[i] = fieldData[i];
				for (int i = index+1; i < fieldData.length; i++)
					updatedArray[i-1] = fieldData[i];
			}
			fieldData = updatedArray;
		}
	}
}