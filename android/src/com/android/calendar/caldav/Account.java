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

import java.util.Arrays;
import java.util.UUID;
import android.util.Log;

/**
 * Account stores all of the settings for a single account defined by the user. It is able to save
 * and delete itself given a Preferences to work with. Each account is defined by a UUID. 
 */
public class Account {

    // transient values - do not serialize
    private transient Preferences mPreferences;

    // serialized values
    String mUuid;
    String mName;
    String mUser;
    String mPassword;
    String mHost;
    String mProtocol;
    String mHome;
    String mCollection;
    int mPort;
    int mAccountNumber;

    public Account(Context context) {
        mUuid = UUID.randomUUID().toString();
        mName = null;
        mUser= null;
        mPassword = null;
        mHost = null;
        mProtocol = null;
        mHome = null;
        mCollection = null;
        mPort = -1;
        mAccountNumber = -1;
    }
    public Account(Context context, String name, String user, String password, String host, String proto,
            String home, String coll, int port) {
        mUuid = UUID.randomUUID().toString();
        mName = name;
        mUser= user;
        mPassword = password;
        mHost = host;
        mProtocol = proto;
        mHome = home;
        mCollection = coll;
        mPort = port;
        mAccountNumber = -1;
    }

    Account(Preferences preferences, String uuid) {
        this.mUuid = uuid;
        refresh(preferences);
    }
    
    /**
     * Refresh the account from the stored settings.
     */
    public void refresh(Preferences preferences) {
        mPreferences = preferences;

        mName = preferences.mSharedPreferences.getString(mUuid + ".name", mName);
        mUser = preferences.mSharedPreferences.getString(mUuid + ".username", mUser);
        mPassword = preferences.mSharedPreferences.getString(mUuid + ".password", mPassword);
        mHost = preferences.mSharedPreferences.getString(mUuid + ".host", mHost);
        mProtocol = preferences.mSharedPreferences.getString(mUuid + ".protocol", mProtocol);
        mHome = preferences.mSharedPreferences.getString(mUuid + ".home", mHome);
        mCollection = preferences.mSharedPreferences.getString(mUuid + ".collection", mCollection);
        mPort = preferences.mSharedPreferences.getInt(mUuid + ".port", mPort);
        mAccountNumber = preferences.mSharedPreferences.getInt(mUuid + ".accountNumber", mAccountNumber);
    }

	public String getName()
	{
		return mName;
	}
	public void setName(String aName){
		mName=aName;
	}

	public String getUser()
	{
		return mUser;
	}
	public void setUser(String aUser){
		mUser=aUser;
	}

	public String getPassword()
	{
		return mPassword;
	}
	public void setPassword(String aPassword){
		mPassword=aPassword;
	}

	public String getHost()
	{
		return mHost;
	}
	public void setHost(String aHost){
		mHost=aHost;
	}

	public String getProtocol()
	{
		return mProtocol;
	}
	public void setProtocol(String aProtocol){
		mProtocol=aProtocol;
	}

	public String getHome()
	{
		return mHome;
	}
	public void setHome(String aHome){
		mHome=aHome;
	}

	public String getCollection()
	{
		return mCollection;
	}
	public void setCollection(String aCollection){
		mCollection=aCollection;
	}

	public int getPort()
	{
		return mPort;
	}
	public void setPort(int aPort){
		mPort=aPort;
	}

    public int getAccountNumber () {
        return mAccountNumber;
    }
    
    public String getEmail () {
        return mUser + "@" + mHost;
    }
    public String getUrl () {
        return mProtocol + "://" + mHost + mHome + mCollection;
    }

    public void delete(Preferences preferences) {
        String[] uuids = preferences.mSharedPreferences.getString("accountUuids", "").split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = uuids.length; i < length; i++) {
            if (!uuids[i].equals(mUuid)) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(uuids[i]);
            }
        }
        String accountUuids = sb.toString();
        SharedPreferences.Editor editor = preferences.mSharedPreferences.edit();
        editor.putString("accountUuids", accountUuids);

        editor.remove(mUuid + ".name");
        editor.remove(mUuid + ".user");
        editor.remove(mUuid + ".password");
        editor.remove(mUuid + ".host");
        editor.remove(mUuid + ".protocol");
        editor.remove(mUuid + ".port");
        editor.remove(mUuid + ".home");
        editor.remove(mUuid + ".collection");
        editor.remove(mUuid + ".accountNumber");
        
        editor.commit();
    }

    public void save(Preferences preferences) {
        mPreferences = preferences;
        
        if (!preferences.mSharedPreferences.getString("accountUuids", "").contains(mUuid)) {
            /*
             * When the account is first created we assign it a unique account number. The
             * account number will be unique to that account for the lifetime of the account.
             * So, we get all the existing account numbers, sort them ascending, loop through
             * the list and check if the number is greater than 1 + the previous number. If so
             * we use the previous number + 1 as the account number. This refills gaps.
             * mAccountNumber starts as -1 on a newly created account. It must be -1 for this
             * algorithm to work.
             * 
             * I bet there is a much smarter way to do this. Anyone like to suggest it?
             */
            Account[] accounts = preferences.getAccounts();
            int[] accountNumbers = new int[accounts.length];
            for (int i = 0; i < accounts.length; i++) {
                accountNumbers[i] = accounts[i].getAccountNumber();
            }
            Arrays.sort(accountNumbers);
            for (int accountNumber : accountNumbers) {
                if (accountNumber > mAccountNumber + 1) {
                    break;
                }
                mAccountNumber = accountNumber;
            }
            mAccountNumber++;
            
            String accountUuids = preferences.mSharedPreferences.getString("accountUuids", "");
            accountUuids += (accountUuids.length() != 0 ? "," : "") + mUuid;
            SharedPreferences.Editor editor = preferences.mSharedPreferences.edit();
            editor.putString("accountUuids", accountUuids);
            editor.commit();
        }

        SharedPreferences.Editor editor = preferences.mSharedPreferences.edit();

        editor.putString(mUuid + ".name", mName);
        editor.putString(mUuid + ".username", mUser);
        editor.putString(mUuid + ".password", mPassword);
        editor.putString(mUuid + ".host", mHost);
        editor.putString(mUuid + ".protocol", mProtocol);
        editor.putString(mUuid + ".home", mHome);
        editor.putString(mUuid + ".collection", mCollection);
        editor.putInt   (mUuid + ".port", mPort);
        editor.putInt   (mUuid + ".accountNumber", mAccountNumber);
        editor.commit();
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Account) {
            Account a = (Account)o;
            Log.d ("CalDAVAccount", "comparing 1 " + mUser + mHost + mCollection + mHome + " 2 " + a.getUser
                    () + a.getHost () + a.getCollection () + a.getHome ());
            return a.getUser ().equals (mUser) && 
                   a.getHost ().equals (mHost) && 
                   a.getCollection ().equals (mCollection) &&
                   a.getHome ().equals (mHome);
        }
        return super.equals(o);
    }
}
