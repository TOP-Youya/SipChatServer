package sipchat.manager;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by leeshun on 2017/7/11.
 */
public class SipProfile {
    private String localHostAddress;
    private int localHostPort;
    private String username;

    private static SipProfile sipProfile;

    private SipProfile(final String username,final int port) throws UnknownHostException {
        this.localHostAddress = InetAddress.getLocalHost().getHostAddress();
        this.localHostPort = port;
        this.username = username;
    }

    public static  SipProfile getInstance(final String username,final int port) {
        if(sipProfile == null) {
            try {
                sipProfile = new SipProfile(username,port);
            } catch (UnknownHostException e) {
                System.err.println("SIPProfile initialize error" + e.getMessage());
            }
        }

        return sipProfile;
    }

    public static SipProfile getInstance() throws
            NullPointerException{
        if(sipProfile == null) {
            throw new NullPointerException("SIPProfile don't initialized");
        }
        return sipProfile;
    }

    public String getLocalHostAddress() {
        return localHostAddress;
    }

    public int getLocalHostPort() {
        return localHostPort;
    }

    public String getUsername() {
        return username;
    }
}
