package kz.insar.checkbinance.repositories;

import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SymbolRepository extends JpaRepository<SymbolEntity, Integer> {
    default Optional<SymbolEntity> findById(SymbolId id) {
        return findById(id.getId());
    }

    //@Query(~OQL)
    Optional<SymbolEntity> findBySymbolName(String symbolName);
}
