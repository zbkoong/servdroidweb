# Introduction #
ServDroid (v0.2 and above) is able to open a socket under port 1024. This text explains how to use this feature.

# Requirements #
ServDroid needs the following:
  * A rooted device.
  * The iptables command.

Android applications are not allowed to open ports under 1024, so ServDroid uses the command iptables (nat option) to create a bridge connection between the port X and the fixed port 65485.

# How it works #
For example, if servDroid is configured to run on port 80, it will execute the following command as a super user:

_iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 65485_

And will open a socket using java on port 65485.

When the server is stopped, it cleans the iptables nat table.

# Configuring the server to run under 1024 port #

To configure the server is simple, just go to _Preferences_ --> _Port_ and write a port under 1024. Then ServDroid will check if the device has the minimum requirements or not.

# Opening the ports under 1024 using the console #
It is possible to open port under 1024 if your device has the needed requirements.
If you want to open a port using console just use the following command line as a **super user**:

_iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080_

Finally you just need to configure the server to run on port 8080