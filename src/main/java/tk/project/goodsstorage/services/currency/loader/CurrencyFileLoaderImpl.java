package tk.project.goodsstorage.services.currency.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.dto.CurrenciesDto;
import tk.project.goodsstorage.exceptionhandler.exceptions.currency.DeserializeCurrencyFileException;
import tk.project.goodsstorage.exceptionhandler.exceptions.currency.LoadCurrencyFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@Component
public class CurrencyFileLoaderImpl implements CurrencyFileLoader {
    private static final String DEFAULT_FILE_PATH = "src/main/resources/app-info/exchange-rate.json";
    private final String filePath;
    private final ObjectMapper objectMapper;

    public CurrencyFileLoaderImpl() {
        this(null);
    }

    public CurrencyFileLoaderImpl(final String filePath) {
        this.objectMapper = new ObjectMapper();
        this.filePath = Objects.nonNull(filePath) ? filePath : DEFAULT_FILE_PATH;
    }

    @Override
    public CurrenciesDto loadCurrencies() {
        try {
            final String content = Files.readString(Path.of(filePath));
            return objectMapper.readValue(content, CurrenciesDto.class);

        } catch (JsonProcessingException e) {
            final String message = "Exception during deserializing content of currency file";
            log.warn(message);
            throw new DeserializeCurrencyFileException(message);

        } catch (IOException e) {
            final String message = "Exception during loading currency file";
            log.warn(message);
            throw new LoadCurrencyFileException(message);
        }
    }
}