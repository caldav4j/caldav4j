/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.caldav;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.UUID;

public class Preferences {

    // Preferences file
    private static final String PREFERENCES_FILE = "AndroidCalendar.Main";

    // Preferences field names
    private static final String ACCOUNT_UUIDS = "accountUuids";

    private static Preferences preferences;

    SharedPreferences mSharedPreferences;

    private Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    /**
     * TODO need to think about what happens if this gets GCed along with the
     * Activity that initialized it. Do we lose ability to read Preferences in
     * further Activities? Maybe this should be stored in the Application
     * context.
     */
    public static synchronized Preferences getPreferences(Context context) {
        if (preferences == null) {
            preferences = new Preferences(context);
        }
        return preferences;
    }

    /**
     * Returns an array of the accounts on the system. If no accounts are
     * registered the method returns an empty array.
     */
    public Account[] getAccounts() {
        String accountUuids = mSharedPreferences.getString(ACCOUNT_UUIDS, null);
        if (accountUuids == null || accountUuids.length() == 0) {
            return new Account[] {};
        }
        String[] uuids = accountUuids.split(",");
        Account[] accounts = new Account[uuids.length];
        for (int i = 0, length = uuids.length; i < length; i++) {
            accounts[i] = new Account(this, uuids[i]);
        }
        return accounts;
    }
    public boolean accountExists(Account a) {
        String accountUuids = mSharedPreferences.getString(ACCOUNT_UUIDS, null);
        if (accountUuids == null || accountUuids.length() == 0) {
            return false;
        }
        String[] uuids = accountUuids.split(",");
        Account tmp;
        for (int i = 0, length = uuids.length; i < length; i++) {
            tmp = new Account(this, uuids[i]);
            if (tmp.equals (a))
                return true;
        }
        return false;
    }

    public void save() {
    }

    public void clear() {
        mSharedPreferences.edit().clear().commit();
    }
}
