package election_map_viewer;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;

import dbf_framework.DBFRecord;
import dbf_framework.DBFTable;
//@author Aaron Meltzer
public class Candidate {
	//keep track of various candidate attributes
	private String name;
	private BigDecimal votes;
	private int position;
	private Color theColor;
	/*@params- position:int name:String theColor:Color
	 */
	public Candidate(int position, String name, Color theColor) throws IOException{
		this.position=position; 
		this.name = name;
		this.theColor = theColor;
		this.votes = BigDecimal.ZERO;
	}
	//@return: String
	public String getName(){ return name;	}
	//@return: BigDecimal
	public BigDecimal getVotes(){return votes;	}
	//@return: int
	public int getPosition(){return position;	}
	//@return: Color
	public Color getColor(){return theColor;	}
	
	//@params: name:String
	public void setName(String name){this.name=name;	}
	
	/*@params- currentTable:DBFTable, currentRecord:DBFRecord, position:int
	 * @throws- IOException
	 * depending on which info is given the votes are either added up or simply set form a specific position.
	 */
	public void setVotes(DBFTable currentTable,DBFRecord currentRecord, int position) throws IOException
	{
		if(currentTable==null){
			this.votes= new BigDecimal((Long)currentRecord.getData(position));
		}
		else{
			for(int i=0; i<currentTable.getNumberOfRecords(); i++){
				Long data = (Long)currentTable.getRecord(i).getData(position);
				this.votes=this.votes.add(new BigDecimal(data));
			}
		}
	}
	/*
	 * @params- votes:BigDecimal 
	 * in case the votes just need to be set
	 */
	public void setDefaultVotes(BigDecimal votes) {this.votes=votes;	}
}