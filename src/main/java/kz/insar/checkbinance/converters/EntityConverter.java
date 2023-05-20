package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.SymbolUpdate;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;

public class EntityConverter {
    public Symbol toDomain(SymbolEntity entity) {
        return Symbol.builder()
                .id(SymbolId.of(entity.getSymbolId()))
                .name(entity.getSymbolName())
                .status(entity.getSymbolStatus())
                .baseAsset(entity.getBaseAsset())
                .baseAssetPrecision(entity.getBaseAssetPrecision())
                .quoteAsset(entity.getQuoteAsset())
                .quotePrecision(entity.getQuotePrecision())
                .quoteAssetPrecision(entity.getQuoteAssetPrecision())
                .build();
    }

    public SymbolEntity fromDomain(SymbolCreate request) {
        return SymbolEntity.builder()
                .symbolName(request.getName())
                .symbolStatus(request.getStatus())
                .baseAsset(request.getBaseAsset())
                .baseAssetPrecision(request.getBaseAssetPrecision())
                .quoteAsset(request.getQuoteAsset())
                .quotePrecision(request.getQuotePrecision())
                .quoteAssetPrecision(request.getQuoteAssetPrecision())
                .build();
    }

    public SymbolEntity fromDomain(SymbolEntity entity, SymbolUpdate request) {
        entity.setSymbolStatus(request.getStatus());
        entity.setBaseAsset(request.getBaseAsset());
        entity.setBaseAssetPrecision(request.getBaseAssetPrecision());
        entity.setQuoteAsset(request.getQuoteAsset());
        entity.setQuotePrecision(request.getQuotePrecision());
        entity.setQuoteAssetPrecision(request.getQuoteAssetPrecision());
        return entity;
    }
}
