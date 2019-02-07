package solver;

import java.util.Arrays;

// TODO Class not needed anymore - a failed idea!
public class Region {
	
	int[][] region;
	int hashCode = -1;
	
	public Region(int[][] region) {
		this.region = region;
	}
	
	public int[][] getRegion() {
		return region;
	}
	public void setRegion(int[][] region) {
		this.region = region;
	}
	@Override
	public String toString() {
		String retVal = "";
		for (int row = 0; row < region.length; row++) {
			retVal += Arrays.toString(region[row]) + "\n";
		}
		return retVal;
	}

	@Override
	public int hashCode() {
		if (hashCode == -1) {
			hashCode = 0;
			for (int row = 0; row < region.length; row++) {
				for (int col = 0; col < region[row].length; col++) {
					if (region[row][col] >= 0) {
						hashCode++;
					}
				}
			}
		}
		return hashCode;
	}
	public boolean equals(Region region2) {
		
		int[][] arrRegion2 = region2.getRegion();
		
		for (int row = 0; row < region.length; row++) {
			for (int col = 0; col < region[row].length; col++) {
				if ( (region[row][col] >= 0) && (arrRegion2[row][col] < 0)) {
					return false;
				}
				if ( (region[row][col] < 0) && (arrRegion2[row][col] >= 0)) {
					return false;
				}
			}
		}
		return true;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Region) {
			return this.equals((Region)obj);
		}
		return super.equals(obj);
	}
	

}
