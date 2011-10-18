package se.cygni.ruhe.sl;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.xerces.parsers.DOMParser;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.stereotype.Service;
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

    public List<ClassResult> parse(InputStreamReader html) throws IOException, SAXException {
        DOMParser neko = new DOMParser();
        neko.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        neko.parse(new InputSource(html));

        return getResults(neko);
    }

    private List<ClassResult> getResults(DOMParser neko) {
        NodeList classResults = neko.getDocument().getElementsByTagName("ClassResult");
        return (List<ClassResult>) collect(getChildren(classResults), classResultTransformer());
    }

    private Transformer classResultTransformer() {
        return new Transformer() {
            public Object transform(Object o) {
                Node node = (Node) o;
                ClassResult r = new ClassResult();
                r.setName(getChild(node, "ClassShortName").getFirstChild().getTextContent());
                Collection personResults = select(getChildren(node), hasNodeName("PersonResult"));
                r.setList((List<Competitor>) collect(personResults, competitorTransformer()));
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

    private Transformer competitorTransformer() {
        return new Transformer() {
            public Object transform(Object o) {
                Node result = getChild((Node) o, "Result");
                Node node = getChild((Node) o, "Person");
                Competitor r = new Competitor();
                r.setName(createName(getChild(node, "PersonName")));
                String time = createTime(getChild(result, "Time"));
                if (time != null) {
                    r.setTime(createPeriod(time));
                }
                r.setStatus(getChild(result, "CompetitorStatus").getAttributes().getNamedItem("value").getTextContent());
                r.setSplits(getSplits(result));
                return r;
            }
        };
    }

    private Period createPeriod(String time) {
        return Period.parse(time, periodParser);
    }

    private Transformer periodTransformer() {
        return new Transformer() {
            public Object transform(Object o) {
                String time = (String) o;
                return Period.parse(time, periodParser);
            }
        };
    }

    private List getSplits(Node result) {
        Collection times = collect(select(getChildren(result), hasNodeName("SplitTime")), childTransformer("Time"));
        Collection strings = collect(select(times, NotNullPredicate.getInstance()), textTransformer());
        return (List) collect(strings, periodTransformer());
    }

    private Transformer textTransformer() {
        return new Transformer() {
            public Object transform(Object o) {
                Node n = (Node) o;
                return n.getFirstChild().getTextContent();
            }
        };
    }

    private Transformer childTransformer(final String name) {
        return new Transformer() {
            public Object transform(Object o) {
                Node n = (Node) o;
                return getChild(n, name);
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
