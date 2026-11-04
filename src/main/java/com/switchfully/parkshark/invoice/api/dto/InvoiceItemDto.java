package com.switchfully.parkshark.invoice.api.dto;

import java.math.BigDecimal;

public record InvoiceItemDto(Long parkingAllocationId, int billedHours, BigDecimal price, BigDecimal fine, BigDecimal total) {
}