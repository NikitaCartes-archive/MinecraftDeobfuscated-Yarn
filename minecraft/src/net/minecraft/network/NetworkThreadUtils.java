package net.minecraft.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.thread.ThreadExecutor;
import org.slf4j.Logger;

public class NetworkThreadUtils {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ServerWorld world) throws OffThreadException {
		forceMainThread(packet, listener, world.getServer());
	}

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ThreadExecutor<?> engine) throws OffThreadException {
		if (!engine.isOnThread()) {
			engine.executeSync(() -> {
				if (listener.accepts(packet)) {
					try {
						packet.apply(listener);
					} catch (Exception var4) {
						if (var4 instanceof CrashException crashException && crashException.getCause() instanceof OutOfMemoryError) {
							throw method_59854(var4, packet, listener);
						}

						listener.method_59807(packet, var4);
					}
				} else {
					LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
				}
			});
			throw OffThreadException.INSTANCE;
		}
	}

	public static <T extends PacketListener> CrashException method_59854(Exception exception, Packet<T> packet, T packetListener) {
		if (exception instanceof CrashException crashException) {
			fillCrashReport(crashException.getReport(), packetListener, packet);
			return crashException;
		} else {
			CrashReport crashReport = CrashReport.create(exception, "Main thread packet handler");
			fillCrashReport(crashReport, packetListener, packet);
			return new CrashException(crashReport);
		}
	}

	private static <T extends PacketListener> void fillCrashReport(CrashReport report, T listener, Packet<T> packet) {
		CrashReportSection crashReportSection = report.addElement("Incoming Packet");
		crashReportSection.add("Type", (CrashCallable<String>)(() -> packet.getPacketId().toString()));
		crashReportSection.add("Is Terminal", (CrashCallable<String>)(() -> Boolean.toString(packet.transitionsNetworkState())));
		crashReportSection.add("Is Skippable", (CrashCallable<String>)(() -> Boolean.toString(packet.isWritingErrorSkippable())));
		listener.fillCrashReport(report);
	}
}
