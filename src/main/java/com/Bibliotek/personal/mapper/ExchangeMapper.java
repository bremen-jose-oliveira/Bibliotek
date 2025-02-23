package com.bibliotek.personal.mapper;

import com.bibliotek.personal.dto.ExchangeDTO;
import com.bibliotek.personal.entity.Exchange;

public class ExchangeMapper {

    // Convert Exchange entity to ExchangeDTO
    public static ExchangeDTO toDTO(Exchange exchange) {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setId(exchange.getId());
        exchangeDTO.setStatus(exchange.getStatus());
        exchangeDTO.setExchangeDate(exchange.getExchangeDate());
        exchangeDTO.setCreatedAt(exchange.getCreatedAt());
        exchangeDTO.setUpdatedAt(exchange.getUpdatedAt());

        // Map related entities (Book and User)
        exchangeDTO.setBook(BookMapper.toDTO(exchange.getBook())); // Convert Book entity to BookDTO
        exchangeDTO.setBorrower(UserMapper.toDTO(exchange.getBorrower())); // Convert User entity to UserDTO

        return exchangeDTO;
    }
}
