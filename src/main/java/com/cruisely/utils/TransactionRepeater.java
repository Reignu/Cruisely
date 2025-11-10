package com.cruisely.utils;

import lombok.extern.java.Log;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.TransactionRepeaterException;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.ejb.EJBTransactionRolledbackException;
import javax.interceptor.Interceptors;
import java.util.Properties;

/**
 * Utility class for retrying application transactions when transient errors occur
 */
@Interceptors(TrackingInterceptor.class)
@Log
public class TransactionRepeater {
    private static final Properties securityProperties = PropertiesReader.getSecurityProperties();
    private static final int REPEAT_COUNT = Integer.parseInt(securityProperties.getProperty("transaction.repeat.count"));

    /**
     * Executes the provided void operation and retries it on transient transaction failures.
     *
     * @param endpoint        Endpoint that starts the transaction
     * @param repeatInterface Functional interface wrapping the operation to retry
     * @throws BaseAppException Base application exception
     */
    public static void tryAndRepeat(TransactionalEndpoint endpoint, RepeatVoidInterface repeatInterface) throws BaseAppException {
        for (int i = 0; i < REPEAT_COUNT; i++) {
            try {
                repeatInterface.execute();
                checkTransactionStatus(endpoint);
                return;
            } catch (BaseAppException | EJBTransactionRolledbackException e) {
                log.warning(getRepeatLogMessage(e, i + 1));
            }
        }
        repeatInterface.execute();
    }

    /**
     * Executes the provided operation that returns a value and retries it on transient transaction failures.
     *
     * @param endpoint        Endpoint that starts the transaction
     * @param repeatInterface Functional interface wrapping the operation to retry
     * @param <T>             Return type of the operation
     * @return Result of the operation
     * @throws BaseAppException Base application exception
     */
    public static <T> T tryAndRepeat(TransactionalEndpoint endpoint, RepeatResultInterface<T> repeatInterface) throws BaseAppException {
        for (int i = 0; i < REPEAT_COUNT; i++) {
            try {
                T result = repeatInterface.execute();
                checkTransactionStatus(endpoint);
                return result;
            } catch (BaseAppException | EJBTransactionRolledbackException e) {
                log.warning(getRepeatLogMessage(e, i + 1));
            }
        }
        return repeatInterface.execute();
    }

    /**
     * Functional interface that wraps a void operation which may throw BaseAppException
     */
    @FunctionalInterface
    public interface RepeatVoidInterface {

        void execute() throws BaseAppException;
    }

    /**
     * Functional interface that wraps an operation returning a value which may throw BaseAppException
     */
    @FunctionalInterface
    public interface RepeatResultInterface<T> {

        T execute() throws BaseAppException;
    }

    private static void checkTransactionStatus(TransactionalEndpoint transactionEndpoint) throws TransactionRepeaterException {
        if (transactionEndpoint.isTransactionRolledBack()) {
            throw TransactionRepeaterException.transactionStatusRolledBack(transactionEndpoint.getLastTransactionID());
        }
    }

    private static String getRepeatLogMessage(Throwable t, int repeatNumber) {
        return String.format("Exception %s caught in repeat interface" +
                "\nProceeding to transaction repeat number: %s ", t.getMessage(), repeatNumber);
    }
}


