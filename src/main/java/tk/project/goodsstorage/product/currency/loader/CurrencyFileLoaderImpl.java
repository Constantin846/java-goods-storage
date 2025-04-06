package tk.project.goodsstorage.product.currency.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.exceptionhandler.goodsstorage.exceptions.currency.DeserializeCurrencyFileException;
import tk.project.exceptionhandler.goodsstorage.exceptions.currency.LoadCurrencyFileException;
import tk.project.goodsstorage.product.currency.CurrenciesDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class CurrencyFileLoaderImpl implements CurrencyFileLoader {
    private static final String filePath = "src/main/resources/app-info/exchange-rate.json";
    private final ObjectMapper objectMapper;

    public CurrencyFileLoaderImpl() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CurrenciesDto loadCurrencies() {
        try {
            String content = Files.readString(Path.of(filePath));
            return objectMapper.readValue(content.toLowerCase(), CurrenciesDto.class);

        } catch (JsonProcessingException e) {
            String message = "Exception during deserializing content of currency file";
            log.warn(message);
            throw new DeserializeCurrencyFileException(message);

        } catch (IOException e) {
            String message = "Exception during loading currency file";
            log.warn(message);
            throw new LoadCurrencyFileException(message);
        }
    }
}