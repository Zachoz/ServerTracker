ServerTracker
==============

A simple Java application to monitor multiple servers or services and send a SMS message to your mobile phone when a server goes offline and comes back online.
ServerTracker uses Twilio to send SMS messages to your phone.

##Running:
I would recommend running this in a screen to keep it running
```
java -jar ServerTracker.jar
```

#####Optional Java arguments:
-Dconf=/path/to/config.xml - Defines the file path of config.xml

**Example:**
```
java -Dconf=/path/to/config.xml -jar ServerTracker.jar
```

##Configuration:
The configuration is handled via the config.xml file. This file will be automatically created when the jar file is first run.

###General settings:

* **queryInterval:** Seconds between each query of all servers
* **sendBackOnlineAlert:** Whether or not to send an alert that the server has come back online

```xml
<general>
    <queryInterval>30</queryInterval>
    <sendBackOnlineAlert>true</sendBackOnlineAlert>
</general>
```

###Twilio settings:
To use this program a [Twilio](https://www.twilio.com/) account is required. Twilio is a paid service, however a free trial is available. View their website to find SMS pricing and trial account information.

You can find your sid and token at twilio.com/user/account

* **fromPhoneNumber:** The phone number on your twilio account that the sms will be sent through
* **toPhoneNumber:** The phone number that the sms will be sent to

```xml
<twilio>
    <sid>AC6ea710fee11a454536597w3678gfgf</sid>
    <authId>454873tr684t8fb4vf4874944</authId>
    <fromPhoneNumber>+10000000000</fromPhoneNumber>
    <toPhoneNumber>+61000000000</toPhoneNumber>
</twilio>

```

###Servers settings:
Servers settings are pretty self explanatory:

* **name:** The name of the server/service, this is what will be shown in the SMS
* **hostname:** the hostname or IP address of the server or service
* **port:** the port of the server or service

You may test multiple ports for different services on the same hostname. You may also add as many services inbetween the servers tag as you wish

```xml
<servers>
    <server>
        <name>Website</name>
        <hostname>example.com</hostname>
        <port>80</port>
    </server>
    <server>
        <name>VPS</name>
        <hostname>vps.example.com</hostname>
        <port>22</port>
    </server>
<servers>
```

###Disclaimer:
I'm not responsible for any fees or charges you may incur from Twilio from using this application. This application will send 2 sms messages in a downtime event, one for when it goes down and one for when it comes back online.
I recommend setting spending limits or only leaving a small amount of credit in your account. Additionally a free trial account may suffice for your usage.