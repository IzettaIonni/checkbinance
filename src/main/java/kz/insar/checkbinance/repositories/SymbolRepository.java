package kz.insar.checkbinance.repositories;

import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<SymbolEntity, Integer> {
}
