package name.hersen.livesplits;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.*;
import static org.apache.commons.collections.ListUtils.union;

@SuppressWarnings({"unchecked"})
@Service
public class Parser {

    private PeriodFormatter periodParser;

    public Parser() {
        periodParser = new PeriodFormatterBuilder().appendHours().appendLiteral(":").appendMinutes().appendLiteral(":").appendSeconds().toFormatter();
    }

    public List<ClassResult> parseResultList(InputStreamReader xml, List controls) throws IOException, SAXException {
        return getClassResults(parseXmlReader(xml), controls);
    }

    public List parseCourseData(InputStreamReader xml) throws IOException, SAXException {
        return getControls(parseXmlReader(xml));
    }

    private DOMParser parseXmlReader(InputStreamReader xml) throws SAXException, IOException {
        DOMParser p = new DOMParser();
        p.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        p.parse(new InputSource(xml));
        return p;
    }

    private List<ClassResult> getClassResults(DOMParser p, List<Control> controls) {
        NodeList classResults = p.getDocument().getElementsByTagName("ClassResult");
        return (List<ClassResult>) collect(getChildren(classResults), classResultTransformer(controls));
    }

    private List<Control> getControls(DOMParser p) {
        NodeList courseDatas = p.getDocument().getElementsByTagName("CourseData");
        Node courseData = getChildren(courseDatas).iterator().next();
        Collection<Node> children = getChildren(courseData);
        List startPointCodes = findControls(children, "StartPoint", "StartPointCode");
        List controlCodes = findControls(children, "Control", "ControlCode");
        List finishPointCodes = findControls(children, "FinishPoint", "FinishPointCode");
        return union(union(startPointCodes, controlCodes), finishPointCodes);
    }

    private List<Control> findControls(Collection<Node> children, String parent, String child) {
        Collection controls = select(children, hasNodeName(parent));
        return (List) collect(controls, controlTransformer(child));
    }

    private Transformer controlTransformer(final String name) {
        return new Transformer() {
            public Object transform(Object o) {
                Node n = (Node) o;
                String code = getText(getChild(n, name)).trim();
                NamedNodeMap attributes = getChild(n, "MapPosition").getAttributes();
                String x = attributes.getNamedItem("x").getTextContent();
                String y = attributes.getNamedItem("y").getTextContent();
                return new Control(code, Double.parseDouble(x), Double.parseDouble(y));
            }
        };
    }

    private Transformer classResultTransformer(final List<Control> controls) {
        return new Transformer() {
            public Object transform(Object o) {
                Node node = (Node) o;
                String name = getText(getChild(node, "ClassShortName"));
                Collection personResults = select(getChildren(node), hasNodeName("PersonResult"));
                return new ClassResult(name, (List<FormattedCompetitor>) collect(personResults, competitorTransformer(controls)));
            }
        };
    }

    private Predicate hasNodeName(final String name) {
        return new Predicate() {
            public boolean evaluate(Object o) {
                return ((Node) o).getNodeName().equalsIgnoreCase(name);
            }
        };
    }

    private Transformer competitorTransformer(final List<Control> controls) {
        return new Transformer() {
            public Object transform(Object o) {
                Node result = getChild((Node) o, "Result");
                Node person = getChild((Node) o, "Person");
                Competitor r = new Competitor();
                r.setName(createName(getChild(person, "PersonName")));
                r.setId(getChild(person, "PersonId").getTextContent());
                String time = createTime(getChild(result, "Time"));
                Period total = new Period(0);
                if (time != null) {
                    total = Period.parse(time, periodParser);
                    r.setTime(total);
                }
                r.setStatus(getChild(result, "CompetitorStatus").getAttributes().getNamedItem("value").getTextContent());
                r.setSplits(getAllSplits(result, controls, total));
                return r.format();
            }
        };
    }

    private List getAllSplits(Node result, List<Control> controls, Period total) {
        Split start = new Split(new Period(0), controls.get(0));
        Split finish = new Split(total, controls.get(controls.size() - 1));
        return union(union(singletonList(start), getSplits(result, controls)), singletonList(finish));
    }

    private List getSplits(Node result, List<Control> controls) {
        Collection splitTimes = select(getChildren(result), hasNodeName("SplitTime"));
        return (List) collect(splitTimes, splitTransformer(controls));
    }

    private Transformer splitTransformer(final List<Control> controls) {
        return new Transformer() {
            public Object transform(Object o) {
                Node splitTime = (Node) o;
                Node time = getChild(splitTime, "Time");
                if (time == null) {
                    return null;
                }
                Node controlCode = getChild(splitTime, "ControlCode");
                String code = controlCode.getTextContent();
                Control control = (Control) find(controls, hasControlCode(code));
                return new Split(Period.parse(time.getTextContent(), periodParser), control);
            }
        };
    }

    private Predicate hasControlCode(final String controlCode) {
        return new Predicate() {
            public boolean evaluate(Object o) {
                Control control = (Control) o;
                return control.getCode().equals(controlCode);
            }
        };
    }

    private String createName(Node n) {
        return getText(getChild(n, "Given")) + " " + getText(getChild(n, "Family"));
    }

    private String createTime(Node n) {
        if (n == null) {
            return null;
        }

        return getText(n);
    }

    private Node getChild(Node parent, String name) {
        return (Node) find(getChildren(parent), hasNodeName(name));
    }

    private Collection<Node> getChildren(Node node) {
        return getChildren(node.getChildNodes());
    }

    private Collection<Node> getChildren(NodeList list) {
        Collection<Node> r = new ArrayList<Node>();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            r.add(n);
        }
        return r;
    }

    private String getText(Node parent) {
        return parent.getFirstChild().getTextContent();
    }
}
