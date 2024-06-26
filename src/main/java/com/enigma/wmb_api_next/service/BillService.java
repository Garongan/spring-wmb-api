package com.enigma.wmb_api_next.service;

import com.enigma.wmb_api_next.dto.request.BillRequest;
import com.enigma.wmb_api_next.dto.request.SearchBillRequest;
import com.enigma.wmb_api_next.dto.request.UpdateBillRequest;
import com.enigma.wmb_api_next.dto.response.BillResponse;
import org.springframework.data.domain.Page;


public interface BillService {
    BillResponse save(BillRequest request);

    BillResponse getById(String id);

    Page<BillResponse> getAll(SearchBillRequest request);

    void updateStatusPayment(UpdateBillRequest request);
}
