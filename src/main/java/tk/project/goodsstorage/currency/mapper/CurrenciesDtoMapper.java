package tk.project.goodsstorage.currency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.currency.dto.CurrenciesDto;
import tk.project.goodsstorage.currency.dto.CurrenciesDtoDoubles;

@Mapper(componentModel = "spring")
public interface CurrenciesDtoMapper {
    CurrenciesDtoMapper MAPPER = Mappers.getMapper(CurrenciesDtoMapper.class);

    CurrenciesDto toCurrenciesDto(CurrenciesDtoDoubles currenciesDtoDoubles);
}
