package com.Bibliotek.personal.dto;


import com.Bibliotek.personal.entity.Exchange;

public class ExchangeStatusUpdateDTO {
    private Exchange.ExchangeStatus status;

    public ExchangeStatusUpdateDTO() {}

    public ExchangeStatusUpdateDTO(Exchange.ExchangeStatus status) {
        this.status = status;
    }

    public Exchange.ExchangeStatus getStatus() {
        return status;
    }

    public void setStatus(Exchange.ExchangeStatus status) {
        this.status = status;
    }
}
