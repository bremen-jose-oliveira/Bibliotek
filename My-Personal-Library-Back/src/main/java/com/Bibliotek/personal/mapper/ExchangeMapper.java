package com.Bibliotek.personal.mapper;


import com.Bibliotek.personal.dto.ExchangeDTO;
import com.Bibliotek.personal.entity.Exchange;

public class ExchangeMapper {

    // Convert Exchange entity to ExchangeDTO
    public static ExchangeDTO toDTO(Exchange exchange) {
        if (exchange == null) return null;
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setId(exchange.getId());
        exchangeDTO.setStatus(exchange.getStatus());
        exchangeDTO.setExchangeDate(exchange.getExchangeDate());
        exchangeDTO.setCreatedAt(exchange.getCreatedAt());
        exchangeDTO.setUpdatedAt(exchange.getUpdatedAt());

        if (exchange.getBook() != null) {
            exchangeDTO.setBook(BookMapper.toDTO(exchange.getBook()));
        }
        if (exchange.getBorrower() != null) {
            exchangeDTO.setBorrower(UserMapper.toDTO(exchange.getBorrower()));
        }

        return exchangeDTO;
    }
}
