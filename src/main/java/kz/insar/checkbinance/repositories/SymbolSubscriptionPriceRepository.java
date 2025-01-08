package kz.insar.checkbinance.repositories;

import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import kz.insar.checkbinance.repositories.entities.SymbolSubscriptionPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SymbolSubscriptionPriceRepository extends JpaRepository<SymbolSubscriptionPriceEntity, Integer> {
    Optional<SymbolSubscriptionPriceEntity> findBySymbol_SymbolId(Integer symbolId);
    Optional<SymbolSubscriptionPriceEntity> findBySymbol(SymbolEntity symbol);

    //хорошая ли это потеря равночтения из идеи не менять зависимый от нас код?
    @Deprecated
    default Optional<SymbolSubscriptionPriceEntity> findBySymbol(Integer symbolId) {
        return findBySymbol_SymbolId(symbolId);
    }

    default List<String> getAllSymbolNames() {
        return findAll().stream().map(e -> e.getSymbol().getSymbolName()).collect(Collectors.toList());
    }

}
