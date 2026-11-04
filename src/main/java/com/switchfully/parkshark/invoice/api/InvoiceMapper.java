package com.switchfully.parkshark.invoice.api;

import com.switchfully.parkshark.invoice.api.dto.InvoiceDto;
import com.switchfully.parkshark.invoice.api.dto.InvoiceItemDto;
import com.switchfully.parkshark.invoice.domain.Invoice;
import com.switchfully.parkshark.invoice.domain.InvoiceItem;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceDto toDto(Invoice invoice) {
        return new InvoiceDto(
                invoice.getId(),
                invoice.getReference(),
                invoice.getMember().getId(),
                invoice.getCreationDate(),
                invoice.getExpirationDate(),
                invoice.getStatus(),
                invoice.getPaymentDate(),
                invoice.getMembershipCost(),
                invoice.getItems().stream().map(this::toDto).toList(),
                invoice.getTotalPrice());
    }

    private InvoiceItemDto toDto(InvoiceItem item) {
        return new InvoiceItemDto(
                item.getParkingAllocation().getId(),
                item.getBilledHours(),
                item.getPrice(),
                item.getFine(),
                item.getTotal());
    }
}