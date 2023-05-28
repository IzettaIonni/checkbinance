package kz.insar.checkbinance.repositories;

import kz.insar.checkbinance.repositories.entities.SymbolSubscriptionPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymbolSubscriptionPriceRepository extends JpaRepository<SymbolSubscriptionPriceEntity, Integer> {
    Optional<SymbolSubscriptionPriceEntity> findBySymbol(Integer symbol);
}
