package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.client.StockClientAccessException;
import kz.insar.checkbinance.client.StockClientException;
import kz.insar.checkbinance.client.StockClientNotFoundException;
import kz.insar.checkbinance.client.StockClientRemoteServiceException;
import kz.insar.checkbinance.converters.ApiConverter;
import kz.insar.checkbinance.converters.ControllerConverter;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.exeptions.InvalidDataException;
import kz.insar.checkbinance.domain.exeptions.ObjectNotFoundException;
import kz.insar.checkbinance.services.TickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@Validated
@RequestMapping("ticker")
@Slf4j
public class TickerController {

    @Autowired
    private TickerService tickerService;

    @Autowired
    private ControllerConverter controllerConverter;

    private final ApiConverter apiConverter = new ApiConverter();

    @GetMapping("/lastprice") //uses getPrices
    public List<LastPriceDTO> lastPrices(@Nullable @RequestParam(defaultValue = "SYMBOL") @Valid LastPriceColumns sortKey,
                                         @Nullable @RequestParam(defaultValue = "ASC") @Valid SortDirection sortDir) {
//        MDC.put("RequestId", UUID.randomUUID().toString());
        log.info("request lastPrice with sortKey = " + sortKey.name() + " and sortDir = " +sortDir.name());
        var response = tickerService.lastPrices(controllerConverter.toSort(sortKey, sortDir));
        log.info("request successfully done");
//        MDC.clear();
        return response;
    }

    @GetMapping("/legacylastprice")
    public List<LastPriceDTO> legacyLastPrices(@Nullable @RequestParam(defaultValue = "1") @Valid int limit,
                                               @Nullable @RequestParam(defaultValue = "SYMBOL") @Valid LastPriceColumns sortKey,
                                               @Nullable @RequestParam(defaultValue = "ASC") @Valid SortDirection sortDir) {
//        MDC.put("RequestId", UUID.randomUUID().toString());
        log.info("request legacyLastPrice with sortKey = "
                + sortKey.name() + " and sortDir = " +sortDir.name());
        var response = tickerService.legacyLastPrices(limit, controllerConverter.toSort(sortKey, sortDir));
        log.info("request successfully done");
//        MDC.clear();
        return response;
    }


    @GetMapping("/exchangeinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo(@Nullable @RequestParam() @Valid List<String> symbols) {
        if (symbols == null) {
            log.debug("request exchangeInfo without params");
            throw new InvalidDataException();
        }
//        MDC.put("RequestId", UUID.randomUUID().toString());
        log.info("request exchangeInfo with symbols: " + symbols);
        var response = tickerService.exchangeInfo(symbols);
        log.info("request successfully done");
//        MDC.clear();
        return response;
    }

    @GetMapping("/exchangeallinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo() {
//        MDC.put("RequestId", UUID.randomUUID().toString());
        log.info("request exchangeAllInfo");
        var response = tickerService.exchangeInfo();
        log.info("request successfully done");
//        MDC.clear();
        return response;
    }
    
    @PostMapping("/subscribeticker")
    public void subscribeTicker(@RequestParam(required = false) @Valid @Nullable Integer id,
                                @RequestParam(required = false) @Valid @Nullable String name) {
        if (id == null && name == null) {
            log.debug("request subscribeTicker without params");
            throw new InvalidDataException("ticker name and ticker id is null");
        }
//        MDC.put("RequestId", UUID.randomUUID().toString());
        log.info("request subscribeTicker");
        if (id != null) {
            tickerService.subscribeOnPrice(SymbolId.of(id));
            log.info("request is done with tickerId = " + id);
        }
        else {
            tickerService.subscribeOnPrice(name);
            log.info("request is done with tickerName = " + name);
        }
//        MDC.clear();
    }

    @PostMapping("/unsubscribeticker")
    public void unsubscribeTicker(@RequestParam(required = false) @Valid @Nullable Integer id,
                                  @RequestParam(required = false) @Valid @Nullable String name) {
        if (id == null && name == null) {
            log.debug("request unsubscribeTicker without params");
            throw new InvalidDataException("ticker name and ticker id is not found");
        }
//        MDC.put("RequestId", UUID.randomUUID().toString());
        log.info("request unsubscribeTicker");
        if (id != null) {
            tickerService.unsubscribeOnPrice(SymbolId.of(id));
            log.info("request is done with tickerId = " + id);
        }
        else {
            tickerService.unsubscribeOnPrice(name);
            log.info("request is done with tickerName = " + name);
        }
//        MDC.clear();
    }

    @GetMapping("/subscriptions")
    public List<SymbolShortDTO> subscriptions() {
//        MDC.put("RequestId", UUID.randomUUID().toString());
        log.info("request subscriptions");
        var response = apiConverter.fromDomainToShortList(tickerService.listSubscriptionOnPrices());
        log.info("request successfully done");
//        MDC.clear();
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

    @ExceptionHandler({StockClientRemoteServiceException.class})
    public ResponseEntity<Void> stockClientRemoteServiceException(StockClientRemoteServiceException e) {
        log.error("Stock Exchange Service Internal Error: ", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @ExceptionHandler({StockClientNotFoundException.class})
    public ResponseEntity<Void> stockClientNotFoundException(StockClientNotFoundException e) {
        log.error("Stock Exchange Client Data Not Found Error: ", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({StockClientAccessException.class})
    public ResponseEntity<Void> stockClientAccessException(StockClientAccessException e) {
        log.error("Stock Exchange Client Access Error: ", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler({StockClientException.class})
    public ResponseEntity<Void> stockClientException(StockClientException e) {
        log.error("Stock Exchange Client Error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
