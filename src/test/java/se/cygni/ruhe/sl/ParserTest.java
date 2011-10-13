package se.cygni.ruhe.sl;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    private Parser target;

    @Before
    public void setUp() throws Exception {
        target = new Parser();
    }

    @Test
    public void testInvoke() throws Exception {
        List<ClassResult> classes = testParse("result.xml");
        assertEquals(3, classes.size());

        ClassResult m5 = classes.get(0);
        assertEquals("ÖM5", m5.getName());
        assertEquals("Kjell Ohlsson", m5.getList().get(0).getName());
        assertEquals("Fred Wennang", m5.getList().get(2).getName());
        assertEquals("33:54", m5.getList().get(2).getTime());

        ClassResult m3 = classes.get(1);
        assertEquals("ÖM3", m3.getName());
        assertEquals("Anneli Blomdahl", m3.getList().get(0).getName());
        assertEquals("Filip Hatlen", m3.getList().get(2).getName());
    }

    private List<ClassResult> testParse(String file) throws IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream("/" + file);
        return target.parse(new InputStreamReader(stream, "UTF-8"));
    }

}
