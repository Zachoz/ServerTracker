package com.zachdekoning.servertracker;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class TwilioUtils {

    public static String ACCOUNT_SID;
    public static String AUTH_TOKEN;
    public static String PHONE_NUMBER_FROM;

    public static boolean sendSMS(String phoneNumberTo, String text) {
        try {
            TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

            // Build a filter for the MessageList
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Body", text));
            params.add(new BasicNameValuePair("To", phoneNumberTo));
            params.add(new BasicNameValuePair("From", PHONE_NUMBER_FROM));

            MessageFactory messageFactory = client.getAccount().getMessageFactory();
            Message message = messageFactory.create(params);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false; // Failed to send SMS
        }

        return true;
    }
}
