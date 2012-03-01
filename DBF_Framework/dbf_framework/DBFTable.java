package dbf_framework;

import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Objects of this class would be used to store all of the data
 * found in a single .dbf file. Note that the columns data would be stored
 * in fields and records, just as in a database table.
 * 
 * @author Richard McKenna
 */
public class DBFTable 
{
	// COLUMN HEADERS
	private ArrayList<DBFField> fields;
	
	// ROW DATA
	private ArrayList<DBFRecord> records;
	private int keyIndex;

	// DBF FILE DESCRIPTION
	private byte fileType;
	private GregorianCalendar lastModifiedDate;
	private int numberOfRecords;
	private short positionOfFirstDataRecorded;
	private short dataRecordLength;
	private short zeroes;
	private byte dbaseTransactionFlag;
	private byte dbaseEncryptionFlag;
	private int[] mup;
	private byte flags;
	private byte codePageMark;
	private short reserved;
	private byte terminator;

	/**
	 * This default constructor simply sets up the fields and records ArrayLists.
	 */
	public DBFTable()
	{
		// INITIALIZE OUR DATA STRUCTURES
		fields = new ArrayList<DBFField>();
		records = new ArrayList<DBFRecord>();
		
		// BY DEFAULT THE KEY INDEX IS 0
		keyIndex = 0;
	}

	// ACCESSOR METHODS
	public int					getKeyIndex()						{ return keyIndex;						}
	public byte 				getFileType() 						{ return fileType; 						}
	public GregorianCalendar	getLastModifiedDate() 				{ return lastModifiedDate; 				}
	public int 					getNumberOfRecords() 				{ return numberOfRecords; 				}
	public short 				getPositionOfFirstDataRecorded()	{ return positionOfFirstDataRecorded;	}
	public short 				getDataRecordLength()				{ return dataRecordLength;				}
	public short 				getZeroes()							{ return zeroes;						}
	public byte 				getDbaseTransactionFlag()			{ return dbaseTransactionFlag;			}
	public byte 				getDbaseEncryptionFlag()			{ return dbaseEncryptionFlag;			}
	public int[] 				getMup()							{ return mup;							}
	public byte 				getFlags()							{ return flags;							}
	public byte					getCodePageMark()					{ return codePageMark;					}
	public short				getReserved()						{ return reserved;						}
	public byte					getTerminator()						{ return terminator;					}
	public DBFField 			getField(int index)					{ return fields.get(index); 			}
	public int 					getNumFields()						{ return fields.size();					}
	public int 					getNumRecords()						{ return records.size();				}
	public ArrayList <DBFRecord> getTree()							{ return records; 						}
	/**
	 * This accessor method gets the index of a specific column with a header
	 * name equivalent to that of the testFieldName index. If no column is
	 * found with that name, -1 is returned.
	 * 
	 * @param testFieldName The name of the field that we want the index of.
	 * 
	 * @return The index of the testFieldName per the order of fields in the table. Note
	 * that fields are not sorted alphabetically, so this uses the rather unfortunate
	 * sequential search.
	 */
	public int getFieldIndex(String testFieldName)
	{
		// BOO, SEQUENTIAL SEARCH
		int index = 0;
		Iterator<DBFField> fieldsIt = fieldsIterator();
		while(fieldsIt.hasNext())
		{
			DBFField field = fieldsIt.next();
			if (field.getName().equals(testFieldName))
				return index;
			else
				index++;
		}
		return -1;
	}
	
	/**
	 * Accessor method for getting the key value for the provided record argument.
	 * 
	 * @param record The record for which we want the key.
	 * 
	 * @return The key object, which must be Comparable, for the provided
	 * record argument.
	 */
	public Comparable getKey(DBFRecord record)
	{
		return (Comparable)record.getData(keyIndex);
	}
	
	/**
	 * Accessor method for getting the data object associated with the key argument.
	 * 
	 * @param key The key for the record we are interested in.
	 * 
	 * @return The data object corresponding to the provided key argument.
	 */
	public DBFRecord getRecord(int key)
	{
		return records.get(key);
	}
		
