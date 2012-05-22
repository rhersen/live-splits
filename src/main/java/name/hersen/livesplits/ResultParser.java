package name.hersen.livesplits;

import org.apache.xerces.parsers.DOMParser;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

@Service
public class ResultParser {

    private final PeriodFormatter periodParser;
    @Autowired XmlHelper xml;
    @Autowired CourseParser courseParser;

    public ResultParser() {
        periodParser = new PeriodFormatterBuilder().appendHours().appendLiteral(":").appendMinutes().appendLiteral(":").appendSeconds().toFormatter();
    }

    public Collection<ClassResult> parseResultList(Reader splits, Reader courses) throws IOException, SAXException {
        Deque<Control> controls = courseParser.parseCourseData(courses);
        return getClassResults(xml.createParser(splits), controls);
    }

    private Collection<ClassResult> getClassResults(DOMParser p, Deque<Control> controls) {
        Collection<ClassResult> r = new ArrayDeque<ClassResult>();
        for (Node n : xml.getChildren(p.getDocument().getElementsByTagName("ClassResult"))) {
            r.add(createClassResult(n, controls));
        }
        return r;
    }

    private ClassResult createClassResult(Node classResult, Deque<Control> controls) {
        List<FormattedCompetitor> competitors = new ArrayList<FormattedCompetitor>();
        for (Node n : xml.getChildren(classResult)) {
            if (xml.hasNodeName(n, "PersonResult")) {
                competitors.add(formatCompetitor(n, controls));
            }
        }
        String classShortName = xml.getText(xml.getChild(classResult, "ClassShortName"));
        return new ClassResult(classShortName, competitors);
    }

    private FormattedCompetitor formatCompetitor(Node node, Deque<Control> controls) {
        Node person = xml.getChild(node, "Person");

        String t = xml.getText(xml.getChild(xml.getChild(node, "Result"), "Time"));
        Period total = new Period(0);
        Period time = null;
        if (t != null) {
            total = Period.parse(t, periodParser);
            time = total;
        }

        Node statusNode = xml.getChild(xml.getChild(node, "Result"), "CompetitorStatus");
        String status = statusNode.getAttributes().getNamedItem("value").getTextContent();
        List<Split> splits = getAllSplits(xml.getChild(node, "Result"), controls, total);
        String id = xml.getChild(person, "PersonId").getTextContent();
        String fullName = xml.getFullName(xml.getChild(person, "PersonName"));
        Competitor r = new Competitor(fullName, time, status, splits, id);
        return r.format();
    }

    private List<Split> getAllSplits(Node parent, Deque<Control> controls, Period total) {
        List<Split> r = new ArrayList<Split>();
        Period period = new Period(0);
        r.add(new Split(controls.getFirst(), period, period));
        Period previous = period;
        for (Node splitTime : xml.getChildrenWithName(parent, "SplitTime")) {
            Node time = xml.getChild(splitTime, "Time");
            if (time != null) {
                period = Period.parse(time.getTextContent(), periodParser);
                Control control = findByControlCode(controls, xml.getChild(splitTime, "ControlCode").getTextContent());
                r.add(new Split(control, period, minus(period, previous)));
                previous = period;
            }
        }
        r.add(new Split(controls.getLast(), total, minus(total, previous)));
        return r;
    }

    private Period minus(Period period, ReadablePeriod previous) {
        return period.minus(previous).normalizedStandard();
    }

    private Control findByControlCode(Iterable<Control> controls, String controlCode) {
        for (Control r : controls) {
            if (r.getCode().equals(controlCode)) {
                return r;
            }
        }
        return null;
    }

}
