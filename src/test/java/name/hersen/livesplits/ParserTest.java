package name.hersen.livesplits;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {

    private Parser target;

    @Before
    public void setUp() throws Exception {
        target = new Parser();
    }

    @Test
    public void result() throws Exception {
        List<ClassResult> classes = testParseResultList("result.xml");
        assertEquals(3, classes.size());

        ClassResult m5 = classes.get(0);
        assertEquals("ÖM5", m5.getName());
        assertEquals("Kjell Ohlsson", m5.getList().get(0).getName());
        FormattedCompetitor competitor = m5.getList().get(2);
        assertEquals("Fred Wennang", competitor.getName());
        assertEquals("33.54", competitor.getTime());

        ClassResult m3 = classes.get(1);
        assertEquals("ÖM3", m3.getName());
        assertEquals("Anneli Blomdahl", m3.getList().get(0).getName());
        assertEquals("Filip Hatlen", m3.getList().get(2).getName());
    }
    
    @Test
    public void splits() throws Exception {
        List<ClassResult> classes = testParseResultList("splits.xml");
        assertEquals(9, classes.size());

        ClassResult h16 = classes.get(0);
        assertEquals("H16", h16.getName());
        assertEquals("Niklas Sundqvist", h16.getList().get(2).getName());
        assertEquals("41.47", h16.getList().get(3).getTime());
        assertEquals("Emil Andersson", h16.getList().get(4).getName());
        assertEquals("45.40", h16.getList().get(4).getTime());

        ClassResult d16 = classes.get(1);
        assertEquals("D16", d16.getName());
        assertEquals("Alexandra Lengquist", d16.getList().get(2).getName());
        assertEquals("Elsa Rajala", d16.getList().get(3).getName());
        assertEquals("MisPunch", d16.getList().get(3).getTime());

        ClassResult l = classes.get(3);
        assertEquals("Ebba Leickt", l.getList().get(2).getName());
        FormattedCompetitor competitor = l.getList().get(1);
        assertEquals("Amanda Berggren", competitor.getName());
        assertEquals("26.02", competitor.getTime());
        assertEquals("11.52", competitor.getSplits().get(4).getTime());
    }
    @Test
    public void splitsShouldHaveControls() throws Exception {
        List<ClassResult> classes = testParseResultList("splits.xml");
        assertEquals(9, classes.size());

        ClassResult d10 = classes.get(4);
        assertEquals("Klara Bolin", d10.getList().get(0).getName());
        FormattedCompetitor competitor = d10.getList().get(1);
        assertEquals("Tilda Andersson", competitor.getName());
        assertEquals("23493", competitor.getId());
        assertEquals("20.05", competitor.getTime());
        assertEquals("10.35", competitor.getSplits().get(4).getTime());

        FormattedSplit split = competitor.getSplits().get(4);
        assertEquals("10.35", split.getTime());
        Control control = split.getControl();
        assertEquals("55", control.getCode());

        split = competitor.getSplits().get(0);
        assertEquals("00.00", split.getTime());
        control = split.getControl();
        assertEquals("S1", control.getCode());

        split = competitor.getSplits().get(8);
        assertEquals("20.05", split.getTime());
        control = split.getControl();
        assertEquals("M1", control.getCode());
    }

    @Test
    public void courses() throws Exception {
        List controls = testParseCourseData("courses.xml");
        assertEquals(28, controls.size());
        Control first = (Control) controls.get(1);
        assertEquals("31", first.getCode());
        assertTrue(first.getX() > 0);
        assertTrue(first.getY() > 0);
        assertEquals("S1", ((Control) controls.get(0)).getCode());
        assertEquals("M1", ((Control) controls.get(27)).getCode());
    }

    private List<ClassResult> testParseResultList(String file) throws IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream("/" + file);
        return target.parseResultList(new InputStreamReader(stream, "UTF-8"), testParseCourseData("courses.xml"));
    }
    private List testParseCourseData(String file) throws IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream("/" + file);
        return target.parseCourseData(new InputStreamReader(stream, "UTF-8"));
    }

}
