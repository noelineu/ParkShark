package com.switchfully.parkshark.invoice.service;

import com.switchfully.parkshark.exception.MemberNotFoundException;
import com.switchfully.parkshark.invoice.api.InvoiceMapper;
import com.switchfully.parkshark.invoice.api.dto.InvoiceDto;
import com.switchfully.parkshark.invoice.domain.Invoice;
import com.switchfully.parkshark.invoice.domain.InvoiceRepository;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocationRepository;
import com.switchfully.parkshark.parkinglot.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    private final MemberRepository memberRepository;
    private final ParkingAllocationRepository parkingAllocationRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceService(MemberRepository memberRepository, ParkingAllocationRepository parkingAllocationRepository,
                          InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.memberRepository = memberRepository;
        this.parkingAllocationRepository = parkingAllocationRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    public InvoiceDto createMonthlyInvoice(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Invoice invoice = new Invoice(member);
        parkingAllocationRepository.findParkingAllocationsByMemberAndStatus(member, Status.INACTIVE)
                .forEach(invoice::addItem);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        logger.info("Created invoice {} for member {} with {} item(s)",
                savedInvoice.getReference(), memberId, savedInvoice.getItems().size());
        return invoiceMapper.toDto(savedInvoice);
    }
}