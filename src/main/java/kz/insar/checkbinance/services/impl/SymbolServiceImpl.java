package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.converters.EntityConverter;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.SymbolUpdate;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import kz.insar.checkbinance.services.SymbolService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SymbolServiceImpl implements SymbolService {

    private final SymbolRepository repository;

    private final EntityConverter converter;

    public SymbolServiceImpl(SymbolRepository repository, EntityConverter converter) {
        if(repository == null || converter == null) {
            throw new NullPointerException();
        }
        this.repository = repository;
        this.converter = converter;
    }

    @Autowired
    public SymbolServiceImpl(SymbolRepository repository) {
        this(repository, new EntityConverter());
    }
    @Override
    public Symbol createSymbol(SymbolCreate request) {
        var entity = converter.fromDomain(request);

        repository.save(entity);

        return converter.toDomain(entity);
    }

    @Override
    public Symbol updateSymbol(SymbolUpdate request) {
        var oEntity = repository.findById(request.getId());
        if(oEntity.isEmpty()) {
            throw new NullPointerException("Symbol with this id is not found: " + request.getId());
        }
        var entity = oEntity.get();
        entity = converter.fromDomain(entity, request);
        repository.save(entity);

        return converter.toDomain(entity);
    }

    @Override
    public List<Symbol> getSymbols() {
        var allEntities = repository.findAll();
        List<Symbol> result = new ArrayList<>();
        for (SymbolEntity entity : allEntities) {
            result.add(converter.toDomain(entity));
        }
        return result;
    }
}
