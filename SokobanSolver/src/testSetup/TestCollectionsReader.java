package testSetup;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.CollectionsReader;

class TestCollectionsReader {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testReadCollection() {
		File file = new File("/Users/zhipinghe/Desktop/SokobanOriginalLevels.txt");
		new CollectionsReader().readCollection(file);
	}

}
