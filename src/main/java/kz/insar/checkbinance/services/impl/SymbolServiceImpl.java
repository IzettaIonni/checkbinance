package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.converters.EntityConverter;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.SymbolUpdate;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.repositories.SymbolSubscriptionPriceRepository;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import kz.insar.checkbinance.repositories.entities.SymbolSubscriptionPriceEntity;
import kz.insar.checkbinance.services.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SymbolServiceImpl implements SymbolService {

    private final SymbolRepository symbolRepository;

    private final SymbolSubscriptionPriceRepository subscriptionRepository;

    private final EntityConverter converter;

    public SymbolServiceImpl(SymbolRepository symbolRepository,
                             SymbolSubscriptionPriceRepository subscriptionRepository, EntityConverter converter) {
        if(symbolRepository == null || converter == null || subscriptionRepository == null) {
            throw new NullPointerException();
        }
        this.symbolRepository = symbolRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.converter = converter;
    }

    @Autowired
    public SymbolServiceImpl(SymbolRepository symbolRepository,
                             SymbolSubscriptionPriceRepository subscriptionRepository) {
        this(symbolRepository, subscriptionRepository, new EntityConverter());
    }

    @Override
    public Symbol createSymbol(SymbolCreate request) {
        var entity = converter.fromDomain(request);

        this.symbolRepository.save(entity);

        return this.converter.toDomain(entity);
    }

    @Override
    public Symbol updateSymbol(SymbolUpdate request) {
        var oEntity = symbolRepository.findById(request.getId());
        if(oEntity.isEmpty()) {
            throw new NullPointerException("Symbol with this id is not found: " + request.getId());
        }
        var entity = oEntity.get();
        entity = converter.fromDomain(entity, request);
        symbolRepository.save(entity);

        return converter.toDomain(entity);
    }

    @Override
    public List<Symbol> getSymbols() {
        var allEntities = symbolRepository.findAll();
        List<Symbol> result = new ArrayList<>();
        for (SymbolEntity entity : allEntities) {
            result.add(converter.toDomain(entity));
        }
        return result;
    }

    @Override
    public void addPriceSubscription(SymbolId id) {
        var entity = subscriptionRepository.findBySymbol(id.getId()).orElse(null);
        if (entity == null) {
            entity = SymbolSubscriptionPriceEntity.builder()
                    .symbol(id.getId())
                    .subscriptionStatus(true)
                    .build();
            subscriptionRepository.save(entity);
        }
        else {
            if (!entity.getSubscriptionStatus()) {
                entity.setSubscriptionStatus(true);
                subscriptionRepository.save(entity);
            }
        }
    }

    @Override
    public void removePriceSubscription(SymbolId id) {
        var entity = subscriptionRepository.findBySymbol(id.getId()).orElse(null);
        if (entity != null) {
            subscriptionRepository.delete(entity);
        }
    }

    @Override
    public List<Symbol> getListOfPriceSubscriptions() {
        var symbolIds = subscriptionRepository.findAll().stream()
                .map(SymbolSubscriptionPriceEntity::getSymbol)
                .collect(Collectors.toList());
        return symbolRepository.findAllById(symbolIds).stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
}
