package org.matsim.trafficConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.matsim.project.MyNode;
import org.matsim.project.Vehicle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlTrafficConverter {

	public static void main(String args[]) {
		
		try {
			List<MyNode> node = readXMLnodes();
			List<Vehicle> vehicle = readXMLvehicle();
			
			try {
				writeXML(node, vehicle);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			for(MyLink bla : list) {
//				System.out.println(bla.getDurchmesser());
//			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		try {
//			writeXML(MyLinkList);
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static List<MyNode> readXMLnodes() throws SAXException, IOException, ParserConfigurationException {

		//File xmlFile = new File("scenarios\\equil\\network.xml");
		File xmlFile = new File("scenarios\\equil\\networkphuc.xml");
		
//		File xmlFile = new File("scenarios\\equil\\networkOpenDS.xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = factory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);

		doc.getDocumentElement().normalize();

		
		NodeList nodelist = doc.getElementsByTagName("node");
		
		List<MyNode> result = new ArrayList<MyNode>();
		
		for (int i = 0; i < nodelist.getLength(); i++) {

			Node node = nodelist.item(i);
			
			MyNode myNode = new MyNode();
			

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element elem = (Element) node;
				String id = elem.getAttribute("id");
				//System.out.printf("Link id: %s%n", id);

//				System.out.println(elem.getAttribute("x"));
//				System.out.println(elem.getAttribute("y")); 
				myNode.setID(Integer.parseInt(id));
				myNode.setX(Double.parseDouble(elem.getAttribute("x")));
				myNode.setZ(Double.parseDouble(elem.getAttribute("y")));
				result.add(myNode);
				}
				
		}
		
		return result;
	}
	
	public static List<Vehicle> readXMLvehicle() throws SAXException, IOException, ParserConfigurationException {
		
//		File xmlFile = new File("scenarios\\equil\\ownPlans.xml");
		File xmlFile = new File("scenarios\\equil\\ownPlans1000agents.xml");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = factory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);

		doc.getDocumentElement().normalize();

		
		NodeList vehiclelist = doc.getElementsByTagName("person");
		List<Vehicle> result = new ArrayList<Vehicle>();
		
		for (int i = 0; i < vehiclelist.getLength(); i++) {

			Node person = vehiclelist.item(i);
			
			Vehicle vehicle = new Vehicle();
			

			if (person.getNodeType() == Node.ELEMENT_NODE) {

				Element elem = (Element) person;
				String id = elem.getAttribute("id");
//				System.out.printf("Link id: %s%n", id);

				vehicle.setID(Integer.parseInt(elem.getAttribute("id")));
				
				NodeList elem2 = elem.getElementsByTagName("route");
//				System.out.println(elem2.getLength());
				
				
				for(int j=0; j<elem2.getLength();j++ ) {
					Node route = elem2.item(j);
					String test= route.getTextContent();
//					System.out.println(test);
					List<String> routeList = GetRouteListFromXMLwithoutWhiteSpace(test);
					
//					System.out.println(routeList);
					
//					List<String> supplierNames = Arrays.asList("sup1", "sup2", "sup3");
//					System.out.println(supplierNames);
//
					vehicle.addNode(routeList);
					
					
				}
				result.add(vehicle);
				}
				
		}
		
		
		return result;
	}

public static void writeXML(List<MyNode> myNodeList, List<Vehicle> myVehicleList) throws ParserConfigurationException, TransformerException{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        Element root = doc.createElementNS("OpenDS", "Traffic");
        doc.appendChild(root);

        
        for(int i= 0; i< myVehicleList.size();i++) {
        	root.appendChild(createModel(doc, myNodeList,myVehicleList , i));
        }
        

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transf = transformerFactory.newTransformer();
        
        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transf.setOutputProperty(OutputKeys.INDENT, "yes");
        transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        

        File myFile = new File("C:\\Users\\Thien\\Desktop\\test für java text\\traffic.xml");
        
        StreamResult console = new StreamResult(System.out);
        StreamResult file = new StreamResult(myFile);
        transf.transform(source, console);
        transf.transform(source, file);

        
    }

    private static Node createModel(Document doc, List<MyNode> myNodeList, List<Vehicle> myVehicleList, int listPosition) {
        
        Element model = doc.createElement("vehicle");

        model.setAttribute("id", "car1a");
//        model.setAttribute("key", "");
//        model.setAttribute("ref", "box");
        model.appendChild(createUserElement(doc, "modelPath", "Models/Cars/drivingCars/bmw1/Car.j3o"));
        model.appendChild(createUserElement(doc, "mass", "800"));
        model.appendChild(createUserElement(doc, "acceleration", "3.3"));
        model.appendChild(createUserElement(doc, "decelerationBrake", "8.7"));
        model.appendChild(createUserElement(doc, "decelerationFreeWheel", "2.0"));
        model.appendChild(createUserElement(doc, "engineOn", "true"));
        model.appendChild(createUserElement(doc, "maxDistanceFromPath", "10.0"));
        model.appendChild(createUserElement(doc, "curveTension", "0.05"));
        model.appendChild(createUserElement(doc, "pathIsCycle", "true"));
        model.appendChild(createUserElement(doc, "pathIsVisible", "true"));
        
        List<List<String>> routesList = myVehicleList.get(listPosition).getRoutes();
//        System.out.println(routesList.size());
        List<String> routeList = routesList.get(0);
        String firstWaypoint = routeList.get(0);
        model.appendChild(createUserElement(doc, "startWayPoint", "WayPoint_" +firstWaypoint));
        Node wayPoints = model.appendChild(createEmptyElement(doc, "wayPoints"));
        
        int wayPointNumber =1;
        for(int i=0; i<routesList.size();i++) {
        	List<String> currentRouteList = routesList.get(i);
        	for(int j=0; j<routesList.get(i).size();j++) {
                int currentWaypoint = Integer.parseInt(routeList.get(j));
                System.out.println(currentWaypoint);

//              Node wayPoint = wayPoints.appendChild(createWayPoint(doc, "wayPoint", String.valueOf(wayPointNumber)) ).appendChild(createEmptyElement(doc, "translation")).appendChild(createVector(doc, "vector", "3"));
                Node wayPoint = wayPoints.appendChild(createWayPoint(doc, "wayPoint", String.valueOf(wayPointNumber)) );
                Node translation = wayPoint.appendChild(createEmptyElement(doc, "translation")).appendChild(createVector(doc, "vector", "3"));
                
              MyNode currentNode= myNodeList.get(currentWaypoint-1);
              String x = String.valueOf(currentNode.getX());
              String y = String.valueOf(currentNode.getY());
              String z = String.valueOf(currentNode.getZ());

//              wayPoint.appendChild(createVectorEntries(doc, "entry", x));
//              wayPoint.appendChild(createVectorEntries(doc, "entry", y));
//              wayPoint.appendChild(createVectorEntries(doc, "entry", z));
              
              translation.appendChild(createVectorEntries(doc, "entry", x));
              translation.appendChild(createVectorEntries(doc, "entry", y));
              translation.appendChild(createVectorEntries(doc, "entry", z));
              
              wayPointNumber = wayPointNumber +1;

              wayPoint.appendChild(wayPoint.appendChild(createUserElement(doc, "speed", "30")));
//        		wayPoints.appendChild(createVectorEntries(doc, "wayPoint id","WayPoint_"+wayPointNumber));
//        		wayPoints.appendChild(createVectorEntries(doc, "entry","1"));

        	}
        	
        }


//        
//        Node colorvector = model.appendChild(createEmptyElement(doc, "material")).appendChild(createEmptyElement(doc, "color")).appendChild(createVector(doc, "vector","4"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","1"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","0"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","0"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","1"));
//        
//        model.appendChild(createUserElement(doc, "visible", "true"));
//        model.appendChild(createUserElement(doc, "collisionShape", "none"));
//        
//        double scale = myLinkList.get(listPosition).getDurchmesser(); //*8/100;
//        String scaleString = Double.toString(scale);
//        
//        Node scalevector = model.appendChild(createEmptyElement(doc, "scale")).appendChild(createVector(doc, "vector", "3"));
//        scalevector.appendChild(createVectorEntries(doc, "entry", "1"));
//        scalevector.appendChild(createVectorEntries(doc, "entry", "1"));
//        scalevector.appendChild(createVectorEntries(doc, "entry", scaleString));
//        
//        double rot = myLinkList.get(listPosition).getRotation();
//        String rotString = Double.toString(rot);
//        
//        Node rotation = model.appendChild(createRotation(doc, "rotation")).appendChild(createVector(doc, "vector", "3"));
//        rotation.appendChild(createVectorEntries(doc, "entry", "0"));
//        rotation.appendChild(createVectorEntries(doc, "entry", rotString));
//        rotation.appendChild(createVectorEntries(doc, "entry", "0"));
//        
//        double transX = myLinkList.get(listPosition).getMittelpunktX()/100;; ///100;
//        double transZ = myLinkList.get(listPosition).getMittelpunktY()/100;; ///100;
//        String transXstring = Double.toString(transX);
//        String transZstring = Double.toString(transZ);
//        
//        Node translation = model.appendChild(createEmptyElement(doc, "translation")).appendChild(createVector(doc, "vector", "3"));
//        translation.appendChild(createVectorEntries(doc, "entry", transXstring));
//        translation.appendChild(createVectorEntries(doc, "entry", "0"));
//        translation.appendChild(createVectorEntries(doc, "entry", transZstring));
        

        return model;
    }
/*
    private static Node createRotation(Document doc, String name) {

        Element node = doc.createElement(name);
        node.setAttribute("quaternion", "true");
        
        
        return node;
    }
    
    */
    
    private static Node createVector(Document doc, String name, String size) {

        Element node = doc.createElement(name);
        node.setAttribute("jtype", "java_lang_Float");
        node.setAttribute("size", size);
        
        return node;
    }
    
    private static Node createWayPoint(Document doc, String name, String id) {

        Element node = doc.createElement(name);
        node.setAttribute("id", "WayPoint_"+id);
        
        return node;
    }
    
   
    private static Node createVectorEntries(Document doc, String name, String value) {

        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        
        return node;
    }
    
    private static Node createEmptyElement(Document doc, String name) {

        Element node = doc.createElement(name);
        return node;
    }

    private static Node createUserElement(Document doc, String name, 
            String value) {

        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));

        return node;
    }
	
	
	public static List<String> GetRouteListFromXMLwithoutWhiteSpace(String routeList){
        // Creating array of string length 
        char[] ch = new char[routeList.length()];

        // Copy character by character into array 
        for (int i = 0; i < routeList.length(); i++)
        {
            ch[i] = routeList.charAt(i);
        }

        List<String> stringList = new ArrayList<String>();
        String node = "";
        // Printing content of array 
        for (int i = 0; i < ch.length; i++)
        {
        	if (ch[i] != ' ')
            {
                node = node + ch[i];
            }
            else
            {
                stringList.add(node);
                node = "";
            }
            if(i== (ch.length - 1))
            {
                stringList.add(node);
            }
        }
        return stringList;
    }
}
