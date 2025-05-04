package com.gb.p360.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryDTO {
    private Long materialId;
    private String materialName;
    private Long vendorId;
    private String vendorName;
    private BigDecimal unitPrice;
    private LocalDate orderDate;
}