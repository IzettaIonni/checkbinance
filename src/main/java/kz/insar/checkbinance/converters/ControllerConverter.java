package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.domain.exeptions.InvalidDataException;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.sort.params.SortParams;
import org.springframework.stereotype.Service;

@Service
public class ControllerConverter {
    public SortParams<LastPriceColumns> toSort(String sortKey, String sortDir) {
        SortParams<LastPriceColumns> sortParams = null;
        sortKey = sortKey.toUpperCase();
        sortDir = sortDir.toUpperCase();
        switch (sortDir) {
            case "ASC":
                switch (sortKey) {

                    case "SYMBOL":
                        sortParams = new SortParams<>(LastPriceColumns.SYMBOL, SortDirection.ASC);
                        break;

                    case "ID":
                        sortParams = new SortParams<>(LastPriceColumns.ID, SortDirection.ASC);
                        break;

                    case "PRICE":
                        sortParams = new SortParams<>(LastPriceColumns.PRICE, SortDirection.ASC);
                        break;

                    case "TIME":
                        sortParams = new SortParams<>(LastPriceColumns.TIME, SortDirection.ASC);
                        break;

                }
                break;

            case "DESC":

                switch (sortKey) {

                    case "SYMBOL":
                        sortParams = new SortParams<>(LastPriceColumns.SYMBOL, SortDirection.DESC);
                        break;

                    case "ID":
                        sortParams = new SortParams<>(LastPriceColumns.ID, SortDirection.DESC);
                        break;

                    case "PRICE":
                        sortParams = new SortParams<>(LastPriceColumns.PRICE, SortDirection.DESC);
                        break;

                    case "TIME":
                        sortParams = new SortParams<>(LastPriceColumns.TIME, SortDirection.DESC);
                        break;

                }
                break;

            default:
                throw new InvalidDataException("sort parameters incorrect");
        }
        return sortParams;
    }
}
