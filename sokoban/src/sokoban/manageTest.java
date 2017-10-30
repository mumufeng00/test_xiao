package sokoban;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class manageTest {
private static manage m_manage=new manage();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testIsincorner() {
	
		 assertEquals(true,m_manage.isincorner(1 ,1));
//		 fail("Not yet implemented");
	
	}

	@Test
	public void testCaculate_box_shortpath() {
		
//		fail("Not yet implemented");
	}

	@Test
	public void testBoxDFSa() {
		
		 assertEquals(5,m_manage.BoxDFSa(1, 1, 4, 3, 0,0 ));
//		fail("Not yet implemented");
	}

}
