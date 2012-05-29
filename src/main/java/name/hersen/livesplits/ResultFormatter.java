package name.hersen.livesplits;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ResultFormatter {
    public FormattedCompetitor format(Competitor competitor) {
        List<FormattedSplit> formattedSplits = new ArrayList<FormattedSplit>();
        Collection<Split> splitList = competitor.getSplits();
        for (Split split : splitList) {
            if (split != null) {
                formattedSplits.add(format(split));
            }
        }
        return new FormattedCompetitor(competitor.getId(), competitor.getName(), competitor.getTime(), competitor.getStatus(), formattedSplits, competitor.getLaps());
    }

    public FormattedSplit format(Split split) {
        return new FormattedSplit(split.getTime(), split.getControl());
    }
}