	// ITERATOR METHODS - THESE WILL RETURN ITERATORS FOR GOING THROUGH ALL THE DATA IN THE TABLE
	public Iterator<DBFField> 	fieldsIterator() 	{ return fields.iterator(); 	}
	public Iterator<DBFRecord> 	recordsIterator()	{ return records.iterator();	}
	
	// MUTATOR METHODS		
	public void setKeyIndex(int initKeyIndex)
	{
		if ((initKeyIndex >= 0) && (initKeyIndex < fields.size()))
			keyIndex = initKeyIndex;
	}
	
	public void setFileType(byte initFileType)
	{
		fileType = initFileType;
	}
	
	public void setLastModifiedDate(int year, int month, int day)
	{
		lastModifiedDate = new GregorianCalendar(year, month, day);
	}
	
	public void setNumberOfRecords(int initNumberOfRecords)
	{
		numberOfRecords = initNumberOfRecords;
	}
	
	public void setPositionOfFirstDataRecorded(short initPositionOfFirstDataRecorded)
	{
		positionOfFirstDataRecorded = initPositionOfFirstDataRecorded;
	}

	public void setDataRecordLength(short initDataRecordLength)
	{
		dataRecordLength = initDataRecordLength;
	}
	
	public void setZeroes(short initZeroes)
	{
		zeroes = initZeroes;
	}
	
	public void setDbaseTransactionFlag(byte initDbaseTransactionFlag)
	{
		dbaseTransactionFlag = initDbaseTransactionFlag;
	}

	public void setDbaseEncryptionFlag(byte initDbaseEncryptionFlag)
	{
		dbaseEncryptionFlag = initDbaseEncryptionFlag;
	}
	
	public void setMup(int[] initMup)
	{
		mup = initMup;
	}
	
	public void setFlags(byte initFlags)
	{
		flags = initFlags;
	}
	
	public void setCodePageMark(byte initCodePageMark)
	{
		codePageMark = initCodePageMark;
	}
	
	public void setReserved(short initReserved)
	{
		reserved = initReserved;
	}
	
	public void setTerminator(byte initTerminator)
	{
		terminator = initTerminator;
	}
	
	// TABLE BUILDING METHODS - THESE SHOULD ONLY BE USED FOR LOADING FROM A FILE
	// SINCE THEY DO NOT UPDATE ALL RELATED VARIABLES
	public void addField(DBFField dbf)		{ fields.add(dbf);		}
	public void addRecord(DBFRecord dbr)		
	{ 
		records.add(dbr);
	}	

	// TABLE MODIFICATION METHODS //
	
	/**
	 * This method adds a new field to the table. This would be used
	 * during table editing. Note that all records in the table
	 * are updated by this method to accommodate the new field.
	 * 
	 * @param fieldName Name of the field to add.
	 * 
	 * @param fieldType Type of the field to add, C (String) or N (Long)
	 * 
	 * @param length Number of characters needed to represent the data.
	 */
	public void addField(String fieldName, DBFFieldType fieldType, int length)
	{
		// MAKE OUR NEW FIELD
		DBFField fieldToAdd = new DBFField();
		fieldToAdd.setName(fieldName);
		fieldToAdd.setType(fieldType);
		fieldToAdd.setLength(length);
		
		// PUT IT IN THE TABLE
		addField(fieldToAdd);
		
		// AND UPDATE ALL THE RECORDS
		Iterator<DBFRecord> it = recordsIterator();
		while(it.hasNext())
		{
			DBFRecord rec = it.next();
			rec.addField();
		}
		
		// UPDATE TABLE STATS
		update();
	}

	/**
	 * This method adds a new, blank record (no data) to the
	 * end of the table.
	 */
	/*
	public void addBlankRecord()
	{
		// ADD A NEW EMPTY RECORD, IT WILL BE FILLED IN
		// BY THE USER LATER
		DBFRecord recordToAdd = new DBFRecord(fields.size());
		int key = generateKey();
		records.add(key, recordToAdd);
		numberOfRecords++;
		
		// UPDATE THE TABLE STATS
		update();
	}*/
	
	/**
	 * Generates and returns a key that's not in use.
	 * 
	 * @return A key that is not in use. This would be used for adding records
	 * to the table with placeholders where the real key has not yet been
	 * provided.
	 */
	/**
	public String generateKey()
	{
		int counter = 1;
		while (records.containsKey("Key" + counter))
			counter++;
		return "Key" + counter;
	}*/
	
