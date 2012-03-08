package election_map_viewer;

import java.awt.Color;
import java.io.File;
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
	//@Returns String
	public String getName(){ return name;	}
	public BigDecimal getVotes(){return votes;	}
	public int getPosition(){return position;	}
	public Color getColor(){return theColor;	}
	
	public void setName(String name){this.name=name;	}
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
	public void setDefaultVotes(BigDecimal votes) {this.votes=votes;	}
}