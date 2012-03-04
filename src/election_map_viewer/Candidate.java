package election_map_viewer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class Candidate {
	private String name;
	private BigDecimal votes;
	private int position;
	private Color theColor;
	
	public Candidate(int position, String name, Color theColor) throws IOException{
		this.position=position; 
		this.name = name;
		this.theColor = theColor;
		this.votes = BigDecimal.ZERO;
	}
	public String getName(){ return name;	}
	public BigDecimal getVotes(){return votes;	}
	public int getPosition(){return position;	}
	public Color getColor(){return theColor;	}
	
	public void setName(String name){this.name=name;	}
	public void setVotes(File file, int position) throws IOException
	{
		this.votes= (new ElectionMapDataModel()).candidateVotes(position, file);	
	}
	public void setDefaultVotes(BigDecimal votes) {this.votes=votes;	}
}