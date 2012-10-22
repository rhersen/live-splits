package name.hersen.livesplits;

import org.apache.xerces.parsers.DOMParser;
import org.joda.time.*;
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
    @Autowired
    private XmlHelper xml;
    @Autowired
    private CourseParser courseParser;
    @Autowired
    private ResultFormatter resultFormatter;
    private final PeriodFormatter periodFormatter;
    private long sequence = 0L;

    public ResultParser() {
        periodParser = new PeriodFormatterBuilder().appendSeconds().appendSeparator(".").appendLiteral("0").toFormatter();
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
        for (Node node : xml.getChildren(p.getDocument().getElementsByTagName("ClassResult"))) {
            r.add(createClassResult(node, controls));
        }
        return r;
    }

    private ClassResult createClassResult(Node classResult, Deque<Control> controls) {
        List<FormattedCompetitor> competitors = new ArrayList<FormattedCompetitor>();
        for (Node node : xml.getChildren(classResult)) {
            if (xml.hasNodeName(node, "PersonResult")) {
                competitors.add(resultFormatter.format(getCompetitor(node, controls)));
            }
        }
        String classShortName = xml.getText(xml.getChild(xml.getChild(classResult, "Class"), "Name"));
        return new ClassResult(classShortName, competitors);
    }

    private Competitor getCompetitor(Node node, Deque<Control> controls) {
        Node person = xml.getChild(node, "Person");
        Node result = xml.getChild(node, "Result");

        String t = xml.getText(xml.getChild(result, "Time"));
        String status = xml.getText(xml.getChild(result, "Status"));
        double totalSeconds = t != null ? Double.valueOf(t) : 0.0;
        Duration total = Duration.standardSeconds((long) totalSeconds);
        Duration time = total.equals(Duration.ZERO) ? null : total;

        List<Split> splits = getAllSplits(result, controls, total.toPeriod());
        String id = getId(person);
        String fullName = xml.getFullName(xml.getChild(person, "Name"));
        Deque<String> laps = getLaps(result, total.toPeriod());

        return new Competitor(fullName, time, status, splits, id, laps);
    }

    private String getId(Node person) {
        Node id = xml.getChild(person, "Id");
        if (id == null) {
            return String.valueOf(++sequence);
        }
        return id.getTextContent();
    }

    private List<Split> getAllSplits(Node parent, Deque<Control> controls, Period total) {
        List<Split> r = new ArrayList<Split>();
        Duration p = new Duration(0L);
        r.add(new Split(controls.getFirst(), p.toPeriod()));
        for (Node node : xml.getChildrenWithName(parent, "SplitTime")) {
            Node t = xml.getChild(node, "Time");
            if (t != null) {
                String textContent = t.getTextContent();
                p = Duration.standardSeconds((long) Double.parseDouble(textContent));
                Control control = findByControlCode(controls, xml.getChild(node, "ControlCode").getTextContent());
                r.add(new Split(control, p.toPeriod()));
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
        Period current = new Period(0L);
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
