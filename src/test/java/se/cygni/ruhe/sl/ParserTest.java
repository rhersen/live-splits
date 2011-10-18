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
    public void result() throws Exception {
        List<ClassResult> classes = testParse("result.xml");
        assertEquals(3, classes.size());

        ClassResult m5 = classes.get(0);
        assertEquals("ÖM5", m5.getName());
        assertEquals("Kjell Ohlsson", m5.getList().get(0).getName());
        assertEquals("Fred Wennang", m5.getList().get(2).getName());
        assertEquals("33.54", m5.getList().get(2).getTimeString());

        ClassResult m3 = classes.get(1);
        assertEquals("ÖM3", m3.getName());
        assertEquals("Anneli Blomdahl", m3.getList().get(0).getName());
        assertEquals("Filip Hatlen", m3.getList().get(2).getName());
    }
    
    @Test
    public void splits() throws Exception {
        List<ClassResult> classes = testParse("splits.xml");
        assertEquals(4, classes.size());

        ClassResult m5 = classes.get(0);
        assertEquals("Korta", m5.getName());
        assertEquals("Olle Andersson", m5.getList().get(2).getName());
        assertEquals("50" + "." + "00", m5.getList().get(3).getTimeString());
        assertEquals("Magdalena van de Voorde", m5.getList().get(5).getName());
        assertEquals("51.30", m5.getList().get(5).getTimeString());
        assertEquals("Alexandra Lengquist", m5.getList().get(6).getName());
        assertEquals("MisPunch", m5.getList().get(6).getTimeString());

        ClassResult m3 = classes.get(1);
        assertEquals("Mellan", m3.getName());
        assertEquals("Andreas Hultqvist", m3.getList().get(0).getName());
        assertEquals("51.01", m3.getList().get(0).getTimeString());
        assertEquals("Emil Andersson", m3.getList().get(2).getName());
        assertEquals("Daniel Bengtsson", m3.getList().get(9).getName());
        assertEquals("1.03.39", m3.getList().get(9).getTimeString());
        assertEquals("Staffan Persson", m3.getList().get(11).getName());
        assertEquals("1.00.04", m3.getList().get(11).getSplitStrings().get(9));

        ClassResult l = classes.get(3);
        assertEquals("Sina Tommer", l.getList().get(2).getName());
        Competitor competitor = l.getList().get(0);
        assertEquals("Amanda Berggren", competitor.getName());
        assertEquals("31.29", competitor.getTimeString());
        assertEquals("11.17", competitor.getSplitStrings().get(0));
    }

    private List<ClassResult> testParse(String file) throws IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream("/" + file);
        return target.parse(new InputStreamReader(stream, "UTF-8"));
    }

}
