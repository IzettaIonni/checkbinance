package kz.insar.checkbinance;

import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import kz.insar.checkbinance.services.SymbolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CheckbinanceApplicationTests {

	@Autowired
	private SymbolService symbolService;

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


	@Test void Asd() {
		var s = SymbolId.of(1);
		s.getId();
		SymbolId.of(2).getId();
		System.out.println(s);
	}
}
