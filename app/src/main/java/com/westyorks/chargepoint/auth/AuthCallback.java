package com.westyorks.chargepoint.auth;

/**
 * Callback interface for Firebase authentication operations
 */
public interface AuthCallback {
    /**
     * Called when the authentication operation is successful
     */
    void onSuccess();

    /**
     * Called when the authentication operation fails
     * @param error The error message describing what went wrong
     */
    void onError(String error);
}
