package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import kz.insar.checkbinance.services.SymbolService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class SymbolServiceImpl implements SymbolService {
    private final SymbolRepository symbolRepository;
    @Override
    @Transactional
    public Symbol createSymbol(SymbolCreate request) {
        var entity = SymbolEntity.builder()
                .symbolName(request.getName())
                .symbolStatus(request.getStatus())
                .baseAsset(request.getBaseAsset())
                .baseAssetPrecision(request.getBaseAssetPrecision())
                .quoteAsset(request.getQuoteAsset())
                .quotePrecision(request.getQuotePrecision())
                .quoteAssetPrecision(request.getQuoteAssetPrecision())
                .build();

        symbolRepository.save(entity);

        var symbol = Symbol.builder()
                .id(SymbolId.of(entity.getSymbolId()))
                .name(entity.getSymbolName())
                .status(entity.getSymbolStatus())
                .baseAsset(entity.getBaseAsset())
                .baseAssetPrecision(entity.getBaseAssetPrecision())
                .quoteAsset(entity.getQuoteAsset())
                .quotePrecision(entity.getQuotePrecision())
                .quoteAssetPrecision(entity.getQuoteAssetPrecision())
                .build();
        return symbol;
    }
}
