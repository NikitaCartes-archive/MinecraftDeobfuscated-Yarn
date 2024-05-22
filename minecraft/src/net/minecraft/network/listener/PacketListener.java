package net.minecraft.network.listener;

import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

/**
 * A packet listener listens to packets on a {@linkplain
 * net.minecraft.network.ClientConnection connection}.
 * 
 * <p>Its listener methods will be called on the netty event loop than the
 * client or server game engine threads.
 */
public interface PacketListener {
	NetworkSide getSide();

	NetworkPhase getPhase();

	/**
	 * Called when the connection this listener listens to has disconnected.
	 * Can be used to display the disconnection reason.
	 */
	void onDisconnected(DisconnectionInfo info);

	default void onPacketException(Packet packet, Exception exception) throws CrashException {
		throw NetworkThreadUtils.createCrashException(exception, packet, this);
	}

	default DisconnectionInfo createDisconnectionInfo(Text reason, Throwable exception) {
		return new DisconnectionInfo(reason);
	}

	boolean isConnectionOpen();

	default boolean accepts(Packet<?> packet) {
		return this.isConnectionOpen();
	}

	default void fillCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Connection");
		crashReportSection.add("Protocol", (CrashCallable<String>)(() -> this.getPhase().getId()));
		crashReportSection.add("Flow", (CrashCallable<String>)(() -> this.getSide().toString()));
		this.addCustomCrashReportInfo(report, crashReportSection);
	}

	default void addCustomCrashReportInfo(CrashReport report, CrashReportSection section) {
	}
}
