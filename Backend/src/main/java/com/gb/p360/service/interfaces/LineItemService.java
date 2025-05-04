package com.gb.p360.service.interfaces;

import com.gb.p360.data.LineItemDTO;
import com.gb.p360.data.LineItemStatusUpdateDTO;
import com.gb.p360.models.LineItem;

import java.util.List;

public interface LineItemService {
    LineItem updateLineItemStatus(Long id, LineItemStatusUpdateDTO updateDTO, String username);

    LineItem updateOrderDetails(Long id, LineItemDTO lineItemDTO, String username);

    LineItem markAsReceived(Long id, String remarks, String username);

    List<LineItem> getLineItemsByRequestId(Long requestId);

    LineItem getLineItemById(Long id);
}