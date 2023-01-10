package org.matsim.BachelorArbeit;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class BachelorEventHandler implements LinkEnterEventHandler {

    public HashMap<String, LinkedList<String>> cars = new HashMap<>();

    double time;
    Id<Link> linkID;
    Id<Vehicle> vehicleID;

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();

    Element root = doc.createElementNS("BachelorArbeit", "Traffic");

    public BachelorEventHandler() throws ParserConfigurationException {
        doc.appendChild(root);
    }


    @Override
    public void handleEvent(LinkEnterEvent event) {

        if(Character.isDigit(event.getVehicleId().toString().charAt(0))==true) {
            if (!cars.containsKey(event.getVehicleId().toString())) {

                LinkedList<String> newRouteFirst = new LinkedList<>();
                newRouteFirst.add(event.getLinkId().toString());
                cars.put(event.getVehicleId().toString(), newRouteFirst);

            } else {

                LinkedList<String> newRoute = cars.get(event.getVehicleId().toString());
                newRoute.add(event.getLinkId().toString());
                cars.put(event.getVehicleId().toString(), newRoute);

            }
        }

    }

    public void mapToXml(){


        for (Map.Entry<String, LinkedList<String>> pair : cars.entrySet()) {
            Element model = doc.createElement("person");
            model.setAttribute("id", pair.getKey() );

            Node plan = doc.createElement("plan");
            model.appendChild(plan);

            Node act = doc.createElement("act");
            plan.appendChild(act);

            Node leg = doc.createElement("leg");
            plan.appendChild(leg);

            Node route = doc.createElement("route");
            leg.appendChild(route);

            for(int i=0;i <cars.get(pair.getKey()).size();i++){

                String routeElement = (cars.get(pair.getKey()).get(i));
                route.appendChild(doc.createTextNode(routeElement));

                if(i!=cars.get(pair.getKey()).size()-1){
                    route.appendChild(doc.createTextNode(" "));
                }
            }

            root.appendChild(model);
            //System.out.println(String.format("Key (name) is: %s, Value (age) is : %s", pair.getKey(), pair.getValue()));
        }
    }


    public void createFile() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transf = transformerFactory.newTransformer();

        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transf.setOutputProperty(OutputKeys.INDENT, "yes");
        transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);


        File myFile = new File("C:\\Users\\Thien\\Desktop\\test für java text\\BachelorEventHandler.xml");

        StreamResult console = new StreamResult(System.out);
        StreamResult file = new StreamResult(myFile);
        transf.transform(source, console);
        transf.transform(source, file);
    }


    public double getTheTime(){
        return  time;
    }

    public Id<Link> getTheLinkID(){
        return  linkID;
    }

    public Id<Vehicle> getTheVehicleID(){
        return  vehicleID;
    }


    public void returnText(){
        System.out.println(cars);
    }
}
