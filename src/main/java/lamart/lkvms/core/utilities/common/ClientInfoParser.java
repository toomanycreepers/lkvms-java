package lamart.lkvms.core.utilities.common;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;

public class ClientInfoParser {

    private ClientInfoParser(){}

    public static String getBrowser(HttpServletRequest request) {
        String userAgentHeader = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);
        Browser browser = userAgent.getBrowser();
        return browser.getGroup().getName();
    }
    
    public static String getDevice(HttpServletRequest request) {
        String userAgentHeader = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);
        OperatingSystem os = userAgent.getOperatingSystem();
        return String.format("%s %s", 
            os.getGroup().getName(),
            os.getDeviceType().getName());
    }
    
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0];
    }
}
