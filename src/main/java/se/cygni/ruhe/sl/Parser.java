package se.cygni.ruhe.sl;

import org.apache.xerces.parsers.DOMParser;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class Parser {

    public List<ClassResult> parse(InputStreamReader html) throws IOException, SAXException {
        DOMParser neko = new DOMParser();
        neko.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        neko.parse(new InputSource(html));

        return getStartDiv(neko);
    }

    private List<ClassResult> getStartDiv(DOMParser neko) {
        List<ClassResult> r = new ArrayList<ClassResult>();
        Document document = neko.getDocument();
        NodeList classResults = document.getElementsByTagName("ClassResult");
        for (int i = 0; i < classResults.getLength(); ++i) {
            Node classResult = classResults.item(i);
            r.add(createClass(classResult));
        }
        return r;
    }

    private ClassResult createClass(Node classResult) {
        ClassResult r = new ClassResult();
        NodeList childNodes = classResult.getChildNodes();
        List<Competitor> competitors = new ArrayList<Competitor>();
        r.setList(competitors);
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node item = childNodes.item(j);
            if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equalsIgnoreCase("ClassShortName")) {
                r.setName(item.getFirstChild().getTextContent());
            }
            if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equalsIgnoreCase("PersonResult")) {
                competitors.add(createCompetitor(item));
            }
        }

        return r;
    }

    private Competitor createCompetitor(Node personResult) {
        Competitor r = new Competitor();
        for (int i = 0; i < personResult.getChildNodes().getLength(); i++) {
            Node node = personResult.getChildNodes().item(i);
            findName(node, r);
            findTime(node, r);
        }
        return r;
    }

    private void findTime(Node node, Competitor r) {
        if (node.getNodeName().equalsIgnoreCase("Result")) {
            for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                Node item = node.getChildNodes().item(j);
                if (item.getNodeName().equalsIgnoreCase("Time")) {
                    r.setTime(createTime(item));
                }
            }
        }
    }

    private void findName(Node node, Competitor r) {
        if (node.getNodeName().equalsIgnoreCase("Person")) {
            for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                Node person = node.getChildNodes().item(j);
                if (person.getNodeName().equalsIgnoreCase("PersonName")) {
                    r.setName(createName(person));
                }
            }
        }
    }

    private String createTime(Node item) {
        String r = item.getFirstChild().getTextContent();
        if (r.startsWith("00:")) {
            return r.substring(3);
        }
        return r;
    }

    private String createName(Node node) {
        String given = "Vakant";
        String family = "";
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node item = node.getChildNodes().item(i);
            if (item.getNodeName().equalsIgnoreCase("Given")) {
                given = item.getFirstChild().getTextContent();
            }
            if (item.getNodeName().equalsIgnoreCase("Family")) {
                family = item.getFirstChild().getTextContent();
            }
        }
        return given + " " + family;
    }

}
