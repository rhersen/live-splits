package name.hersen.livesplits;

import org.apache.xerces.parsers.DOMParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

@Service
public class CourseParser {
    @Autowired
    private XmlHelper xmlHelper;

    public Deque<Control> parseCourseData(Reader xml) throws IOException, SAXException {
        return getControls(xmlHelper.createParser(xml));
    }

    private Deque<Control> getControls(DOMParser p) {
        Deque<Node> courseData = xmlHelper.getChildren(p.getDocument().getElementsByTagName("CourseData"));
        Collection<Node> children = xmlHelper.getChildren(courseData.getFirst());
        Deque<Control> r = new ArrayDeque<Control>();
        r.addAll(findControls(children, "StartPoint", "StartPointCode"));
        r.addAll(findControls(children, "Control", "ControlCode"));
        r.addAll(findControls(children, "FinishPoint", "FinishPointCode"));
        return r;
    }

    private Collection<Control> findControls(Iterable<Node> children, String parent, String child) {
        Collection<Control> r = new ArrayDeque<Control>();
        for (Node control : children) {
            if (xmlHelper.hasNodeName(control, parent)) {
                r.add(createControl(control, child));
            }
        }
        return r;
    }

    private Control createControl(Node node, String type) {
        String code = getCode(node, type);
        NamedNodeMap attributes = xmlHelper.getChild(node, "MapPosition").getAttributes();
        double x = getCoordinate(attributes, "x");
        double y = getCoordinate(attributes, "y");
        return new Control(code, x, y);
    }

    private String getCode(Node node, String type) {
        return xmlHelper.getText(xmlHelper.getChild(node, type)).trim();
    }

    private double getCoordinate(NamedNodeMap attributes, String coordinateName) {
        return Double.parseDouble(attributes.getNamedItem(coordinateName).getTextContent());
    }
}
