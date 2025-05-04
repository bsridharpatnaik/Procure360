package com.gb.p360.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementRequestDTO {
    private Long factoryId;
    private String priority;
    private String remarks;
    private List<LineItemDTO> lineItems;
}