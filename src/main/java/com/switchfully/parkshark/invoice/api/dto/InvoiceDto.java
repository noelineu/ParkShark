package com.switchfully.parkshark.invoice.api.dto;

import com.switchfully.parkshark.invoice.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceDto(
        Long id,
        String reference,
        Long memberId,
        LocalDate creationDate,
        LocalDate expirationDate,
        InvoiceStatus status,
        LocalDate paymentDate,
        BigDecimal membershipCost,
        List<InvoiceItemDto> items,
        BigDecimal totalPrice) {
}