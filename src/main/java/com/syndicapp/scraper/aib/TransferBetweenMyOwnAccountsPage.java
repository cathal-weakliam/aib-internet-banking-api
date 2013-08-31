// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TransferBetweenMyOwnAccountsPage.java

package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.syndicapp.scraper.FSSUserAgent;
import com.syndicapp.scraper.aib.model.AccountDropdownItem;
import com.syndicapp.scraper.aib.model.AccountDropdownList;
import com.syndicapp.scraper.exception.UnexpectedPageContentsException;

public class TransferBetweenMyOwnAccountsPage extends FSSUserAgent {

	public TransferBetweenMyOwnAccountsPage() {
	}

	public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
			throws Exception {
		HashMap<String, Object> outputParams = new HashMap<String, Object>();
		String transactionToken = null;
		
		Pattern p = Pattern
				.compile("id=\"form2\" action=\"transfersandpaymentslanding.htm\" method=\"post\" onsubmit=\"return isClickEnabled\\(\\)\">\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
		Matcher m;
		for (m = p.matcher(page); m.find();)
			transactionToken = m.group(1);

		HttpPost httppost = new HttpPost(
				"https://aibinternetbanking.aib.ie/inet/roi/transfersandpaymentslanding.htm");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("iBankFormSubmission", "true"));
		nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
		nvps.add(new BasicNameValuePair("selectedPaymentType", "1"));
		log.fatal((new StringBuilder())
				.append("Clicking 'Transfers between my own accounts' with ")
				.append(nvps.toString()).toString());
		httppost.setEntity(new UrlEncodedFormEntity(nvps, "ISO-8859-1"));
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		page = EntityUtils.toString(entity);
		
		if (!page.contains("Transfer between my own AIB Accounts")) {
			throw new UnexpectedPageContentsException(
					"Didn't get to the Transfers between my own accounts Page!");
		}
		
		AccountDropdownList addlFrom = new AccountDropdownList();
		p = Pattern
				.compile("selectedFromAccountIndex\" class=\"jsIntroText0 aibInputStyle04\" onchange=\"return setDrNarrative\\(\\)\">\\s*<option value=\"-1\" selected=\"selected\">Please select:</option>\\s*(.*?)\\s*</select>");
		m = p.matcher(page);
		if (m.find()) {
			p = Pattern
					.compile("<option value=\"(\\d+)\">(.*?) \\(([\\s\\d\\.,DCR]*?)\\)</option>");
			m = p.matcher(m.group(1));
			while (m.find()) {
				addlFrom.addAccountDropdownItem(
						new AccountDropdownItem(m.group(1), m.group(2), ""));
				
				log.fatal((new StringBuilder()).append(m.group(0)).append(": ").append(m.group(1))
						.append(" - ").append(m.group(2)).append(" - ").append(m.group(3))
						.toString());
			}
		}

		AccountDropdownList addlTo = new AccountDropdownList();
		p = Pattern
				.compile("selectedToAccountIndex\" class=\"jsIntroText0 aibInputStyle04\">\\s*<option value=\"-1\" selected=\"selected\">Please select:</option>\\s*(.*?)\\s*</select>");
		m = p.matcher(page);
		if (m.find()) {
			p = Pattern
					.compile("<option value=\"(\\d+)\">(.*?) \\(([\\s\\d\\.,DCR]*?)\\)</option>");
			m = p.matcher(m.group(1)); 
			while(m.find()) {
				addlTo.addAccountDropdownItem(
						new AccountDropdownItem(m.group(1), m.group(2), ""));
				
				log.fatal((new StringBuilder())
					.append(m.group(0)).append(": ").append(m.group(1)).append(" - ")
					.append(m.group(2)).append(" - ").append(m.group(3)).toString());
			}
		}
		
		outputParams.put("accountsFrom", addlFrom);
		outputParams.put("accountsTo", addlTo);
		outputParams.put("page", page);
		return outputParams;
	}
}