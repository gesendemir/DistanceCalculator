import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

/*
 * Hasan Guney Esendemir.
 * 15.03.1995
 * Hochschule Ulm - CTS 3 Student
 *  
 * 
 */

class MyMap{
	
//	
//	First method is for finding end points of each transport. 
//	I've used an ArrayList to store position of end points to the transport numbers. 
	public static ArrayList<Integer> endPoint = new ArrayList<Integer>();
	
	public static void TransportationDetails(ArrayList<MapObject> TransList){
		
		for(int i = 0; i < TransList.size() - 1; i++) {
			//if transport number of the current element is not matching with the next one, add position to ArrayList.
			if(TransList.get(i).getTrans() != TransList.get(i+1).getTrans()) {
				endPoint.add(i);
			//Add last elements position as final end point.
			}else if(i == TransList.size() - 2) {
				endPoint.add(i+1);
			}
		}
		try {
			TransLister(TransList);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		
	}
	

	
	//This second method sends the map objects to urlCreator method by their positions in MapObject ArrayList and the result as output.
	//This method also calculates total distance of the transport and
	//							the distance between first loading and last unloading points of transport.
	
	private static void TransLister(ArrayList<MapObject> TransList) throws IOException, org.json.simple.parser.ParseException {
		int current = 0;
		//Goes on MapObject ArrayList to according to transport number.
		for(int i = 0; i < endPoint.size(); i++) {
			//total value
			float total = 0;
			if(i ==0) {
				//Starts from the beggining.
				for(int j=0; j<=endPoint.get(0); j++) {
					//Send map object to urlCreator to get distance between loading point and unloading point. 
					//urlCreator returns the distance; add distance to total.
					total += urlCreator(TransList.get(j));
					
					//Store the transport number of current map object.
					current = TransList.get(j).getTrans();
					
					System.out.println();
					
				}
				
				System.out.println();
				//Give output on terminal.
				System.out.println("Total Distance for Transport: " + current +" is " + total);
				System.out.println();
				System.out.println("From First Loading Point to Last Unloading Point:");
				//Calculate the distance between first loading point and last unloading point of transport. 
				//This one needs two map objects (The one with first loading point and the one with last unloading point.  
				urlCreator(TransList.get(0) , TransList.get(endPoint.get(0)));
				
				System.out.println("########################################################################################");
				//Clean total value.
				total =0;
			}else {
				//Continue from the last point.
				for(int j = endPoint.get(i-1) + 1; j <= endPoint.get(i); j++) {
					//Rest of is same as before.
					total += urlCreator(TransList.get(j));
					current = TransList.get(j).getTrans();
					System.out.println();
				}
				System.out.println();
				System.out.println("Total Distance for Transport: " + current +" is " + total);
				System.out.println();
				System.out.println("From First Loading Point to Last Unloading Point:");
				urlCreator(TransList.get(endPoint.get(i - 1) + 1) , TransList.get(endPoint.get(i)));
				System.out.println("########################################################################################");
				
				total =0;
			}
		}
	}
	
	private static float urlCreator(MapObject a) throws IOException, org.json.simple.parser.ParseException{

		//As a map provider I've used API from here.com
		//Because here.com directly gives the distance between two points by calculating route according to vehicle type. 
		
		String s = "https://route.api.here.com/routing/7.2/calculateroute.json" + 
				"?app_id=LqVBq2kLncuFdDyjhOQR"+ //APP ID from here.com
				"&app_code=LiEzG8Xcf1psY7BpLC5cbg"; //APP Code from here.com
				s += "&waypoint0" + "=geo!"; //First point
				s += a.getLatv() + "," + a.getLonv(); //Lattitude and Longtitude of First Point
				s += "&waypoint1" + "=geo!";
				s += a.getLatn() + "," + a.getLonn(); //Lattitude and Longtitude of First Point
				s += "&mode=fastest;truck;traffic:disabled" +  //Route Mode, Truck Type, Traffic Options
				"&limitedWeight=30.5" + //Limited weight of calculated routes.
				"&height=4.25"; //Maximum Height of Trucks
				System.out.println("Transport From: " + a.getVon() + " TO: " + a.getNach() );
				return requestLocation(s); //Send url to make a request; Return distance value which is coming from request.
				
	}
	
	
	private static float urlCreator(MapObject a, MapObject b) {
		//Same urlCreator Method with two MapObjects.
		String s = "https://route.api.here.com/routing/7.2/calculateroute.json" + 
				"?app_id=LqVBq2kLncuFdDyjhOQR"+
				"&app_code=LiEzG8Xcf1psY7BpLC5cbg";
				s += "&waypoint0" + "=geo!";
				s += a.getLatv() + "," + a.getLonv();
				s += "&waypoint1" + "=geo!";
				s += b.getLatn() + "," + b.getLonn();
				s += "&mode=fastest;truck;traffic:disabled" + 
				"&limitedWeight=30.5" + 
				"&height=4.25";
			//
			return requestLocation(s);
	}
			

	
	
	
	private static float requestLocation(String s) {
		String str = "";
		try {
		URL url = new URL(s);
		//Connect to url.
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		//Send a GET Request.
		conn.setRequestMethod("GET");
		//Connect to server.
		conn.connect();
		//Get response code.
		int responsecode = conn.getResponseCode();
		//If response code is not 200 throw exception.
		if(responsecode != 200)	throw new RuntimeException("HttpResponseCode: " +responsecode);
		
		else{
			//Store response.
			Scanner sc = new Scanner(url.openStream());
			while(sc.hasNext()){
				str+=sc.nextLine();
				}

			sc.close();
		}
		//Cut the connection.
		conn.disconnect();
		
		//Read Response
		//Response Comes as an JSON; I've used JSON-simple to parse the JSON response.
		
		//Link to JSON Simple : https://code.google.com/archive/p/json-simple/
		
		//Find distance from request and return it.
		JSONParser parse = new JSONParser();
		JSONObject jsonobj = (JSONObject) parse.parse(str);
		//Crawling JSON Object.
		JSONObject respond = (JSONObject) jsonobj.get("response");
		JSONArray route = (JSONArray) respond.get("route");
		JSONObject zero = (JSONObject) route.get(0);
		JSONObject summary = (JSONObject) zero.get("summary");
		
		//Finally Distance.
		float distance = Float.parseFloat(summary.get("distance").toString()) / 1000;
		System.out.println("Distance Between Loading and Unloading Point: " + distance + "km" );
		
		return distance;		
		
	}catch(Exception e){
		e.printStackTrace();
		}
		return 0;
	}
	
	
}