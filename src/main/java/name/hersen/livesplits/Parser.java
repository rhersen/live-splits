package name.hersen.livesplits;

import org.apache.xerces.parsers.DOMParser;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.stereotype.Service;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class Parser {

    private final PeriodFormatter periodParser;

    public Parser() {
        periodParser = new PeriodFormatterBuilder().appendHours().appendLiteral(":").appendMinutes().appendLiteral(":").appendSeconds().toFormatter();
    }

    public Collection<ClassResult> parseResultList(InputStreamReader xml, Deque<Control> controls) throws IOException, SAXException {
        return getClassResults(parseXmlReader(xml), controls);
    }

    public Deque<Control> parseCourseData(InputStreamReader xml) throws IOException, SAXException {
        return getControls(parseXmlReader(xml));
    }

    private DOMParser parseXmlReader(InputStreamReader xml) throws SAXException, IOException {
        DOMParser p = new DOMParser();
        p.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        p.parse(new InputSource(xml));
        return p;
    }

    private Collection<ClassResult> getClassResults(DOMParser p, Deque<Control> controls) {
        Collection<ClassResult> r = new ArrayDeque<ClassResult>();
        for (Node child : getChildren(p.getDocument().getElementsByTagName("ClassResult"))) {
            r.add(createClassResult(child, controls));
        }
        return r;
    }

    private Deque<Control> getControls(DOMParser p) {
        Collection<Node> children = getChildren(getChildren(p.getDocument().getElementsByTagName("CourseData")).iterator().next());
        Deque<Control> r = new ArrayDeque<Control>();
        r.addAll(findControls(children, "StartPoint", "StartPointCode"));
        r.addAll(findControls(children, "Control", "ControlCode"));
        r.addAll(findControls(children, "FinishPoint", "FinishPointCode"));
        return r;
    }

    private Collection<Control> findControls(Iterable<Node> children, String parent, String child) {
        Collection<Control> r = new ArrayDeque<Control>();
        for (Node control : children) {
            if (hasNodeName(parent, control)) {
                r.add(createControl(control, child));
            }
        }
        return r;
    }

    private Control createControl(Node n, String name) {
        NamedNodeMap attributes = getChild(n, "MapPosition").getAttributes();
        return new Control(getText(getChild(n, name)).trim(), Double.parseDouble(attributes.getNamedItem("x").getTextContent()), Double.parseDouble(attributes.getNamedItem("y").getTextContent()));
    }

    private ClassResult createClassResult(Node node, Deque<Control> controls) {
        List<FormattedCompetitor> r = new ArrayList<FormattedCompetitor>();
        for (Node personResult : getChildren(node)) {
            if (hasNodeName("PersonResult", personResult)) {
                r.add(formatCompetitor(personResult, controls));
            }
        }
        return new ClassResult(getText(getChild(node, "ClassShortName")), r);
    }

    private boolean hasNodeName(String name, Node node) {
        return node.getNodeName().equalsIgnoreCase(name);
    }

    private FormattedCompetitor formatCompetitor(Node node, Deque<Control> controls) {
        Node person = getChild(node, "Person");
        String time = createTime(getChild(getChild(node, "Result"), "Time"));
        Period total = new Period(0);
        Period time1 = null;
        if (time != null) {
            total = Period.parse(time, periodParser);
            time1 = total;
        }
        return new Competitor(createName(getChild(person, "PersonName")), time1, getChild(getChild(node, "Result"), "CompetitorStatus").getAttributes().getNamedItem("value").getTextContent(), getAllSplits(getChild(node, "Result"), controls, total), getChild(person, "PersonId").getTextContent()).format();
    }

    private List<Split> getAllSplits(Node result, Deque<Control> controls, Period total) {
        List<Split> r = new ArrayList<Split>();
        r.add(new Split(new Period(0), controls.iterator().next(), new Period(0)));
        Period previous = new Period();
        for (Node splitTime : selectByNodeName(result)) {
            Node time = getChild(splitTime, "Time");
            if (time != null) {
                Period period = Period.parse(time.getTextContent(), periodParser);
                r.add(new Split(period, findByControlCode(controls, getChild(splitTime, "ControlCode").getTextContent()), period.minus(previous).normalizedStandard()));
                previous = period;
            }
        }
        r.add(new Split(total, controls.getLast(), total.minus(previous).normalizedStandard()));
        return r;
    }

    private Iterable<Node> selectByNodeName(Node result) {
        Collection<Node> r = new ArrayList<Node>();
        for (Node child : getChildren(result)) {
            if (hasNodeName("SplitTime", child)) {
                r.add(child);
            }
        }
        return r;
    }

    private Control findByControlCode(Iterable<Control> controls, String controlCode) {
        for (Control control : controls) {
            if (control.getCode().equals(controlCode)) {
                return control;
            }
        }
        return null;
    }

    private String createName(Node n) {
        return getText(getChild(n, "Given")) + " " + getText(getChild(n, "Family"));
    }

    private String createTime(Node n) {
        return n == null ? null : getText(n);
    }

    private Node getChild(Node parent, String name) {
        for (Node child : getChildren(parent)) {
            if (hasNodeName(name, child)) {
                return child;
            }
        }
        return null;
    }

    private Collection<Node> getChildren(Node node) {
        return getChildren(node.getChildNodes());
    }

    private Collection<Node> getChildren(NodeList list) {
        Collection<Node> r = new ArrayList<Node>();
        for (int i = 0; i < list.getLength(); i++) {
            r.add(list.item(i));
        }
        return r;
    }

    private String getText(Node parent) {
        return parent.getFirstChild().getTextContent();
    }
}
