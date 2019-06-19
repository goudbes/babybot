package babybot;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * File: Actions
 * Author: Goudbes
 * Created: 04.06.2017 , 14.05
 */

class Func {

    /**
     *
     * @param x Wind degree
     * @return Wind direction
     */
    static String getWindDirect(double x) {
        String directions[] = {"nord", "nordaust", "aust", "s\u00F8raust", "s\u00F8r", "s\u00F8rvest", "vest", "nordvest", "nord"};
        return directions[(int) Math.round((( x % 360) / 45))];
    }

    /**
     * Formatting degrees
     * author: Akeshwar Jha
     * source: stackoverflow.com/a/38548560
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Formatted location in degrees
     */
    static String getFormattedLocationInDegree(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "E" : "W";

            return Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree + " " + Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {
            return "" + String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude);
        }
    }


    /**
     * Returns list of locations with max precipitation
     * @param document XML document
     * @return List of locations with max precipitation
     */
    static ArrayList<LocationPrecipitation> getMaxPrecipitation(Document document) {
        ArrayList<LocationPrecipitation> locations = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName("maximumPrecipitations");
        ArrayList<Node> places = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).hasChildNodes()) {
                NodeList location = nodeList.item(i).getChildNodes();
                for (int x = 0; x < location.getLength(); x++) {
                    if (location.item(x).hasAttributes())
                        places.add(location.item(x));
                }
            }
        }

        NamedNodeMap childAttributes;
        for (Node p: places) {
            if (p.hasAttributes()) {
                childAttributes = p.getAttributes();
                int climate = Integer.parseInt(childAttributes.getNamedItem("climate").getTextContent());
                String name = childAttributes.getNamedItem("name").getTextContent();
                String stationType = childAttributes.getNamedItem("station_type").getTextContent();
                int wmoide = Integer.parseInt(childAttributes.getNamedItem("wmoid").getTextContent());
                if (p.hasChildNodes()) {
                    NodeList innerChildren = p.getChildNodes();
                    Node innerChild = innerChildren.item(1);
                    if (innerChild.hasAttributes()) {
                        NamedNodeMap innerChildAttributes = innerChild.getAttributes();
                        double value = Double.parseDouble(innerChildAttributes.getNamedItem("value").getTextContent());
                        LocationPrecipitation l = new LocationPrecipitation(climate, name, value, stationType, wmoide);
                        locations.add(l);
                    }
                }
            }
        }
        locations.sort(Comparator.comparing(LocationPrecipitation::getValue).reversed());
        return locations;
    }

    /**
     * Locations with temperature
     * @param document XML document
     * @return list of locations
     */
    static ArrayList<LocationTemperature> getTemperatures(Document document) {
        ArrayList<LocationTemperature> locations = new ArrayList<>();

        NodeList maxTemperatureNodes = document.getElementsByTagName("highestTemperatures");
        NodeList minTemperatureNodes = document.getElementsByTagName("lowestTemperatures");

        ArrayList<Node> places = new ArrayList<>();
        for (int i = 0; i < maxTemperatureNodes.getLength(); i++) {
            if (maxTemperatureNodes.item(i).hasChildNodes()) {
                NodeList location = maxTemperatureNodes.item(i).getChildNodes();
                for (int j = 0; j < location.getLength(); j++) {
                    if (location.item(j).hasAttributes()) {
                        places.add(location.item(j));
                    }
                }
            }
        }

        for (int i = 0; i < minTemperatureNodes.getLength(); i++) {
            if (minTemperatureNodes.item(i).hasChildNodes()) {
                NodeList location = minTemperatureNodes.item(i).getChildNodes();
                for (int j = 0; j < location.getLength(); j++) {
                    if (location.item(j).hasAttributes()) {
                        places.add(location.item(j));
                    }
                }
            }
        }

        NamedNodeMap childAttributes;
        for (Node p: places) {
            if (p.hasAttributes()) {
                childAttributes = p.getAttributes();
                int climate = Integer.parseInt(childAttributes.getNamedItem("climate").getTextContent());
                String name = childAttributes.getNamedItem("name").getTextContent();
                String stationType = childAttributes.getNamedItem("station_type").getTextContent();
                int wmoide = Integer.parseInt(childAttributes.getNamedItem("wmoid").getTextContent());
                if (p.hasChildNodes()) {
                    NodeList innerChildren = p.getChildNodes();
                    Node innerChild = innerChildren.item(1);
                    if (innerChild.hasAttributes()) {
                        NamedNodeMap innerChildAttributes = innerChild.getAttributes();
                        double value = Double.parseDouble(innerChildAttributes.getNamedItem("value").getTextContent());
                        LocationTemperature l = new LocationTemperature(climate, name, value, stationType, wmoide);
                        locations.add(l);
                    }
                }
            }
        }
        locations.sort(Comparator.comparing(LocationTemperature::getValue).reversed());
        return locations;
    }
}


