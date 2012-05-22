package name.hersen.livesplits;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Deque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourseParserTest {

    private CourseParser target;

    @Before
    public void setUp() throws Exception {
        XmlHelper xmlHelper = new XmlHelper();

        CourseParser courseParser = new CourseParser();
        courseParser.xml = xmlHelper;

        target = courseParser;
    }

    @Test
    public void courses() throws Exception {
        Deque<Control> controls1 = testParseCourseData("courses.xml");
        ArrayList<Control> controls = new ArrayList<Control>(controls1);
        assertEquals(28, controls.size());
        Control first = controls.get(1);
        assertEquals("31", first.getCode());
        assertTrue(first.getX() > 0);
        assertTrue(first.getY() < first.getX());
        assertEquals("S1", controls.get(0).getCode());
        assertEquals("M1", controls.get(27).getCode());
    }

    private Deque<Control> testParseCourseData(String file) throws IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream("/" + file);
        return target.parseCourseData(new InputStreamReader(stream, "UTF-8"));
    }

}
