OpenMRS-Module-Messaging
========================

The Messaging Module allows you to send all kinds of messages, including email and SMS. It also helps you handle various ancillary aspects of messaging, including managing addresses and browsing past conversations. Recently, it has an easy-to-use API so that other modules can incorporate messaging into their own projects.

Developer Quick Start
========================

# Sending a Message

To send an SMS message, use the following code:
This sends the message "Hello, world!" to the number 18007654321 via SMS. If the supplied phone number or message are badly formatted, an exception will be thrown.


Sending a Message to Multiple Recipients
To send a message to multiple recipients, you must create a Message object and add messaging addresses as recipients. You should retrieve 


# Listening for Received Messages

To register a listener that will receive alerts when a message comes in, use this line of code:
The listener must implement the IncomingMessageListener interface.


For full documentation, please visit: https://wiki.openmrs.org/display/docs/Messaging+Module
