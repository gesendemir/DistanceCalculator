import java.util.ArrayList;
/*
 * Hasan Guney Esendemir.
 * 15.03.1995
 * Hochschule Ulm - CTS 3 Student
 *  
 * 
 */

public class Main {

	public static void main(String[] args){
		
		
		ArrayList<MapObject> test = new ArrayList<MapObject>();
		MapObject.readCSV("testdata.csv", test); //Location of testdata.csv
		MyMap.TransportationDetails(test);
		
		


	}
}