package kz.insar.checkbinance;

import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.containers.ContainerHolder;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class TickerServiceIT {

	@Autowired
	private SymbolService symbolService;
	@Autowired
	private TickerService tickerService;


	private final PostgreSQLContainer<?> postgreSQL = ContainerHolder.getPostgreSQL();
	
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
	
	@Test
	void contextLoads() {
		var builder = SymbolCreate.builder();
		builder.name("testy");
		builder.baseAsset("testy1");
		builder.quoteAsset("testy2");
		builder.quotePrecision(4);
		builder.baseAssetPrecision(5);
		builder.status(SymbolStatus.POST_TRADING);
		builder.quoteAssetPrecision(6);
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
		assertEquals(expected, actual);
	}

	@Test
	void testListSymbols2() {
		//given
		var symbolOne = createSymbol("BTCBNB");
		var symbolTwo = createSymbol("BTCETH");

		//when
		var actual = tickerService.listSymbols();

		//then
		var expected = List.of(symbolOne, symbolTwo);
		assertEquals(expected, actual);
	}

	@Test
	void testSubscribeOnPrice() {
		//given
		var symbolOne = createSymbol("BTCBNB");
		var symbolTwo = createSymbol("BTCETH");

		//when
		tickerService.subscribeOnPrice(symbolOne);

		//then
		var expected = List.of(symbolOne);
		var actual = tickerService.listSubscribtionOnPrices();
		assertEquals(expected, actual);

	}

	@Test
	void testSubscribeOnPrice_ShouldThrowIfSymbolNotExist() {
		assertThrows(Throwable.class, () -> tickerService.subscribeOnPrice(SymbolId.of(102)));
	}

	@Test void testUnsubscribeOnPrice() {
		//given
		var symbolOne = createSymbol("BTCBNB");
		var symbolTwo = createSymbol("BTCETH");
		tickerService.subscribeOnPrice(symbolOne);
		tickerService.subscribeOnPrice(symbolTwo);

		//when
		tickerService.unsubscribeOnPrice(symbolOne);

		//then
		var expected = List.of(symbolTwo);
		var actual = tickerService.listSubscribtionOnPrices();
		assertEquals(expected, actual);
	}

	@Test void D() {
		System.out.println(tickerService.listSubscribtionOnPrices());
	}

	@Test
	void asd() {
		var converter = new ApiConvertrer();
	}
}
