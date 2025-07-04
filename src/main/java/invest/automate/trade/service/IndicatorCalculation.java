package invest.automate.trade.service;

import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

@Service
public class IndicatorCalculation {

    public Strategy buildEmaCrossoverStrategy(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 21);

        Rule entryRule = new CrossedUpIndicatorRule(shortEma, longEma);
        Rule exitRule = new CrossedDownIndicatorRule(shortEma, longEma);

        return new BaseStrategy(entryRule, exitRule);
    }
}
