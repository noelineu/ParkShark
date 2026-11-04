package com.switchfully.parkshark.invoice.api;

import com.switchfully.parkshark.invoice.api.dto.InvoiceDto;
import com.switchfully.parkshark.invoice.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(path = "/members/{memberId}/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createMonthlyInvoice(@PathVariable Long memberId) {
        return invoiceService.createMonthlyInvoice(memberId);
    }
}