	/**
	 * Remove the record at the recordIndex argument location
	 * from the table.
	 * 
	 * @param recordIndex Index of the record to remove from the table.
	 */
	public void removeRecord(int recordIndex)
	{
		// MAKE SURE IT'S A LEGAL TABLE INDEX
		if ((recordIndex >= 0) && (recordIndex < records.size()))
		{
			// TAKE IT OUT OF THE TABLE
			records.remove(recordIndex);
			numberOfRecords--;
			
			// AND UPDATE THE TABLE STATS
			update();
		}
	}

	/**
	 * This method updates all the table stats important for saving
	 * a .dbf file. This method should be called whenever the table
	 * structure is changed. 
	 */
	public void update()
	{
		lastModifiedDate = new GregorianCalendar();
		updateDataRecordLength();
		updatePositionOfFirstDataRecorded();
	}

	/**
	 * This method determines the length of all the fields in the 
	 * table and sums these values, then using this result to set
	 * the dataRecordLength variable, which may later be written
	 * to a .dbf file.
	 */
	public void updateDataRecordLength()
	{
		Iterator<DBFField> it = fields.iterator();
		dataRecordLength = 0;
		while(it.hasNext())
		{
			DBFField field = it.next();
			dataRecordLength += field.getLength();
		}
		dataRecordLength++;
	}

	/**
	 * This method determines the position of the first record
	 * inside a .dbf table if the current table being edited were
	 * to be saved. This value is assigned to the positionOfFirstDatRecorded
	 * variable, which is written to a .dbf file during saving.
	 */
	public void updatePositionOfFirstDataRecorded()
	{
		positionOfFirstDataRecorded = (short)(32 + (32 * fields.size()) + 1);
	}

	/**
	 * This method removes the field at fieldIndex from the table,
	 * making sure all the rows are informed such that the update
	 * their data.
	 * 
	 * @param fieldIndex Index of the the field to remove from the table.
	 */
	public void removeField(int fieldIndex)
	{
		// ONLY DO THIS IF IT'S A LEGAL INDEX
		if ((fieldIndex >= 0) && (fieldIndex < fields.size()))
		{
			// REMOVE THE FIELD
			fields.remove(fieldIndex);
			
			// GO THROUGH ALL THE RECORDS AND UPDATE THEM
			Iterator<DBFRecord> it = recordsIterator();
			while(it.hasNext())
			{
				DBFRecord recordToUpdate = it.next();
				recordToUpdate.removeField(fieldIndex);
			}
			
			// AND UPDATE THE TABLE STATS
			update();
		}
	}

	/**
	 * This method sorts the records in this table according to the
	 * provided field name and in increasing order if the increasing
	 * argument is true, in decreasing order otherwise.
	 * 
	 * @param fieldName Field by which to use as the criteria for sorting
	 * the records.
	 * 
	 * @param increasing If true, the records will be sorted in increasing
	 * order. It will use decreasing order otherwise.
	 */
	public void sortRecords(String fieldName, boolean increasing)
	{
		// FIND THE SORTING CRITERIA INDEX
		int fieldIndex = -1;
		int index = 0;
		while ((fieldIndex < 0) && (index < fields.size()))
		{
			DBFField testField = fields.get(index);
			if (testField.getName().equals(fieldName))
				fieldIndex = index;
			index++;
		}

		// ONLY SORT IF IT'S FOUND
		if (fieldIndex >= 0)
		{
			DBFRowSorter rowSorter = new DBFRowSorter(fieldIndex, increasing);
			
			// UPDATE THE TABLE STATS
			update();
		}
	}	

	/**
	 * This helper class performs all comparisons between
	 * objects during table sorting.
	 */
	private class DBFRowSorter implements Comparator
	{
		// THESE DICTATE THE SORTING CRITERIA
		private int sortingIndex;
		private boolean increasing;

		/**
		 * This constructor sets up this object for sorting. Note that
		 * we'll construct a temp one each time we sort, so I didn't 
		 * provide any mutator methods.
		 * 
		 * @param initSortingIndex Field index to use for sorting criteria.
		 * 
		 * @param initIncreasing Used to initialize whether this will be for
		 * increasing sorting or not.
		 */
		public DBFRowSorter(int initSortingIndex, boolean initIncreasing)
		{
			sortingIndex = initSortingIndex;
			increasing = initIncreasing;
		}
		
