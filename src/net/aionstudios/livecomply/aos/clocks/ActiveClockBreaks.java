package net.aionstudios.livecomply.aos.clocks;

public class ActiveClockBreaks {
	
	private int clockIn = 0;
	private int last10 = 0;
	private int last30 = 0;
	
	private boolean last10CoveredBy30 = false;
	
	private int next10By = 0;
	private int next30By = 0;
	
	public ActiveClockBreaks() {
		
	}
	
	public void setClockedIn(int i) {
		clockIn = i;
	}
	
	public void updateLast10(int i) {
		if(last10<i)last10=i;
	}
	
	public void updateLast30(int i) {
		if(last30<i)last30=i;
	}
	
	public void calcNextBreaks() {
		int n10 = clockIn;
		int n30 = clockIn;
		while(last10>n10)n10+=200;
		while(last30>n10)n10+=200;
		n10+=200;
		while(last30>n30)n30+=400;
		n30+=400;
		if(n30-200==n10&&last30!=0) {
			last10CoveredBy30 = true;
		}
		next10By = n10;
		next30By = n30;
	}
	
	public int getNext10() {
		return next10By;
	}
	
	public int getNext30() {
		return next30By;
	}
	
	public int getInTime() {
		return clockIn;
	}
	
	public int getLast10() {
		return last10;
	}
	
	public int getLast30() {
		return last30;
	}
	
	public boolean isLast10CoveredBy30() {
		return last10CoveredBy30;
	}

}
