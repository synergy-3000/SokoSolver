package testSetup;

import java.awt.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gui.PlayerMove;

class TestSokoMove {
	
	int[] to;
	int[] from;
	
	@BeforeEach
	void setUp() throws Exception {
		PlayerMove.UP.setEnabled(false);
		PlayerMove.LEFT.setEnabled(false);
		to = new int[2];
		from = new int[2];
		to[0]=0; to[1]=1;
		from[0] = to[0];
		from[1] = to[1];
		to[0] = 5;
		System.out.println("from[0]= " + from[0] + " from[1]= " + from[1]);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testExecute() {
		System.out.println("Start testExecute");
		PlayerMove[] moves = getMoves();
		//moves[0].execute(null, null, null);
		
		System.out.println("Finish testExecute");
	}

	@Test
	void testIsEnabled() {
		System.out.println("Start testIsEnabled");
		for (PlayerMove move : PlayerMove.values()) {
			//move.execute(null, null, null);
		}
		System.out.println("Finish testIsEnabled");
	}

	@Test
	void testSetEnabled() {
		//fail("Not yet implemented");
	}
	PlayerMove[] getMoves() {
		return PlayerMove.values();
	}
	@Test
	void testContains() {
		System.out.println("Start testContains");
		Rectangle square = new Rectangle(0,0,50,50);
		Rectangle clip = new Rectangle(3*50,3*50,3*50,50);
		for (int r=0; r<6; r++) {
			for(int c=0; c<6; c++) {
				square.setLocation(c*50, r*50);
				if (clip.contains(square)) System.out.println("Clip contains ("+r+","+c+")"); 
			}
		}
		System.out.println("End testContains");
	}

}