		/**
		 * Here's the method that does the real work. Ito compares the
		 * two Object arguments and returns 0 if they are equivalent. -1 if
		 * obj1 is "smaller" than obj2, and 1 otherwise.
		 * 
		 * @param obj1 The first record being compared.
		 * 
		 * @param obj2 The second record being compared.
		 */
		public int compare(Object obj1, Object obj2) 
		{
			DBFRecord record1 = (DBFRecord)obj1;
			DBFRecord record2 = (DBFRecord)obj2;
			int result;

			// ARE WE COMPARING TEXT?
			if (record1.getData(sortingIndex) instanceof String)
			{
				String text1 = (String)record1.getData(sortingIndex);
				String text2 = (String)record2.getData(sortingIndex);
				result = text2.compareTo(text1);
			}
			// TREAT EVERYTHING ELSE AS A DOUBLE
			else
			{
				Double double1 = 0.0;
				Double double2 = 0.0;
				if (record1.getData(sortingIndex) instanceof Double)
				{
					double1 = (Double)record1.getData(sortingIndex);
					if (record2.getData(sortingIndex) instanceof Long)
					{
						Long tempLong = (Long)record2.getData(sortingIndex);
						double2 = (double)tempLong;
					}
					else
					{
						double2 = (Double)record2.getData(sortingIndex);						
					}
				}
				// EVEN IF IT'S ACTUALLY A LONG, IT MAKES COMPARISONS EASIER
				else
				{
					Long tempLong = (Long)record1.getData(sortingIndex);
					if (tempLong == null)
						double1 = new Double(0);
					else
						double1 = (double)tempLong;
					if (record2.getData(sortingIndex) instanceof Long)
					{
						tempLong = (Long)record2.getData(sortingIndex);
						if (tempLong == null)
							double2 = new Double(0);
						else
							double2 = (double)tempLong;
					}
					else
					{
						Double tempDouble = (Double)record2.getData(sortingIndex);
						if (tempDouble == null)
							double2 = new Double(0);
						else
							double2 = (Double)record2.getData(sortingIndex);
					}
				}
				if (double1 == null) double1 = 0.0;
				if (double2 == null) double2 = 0.0;
				if (double1 < double2) result = 1;
				else if (double1 > double2) result = -1;
				else result = 0;
			}
			// IF INCREASING, JUST REVERSE THE RESULTS
			if (increasing)
				result *= -1;
			return result;
		}	
	}

	/**
	 * This method checks to see if there is already a field name in
	 * the table that is equivalent to the testName argument. If there
	 * is, true is returned, else false. Note that no two columns should
	 * have the same name in a table, so this method should be used
	 * to help prevent that from happening.
	 * 
	 * @param testName Field name to test and see if it's already in the table.
	 * 
	 * @return true if the field name is already in use, false otherwise.
	 */
	public boolean containsNamedColumn(String testName)
	{
		// SEARCH THROUGH ALL THE FIELDS
		Iterator<DBFField> it = fieldsIterator();
		while (it.hasNext())
		{
			DBFField field = it.next();
			if (field.getName().equals(testName))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Used for comparing two records to enable sorting of records. Note
	 * that this is redundant since we already have DBFRowSorter. This simply
	 * provides an implementation using record keys, where DBFRowSorter uses
	 * a more flexible implementation.
	 */
	class RecordComparator implements Comparator
	{
		/**
		 * Here's the method that does the real work. It compares the
		 * two Object arguments and returns 0 if they are equivalent. -1 if
		 * obj1 is "smaller" than obj2, and 1 otherwise.
		 * 
		 * @param obj1 The first record being compared.
		 * 
		 * @param obj2 The second record being compared.
		 */
		public int compare(Object r1, Object r2) 
		{
			DBFRecord record1 = (DBFRecord)r1;
			Comparable record1Key = DBFTable.this.getKey(record1);
			DBFRecord record2 = (DBFRecord)r2;
			Comparable record2Key = DBFTable.this.getKey(record2);
			
			return record1Key.compareTo(record2Key);
		}	
	}
}