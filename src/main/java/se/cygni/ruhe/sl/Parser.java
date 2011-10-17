package se.cygni.ruhe.sl;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
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
import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.collect;
import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.collections.CollectionUtils.select;

@SuppressWarnings({"unchecked"})
@Service
public class Parser {

    public List<ClassResult> parse(InputStreamReader html) throws IOException, SAXException {
        DOMParser neko = new DOMParser();
        neko.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        neko.parse(new InputSource(html));

        return getResults(neko);
    }

    private List<ClassResult> getResults(DOMParser neko) {
        NodeList classResults = neko.getDocument().getElementsByTagName("ClassResult");
        return (List<ClassResult>) collect(getChildren(classResults), createClass());
    }

    private Transformer createClass() {
        return new Transformer() {
            public Object transform(Object o) {
                Node node = (Node) o;
                ClassResult r = new ClassResult();
                r.setName(getChild(node, "ClassShortName").getFirstChild().getTextContent());
                Collection personResults = select(getChildren(node), hasNodeName("PersonResult"));
                r.setList((List<Competitor>) collect(personResults, createCompetitor()));
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

    private Transformer createCompetitor() {
        return new Transformer() {
            public Object transform(Object o) {
                Competitor r = new Competitor();
                r.setName(createName(getChild(getChild((Node) o, "Person"), "PersonName")));
                r.setTime(createTime(getChild(getChild((Node) o, "Result"), "Time")));
                return r;
            }
        };
    }

    private String createName(Node n) {
        return getChild(n, "Given").getFirstChild().getTextContent()
                + " "
                + getChild(n, "Family").getFirstChild().getTextContent();
    }

    private String createTime(Node n) {
        String r = n.getFirstChild().getTextContent();
        if (r.startsWith("00:")) {
            return r.substring(3);
        }
        return r;
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
