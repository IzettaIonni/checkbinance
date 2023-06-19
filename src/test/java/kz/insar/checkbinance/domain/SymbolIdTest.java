package kz.insar.checkbinance.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolIdTest {

    static final Integer ID = 123;
    private SymbolId service;



    @BeforeEach
    void setUp() {
        service = new SymbolId(ID);
    }

    @Test
    void testCtor1_ShouldThrowIfNullId() {
        var e = assertThrows(NullPointerException.class, () -> new SymbolId(null));
        assertEquals("id must not be null", e.getMessage());
    }

    @Test
    void testCtor2_SetIdEqualsRealId() {
        service = new SymbolId(44);
        assertEquals(44, service.getId(), "Id mismatched");
    }

    @Test
    void testToString() {
        //given

        //when
        var actual = service.toString();
        //then
        var expected = "SymbolId[" + ID + "]";
        assertEquals(expected, actual);
    }

    @Test
    void testEquals_SpecialCases() {
        assertTrue(service.equals(service));
        assertFalse(service.equals(null));
        assertFalse(service.equals(this));
    }

    @Test
    void testHashCode_SpecialCases() {
        assertEquals(service.hashCode(), service.hashCode());
    }

    @Test
    void testEqualsAndHashCode() {
        var a = new SymbolId(12);
        var b = new SymbolId(12);
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testOf_ShouldReturnSymbolIdObject() {
        var actual = SymbolId.of(ID);
        var expected = new SymbolId(44);
        assertEquals(expected, actual, ".of method creates object with wrong id");
    }
}
