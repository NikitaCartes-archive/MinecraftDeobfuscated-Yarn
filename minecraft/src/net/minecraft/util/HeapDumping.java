package net.minecraft.util;

import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.annotation.Nullable;
import javax.management.MBeanServer;

/**
 * A heap-dumping (dumps all object data in a JVM) utility using
 * {@link HotSpotDiagnosticMXBean} from the Java Management Extensions API.
 */
public class HeapDumping {
	/**
	 * String representation of the {@code ObjectName} for the {@link HotSpotDiagnosticMXBean}.
	 */
	private static final String HOTSPOT_DIAGNOSTIC_MXBEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
	@Nullable
	private static HotSpotDiagnosticMXBean bean;

	private static HotSpotDiagnosticMXBean getBean() {
		if (bean == null) {
			try {
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				bean = (HotSpotDiagnosticMXBean)ManagementFactory.newPlatformMXBeanProxy(
					mBeanServer, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class
				);
			} catch (IOException var1) {
				throw new RuntimeException(var1);
			}
		}

		return bean;
	}

	/**
	 * Dumps the heap to the {@code outputFile} file in the same format as the hprof
	 * heap dump.
	 * 
	 * @see HotSpotDiagnosticMXBean#dumpHeap(String, boolean)
	 * 
	 * @param outputFile the system-dependent file name
	 * @param live if {@code true} dump only live objects, i.e. objects that are reachable from others
	 */
	public static void dump(String outputFile, boolean live) {
		try {
			getBean().dumpHeap(outputFile, live);
		} catch (IOException var3) {
			throw new RuntimeException(var3);
		}
	}
}
