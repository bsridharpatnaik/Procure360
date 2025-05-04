package com.gb.p360.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItemStatusUpdateDTO {
    private String status;
    private String remarks;
}