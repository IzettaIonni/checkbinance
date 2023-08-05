package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.converters.ControllerConverter;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.exeptions.InvalidDataException;
import kz.insar.checkbinance.domain.exeptions.ObjectNotFoundException;
import kz.insar.checkbinance.services.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@Validated
@RequestMapping("ticker")
public class TickerController {

    @Autowired
    private TickerService tickerService;

    @Autowired
    private ControllerConverter controllerConverter;

    private final ApiConvertrer apiConverter = new ApiConvertrer();

    @GetMapping("/lastprice")
    public List<LastPriceDTO> lastPrices(@Nullable @RequestParam(defaultValue = "SYMBOL") @Valid LastPriceColumns sortKey,
                                         @Nullable @RequestParam(defaultValue = "ASC") @Valid SortDirection sortDir) {
            return tickerService.lastPrices(controllerConverter.toSort(sortKey, sortDir));
    }

    @GetMapping("/legacylastprice")
    public List<LastPriceDTO> legacyLastPrices(@Nullable @RequestParam(defaultValue = "1") @Valid int limit,
                                               @Nullable @RequestParam(defaultValue = "SYMBOL") @Valid LastPriceColumns sortKey,
                                               @Nullable @RequestParam(defaultValue = "ASC") @Valid SortDirection sortDir) {
        return tickerService.legacyLastPrices(limit, controllerConverter.toSort(sortKey, sortDir));

    }

    @GetMapping("/exchangeinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo(@Nullable @RequestParam @Valid List<String> symbols) {
        return tickerService.exchangeInfo(symbols);
    }

    @GetMapping("/exchangeallinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo() {
        return tickerService.exchangeInfo();
    }

    @GetMapping("/subscribeticker")
    public void subscribeTicker(@RequestParam(required = false) @Valid @Nullable Integer id,
                                @RequestParam(required = false) @Valid @Nullable String name) {
        if (id == null && name == null) {
            throw new InvalidDataException("ticker name and ticker id is null");
        }
        if (id != null) {
            tickerService.subscribeOnPrice(SymbolId.of(id));
        }
        else {
            tickerService.subscribeOnPrice(name);
        }
    }

    @GetMapping("/unsubscribeticker")
    public void unsubscribeTicker(@RequestParam(required = false) @Valid @Nullable Integer id,
                                  @RequestParam(required = false) @Valid @Nullable String name) {
        if (id == null && name == null) {
            throw new InvalidDataException("ticker name and ticker id is not found");
        }
        if (id != null) {
            tickerService.unsubscribeOnPrice(SymbolId.of(id));
        }
        else {
            tickerService.unsubscribeOnPrice(name);
        }
    }

    @GetMapping("/subscriptions")
    public List<SymbolShortDTO> subscriptions() {
        return apiConverter.fromDomainToShortList(tickerService.listSubscriptionOnPrices());
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    public ResponseEntity<Void> notFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({InvalidDataException.class})
    public ResponseEntity<Void> badRequestException() {
        return ResponseEntity.badRequest().build();
    }
}
