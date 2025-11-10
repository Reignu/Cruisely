package com.cruisely.exceptions;

public class TransactionRepeaterException extends BaseAppException {
    public TransactionRepeaterException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionRepeaterException(String message) {
        super(message);
    }

    public static TransactionRepeaterException transactionStatusRolledBack(String transactionID) throws TransactionRepeaterException {
        throw new TransactionRepeaterException("Transaction with ID " + transactionID + " resulted in ROLLED BACK status, despite not throwing any of mapped exceptions");
    }
}
