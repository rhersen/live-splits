package se.cygni.ruhe.sl;

import org.apache.commons.collections.ListUtils;
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

import static org.apache.commons.collections.CollectionUtils.*;

@SuppressWarnings({"unchecked"})
@Service
public class Parser {

    private PeriodFormatter periodParser;

    public List<ClassResult> parseResultList(InputStreamReader xml, List controls) throws IOException, SAXException {
        DOMParser p = new DOMParser();
        p.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        p.parse(new InputSource(xml));
        return getClassResults(p, controls);
    }

    public List parseCourseData(InputStreamReader xml) throws IOException, SAXException {
        DOMParser p = new DOMParser();
        p.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        p.parse(new InputSource(xml));
        return getControls(p);
    }

    private List<ClassResult> getClassResults(DOMParser p, List<Control> controls) {
        NodeList classResults = p.getDocument().getElementsByTagName("ClassResult");
        return (List<ClassResult>) collect(getChildren(classResults), classResultTransformer(controls));
    }

    private List<Control> getControls(DOMParser p) {
        NodeList courseDatas = p.getDocument().getElementsByTagName("CourseData");
        Node courseData = getChildren(courseDatas).iterator().next();
        Collection<Node> children = getChildren(courseData);
        List controlCodes = findControls(children, "Control", "ControlCode");
        List startPointCodes = findControls(children, "StartPoint", "StartPointCode");
        List finishPointCodes = findControls(children, "FinishPoint", "FinishPointCode");
        return ListUtils.union(ListUtils.union(startPointCodes, controlCodes), finishPointCodes);
    }

    private List<Control> findControls(Collection<Node> children, String parent, String child) {
        Collection controls = select(children, hasNodeName(parent));
        return (List) collect(controls, controlTransformer(child));
    }

    private Transformer classResultTransformer(final List<Control> controls) {
        return new Transformer() {
            public Object transform(Object o) {
                Node node = (Node) o;
                ClassResult r = new ClassResult();
                r.setName(getChild(node, "ClassShortName").getFirstChild().getTextContent());
                Collection personResults = select(getChildren(node), hasNodeName("PersonResult"));
                r.setList((List<Competitor>) collect(personResults, competitorTransformer(controls)));
                return r;
            }
        };
    }

    private Predicate hasNodeName(final String name) {
        return new Predicate() {
            public boolean evaluate(Object o) {
                Node item = (Node) o;
                return item.getNodeName().equalsIgnoreCase(name);
            }
        };
    }

    public Parser() {
        periodParser = new PeriodFormatterBuilder().appendHours().appendLiteral(":").appendMinutes().appendLiteral(":").appendSeconds().toFormatter();
    }

    private Transformer competitorTransformer(final List<Control> controls) {
        return new Transformer() {
            public Object transform(Object o) {
                Node result = getChild((Node) o, "Result");
                Node node = getChild((Node) o, "Person");
                Competitor r = new Competitor();
                r.setName(createName(getChild(node, "PersonName")));
                r.setId(getChild(node, "PersonId").getTextContent());
                String time = createTime(getChild(result, "Time"));
                if (time != null) {
                    r.setTime(createPeriod(time));
                }
                r.setStatus(getChild(result, "CompetitorStatus").getAttributes().getNamedItem("value").getTextContent());
                r.setSplits(getSplits(result, controls));
                return r;
            }
        };
    }

    private Period createPeriod(String time) {
        return Period.parse(time, periodParser);
    }

    private Transformer splitTransformer(final List<Control> controls) {
        return new Transformer() {
            public Object transform(Object o) {
                Node splitTime = (Node) o;
                Node time = getChild(splitTime, "Time");
                Node controlCode = getChild(splitTime, "ControlCode");
                if (time == null) {
                    return null;
                }
                Control control = findControl(controlCode.getTextContent(), controls);
                return new Split(Period.parse(time.getTextContent(), periodParser), control);
            }
        };
    }

    private Control findControl(String controlCode, List<Control> controls) {
        for (Control control : controls) {
            if (control.getCode().equals(controlCode)) {
                return control;
            }
        }
        throw new IllegalStateException(controlCode + " not found in " + controls);
    }

    private List getSplits(Node result, List<Control> controls) {
        Collection splitTimes = select(getChildren(result), hasNodeName("SplitTime"));
        return (List) collect(splitTimes, splitTransformer(controls));
    }

    private Transformer controlTransformer(final String name) {
        return new Transformer() {
            public Object transform(Object o) {
                Node n = (Node) o;
                String code = getChild(n, name).getFirstChild().getTextContent().trim();
                NamedNodeMap attributes = getChild(n, "ControlPosition").getAttributes();
                String x = attributes.getNamedItem("x").getTextContent();
                String y = attributes.getNamedItem("y").getTextContent();
                return new Control(code, Double.parseDouble(x), Double.parseDouble(y));
            }
        };
    }

    private String createName(Node n) {
        return getChild(n, "Given").getFirstChild().getTextContent()
                + " "
                + getChild(n, "Family").getFirstChild().getTextContent();
    }

    private String createTime(Node n) {
        if (n == null) {
            return null;
        }

        return n.getFirstChild().getTextContent();
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
}
