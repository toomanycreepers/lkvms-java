package lamart.lkvms.core.utilities.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpChecker {

    private IpChecker(){}

    public static boolean isCorrectGlobalIp(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return !inetAddress.isAnyLocalAddress() && 
                   !inetAddress.isLoopbackAddress() &&
                   !inetAddress.isLinkLocalAddress() &&
                   !inetAddress.isSiteLocalAddress() &&
                   !inetAddress.isMulticastAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
