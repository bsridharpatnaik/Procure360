package com.gb.p360.data;

import lombok.Data;
import org.springframework.lang.NonNull;

@Data
public class FileInformationDAO {
    @NonNull
    String fileUUId;
    @NonNull
    String fileName;
}
