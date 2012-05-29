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
    @Autowired ResultFormatter resultFormatter;
    private final PeriodFormatter periodFormatter;

    public ResultParser() {
        periodParser = new PeriodFormatterBuilder().appendHours().appendLiteral(":").appendMinutes().appendLiteral(":").appendSeconds().toFormatter();
        periodFormatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparatorIfFieldsBefore(".")
                .appendMinutes()
                .appendSeparator(".")
                .minimumPrintedDigits(2)
                .printZeroAlways()
                .appendSeconds()
                .toFormatter();
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
                competitors.add(resultFormatter.format(getCompetitor(n, controls)));
            }
        }
        String classShortName = xml.getText(xml.getChild(classResult, "ClassShortName"));
        return new ClassResult(classShortName, competitors);
    }

    private Competitor getCompetitor(Node node, Deque<Control> controls) {
        Node person = xml.getChild(node, "Person");
        Node result = xml.getChild(node, "Result");
        Node statusNode = xml.getChild(result, "CompetitorStatus");

        String t = xml.getText(xml.getChild(result, "Time"));
        Period total = t != null ? Period.parse(t, periodParser) : new Period(0);
        Period time = t != null ? total : null;

        String status = statusNode.getAttributes().getNamedItem("value").getTextContent();
        List<Split> splits = getAllSplits(result, controls, total);
        String id = xml.getChild(person, "PersonId").getTextContent();
        String fullName = xml.getFullName(xml.getChild(person, "PersonName"));
        Deque<String> laps = getLaps(result, total);

        return new Competitor(fullName, time, status, splits, id, laps);
    }

    private List<Split> getAllSplits(Node parent, Deque<Control> controls, Period total) {
        List<Split> r = new ArrayList<Split>();
        Period p = new Period(0);
        r.add(new Split(controls.getFirst(), p));
        for (Node n : xml.getChildrenWithName(parent, "SplitTime")) {
            Node t = xml.getChild(n, "Time");
            if (t != null) {
                p = Period.parse(t.getTextContent(), periodParser);
                Control c = findByControlCode(controls, xml.getChild(n, "ControlCode").getTextContent());
                r.add(new Split(c, p));
            }
        }
        r.add(new Split(controls.getLast(), total));
        return r;
    }

    private Deque<String> getLaps(Node result, Period total) {
        Deque<String> laps = new ArrayDeque<String>();
        for (Period lap : getLaps0(result, total)) {
            laps.add(lap.toString(periodFormatter));
        }
        return laps;
    }

    private Iterable<Period> getLaps0(Node parent, Period total) {
        Collection<Period> r = new ArrayDeque<Period>();
        Period current = new Period(0);
        Period previous = current;
        for (Node splitTime : xml.getChildrenWithName(parent, "SplitTime")) {
            Node time = xml.getChild(splitTime, "Time");
            if (time != null) {
                current = Period.parse(time.getTextContent(), periodParser);
                r.add(minus(current, previous));
                previous = current;
            }
        }
        r.add(minus(total, previous));
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
