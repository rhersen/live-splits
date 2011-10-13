package se.cygni.ruhe.sl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Controller
@RequestMapping(value = "/r")
public class StationController implements ServletContextAware {

    @Autowired
    Parser parser;
    private ServletContext servletContext;

    @RequestMapping(method = RequestMethod.GET)
    public String getStatic(Model model) throws IOException, SAXException {
//        URL url = new URL("http://mobilrt.sl.se/?tt=TRAIN&SiteId=" + id);
        URL url = servletContext.getResource("/WEB-INF/result.xml");

        model.addAttribute("classes", parser.parse(new InputStreamReader(url.openStream(), "UTF-8")));
        return "results";
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
