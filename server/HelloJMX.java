import java.lang.management.*;
import javax.management.*;

public class HelloJMX implements Runnable{
    private MBeanServer mbs = null;
    public HelloJMX() {
        try {
            //            mbs = MBeanServerFactory.createMBeanServer("HelloJMX");
            mbs = ManagementFactory.getPlatformMBeanServer();
            Hello h = new Hello();
            ObjectName helloName = new ObjectName("HelloJMX:name=Hello");
            mbs.registerMBean(h, helloName);
            System.out.println("Bean registered");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    public void run() {
        while(true) {
            try {
                Thread.sleep(300);
            } catch (Exception e) {
            }
        }
    }
    public static void main (String args[]) {
        System.out.println("Starting HelloJMX");
        try {
            HelloJMX hjmx = new HelloJMX();
            Thread helloThread = new Thread(hjmx, "HelloJMX");
            helloThread.start();
            System.out.println("Thread running");
        } catch (Exception e) {
        }
    }
}