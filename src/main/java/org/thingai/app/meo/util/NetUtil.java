package org.thingai.app.meo.util;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public final class NetUtil {
    private NetUtil() {
    }

    // Resolve the gateway's LAN IPv4 address — the broker host written to
    // devices during provisioning. Primary: connecting a UDP socket toward a
    // public address exposes the local address of the default-route interface
    // (no packet is ever sent). Fallback: first site-local IPv4 on any
    // non-loopback interface that is up. Returns null when no address is found.
    public static String lanIpv4() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53));
            InetAddress local = socket.getLocalAddress();
            if (local instanceof Inet4Address && !local.isLoopbackAddress() && !local.isAnyLocalAddress()) {
                return local.getHostAddress();
            }
        } catch (Exception ignored) {
            // fall through to interface scan
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nic = interfaces.nextElement();
                if (!nic.isUp() || nic.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nic.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
            // no usable interface
        }
        return null;
    }
}
