package com.liato.bankdroid.banking.banks;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.InputType;

import com.liato.bankdroid.R;
import com.liato.bankdroid.banking.Account;
import com.liato.bankdroid.banking.Bank;
import com.liato.bankdroid.banking.exceptions.BankChoiceException;
import com.liato.bankdroid.banking.exceptions.BankException;
import com.liato.bankdroid.banking.exceptions.LoginException;
import com.liato.bankdroid.provider.IBankTypes;

public class Chalmrest extends Bank {
	private static final String TAG = "Chalmrest";
	private static final String NAME = "Chalmrest";
	private static final String NAME_SHORT = "chalmrest";
	private static final int BANKTYPE_ID = IBankTypes.CHALMREST;

	public Chalmrest(Context context) {
		super(context);

		super.TAG = TAG;
		super.NAME = NAME;
		super.NAME_SHORT = NAME_SHORT;
		super.BANKTYPE_ID = BANKTYPE_ID;
		super.INPUT_TITLETEXT_USERNAME = R.string.card_number;
		super.INPUT_HINT_USERNAME = "XXXXXXXXXXXXXXXX";
		super.INPUT_TYPE_USERNAME = InputType.TYPE_CLASS_NUMBER;
		super.INPUT_HIDDEN_PASSWORD = true;
	}

	public Chalmrest(String username, String password, Context context) throws BankException, LoginException, BankChoiceException {
		this(context);
		this.update(username, password);
	}

	@Override
	public void update() throws BankException, LoginException, BankChoiceException {
		super.update();
		if (username == null || username.length() == 0) 
			throw new LoginException(res.getText(R.string.invalid_username_password).toString());
		
		try {
			String cardNr = username;
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet("http://emilan.se/chalmrest/?nr=" + cardNr);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity == null)
				throw new BankException("Couldn't connect!");
			
		    String s1 = EntityUtils.toString(entity);
            String[] parts = s1.split("\n");
            if (parts.length != 2)
                throw new BankException("Invalid data!");

            String name = parts[0];
            String value = parts[1].replace(',', '.');
		    
		    accounts.add(new Account(name, BigDecimal.valueOf(Double.parseDouble(value)), "1"));
		}
		catch (Exception e)
		{
			throw new BankException(e.getMessage());
		}
		finally {
			super.updateComplete();
		}
	}
}
