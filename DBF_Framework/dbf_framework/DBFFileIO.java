package dbf_framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
/**
 * This class may be used for reading and writing data to and from
 * a .dbf file. Note that both processes must assume a similar DBF format.
 * 
 * @author Richard McKenna
 */

public class DBFFileIO
{
	/**
	 * This method saves the tableToSave argument to the file location. Note
	 * that we are only writing C and N data types.

	 * @param tableToSave DBFTable data to save to the file.
	 * 
	 * @param file File location to save the table.
	 * 
	 * @throws IOException Thrown when writing fails. A reason for this
	 * might be the file is already being used by another program.
	 */
	public void saveDBF(DBFTable tableToSave, File file) throws IOException
	{
		// WE ARE GOING TO WRITE RAW BYTE DATA
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);

		// SAVE THE FIRST 32 BYTES OF THE HEADER
		saveHeader(dos, tableToSave);

		// SUBRECORDS (32-(positionOfFirstDataRecorded-3))
		saveFields(dos, tableToSave);

		// HEADER RECORD TERMINATOR (SHOULD BE 0x0D)
		dos.writeByte(tableToSave.getTerminator());

		// AND NOW SAVE THE ACTUAL DATA
		saveRecords(dos, tableToSave);	
	}

	/**
	 * This helper method saves just the .dbf file header portion
	 * of the mapTable argument using the dos stream.
	 * 
	 * @param dos The stream writing to the .dbf file.
	 * 
	 * @param mapTable The dbf table being saved.
	 * 
	 * @throws IOException Thrown when the stream fails.
	 */
	private void saveHeader( DataOutputStream dos, DBFTable mapTable) throws IOException
	{
		// DBF file type (0)
		dos.writeByte(mapTable.getFileType());

		// LAST UPDATE (1-3)
		byte year = (byte)(mapTable.getLastModifiedDate().get(Calendar.YEAR)-1900);
		byte month = (byte)(mapTable.getLastModifiedDate().get(Calendar.MONTH)+1);
		byte day = (byte)(mapTable.getLastModifiedDate().get(Calendar.DATE));
		dos.writeByte(year);
		dos.writeByte(month);
		dos.writeByte(day);

		// NUMBER OF RECORDS IN FILE (4-7)
		int numberOfRecordsInFile = Integer.reverseBytes(mapTable.getNumberOfRecords());
		dos.writeInt(numberOfRecordsInFile);

		// POSITION OF FIRST DATA RECORDED (8-9)
		short positionOfFirstDataRecorded = Short.reverseBytes(mapTable.getPositionOfFirstDataRecorded());
		dos.writeShort(positionOfFirstDataRecorded);

		// LENGTH OF ONE DATA RECORD, INCLUDING DELETE FLAG (10-11)
		short dataRecordLength = Short.reverseBytes(mapTable.getDataRecordLength());
		dos.writeShort(dataRecordLength);

		// ZEROES (12-13)
		short zeroes = Short.reverseBytes(mapTable.getZeroes());
		dos.writeShort(zeroes);

		// DBASE IV Transaction Flag (14)
		dos.writeByte(mapTable.getDbaseTransactionFlag());

		// DBASE IV Encryption Flag (15)
		dos.writeByte(mapTable.getDbaseEncryptionFlag());

		// Multiuser Processing 12 Bytes (16-27)
		int[] mup = mapTable.getMup();
		dos.writeInt(Integer.reverseBytes(mup[0]));
		dos.writeInt(Integer.reverseBytes(mup[1]));
		dos.writeInt(Integer.reverseBytes(mup[2]));

		// TABLE FLAGS (28)
		dos.writeByte(mapTable.getFlags());

		// CODE PAGE MARK/LANGUAGE DRIVER ID (29)
		dos.writeByte(mapTable.getCodePageMark());

		// RESERVED, CONTAINS 0x00 (30-31)
		dos.writeShort(Short.reverseBytes(mapTable.getReserved()));
	}

	/**
	 * This method saves all of the fields in the mapTable argument
	 * via the dos stream, which is setup at the proper location.
	 * 
	 * @param dos Output stream writing to the .dbf file.
	 * 
	 * @param mapTable The dbf table being saved.
	 * 
	 * @throws IOException Thrown when the stream fails.
	 */
	private void saveFields(DataOutputStream dos, DBFTable mapTable) throws IOException
	{
		// NUMBER OF FIELDS (COLUMNS) IN THE TABLE
		int numFields = (mapTable.getPositionOfFirstDataRecorded() - 32 - 2 + 1)/32;

		for (int i = 0; i < numFields; i++)
		{
			// HERE'S THE FIELD WE'RE SAVING
			DBFField fieldToSave = mapTable.getField(i);

			// FIELD NAME
			String fieldName = fieldToSave.getName();
			int j = 0;
			for (; j < fieldName.length(); j++)
			{
				byte c = (byte)fieldName.charAt(j);
				dos.writeByte(c);
			}
			byte z = (byte)0x00;
			while (j < 11)
			{
				dos.writeByte(z);
				j++;
			}

			// FIELD TYPE
			dos.writeByte((byte)(fieldToSave.getType().toString().charAt(0)));

			// DISPLACEMENT OF FIELD IN RECORD (12-15)
			dos.writeInt(Integer.reverseBytes(fieldToSave.getDisplacement()));

			// LENGTH OF FIELD (16)
			dos.writeByte((byte)fieldToSave.getLength());

			// NUMBER OF DECIMAL PLACES (17)
			dos.writeByte(fieldToSave.getNumberOfDecimalPlaces());

			// FIELD FLAGS (18)
			dos.writeByte(fieldToSave.getFlags());

			// AUTOINCREMENT NEXT (19-22)
			dos.writeInt(Integer.reverseBytes(fieldToSave.getNext()));

			// AUTOINCREMENT STEP (23)
			dos.writeByte(fieldToSave.getStep());

			// RESERVED (24-31) - WE WON'T USE THIS
			dos.writeLong(fieldToSave.getReservedData());
		}
	}

	/**
	 * This method saves all the records in the mapTable argument
	 * using the dos stream.
	 * 
	 * @param dos Output stream writing to the .dbf file.
	 * 
	 * @param mapTable The dbf table being saved.
	 * 
	 * @throws IOException Thrown when the stream fails.
	 */
	private void saveRecords(	DataOutputStream dos, DBFTable mapTable) throws IOException
	{
		// WRITE ALL ROW DATA
		for (int i = 0; i < mapTable.getNumberOfRecords(); i++)
		{	
			// HERE'S THE RECORD WE ARE SAVING
			DBFRecord recordToSave = mapTable.getRecord(i);

			// THERE IS A MYSTERY BYTE BEFORE EACH RECORD
			dos.writeByte(recordToSave.getMystery());

			// LOAD DATA FOR EACH FIELD
			Iterator<DBFField> fieldsIt = mapTable.fieldsIterator();
			int fieldsCounter = 0;
			while (fieldsIt.hasNext())
			{
				// WHAT TYPE OF FIELD IS IT (character data, numbers, etc.)?
				DBFField field = fieldsIt.next();

				// TEXT?
				if (field.getType() == DBFFieldType.C)
				{
					int j = 0;
					Object data = recordToSave.getData(fieldsCounter);
					String text;
					if (data == null)
						text = "";
					else
						text = data.toString();
					while (j < text.length())
					{
						dos.writeByte((byte)text.charAt(j));
						j++;
					}
					while (j < field.getLength())
					{
						dos.writeByte((byte)' ');
						j++;
					}
				}
				// IT MUST BE AN 'N' TYPE, THOSE ARE THE ONLY TWO WE'RE USING
				else
				{
					Object data = recordToSave.getData(fieldsCounter);
					String numText;
					if (data == null)
						numText = "";
					else
						numText = data.toString();
					int x = 0;
					while (x < numText.length())
					{
						dos.writeByte((byte)numText.charAt(x));
						x++;
					}
					while (x < field.getLength())
					{
						dos.writeByte((byte)' ');
						x++;
					}
				}
				fieldsCounter++;
			}
		}
	}

	/**
	 * This method loads the .dbf file represented by the file argument
	 * and puts all the data found inside the file into a DBFTable
	 * object, which it returns.
	 * 
	 * @param file File location of .dbf to load.
	 * 
	 * @return A constructed and initialized DBF table containing the
	 * data found in the file.
	 * 
	 * @throws IOException Thrown when an error is encountered reading
	 * the .dbf file. Likely problems are that the File is not in the
	 * location specified or that it is spelled differently.
	 */
	public DBFTable loadDBF(File file) throws IOException
	{
		// THIS IS THE TABLE WE'RE GOING TO FILL AND THEN RETURN
		DBFTable mapTable = new DBFTable();

		// WE ARE GOING TO READ RAW BYTE DATA
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);

		// LOAD THE FIRST 32 BYTES OF THE HEADER
		loadHeader(dis, mapTable);

		// SUBRECORDS (32-(positionOfFirstDataRecorded-3))
		loadFields(dis, mapTable);

		// HEADER RECORD TERMINATOR (SHOULD BE 0x0D)
		byte terminator = dis.readByte();
		mapTable.setTerminator(terminator);

		// AND NOW READ THE ACTUAL DATA
		loadRecords(dis, mapTable);

		// ALL DONE, NOW RETURN THE TABLE
		return mapTable;
	}	
	
	/**
	 * This helper method loads just the .dbf file header portion
	 * into the mapTable argument using the dis stream.
	 * 
	 * @param dis The stream reading from the .dbf file.
	 * 
	 * @param mapTable The dbf table being loadd.
	 * 
	 * @throws IOException Thrown when the stream fails.
	 */
	private void loadHeader( 	DataInputStream dis,
								DBFTable mapTable) throws IOException
	{
		// DBF file type (0)
		byte dbfFileType = dis.readByte();
		mapTable.setFileType(dbfFileType);

		// LAST UPDATE (1-3)
		int year = 1900 + dis.readByte();
		int month = dis.readByte();
		int day = dis.readByte();
		mapTable.setLastModifiedDate(year, month, day);

		// NUMBER OF RECORDS IN FILE (4-7)
		int numberOfRecordsInFile = readLittleEndianInt(dis);
		mapTable.setNumberOfRecords(numberOfRecordsInFile);

		// POSITION OF FIRST DATA RECORDED (8-9)
		short positionOfFirstDataRecorded = readLittleEndianShort(dis);
		mapTable.setPositionOfFirstDataRecorded(positionOfFirstDataRecorded);

		// LENGTH OF ONE DATA RECORD, INCLUDING DELETE FLAG (10-11)
		short dataRecordLength = readLittleEndianShort(dis);
		mapTable.setDataRecordLength(dataRecordLength);

		// ZEROES (12-13)
		short zeroes = readLittleEndianShort(dis);
		mapTable.setZeroes(zeroes);

		// DBASE IV Transaction Flag (14)
		byte dbaseTransactionFlag = dis.readByte();
		mapTable.setDbaseTransactionFlag(dbaseTransactionFlag);

		// DBASE IV Encryption Flag (15)
		byte dbaseEncryptionFlag = dis.readByte();
		mapTable.setDbaseEncryptionFlag(dbaseEncryptionFlag);

		// Multiuser Processing 12 Bytes (16-27)
		int[] mup = new int[3];
		mup[0] = readLittleEndianInt(dis);
		mup[1] = readLittleEndianInt(dis);
		mup[2] = readLittleEndianInt(dis);
		mapTable.setMup(mup);

		// TABLE FLAGS (28)
		byte tableFlags = dis.readByte();
		mapTable.setFlags(tableFlags);

		// CODE PAGE MARK/LANGUAGE DRIVER ID (29)
		byte pageMark = dis.readByte();
		mapTable.setCodePageMark(pageMark);

		// RESERVED, CONTAINS 0x00 (30-31)
		short reserved = readLittleEndianShort(dis);
		mapTable.setReserved(reserved);		
	}

	/**
	 * This method loads all of the fields in the mapTable argument
	 * via the dis stream, which is setup at the proper location.
	 * 
	 * @param dis Input stream reading from the .dbf file.
	 * 
	 * @param mapTable The dbf table being loaded.
	 * 
	 * @throws IOException Thrown when the stream fails.
	 */
	private void loadFields(	DataInputStream dis,
								DBFTable mapTable) throws IOException
	{
		// NUMBER OF FIELDS (COLUMNS) IN THE TABLE
		int numFields = (mapTable.getPositionOfFirstDataRecorded() - 32 - 2 + 1)/32;

		for (int i = 0; i < numFields; i++)
		{
			// ANOTHER COLUMN
			DBFField fieldToAdd = new DBFField();
			mapTable.addField(fieldToAdd);

			// FIELD NAME
			String fieldName = "";
			for (int j = 0; j < 11; j++)
			{
				byte c = dis.readByte();
				if (c != 0)
					fieldName += (char)c;
			}
			fieldToAdd.setName(fieldName);

			// FIELD TYPE
			byte fieldTypeAsByte = dis.readByte();
			char fieldType = (char)fieldTypeAsByte;
			if (fieldType == 'C') 	fieldToAdd.setType(DBFFieldType.C);
			else					fieldToAdd.setType(DBFFieldType.N);

			// DISPLACEMENT OF FIELD IN RECORD (12-15)
			int displacementOfFieldInRecord = readLittleEndianInt(dis);
			fieldToAdd.setDisplacement(displacementOfFieldInRecord);

			// LENGTH OF FIELD (16)
			int lengthOfField = dis.readByte();
			if (lengthOfField < 0)
				lengthOfField += 256;
			fieldToAdd.setLength(lengthOfField);

			// NUMBER OF DECIMAL PLACES (17)
			byte numberOfDecimalPlaces = dis.readByte();
			fieldToAdd.setNumberOfDecimalPlaces(numberOfDecimalPlaces);

			// FIELD FLAGS (18)
			byte fieldFlags = dis.readByte();
			fieldToAdd.setFlags(fieldFlags);
			
			// AUTOINCREMENT NEXT (19-22)
			int next = readLittleEndianInt(dis);
			fieldToAdd.setNext(next);

			// AUTOINCREMENT STEP (23)
			byte step = dis.readByte();
			fieldToAdd.setStep(step);

			// RESERVED (24-31) - WE WON'T USE THIS
			long reservedFieldData = dis.readLong();
			fieldToAdd.setReservedData(reservedFieldData);
		}
	}
	
	/**
	 * This helper method reads and loads all the row data (records)
	 * from the .dbf into the mapTable argument. Note that the dis argument
	 * must be ready to read at the start of the records section, which is
	 * right after the fields section.
	 * 
	 * @param dis Input stream reading from the .dbf file.
	 * 
	 * @param mapTable The dbf table being loaded.
	 * 
	 * @throws IOException Thrown when the stream fails.
	 */
	private void loadRecords(	DataInputStream dis,
				DBFTable mapTable) throws IOException
	{
		// READ ALL ROW DATA
		for (int i = 0; i < mapTable.getNumberOfRecords(); i++)
		{				
			// HERE'S THE RECORD WE ARE LOADING
			DBFRecord recordToAdd = new DBFRecord(mapTable.getNumFields());

			// THERE IS A MYSTERY BYTE BEFORE EACH RECORD
			byte mystery = dis.readByte();
			recordToAdd.setMystery(mystery);

			// LOAD DATA FOR EACH FIELD
			Iterator<DBFField> fieldsIt = mapTable.fieldsIterator();
			int fieldsCounter = 0;
			while (fieldsIt.hasNext())
			{
				// WHAT TYPE OF FIELD IS IT (character data, numbers, etc.)?
				DBFField field = fieldsIt.next();

				// TEXT?
				if (field.getType() == DBFFieldType.C)
				{
					String text = "";
					for (int counter = 0; counter < field.getLength(); counter++)
					{
						char c = (char)dis.readByte();
						text += c;
					}
					text = text.trim();
					recordToAdd.setData(text, fieldsCounter);
				}
				// IT MUST BE AN 'N' TYPE SINCE THOSE ARE THE ONLY TWO WE'RE USING
				else 
				{
					String text = "";
					for (int counter = 0; counter < field.getLength(); counter++)
					{
						char c = (char)dis.readByte();
						text += c;
					}
					text = text.trim();
					if (text.contains("."))
					{
						double num = 0.0;
						num = Double.parseDouble(text);
						recordToAdd.setData(num, fieldsCounter);
					}
					else
					{
						long num = 0;
						if (text.length() > 0)
							num = Long.parseLong(text);
						recordToAdd.setData(num, fieldsCounter);
					}
				}
				fieldsCounter++;
			}
		//	Comparable key = (Comparable)recordToAdd.getData(mapTable.getKeyIndex());
			mapTable.addRecord(recordToAdd);
		}		
	}

	/**
	 * This method reads four bytes from the dis argument and
	 * returns an int as represented by those four bytes in
	 * little endian form. Note that when one reads an integer
	 * from a DataInputStream, the default means of doing so
	 * is in big endian form, so this method has to switch the
	 * bytes around.
	 * 
	 */
	private int readLittleEndianInt(DataInputStream dis) throws IOException
	{
		// NOTE THAT I COULD HAVE USED THE Integer.reverseBytes
		// METHOD HERE, BUT SOMETIMES IT'S EDUCATIONAL AND
		// STRANGELY FUN TO DO THINGS YOURSELF
		int num = dis.readInt();
		int n0 = num & 0xff000000;
		n0 = (n0 >> 24) & 0x000000ff;
		int n1 = num & 0x00ff0000;
		n1 = n1 >> 8;
		int n2 = num & 0x0000ff00;
		n2 = n2 << 8;
		int n3 = num & 0x000000ff;
		n3 = n3 << 24;
		return n0 | n1 | n2 | n3;
	}

	/**
	 * This helper method is similar to readLittleEndianInt except it is
	 * used for reading a 2-byte little endian integer (short) and converting
	 * and then returning it as a short.
	 * 
	 * @param dis Input stream being used for loading a .dbf file.
	 * 
	 * @return A two byte integer read from the.dbf file as a
	 * little endian short.
	 * 
	 * @throws IOException This can occur when the stream fails.
	 */
	private short readLittleEndianShort(DataInputStream dis) throws IOException
	{
		short num = dis.readShort();
		return Short.reverseBytes(num);
	}
}