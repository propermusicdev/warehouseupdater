package com.proper.data.diagnostics;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Lebel on 03/03/14.
 */
public class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private long TransactionId;
    private String ApplicationName;
    private String MethodName;
    private String DeviceIMEI;
    private String ExceptionName;
    private String LogMessage;
    private Timestamp LogDate;

    public LogEntry() {
    }

    public LogEntry(long transactionId, String applicationName, String methodName, String deviceIMEI, String exceptionName, String logMessage, Timestamp logDate) {
        TransactionId = transactionId;
        ApplicationName = applicationName;
        MethodName = methodName;
        DeviceIMEI = deviceIMEI;
        ExceptionName = exceptionName;
        LogMessage = logMessage;
        LogDate = logDate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(long transactionId) {
        TransactionId = transactionId;
    }

    public String getApplicationName() {
        return ApplicationName;
    }

    public void setApplicationName(String applicationName) {
        ApplicationName = applicationName;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public String getDeviceIMEI() {
        return DeviceIMEI;
    }

    public void setDeviceIMEI(String deviceIMEI) {
        DeviceIMEI = deviceIMEI;
    }

    public String getExceptionName() {
        return ExceptionName;
    }

    public void setExceptionName(String exceptionName) {
        ExceptionName = exceptionName;
    }

    public String getLogMessage() {
        return LogMessage;
    }

    public void setLogMessage(String logMessage) {
        LogMessage = logMessage;
    }

    public Timestamp getLogDate() {
        return LogDate;
    }

    public void setLogDate(Timestamp logDate) {
        LogDate = logDate;
    }
}
