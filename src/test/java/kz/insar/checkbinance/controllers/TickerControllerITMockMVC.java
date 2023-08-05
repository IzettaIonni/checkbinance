package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.impl.TickerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TickerControllerITMockMVC {

    @Autowired
    private SymbolService symbolService;
    @Autowired
    private TickerServiceImpl tickerService;
    private Symbol createSymbol(String name) {
        return symbolService.createSymbol(SymbolCreate.builder()
                .quotePrecision(567)
                .quoteAsset("asd")
                .baseAsset("sad")
                .name(name)
                .baseAssetPrecision(5678)
                .quoteAssetPrecision(77)
                .status(SymbolStatus.POST_TRADING)
                .build());
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testNotFound() throws Exception {
        this.mockMvc.perform(get("/non-existing-link"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void testBadRequest() throws Exception {
        this.mockMvc.perform(get("/ticker/unsubscribeticker"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLastPrice() throws Exception {
        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);
        System.out.println(symbolTwo);
        this.mockMvc.perform(get("/ticker/lastprice"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.[0].symbol").value(symbolTwo.getName()))
                .andExpect(jsonPath("$.[0].id").value(symbolTwo.getId().getId().toString()))
                .andExpect(jsonPath("$.[1].symbol").value(symbolOne.getName()))
                .andExpect(jsonPath("$.[1].id").value(symbolOne.getId().getId().toString()));
    }

    @Test
    void testLegacyLastPrice() throws Exception {
        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);
        System.out.println(symbolTwo);
        this.mockMvc.perform(get("/ticker/lastprice"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.[0].symbol").value(symbolTwo.getName()))
                .andExpect(jsonPath("$.[0].id").value(symbolTwo.getId().getId().toString()))
                .andExpect(jsonPath("$.[1].symbol").value(symbolOne.getName()))
                .andExpect(jsonPath("$.[1].id").value(symbolOne.getId().getId().toString()));
    }

}
