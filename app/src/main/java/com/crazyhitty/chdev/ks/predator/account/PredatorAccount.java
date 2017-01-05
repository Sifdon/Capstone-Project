/*
 * MIT License
 *
 * Copyright (c) 2016 Kartik Sharma
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.crazyhitty.chdev.ks.predator.account;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;

import rx.Observable;
import rx.Subscriber;

/**
 * Author:      Kartik Sharma
 * Email Id:    cr42yh17m4n@gmail.com
 * Created:     1/2/2017 11:07 AM
 * Description: Unavailable
 */

public class PredatorAccount {
    private PredatorAccount() {

    }

    /**
     * Add a new account to the device for this application, this account can either be either
     * contain a client token or user token.
     *
     * @param context       Current context of the application
     * @param accountName   Name of the account
     * @param accountType   Account type
     * @param authToken     Authentication token
     * @param authTokenType Type of the authentication token
     */
    @SuppressWarnings("MissingPermission")
    @RequiresPermission(Manifest.permission.GET_ACCOUNTS)
    public static void addAccount(Context context,
                                  String accountName,
                                  String accountType,
                                  String authToken,
                                  String authTokenType) {
        Account account = new Account(accountName, accountType);
        AccountManager accountManager = AccountManager.get(context);

        // Check if the user is new and has no previous accounts available.
        if (accountManager.getAccounts() == null ||
                accountManager.getAccounts().length == 0) {
            // Add a new account if no other account are available.
            accountManager.addAccountExplicitly(account, null, null);
        }

        // Set the authentication code.
        accountManager.setAuthToken(account, authTokenType, authToken);
    }

    /**
     * Get a authentication token if available, otherwise add a new account and then get a new
     * authentication token.
     *
     * @param activity      Current activity
     * @param accountType   Name of the account
     * @param authTokenType Type of the authentication token
     * @return An string observable which will provide auth token
     */
    public static Observable<String> getAuthToken(final Activity activity,
                                                  final String accountType,
                                                  final String authTokenType) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                AccountManager accountManager = AccountManager.get(activity.getApplicationContext());
                accountManager.getAuthTokenByFeatures(accountType,
                        authTokenType,
                        null,
                        activity,
                        null,
                        null,
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(AccountManagerFuture<Bundle> future) {
                                Bundle bundle = null;
                                try {
                                    bundle = future.getResult();
                                    final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                    subscriber.onNext(authToken);
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        },
                        null);
            }
        });
    }
}