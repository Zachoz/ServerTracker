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
* **queryTimeOut** Set the timeout for server query in milliseconds

```xml
<general>
    <queryInterval>30</queryInterval>
    <sendBackOnlineAlert>true</sendBackOnlineAlert>
    <queryTimeOut>6000</queryTimeOut>
</general>
```

###Twilio settings:
To use this program a [Twilio](https://www.twilio.com/) account is required. Twilio is a paid service, however a free trial is available. View their website to find SMS pricing and trial account information.

You can find your sid and token at twilio.com/user/account

* **fromPhoneNumber:** The phone number on your twilio account that the sms will be sent through

```xml
<twilio>
    <sid>AC6ea710fee11a454536597w3678gfgf</sid>
    <authId>454873tr684t8fb4vf4874944</authId>
    <fromPhoneNumber>+10000000000</fromPhoneNumber>
</twilio>

```

###Servers settings:
Servers settings are pretty self explanatory:

* **name:** The name of the server/service, this is what will be shown in the SMS
* **hostname:** the hostname or IP address of the server or service
* **port:** the port of the server or service
* **type:** optional, currently allows you to define it's a minecraft server or a http page
* **sendAlertTo:** list of phone numbers to send the sms alert to
* **phoneNumber:** a phone number to send the sms alert to

You may test multiple ports for different services on the same hostname. You may also add as many services inbetween the servers tag as you wish

Each server can send the downtime alert to a different phone number. This way you can alert certain sysadmins of the downtime of a specific service

Scroll to the bottom of this code snippet for information on http page monitoring

```xml
<servers>
    <server>
        <name>Website</name>
        <hostname>example.com</hostname>
        <port>80</port>
        <sendAlertTo>
            <phoneNumber>+61000000000</phoneNumber>
            <phoneNumber>+61000000000</phoneNumber>
        </sendAlertTo>
    </server>
    <server>
        <name>VPS</name>
        <hostname>vps.example.com</hostname>
        <port>22</port>
        <sendAlertTo>
            <phoneNumber>+61000000000</phoneNumber>
        </sendAlertTo>
    </server>
    <server>
        <name>Minecraft Server</name>
        <hostname>mc.example.com</hostname>
        <port>25565</port>
        <type>minecraft</type>
        <sendAlertTo>
            <phoneNumber>+61000000000</phoneNumber>
        </sendAlertTo>
    </server>
    <server>
        <name>Forums</name>
        <hostname>http://example.com/forums/</hostname>
        <port>80</port>
        <type>http</type>
        <triggerAlertForBlankPage>true</triggerAlertForBlankPage>
        <unexpectedContent>
            <content>An unexpected database error occurred. Please try again later.</content>
        </unexpectedContent>
        <sendAlertTo>
            <phoneNumber>+61000000000</phoneNumber>
        </sendAlertTo>
    </server>
<servers>
```

####Monitoring a http page
Unlike the other checks, this check will scan the web page for unexpected content within the page. For example, you could add a check that looks for the string "Error 404", if this was found on the page (or anywhere in the html, including the title, other tags, etc), you would be alerted.
A http check is configured similar to any other server, extra parametres are:
* **hostname:** Like other services, but instead put the URL of the page. (Please put your direct url here, services such as cloudflare may interfere with the checks)
* **triggerAlertForBlankPage:** whether or not to treat a completely blank page as downtime
* **unexpectedContent:** a list of unexpected strings that will trigger a downtime event
* **content:** the string that if found on the webpage will trigger a downtime event

```xml
<server>
    <name>Forums</name>
    <hostname>http://example.com/forums/</hostname>
    <port>80</port>
    <type>http</type>
    <triggerAlertForBlankPage>true</triggerAlertForBlankPage>
    <unexpectedContent>
        <content>An unexpected database error occurred. Please try again later.</content>
    </unexpectedContent>
    <sendAlertTo>
        <phoneNumber>+61000000000</phoneNumber>
    </sendAlertTo>
</server>
```

###Notes:
* This program simple attempts to open a socket connection on the supplied hostnames and ports. If the socket connection is rejected, the program will issue a downtime event.
* For tracking a Minecraft server, include the tag ```<type>minecraft</type>``` in the server block
* It is recommended to not set the query time out lower than 6000 milliseconds, variables such as high latency could lead to a timeout and create a false postive for downtime
* A separate SMS message is sent for each downtime event. If all of your stuff went down at once, expect some SMS spam
* While Twilio's messaging is cheap, especially in countries like the US and UK, it still does cost money. Only leave a very small amount of credit in your account at once. A large false positive could otherwise be slightly costly
* Twilio's free trial account works fine with this, however the message will start with "Send from my Twilio trial account", and you will need to registered the recipient phone numbers with Twilio

###Disclaimer:
I'm not responsible for any fees or charges you may incur from Twilio from using this application. This application will send 2 sms messages in a downtime event, one for when it goes down and one for when it comes back online.
I recommend setting spending limits or only leaving a small amount of credit in your account. Additionally a free trial account may suffice for your usage.

###Compiling:
ServerTracker is compiled using maven. Run the following command inside the root directory:
```
mvn clean install
```