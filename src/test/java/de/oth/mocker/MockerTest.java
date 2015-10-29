package de.oth.mocker;

import org.junit.*;
import java.util.*;
import static de.oth.mocker.Mocker.mock;
import static de.oth.mocker.Mocker.spy;
import static de.oth.mocker.Mocker.verify;
import static de.oth.mocker.Mocker.times;
import static de.oth.mocker.Mocker.never;
import static de.oth.mocker.Mocker.atLeast;
import static de.oth.mocker.Mocker.atMost;

public class MockerTest {

	@Test
	public void mockTest1() {
		List<String> mockObject = mock(ArrayList.class);
		Random mock2 = mock(Random.class);
		
		mockObject.add("John Doe");
		mockObject.add("Max Muster");
		mockObject.add("John Doe");
		mockObject.size();
		//mockObject.clear();
		
		mock2.nextInt(5);
		mock2.nextInt(5);
		mock2.toString();
		mock2.toString();
		mock2.toString();
		mock2.hashCode();
		
		verify(mockObject, never()).clear();
		verify(mockObject).add("Max Muster");
		verify(mockObject, times(2)).add("John Doe");
		verify(mock2, atLeast(2)).toString();
		verify(mock2, times(2)).nextInt(5);
	}
	
	@Test
	public void spyTest1() {
		List<String> someList = new ArrayList<String>();
		List<String> spyObj = spy(someList);
		
		spyObj.add("Test Spy");
		spyObj.add("Test Spy");
		spyObj.add("Test Spy");
		//spyObj.add("Test Spy");
		//spyObj.toString();
		
		verify(spyObj, atMost(3)).add("Test Spy");
		verify(spyObj, never()).toString();
	}
	
}
