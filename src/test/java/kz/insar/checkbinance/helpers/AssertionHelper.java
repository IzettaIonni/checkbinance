package kz.insar.checkbinance.helpers;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.containers.RecentTradesWithSymbol;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import lombok.AllArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@AllArgsConstructor
public class AssertionHelper {

    private ConverterHelper converterHelper;

    public AssertionHelper() {
        this(new ConverterHelper());
    }

    public void lastPriceAssertion(List<SymbolPriceDTO> symbolPriceDTOList,
                                   List<TestSymbol> testSymbolList,
                                   List<LastPriceDTO> actual) {
        var convertedExpected = converterHelper.toLastPriceDTO(symbolPriceDTOList,
                testSymbolList);

        for (int i = 0; i < convertedExpected.size(); i++) {
            assertThat(actual.get(i).getTime())
                    .isCloseTo(convertedExpected.get(i).getTime(), within(10, ChronoUnit.SECONDS));
        }
        assertThat(convertedExpected).usingRecursiveFieldByFieldElementComparatorIgnoringFields("time")
                .containsExactlyElementsOf(actual);
    }

    public void legacyLastPriceAssertion(List<RecentTradesWithSymbol> recentTradesWithSymbolList,
                                         List<TestSymbol> testSymbolList,
                                         List<LastPriceDTO> actual) {
        var expected = converterHelper.
                convertRecentTradesWithSymbol(testSymbolList, recentTradesWithSymbolList);

        assertThat(actual).containsExactlyElementsOf(expected);
    }

}
