package tk.project.goodsstorage.currency.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.currency.dto.CurrenciesDto;
import tk.project.goodsstorage.currency.dto.CurrenciesDtoDoubles;
import tk.project.goodsstorage.currency.mapper.CurrenciesDtoMapper;
import tk.project.goodsstorage.exceptions.DeserializeCurrencyFileException;
import tk.project.goodsstorage.exceptions.LoadCurrencyFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class CurrencyFileLoaderImpl implements CurrencyFileLoader {
    private static final String filePath = "src/main/resources/app-info/exchange-rate.json";
    private final CurrenciesDtoMapper currenciesDtoMapper;
    private final ObjectMapper objectMapper;

    public CurrencyFileLoaderImpl(@Autowired CurrenciesDtoMapper currenciesDtoMapper) {
        this.currenciesDtoMapper = currenciesDtoMapper;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CurrenciesDto loadCurrencies() {
        try {
            String content = Files.readString(Path.of(filePath));
            CurrenciesDtoDoubles currencies = objectMapper.readValue(content, CurrenciesDtoDoubles.class);
            return currenciesDtoMapper.toCurrenciesDto(currencies);

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