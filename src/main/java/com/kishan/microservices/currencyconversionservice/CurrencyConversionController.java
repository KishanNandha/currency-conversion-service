package com.kishan.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kishan.microservices.currencyconversionservice.proxy.CurrencyExcgangeServiceProxy;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExcgangeServiceProxy currencyExcgangeServiceProxy;

	@GetMapping("/currency-converter/form/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		//return usingDefaultRestClient(from,to,quantity);
		
		return usingFeignRestClient(from,to,quantity);
	}

	private CurrencyConversionBean usingDefaultRestClient(String from, String to, BigDecimal quantity) {

		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class,
				uriVariables);
		
		CurrencyConversionBean response = responseEntity.getBody();

		CurrencyConversionBean currencyConversionBean = new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(),
				quantity, response.getPort());
		currencyConversionBean.setTotalCalculatedAmount(quantity.multiply(response.getConversionMultiple()));
		
		return currencyConversionBean;
	}
	
	private CurrencyConversionBean usingFeignRestClient(String from, String to, BigDecimal quantity) {
		
		CurrencyConversionBean response = currencyExcgangeServiceProxy.retrieveExchangeValue(from, to);

		CurrencyConversionBean currencyConversionBean = new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(),
				quantity, response.getPort());
		currencyConversionBean.setTotalCalculatedAmount(quantity.multiply(response.getConversionMultiple()));
		
		return currencyConversionBean;
	}
	
	

}
