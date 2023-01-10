package org.matsim.BachelorArbeit;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


public class RunBachelorEventHandler {

    public static void main(String[] args) throws ParserConfigurationException, TransformerException {



        ////
        //String inputFile = "output/output_events.xml.gz";
        String inputFile = "C:\\Users\\Thien\\Documents\\GitHub\\matsim-kelheim\\output\\999Itera_ChangeSingleTripMode_output-kelheim-1pct\\kelheim.output_events.xml.gz";

        EventsManager eventsManager = EventsUtils.createEventsManager();

        BachelorEventHandler eventHandler = new BachelorEventHandler();
        eventsManager.addHandler(eventHandler);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        eventHandler.mapToXml();
        //eventHandler.returnText();
        eventHandler.createFile();
        /////






/*
        String inputFile = "C:\\Users\\Thien\\Documents\\GitHub\\matsim-kelheim\\output\\500Itera_ChangeSingleTripMode_output-kelheim-1pct\\kelheim.output_events.xml.gz";

        EventsManager eventsManager = EventsUtils.createEventsManager();

        TestEventHandlerMap eventHandler = new TestEventHandlerMap();
        eventsManager.addHandler(eventHandler);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        eventHandler.returnText();


 */


    }

}
