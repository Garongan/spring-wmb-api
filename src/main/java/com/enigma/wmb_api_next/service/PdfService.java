package com.enigma.wmb_api_next.service;

import com.enigma.wmb_api_next.dto.response.BillResponse;

import java.io.IOException;
import java.util.List;

public interface PdfService {
    byte[] generatePdf(List<BillResponse> bills) throws IOException;
}
