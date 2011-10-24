package name.hersen.livesplits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

@Controller
public class SplitsController implements ServletContextAware {

    @Autowired
    Parser parser;
    private ServletContext servletContext;

    @RequestMapping(value = "/r", method = RequestMethod.GET)
    public String getResults(Model model) throws IOException, SAXException {
        model.addAttribute("classes", parseSplits());
        return "results";
    }

    @RequestMapping(value = "/s", method = RequestMethod.GET)
    public String getSplits(Model model) throws IOException, SAXException {
        model.addAttribute("classes", parseSplits());
        return "splits";
    }

    @RequestMapping(value = "/c", method = RequestMethod.GET)
    public String getCompetitor(@RequestParam String id, Model model) throws IOException, SAXException {
        List<ClassResult> classResults = parseSplits();
        model.addAttribute("id", id);
        model.addAttribute("competitor", findCompetitor(id, classResults));
        model.addAttribute("classes", classResults);
        return "course";
    }

    @RequestMapping(value = "/splits", method = RequestMethod.GET)
    @ResponseBody
    public FormattedCompetitor getSplits(@RequestParam String id) throws IOException, SAXException {
        return findCompetitor(id, parseSplits());
    }

    private FormattedCompetitor findCompetitor(String id, List<ClassResult> classResults) {
        for (ClassResult classResult : classResults) {
            List<FormattedCompetitor> competitors = classResult.getList();
            for (FormattedCompetitor competitor : competitors) {
                if (competitor.getId().equals(id)) {
                    return competitor;
                }
            }
        }

        throw new IllegalStateException(id + " not found");
    }

    private List<ClassResult> parseSplits() throws IOException, SAXException {
        List controls = parser.parseCourseData(getXml("courses"));
        return parser.parseResultList(getXml("splits"), controls);
    }

    private InputStreamReader getXml(String name) throws IOException {
        URL url = servletContext.getResource("/WEB-INF/" + name + ".xml");
        return new InputStreamReader(url.openStream(), "UTF-8");
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
