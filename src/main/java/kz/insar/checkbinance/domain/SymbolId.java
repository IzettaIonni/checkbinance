package kz.insar.checkbinance.domain;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class SymbolId {
    private final Integer id;

    private SymbolId(Integer id) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SymbolId[" + id + "]";
    }

    public static SymbolId of(@NonNull Integer id) {
        return new SymbolId(id);
    }

}
