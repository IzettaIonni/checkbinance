package kz.insar.checkbinance.domain.sort.comparators;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import lombok.Data;

import java.util.Comparator;

@Data
public class LastPriceDTOComparator implements Comparator<LastPriceDTO> {

    private final SortDirection dir;
    private final LastPriceColumns columns;
    private int result;

    public LastPriceDTOComparator(SortDirection dir, LastPriceColumns columns) {
        this.dir = dir;
        this.columns = columns;
    }


    @Override
    public int compare(LastPriceDTO o1, LastPriceDTO o2) {

        switch (columns) {
            case SYMBOL:
                result = o1.getSymbol().compareTo(o2.getSymbol());
                break;

            case ID:
                result = o1.getId().compareTo(o2.getId());
                break;

            case PRICE:
                result = o1.getPrice().compareTo(o2.getPrice());
                break;

            case TIME:
                result = o1.getTime().compareTo(o2.getTime());
                break;
        }

        if (dir == SortDirection.DESC) {
            result *= -1;
        }

        return result;
    }

}
