package kz.insar.checkbinance.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
public class BinanceRestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().is5xxServerError() ||
                httpResponse.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        var statusCode = httpResponse.getRawStatusCode();

        if (httpResponse.getStatusCode().is5xxServerError()) {
            throw new StockClientRemoteServiceException("Remote Service Error");
        }

        else if (httpResponse.getStatusCode().is4xxClientError()) {
            if (statusCode == 404) {
                throw new StockClientNotFoundException("Resource not found");
            }
            else if (statusCode == 403 || statusCode == 418 || statusCode == 429) {
                throw new StockClientAccessException("Access denied");
            }
            else if (statusCode == 409) {
                throw new StockClientPartialSuccessException("Partially executed");
            }
            else {
                throw new StockClientException("Unknown binance client exception");
            }
        }

        log.error("Unhandled response " + httpResponse);
        throw new IllegalArgumentException("Unacceptable http code");
    }

}
