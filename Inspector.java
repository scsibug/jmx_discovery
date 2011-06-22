import java.util.List;
import java.lang.management.*;
import java.io.*;
import java.util.*;
import javax.management.*;
import javax.management.remote.*;
import com.sun.tools.attach.*;

// Credit for original sample code: http://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api

public class Inspector {
    private String name = null;
    private String connectorAddr = null;

    public Inspector(String name) {
        this.name = name;
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        
        for (VirtualMachineDescriptor vmd: vms) {
            System.out.println("Found VM: "+vmd.displayName());
            if (vmd.displayName().equals(this.name)) {
                try {
                    VirtualMachine vm = VirtualMachine.attach(vmd.id());
                    connectorAddr = vm.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");
                    System.out.println("    connectorAddress: "+connectorAddr);
                } catch (Exception e) {
                    System.err.println("Exception iterating through VMs");
                    System.err.println(e);
                }
            }
        }
    }

    public static void main(String args[]) {
        System.out.println("Inspector starting");
        Inspector i = new Inspector("HelloJMX");
        try {
            i.printThreads();
        } catch (Exception e) {
            System.err.println("Exception trying to print threads");
            System.err.println(e);
        }
    }

    public void printThreads() throws Exception {
        System.out.println("Creating service URL");
        JMXServiceURL serviceURL = new JMXServiceURL(connectorAddr);
        System.out.println("Connecting to JMX");
        JMXConnector connector = JMXConnectorFactory.connect(serviceURL); 
        System.out.println("Getting MBeanServer connection");
        MBeanServerConnection mbsc = connector.getMBeanServerConnection(); 
        ObjectName objName = new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME);
        Set<ObjectName> mbeans = mbsc.queryNames(objName, null);
        System.out.println("Iterating through query results");        
        for (ObjectName name: mbeans) {
            ThreadMXBean threadBean;
            threadBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, name.toString(), ThreadMXBean.class);
            long threadIds[] = threadBean.getAllThreadIds();
            for (long threadId: threadIds) {
                ThreadInfo threadInfo = threadBean.getThreadInfo(threadId);
                System.out.println (threadInfo.getThreadName() + " / " +
                                    threadInfo.getThreadState());
            }
        }
    }
}

