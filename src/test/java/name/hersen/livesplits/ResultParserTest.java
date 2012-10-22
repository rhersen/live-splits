package name.hersen.livesplits;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class ResultParserTest {

    private ResultParser target;

    @Before
    public void setUp() throws Exception {
        XmlHelper xmlHelper = new XmlHelper();

        ResultFormatter resultFormatter = new ResultFormatter();

        CourseParser courseParser = new CourseParser();
        setField(courseParser, "xmlHelper", xmlHelper);

        target = new ResultParser();
        setField(target, "xml", xmlHelper);
        setField(target, "courseParser", courseParser);
        setField(target, "resultFormatter", resultFormatter);
    }

    @Test
    public void result() throws Exception {
        List<ClassResult> classes = testParseResultList("splits-v3.xml");
        assertEquals(5, classes.size());

        ClassResult m5 = classes.get(0);
        assertEquals("ÖM4", m5.getName());
        assertEquals("Mattias Klintemar", m5.getList().get(0).getName());
        FormattedCompetitor competitor = m5.getList().get(2);
        assertEquals("Tommy Johansson", competitor.getName());
        assertEquals("MissingPunch", competitor.getTime());

        ClassResult m3 = classes.get(1);
        assertEquals("ÖM5", m3.getName());
        assertEquals("Jessica Sjöberg", m3.getList().get(0).getName());
        assertEquals("Anders Kärrström", m3.getList().get(2).getName());
    }
    
    @Test
    public void splits() throws Exception {
        List<ClassResult> classes = testParseResultList("splits-v3.xml");
        assertEquals(5, classes.size());

        ClassResult h16 = classes.get(0);
        assertEquals("ÖM4", h16.getName());
        assertEquals("Tommy Johansson", h16.getList().get(2).getName());
        assertEquals("MissingPunch", h16.getList().get(2).getTime());
        assertEquals("Irene Knutar", h16.getList().get(1).getName());
        assertEquals("35.08", h16.getList().get(1).getTime());

        ClassResult d16 = classes.get(1);
        assertEquals("ÖM5", d16.getName());
        assertEquals("Anders Kärrström", d16.getList().get(2).getName());
        assertEquals("Magnus Bolander", d16.getList().get(3).getName());
        assertEquals("36.04", d16.getList().get(3).getTime());

        ClassResult l = classes.get(3);
        assertEquals("Magnus Eriksson", l.getList().get(2).getName());
        FormattedCompetitor competitor = l.getList().get(1);
        assertEquals("Manfred Axelsson", competitor.getName());
        assertEquals("41.50", competitor.getTime());
        assertEquals("5.48", gs(competitor).get(4).getTime());
    }

    private ArrayList<FormattedSplit> gs(FormattedCompetitor competitor) {
        return new ArrayList<FormattedSplit>(competitor.getSplits());
    }

    @Test
    public void splitsShouldHaveControls() throws Exception {
        List<ClassResult> classes = testParseResultList("splits-v3.xml");
        assertEquals(5, classes.size());

        ClassResult d10 = classes.get(4);
        assertEquals("Oskar Forsberg", d10.getList().get(0).getName());
        FormattedCompetitor competitor = d10.getList().get(1);
        assertEquals("Mattias Samuelsson", competitor.getName());
        assertEquals("25", competitor.getId());
        assertEquals("22.27", competitor.getTime());
        assertEquals("10.47", gs(competitor).get(4).getTime());

        FormattedSplit split = gs(competitor).get(4);
        assertEquals("10.47", split.getTime());
        Control control = split.getControl();
        assertEquals("41", control.getCode());

        split = gs(competitor).get(5);
        assertEquals("14.19", split.getTime());

        split = gs(competitor).get(8);
        assertEquals("22.27", split.getTime());

        split = gs(competitor).get(0);
        assertEquals("", split.getTime());
        control = split.getControl();
        assertEquals("S1", control.getCode());

        split = gs(competitor).get(8);
        assertEquals("22.27", split.getTime());
        control = split.getControl();
        assertEquals("M1", control.getCode());
    }
    @Test
    public void splitsShouldHaveLaps() throws Exception {
        List<ClassResult> classes = testParseResultList("splits-v3.xml");
        assertEquals(5, classes.size());

        ClassResult d10 = classes.get(4);
        FormattedCompetitor competitor = d10.getList().get(1);
        List<String> laps = new ArrayList<String>(competitor.getLaps());
        assertEquals("1.08", laps.get(3));
        assertEquals("3.32", laps.get(4));
        assertEquals("2.09", laps.get(7));
    }

    private List<ClassResult> testParseResultList(String file) throws IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream("/" + file);
        InputStream stream2 = getClass().getResourceAsStream("/" + "courses.xml");
        Collection<ClassResult> r = target.parseResultList(new InputStreamReader(stream, "UTF-8"), new InputStreamReader(stream2, "UTF-8"));
        return new ArrayList<ClassResult>(r);
    }

}
