package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.domain.exeptions.InvalidDataException;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.sort.params.SortParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static kz.insar.checkbinance.domain.sort.params.LastPriceColumns.*;
import static kz.insar.checkbinance.domain.sort.params.SortDirection.*;

public class ControllerConverterTest {

    ControllerConverter service;

    @BeforeEach
    void setUp() {
         service = new ControllerConverter();
    }
    private static Stream<Arguments> dpSortParams() {
        List<Arguments> res = new LinkedList<>();


        res.add(Arguments.of(TIME, ASC));
        res.add(Arguments.of(ID, ASC));
        res.add(Arguments.of(SYMBOL, ASC));
        res.add(Arguments.of(PRICE, ASC));
        res.add(Arguments.of(TIME, DESC));
        res.add(Arguments.of(ID, DESC));
        res.add(Arguments.of(SYMBOL, DESC));
        res.add(Arguments.of(PRICE, DESC));

        return res.stream();
    }

    @ParameterizedTest
    @MethodSource("dpSortParams")
    void testToSort(LastPriceColumns key, SortDirection dir) {
        var actual = service.toSort(key, dir);

        var expected = new SortParams<LastPriceColumns>(key, dir);
        assertEquals(expected, actual);

    }

    @Test
    void testToSort_shouldThrowIfSortKeyInvalid() {
        var e = assertThrows(NullPointerException.class, ()-> service.toSort(null, SortDirection.ASC));
    }

    @Test
    void testToSort_shouldThrowIfSortDirInvalid() {
        var e = assertThrows(NullPointerException.class, ()-> service.toSort(LastPriceColumns.ID, null));
    }
}
