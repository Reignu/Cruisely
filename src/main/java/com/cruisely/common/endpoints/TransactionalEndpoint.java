package com.cruisely.common.endpoints;

/**
 * Interface defining an Endpoint that initiates application transactions
 */
public interface TransactionalEndpoint {
    /**
     * Method determines whether the last transaction executed within the implementing endpoint was rolled back
     * @return Boolean indicating whether the transaction initiated by the method was rolled back
     */
    boolean isTransactionRolledBack();

    /**
     * Returns the identifier of the last application transaction within the implementing endpoint
     * @return Transaction identifier
     */
    String getLastTransactionID();
}
