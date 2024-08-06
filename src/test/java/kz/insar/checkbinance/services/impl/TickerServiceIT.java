package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.containers.BinanceAPIHelper;
import kz.insar.checkbinance.containers.ContainerHolder;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.sort.params.SortParams;
import kz.insar.checkbinance.services.SymbolService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@ExtendWith(ContainerHolder.class)
class TickerServiceIT {

	@Autowired
	private SymbolService symbolService;
	@Autowired
	private TickerServiceImpl tickerService;
	private static BinanceAPIHelper binanceAPIHelper;

	@BeforeAll
	static void beforeClass() {
		binanceAPIHelper = ContainerHolder.createBinanceAPIHelper();
	}

	@AfterEach
	void tearDown() {
		binanceAPIHelper.cleanUp();
	}

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

	private LastPriceDTO createLastPriceDTO(String name) {
		return LastPriceDTO.builder()
				.price(123456)
				.symbol(name)
				.id(32)
				.time(LocalDateTime.MIN)
				.build();
	}

//	@Test
//	void testLastPrices() {
//		//given
//		var symbolOne = createSymbol("CHZBNB");
//		var symbolTwo = createSymbol("BEAMUSDT");
//		tickerService.subscribeOnPrice(symbolOne);
//		tickerService.subscribeOnPrice(symbolTwo);
//		SortParams<LastPriceColumns> sortParams = new SortParams<>(LastPriceColumns.SYMBOL, SortDirection.ASC);
//		var lastPriceDTOOne = createLastPriceDTO(symbolOne.getName());
//		var lastPriceDTOTwo = createLastPriceDTO(symbolTwo.getName());
//
//		List<SymbolPriceDTO> mockResponse = List.of(
//				SymbolPriceDTO.builder().symbol(symbolOne.getName()).price(50600).build(),
//				SymbolPriceDTO.builder().symbol(symbolTwo.getName()).price(44444).build()
//				);
//
//		binanceAPIHelper.mockRequestGetPrices(List.of(symbolOne.getName(), symbolTwo.getName()), mockResponse);
//
//		//when
//		var listOfSymbols = tickerService.lastPrices(sortParams);
//		var actual = List.of(listOfSymbols.get(0).getSymbol(), listOfSymbols.get(1).getSymbol());
//
//		//then
//		var expected = List.of(lastPriceDTOTwo.getSymbol(), lastPriceDTOOne.getSymbol());
//		assertEquals(expected, actual);
//	}

	@Test
	void testLegacyLastPrices() {
		//given
		var symbolOne = createSymbol("CHZBNB");
		var symbolTwo = createSymbol("BEAMUSDT");
		tickerService.subscribeOnPrice(symbolOne);
		tickerService.subscribeOnPrice(symbolTwo);
		SortParams<LastPriceColumns> sortParams = new SortParams<>(LastPriceColumns.SYMBOL, SortDirection.ASC);
		var lastPriceDTOOne = createLastPriceDTO(symbolOne.getName());
		var lastPriceDTOTwo = createLastPriceDTO(symbolTwo.getName());

		//when
		var listOfSymbols = tickerService.legacyLastPrices(sortParams);
		var actual = List.of(listOfSymbols.get(0).getSymbol(), listOfSymbols.get(1).getSymbol());

		//then
		var expected = List.of(lastPriceDTOTwo.getSymbol(), lastPriceDTOOne.getSymbol());
		assertEquals(expected, actual);
	}

	@Test
	void testExchangeInfoWithParams() {
		//given
		var symbols = List.of("BNBBTC", "CHZBNB", "BEAMUSDT");

		//when
		var result = tickerService.exchangeInfo(symbols);
		List<String> actual = new ArrayList<String>();
		for (SymbolParamsDTO symbolParams : result.getSymbols()) {
			actual.add(symbolParams.getSymbol());
		}

		//then
		var expected = symbols;
		assertEquals(expected, actual);
	}

	@Test
	void testExchangeInfo_IsNotEmpty() {
		//given

		//when
		var actual = tickerService.exchangeInfo();

		//then
		Boolean expected = null;
		assertNotEquals(expected, actual);
	}

	@Test
	void testUpdateSymbols() {
		//given
		var expected = symbolService.getSymbols();

		//when
		tickerService.updateSymbols();

		//then
		var actual = symbolService.getSymbols();
		assertNotEquals(expected, actual);
	}

	@Test
	void testListSymbols() {
		//given
		var symbolOne = createSymbol("BTCBNB");
		var symbolTwo = createSymbol("ETHBNB");


		//when
		var actual = tickerService.listSymbols();

		//then
		var expected = List.of(symbolOne, symbolTwo);
		System.out.println(actual);
		assertEquals(expected, actual);
	}

	@Test
	void testSubscribeOnPrice() {
		//given
		var symbolOne = createSymbol("BTCBNB");

		//when
		tickerService.subscribeOnPrice(symbolOne);

		//then
		var expected = List.of(symbolOne);
		var actual = tickerService.listSubscriptionOnPrices();
		assertEquals(expected, actual);

	}

	@Test
	void testSubscribeOnPrice_ViaSymbolId() {
		//given
		var symbolOne = createSymbol("BTCBNB");

		//when
		tickerService.subscribeOnPrice(symbolOne.getName());

		//then
		var expected = List.of(symbolOne);
		var actual = tickerService.listSubscriptionOnPrices();
		assertEquals(expected, actual);
	}

	@Test
	void testSubscribeOnPrice_ShouldThrowIfSymbolNotExist() {
		assertThrows(Throwable.class, () -> tickerService.subscribeOnPrice(SymbolId.of(102)));
	}

	@Test
	void testUnsubscribeOnPrice() {
		//given
		var symbolOne = createSymbol("BTCBNB");
		var symbolTwo = createSymbol("BTCETH");
		tickerService.subscribeOnPrice(symbolOne);
		tickerService.subscribeOnPrice(symbolTwo);

		//when
		tickerService.unsubscribeOnPrice(symbolOne);

		//then
		var expected = List.of(symbolTwo);
		var actual = tickerService.listSubscriptionOnPrices();
		assertEquals(expected, actual);
	}

	@Test
	void testUnsubscribeOnPrice_ViaSymbolName() {
		//given
		var symbolOne = createSymbol("BTCBNB");
		var symbolTwo = createSymbol("BTCETH");
		tickerService.subscribeOnPrice(symbolOne.getName());
		tickerService.subscribeOnPrice(symbolTwo.getName());

		//when
		tickerService.unsubscribeOnPrice(symbolOne);

		//then
		var expected = List.of(symbolTwo);
		var actual = tickerService.listSubscriptionOnPrices();
		assertEquals(expected, actual);
	}

	@Test
	void testListSubscriptionOnPrices() {
		//given
		var symbolOne = createSymbol("BTCBNB");
		var symbolTwo = createSymbol("BTCETH");
		tickerService.subscribeOnPrice(symbolOne.getName());
		tickerService.subscribeOnPrice(symbolTwo.getName());

		//when
		var actual = tickerService.listSubscriptionOnPrices();

		//then
		var expected = List.of(symbolOne, symbolTwo);
		assertEquals(expected, actual);
	}
}
