package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.domain.exeptions.InvalidDataException;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.sort.params.SortParams;
import org.springframework.stereotype.Service;

@Service
public class ControllerConverter {

    public SortParams<LastPriceColumns> toSort(LastPriceColumns sortKey, SortDirection sortDir) {
        return new SortParams<LastPriceColumns>(sortKey, sortDir);
    }

}
