package com.gb.p360.service;

import com.gb.p360.models.RequestLog;
import com.gb.p360.repository.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RequestLogService {

    @Autowired
    private RequestLogRepository requestLogRepository;

    @Async
    public void logRequest(String method, String url, String headers, String body) {
        RequestLog requestLog = new RequestLog();
        requestLog.setMethod(method);
        requestLog.setUrl(url);
        requestLog.setHeaders(headers);
        requestLog.setBody(body);
        requestLog.setTimestamp(new Date());
        requestLogRepository.save(requestLog);
    }
}

