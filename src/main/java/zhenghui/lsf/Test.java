package zhenghui.lsf;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * User: zhenghui
 * Date: 13-11-29
 * Time: 下午1:12
 */
public class Test <T>{

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(getIP());
    }

    private static String getNetworkAddress() {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            List<InetAddress> addressList = new ArrayList<InetAddress>();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces
                        .nextElement();
                Enumeration<InetAddress> addresses=ni.getInetAddresses();
                while(addresses.hasMoreElements()){
                    ip = addresses.nextElement();
                    System.out.println(ip.isLoopbackAddress());
//                    if (!ip.isLoopbackAddress()
//                            && ip.getHostAddress().indexOf(":") == -1) {
//                        return ip.getHostAddress();
//                    } else {
//                        continue;
//                    }
                    addressList.add(ip);
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private static String getIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
