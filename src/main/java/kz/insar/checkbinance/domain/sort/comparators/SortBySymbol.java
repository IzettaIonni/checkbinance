package kz.insar.checkbinance.domain.sort.comparators;

import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;

import java.util.Comparator;

public class SortBySymbol implements Comparator<LastPriceColumns> {

    private final SortDirection dir;

    SortBySymbol(SortDirection dir) {
        this.dir = dir;
    }


    @Override
    public int compare(LastPriceColumns o1, LastPriceColumns o2) {

        return 0;
    }
}
