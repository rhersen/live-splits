package name.hersen.livesplits;

import org.apache.xerces.parsers.DOMParser;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

@Service
public class XmlHelper {
    DOMParser createParser(Reader xml) throws SAXException, IOException {
        DOMParser p = new DOMParser();
        p.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        p.parse(new InputSource(xml));
        return p;
    }

    String getText(Node parent) {
        return parent == null ? null : parent.getFirstChild().getTextContent();
    }

    Deque<Node> getChildren(NodeList list) {
        Deque<Node> r = new ArrayDeque<Node>();
        for (int i = 0; i < list.getLength(); i++) {
            r.add(list.item(i));
        }
        return r;
    }

    Collection<Node> getChildren(Node node) {
        return getChildren(node.getChildNodes());
    }

    boolean hasNodeName(Node node, String name) {
        return node.getNodeName().equalsIgnoreCase(name);
    }

    Node getChild(Node parent, String name) {
        for (Node child : getChildren(parent)) {
            if (hasNodeName(child, name)) {
                return child;
            }
        }
        return null;
    }

    String getFullName(Node n) {
        return getText(getChild(n, "Given")) + " " + getText(getChild(n, "Family"));
    }

    Iterable<Node> getChildrenWithName(Node parent, String nodeName) {
        Collection<Node> r = new ArrayDeque<Node>();
        for (Node child : getChildren(parent)) {
            if (hasNodeName(child, nodeName)) {
                r.add(child);
            }
        }
        return r;
    }
}
