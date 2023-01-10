package org.matsim.project;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * @author Phuc Tam Nguyen Van
 * Creating XML file from a MATSim network to load in Network model in OpenDS
 * the models will be inserted in the \OpenDS\assets\DrivingTasks\Projects\Project\scene.xml scene file
 */
public class XmlMatsimToOpenDS2 {

	public static void main(String args[]) {
		
		//for the OpenDS network, we only need the links from the MATSim network
		List<MyLink> MyLinkList = new ArrayList<MyLink>();
		
		try {
			List<MyLink> list = readXML();
			try {
				writeXML(list);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		}
	/**
	 *read XML file from the MATSim network and save it in List<MyLink> MyLinkList
	 *MATSim network is in 2d cartesian coordinate system, which has to be converted into a 3d coordinate
	 *system
	*/
	public static List<MyLink> readXML() throws SAXException, IOException, ParserConfigurationException {

//		File xmlFile = new File("scenarios\\equil\\network.xml");
		File xmlFile = new File("scenarios\\equil\\networkphuc.xml");
		
//		File xmlFile = new File("scenarios\\equil\\networkOpenDS.xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = factory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);

		doc.getDocumentElement().normalize();

		
		NodeList nodelist = doc.getElementsByTagName("node");
		NodeList linklist = doc.getElementsByTagName("link");
		
		List<MyLink> result = new ArrayList<MyLink>();
		
		for (int i = 0; i < linklist.getLength(); i++) {

			Node link = linklist.item(i);
			
			MyLink myLink = new MyLink();
			

			if (link.getNodeType() == Node.ELEMENT_NODE) {

				Element elem = (Element) link;
				String id = elem.getAttribute("id");
//				System.out.printf("Link id: %s%n", id);

				
				int fromInt = Integer.parseInt(elem.getAttribute("from"));
				int toInt = Integer.parseInt(elem.getAttribute("to"));
				
				
				//System.out.printf("to: %s%n", toInt);
				
				Node FromNode = nodelist.item(fromInt-1);
				Node ToNode = nodelist.item(toInt-1);
				
				if (FromNode.getNodeType() == Node.ELEMENT_NODE && ToNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element fromElem = (Element) FromNode;
					
					//double a = Double.parseDouble(s)
					int fromX = Integer.parseInt(fromElem.getAttribute("x"));
					int fromY = Integer.parseInt(fromElem.getAttribute("y"));
					
//					System.out.printf("from: %s%n", fromInt);
					
					Element toElem = (Element) ToNode;
					
					int toX = Integer.parseInt(toElem.getAttribute("x"));
					int toY = Integer.parseInt(toElem.getAttribute("y"));
					
//					System.out.printf("to: %s%n", toInt);
					
					//mittelpunkt berechnen
					double MittelpunktX = (double)(fromX + toX)/2;
					double MittelpunktY = (double)(fromY + toY)/2;
//					System.out.printf("MittelpunktX: %s%n", MittelpunktX);
//					System.out.printf("MittelpunktY: %s%n", MittelpunktY);
					myLink.setMittelpunktX(MittelpunktX);
					myLink.setMittelpunktY(MittelpunktY);
					
					
					//durchmesser
					double XX = (double)(fromX-toX);
					double YY = (double)(fromY-toY);
					
					double Durchmesser = Math.sqrt(Math.pow(XX,2)+Math.pow(YY, 2));
//					System.out.printf("Durchmesser: %s%n", Durchmesser);
					myLink.setDurchmesser(Durchmesser);
					
					//Winkel
					if((toX-fromX)==0) {
						double anstieg = 90 -90 ;
//						anstieg = 0;	//test heute
//						System.out.printf("anstieg: %s%n", anstieg);
						myLink.setRotation(anstieg);
						
						
						
						System.out.printf("anstieg: %s%n", anstieg+" Durchmesser: %s%n", Durchmesser);
					}
					else if(toY-fromY ==0){
						double anstieg =0 -90;
						System.out.printf("anstieg: %s%n", anstieg+" Durchmesser: %s%n", Durchmesser);
						myLink.setRotation(anstieg);
						}
					else {
						double anstiegZ = (toY-fromY);
						double anstiegX = (toX-fromX);
						double anstieg = Math.atan(anstiegZ/anstiegX);
						anstieg = anstieg*360/(2*Math.PI);	//test heute
//						anstieg = 90 - anstieg*360/(2*Math.PI);
//						anstieg = ((anstieg * (360 / (2 * Math.PI))) * (-1));
//						System.out.printf("anstieg: %s%n", anstieg);
						myLink.setRotation(anstieg);
					
						System.out.printf("anstieg: %s%n", anstieg+" Durchmesser: %s%n", Durchmesser);
					}
					
					result.add(myLink);
				}
				
			}
			
		}
		return result;
	}
	public static void writeXML(List<MyLink> myLinkList) throws ParserConfigurationException, TransformerException{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        Element root = doc.createElementNS("OpenDS", "Links");
        doc.appendChild(root);

        
        for(int i= 0; i< myLinkList.size();i++) {
        	root.appendChild(createModel(doc, myLinkList, i));
        }
        

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transf = transformerFactory.newTransformer();
        
        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transf.setOutputProperty(OutputKeys.INDENT, "yes");
        transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        

        File myFile = new File("C:\\Users\\Thien\\Desktop\\test für java text\\users.xml");
        
        StreamResult console = new StreamResult(System.out);
        StreamResult file = new StreamResult(myFile);
        transf.transform(source, console);
        transf.transform(source, file);

        
    }

    private static Node createModel(Document doc, List<MyLink> myLinkList, int listPosition) {
        
        Element model = doc.createElement("model");

//        model.setAttribute("id", "redBox");
        model.setAttribute("id", "speedLimit70_1");
//        model.setAttribute("key", "");
        model.setAttribute("key", "Models/RoadSigns/speedLimits/Cube/Cube.scene");
//        model.setAttribute("ref", "box");
        model.appendChild(createUserElement(doc, "mass", "0"));
        
//        Node colorvector = model.appendChild(createEmptyElement(doc, "material")).appendChild(createEmptyElement(doc, "color")).appendChild(createVector(doc, "vector","4"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","1"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","0"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","0"));
//        colorvector.appendChild(createVectorEntries(doc, "entry","1"));
        
        model.appendChild(createUserElement(doc, "visible", "true"));
        model.appendChild(createUserElement(doc, "collisionShape", "none"));
        
        double scale = myLinkList.get(listPosition).getDurchmesser()*0.5; //*6/*8/100;
        String scaleString = Double.toString(scale);
        
        Node scalevector = model.appendChild(createEmptyElement(doc, "scale")).appendChild(createVector(doc, "vector", "3"));
        scalevector.appendChild(createVectorEntries(doc, "entry", "1"));
        scalevector.appendChild(createVectorEntries(doc, "entry", "0.1"));
        scalevector.appendChild(createVectorEntries(doc, "entry", scaleString));
        
        double rot = myLinkList.get(listPosition).getRotation();
        String rotString = Double.toString(rot);
        
        Node rotation = model.appendChild(createRotation(doc, "rotation")).appendChild(createVector(doc, "vector", "3"));
        rotation.appendChild(createVectorEntries(doc, "entry", "0"));
        rotation.appendChild(createVectorEntries(doc, "entry", rotString));
        rotation.appendChild(createVectorEntries(doc, "entry", "0"));
        
        double transX = myLinkList.get(listPosition).getMittelpunktX();; ///100;
        double transZ = myLinkList.get(listPosition).getMittelpunktY();; ///100;
        String transXstring = Double.toString(transX);
        String transZstring = Double.toString(transZ);
        
        Node translation = model.appendChild(createEmptyElement(doc, "translation")).appendChild(createVector(doc, "vector", "3"));
        translation.appendChild(createVectorEntries(doc, "entry", transXstring));
        translation.appendChild(createVectorEntries(doc, "entry", "11"));//15   //2
        translation.appendChild(createVectorEntries(doc, "entry", transZstring));
        

        return model;
    }

    private static Node createRotation(Document doc, String name) {

        Element node = doc.createElement(name);
        node.setAttribute("quaternion", "true");
        
        
        return node;
    }
    
    
    private static Node createVector(Document doc, String name, String size) {

        Element node = doc.createElement(name);
        node.setAttribute("jtype", "java_lang_Float");
        node.setAttribute("size", size);
        
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
	
}
