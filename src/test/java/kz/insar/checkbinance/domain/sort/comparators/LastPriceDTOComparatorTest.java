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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LastPriceDTOComparatorTest {

    ArrayList<LastPriceDTO> lastPricesArray;

    @BeforeEach
    void setUp() {
        lastPricesArray = new ArrayList<>();

        LastPriceDTO a = new LastPriceDTO();
            a.setId(1);
            a.setPrice(BigDecimal.valueOf(20));
            a.setSymbol("C");
            a.setTime(LocalDateTime.ofEpochSecond(1687145938, 0, ZoneOffset.UTC));

        LastPriceDTO c = new LastPriceDTO();
            c.setId(3);
            c.setPrice(BigDecimal.valueOf(30));
            c.setSymbol("A");
            c.setTime(LocalDateTime.ofEpochSecond(1687145958, 0, ZoneOffset.UTC));

        LastPriceDTO b = new LastPriceDTO();
            b.setId(2);
            b.setPrice(BigDecimal.valueOf(10));
            b.setSymbol("B");
            b.setTime(LocalDateTime.ofEpochSecond(1687145948, 0, ZoneOffset.UTC));

        lastPricesArray.add(a);
        lastPricesArray.add(b);
        lastPricesArray.add(c);
    }

    @Test
    void testSortBySymbolASC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.SYMBOL);
        lastPricesArray.sort(comparator);
        assertEquals("A", lastPricesArray.get(0).getSymbol());
        assertEquals("B", lastPricesArray.get(1).getSymbol());
        assertEquals("C", lastPricesArray.get(2).getSymbol());
    }

    @Test
    void testSortBySymbolDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.SYMBOL);
        lastPricesArray.sort(comparator);
        assertEquals("C", lastPricesArray.get(0).getSymbol());
        assertEquals("B", lastPricesArray.get(1).getSymbol());
        assertEquals("A", lastPricesArray.get(2).getSymbol());
    }

    @Test
    void testSortByIdASC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.ID);
        lastPricesArray.sort(comparator);
        assertEquals(1, lastPricesArray.get(0).getId());
        assertEquals(2, lastPricesArray.get(1).getId());
        assertEquals(3, lastPricesArray.get(2).getId());
    }

    @Test
    void testSortByIdDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.ID);
        lastPricesArray.sort(comparator);
        assertEquals(3, lastPricesArray.get(0).getId());
        assertEquals(2, lastPricesArray.get(1).getId());
        assertEquals(1, lastPricesArray.get(2).getId());
    }

    @Test
    void testSortByPriceASC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.PRICE);
        lastPricesArray.sort(comparator);
        assertEquals(BigDecimal.valueOf(10), lastPricesArray.get(0).getPrice());
        assertEquals(BigDecimal.valueOf(20), lastPricesArray.get(1).getPrice());
        assertEquals(BigDecimal.valueOf(30), lastPricesArray.get(2).getPrice());
    }

    @Test
    void testSortByPriceDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.PRICE);
        lastPricesArray.sort(comparator);
        assertEquals(BigDecimal.valueOf(30), lastPricesArray.get(0).getPrice());
        assertEquals(BigDecimal.valueOf(20), lastPricesArray.get(1).getPrice());
        assertEquals(BigDecimal.valueOf(10), lastPricesArray.get(2).getPrice());
    }

    @Test
    void testSortByLocalDateTimeASC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.TIME);
        lastPricesArray.sort(comparator);
        assertEquals(LocalDateTime.ofEpochSecond(1687145938, 0, ZoneOffset.UTC),
                lastPricesArray.get(0).getTime());
        assertEquals(LocalDateTime.ofEpochSecond(1687145948, 0, ZoneOffset.UTC),
                lastPricesArray.get(1).getTime());
        assertEquals(LocalDateTime.ofEpochSecond(1687145958, 0, ZoneOffset.UTC),
                lastPricesArray.get(2).getTime());
    }

    @Test
    void testSortByLocalDateTimeDESC() {
        LastPriceDTOComparator comparator = new LastPriceDTOComparator(SortDirection.DESC, LastPriceColumns.TIME);
        lastPricesArray.sort(comparator);
        assertEquals(LocalDateTime.ofEpochSecond(1687145958, 0, ZoneOffset.UTC),
                lastPricesArray.get(0).getTime());
        assertEquals(LocalDateTime.ofEpochSecond(1687145948, 0, ZoneOffset.UTC),
                lastPricesArray.get(1).getTime());
        assertEquals(LocalDateTime.ofEpochSecond(1687145938, 0, ZoneOffset.UTC),
                lastPricesArray.get(2).getTime());
    }

}
