package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.client.BinanceClientExeption;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.converters.ControllerConverter;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.exeptions.InvalidDataException;
import kz.insar.checkbinance.domain.exeptions.ObjectNotFoundException;
import kz.insar.checkbinance.services.TickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@Validated
@RequestMapping("ticker")
@Slf4j
public class TickerController {

    @Autowired
    private TickerService tickerService;

    @Autowired
    private ControllerConverter controllerConverter;

    private final ApiConvertrer apiConverter = new ApiConvertrer();

    @GetMapping("/lastprice")
    public List<LastPriceDTO> lastPrices(@Nullable @RequestParam(defaultValue = "SYMBOL") @Valid LastPriceColumns sortKey,
                                         @Nullable @RequestParam(defaultValue = "ASC") @Valid SortDirection sortDir) {
        var requestId = UUID.randomUUID();
        log.info("request " + requestId + " lastPrice with sortKey = "
                + sortKey.name() + " and sortDir = " +sortDir.name());
        var response = tickerService.lastPrices(controllerConverter.toSort(sortKey, sortDir));
        log.info("request " + requestId + " successfully done");
        return response;
    }

    @GetMapping("/legacylastprice")
    public List<LastPriceDTO> legacyLastPrices(@Nullable @RequestParam(defaultValue = "1") @Valid int limit,
                                               @Nullable @RequestParam(defaultValue = "SYMBOL") @Valid LastPriceColumns sortKey,
                                               @Nullable @RequestParam(defaultValue = "ASC") @Valid SortDirection sortDir) {
        var requestId = UUID.randomUUID();
        log.info("request " + requestId + " legacyLastPrice with sortKey = "
                + sortKey.name() + " and sortDir = " +sortDir.name());
        var response = tickerService.legacyLastPrices(limit, controllerConverter.toSort(sortKey, sortDir));
        log.info("request " + requestId + " successfully done");
        return response;
    }


    @GetMapping("/exchangeinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo(@Nullable @RequestParam() @Valid List<String> symbols) {
        if (symbols == null) {
            log.debug("request exchangeInfo without params");
            throw new InvalidDataException();
        }
        var requestId = UUID.randomUUID();
        log.info("request " + requestId + " exchangeInfo with symbols: " + symbols);
        var response = tickerService.exchangeInfo(symbols);
        log.info("request " + requestId + " successfully done");
        return response;
    }

    @GetMapping("/exchangeallinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo() {
        var requestId = UUID.randomUUID();
        log.info("request " + requestId + " exchangeAllInfo");
        var response = tickerService.exchangeInfo();
        log.info("request " + requestId + " successfully done");
        return response;
    }
    
    @PostMapping("/subscribeticker")
    public void subscribeTicker(@RequestParam(required = false) @Valid @Nullable Integer id,
                                @RequestParam(required = false) @Valid @Nullable String name) {
        if (id == null && name == null) {
            log.debug("request subscribeTicker without params");
            throw new InvalidDataException("ticker name and ticker id is null");
        }
        var requestId = UUID.randomUUID();
        log.info("request " + requestId + " subscribeTicker");
        if (id != null) {
            tickerService.subscribeOnPrice(SymbolId.of(id));
            log.info("request " + requestId + " is done with tickerId = " + id);
        }
        else {
            tickerService.subscribeOnPrice(name);
            log.info("request " + requestId + " is done with tickerName = " + name);
        }

    }

    @PostMapping("/unsubscribeticker")
    public void unsubscribeTicker(@RequestParam(required = false) @Valid @Nullable Integer id,
                                  @RequestParam(required = false) @Valid @Nullable String name) {
        if (id == null && name == null) {
            log.debug("request unsubscribeTicker without params");
            throw new InvalidDataException("ticker name and ticker id is not found");
        }
        var requestId = UUID.randomUUID();
        log.info("request " + requestId + " unsubscribeTicker");
        if (id != null) {
            tickerService.unsubscribeOnPrice(SymbolId.of(id));
            log.info("request " + requestId + " is done with tickerId = " + id);
        }
        else {
            tickerService.unsubscribeOnPrice(name);
            log.info("request " + requestId + " is done with tickerName = " + name);
        }
    }

    @GetMapping("/subscriptions")
    public List<SymbolShortDTO> subscriptions() {
        var requestId = UUID.randomUUID();
        log.info("request " + requestId + " subscriptions");
        var response = apiConverter.fromDomainToShortList(tickerService.listSubscriptionOnPrices());
        log.info("request " + requestId + " successfully done");
        return response;
    }

    @GetMapping("/updatesymbols")
    public void updateSymbols() {
        tickerService.updateSymbols();
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    public ResponseEntity<Void> notFoundException() {
        log.debug("ObjectNotFoundException caught, return NotFound response");
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({InvalidDataException.class})
    public ResponseEntity<Void> badRequestException() {
        log.debug("InvalidDataException caught, return BadRequest response");
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({BinanceClientExeption.class})
    public ResponseEntity<Void> binanceClientException(BinanceClientExeption e) {
        if (e.getCause() != null) {
            var cause = e.getCause();
            if (cause instanceof HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException) cause;
                switch (ex.getRawStatusCode()) {
                case 404:
                    log.warn("Binance resources Not Found", e);
                    return ResponseEntity.notFound().build();
                }
            }
        }

        log.error("Binance Client Error: ", e);
        return ResponseEntity.internalServerError().build();
    }
}
