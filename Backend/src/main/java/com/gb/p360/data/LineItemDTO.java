package com.gb.p360.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItemDTO {
    private String materialName;
    private String unit;
    private BigDecimal requestedQuantity;
    private BigDecimal orderedQuantity;
    private String vendorName;
    private BigDecimal unitPrice;
    private BigDecimal totalOrderValue;
    private String remarks;
}