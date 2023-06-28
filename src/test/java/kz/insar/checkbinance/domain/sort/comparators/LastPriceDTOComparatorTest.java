package kz.insar.checkbinance.domain.sort.comparators;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LastPriceDTOComparatorTest {

    ArrayList<LastPriceDTO> lastPricesArray;

    LastPriceDTO a, b, c;

    @BeforeEach
    void setUp() {
        lastPricesArray = new ArrayList<>();

        a = LastPriceDTO.builder()
                .id(1)
                .price(20L)
                .symbol("C")
                .time(1687145938L)
                .build();


        b = LastPriceDTO.builder()
                .id(3)
                .price(30L)
                .symbol("A")
                .time(1687145958L)
                .build();

        c = LastPriceDTO.builder()
                .id(2)
                .price(10L)
                .symbol("B")
                .time(1687145948L)
                .build();

        lastPricesArray.add(a);
        lastPricesArray.add(b);
        lastPricesArray.add(c);
    }

    @Test
    void testSortBySymbolASC() {
        //given
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.SYMBOL);

        //when
        lastPricesArray.sort(comparator);

        //then
        var expected = List.of(b,c,a);
        assertEquals(expected, lastPricesArray);
    };

    @Test
    void testSortBySymbolDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.SYMBOL);

        lastPricesArray.sort(comparator);

        var expected = List.of(a,c,b);
        assertEquals(expected, lastPricesArray);
    }

    @Test
    void testSortByIdASC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.ID);

        lastPricesArray.sort(comparator);

        var expected = List.of(a,c,b);
        assertEquals(expected, lastPricesArray);
    }

    @Test
    void testSortByIdDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.ID);

        lastPricesArray.sort(comparator)
        ;
        var expected = List.of(b,c,a);
        assertEquals(expected, lastPricesArray);
    }

    @Test
    void testSortByPriceASC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.PRICE);

        lastPricesArray.sort(comparator);

        var expected = List.of(c,a,b);
        assertEquals(expected, lastPricesArray);
    }

    @Test
    void testSortByPriceDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.PRICE);

        lastPricesArray.sort(comparator);

        var expected = List.of(b,a,c);
        assertEquals(expected, lastPricesArray);
    }

    @Test
    void testSortByLocalDateTimeASC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.TIME);

        lastPricesArray.sort(comparator);

        var expected = List.of(a,c,b);
        assertEquals(expected, lastPricesArray);
    }

    @Test
    void testSortByLocalDateTimeDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.TIME);

        lastPricesArray.sort(comparator);

        var expected = List.of(b,c,a);
        assertEquals(expected, lastPricesArray);
    }

}
