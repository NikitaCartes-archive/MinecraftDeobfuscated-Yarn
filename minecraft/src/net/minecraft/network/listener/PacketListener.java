package net.minecraft.network.listener;

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
	 * 
	 * @param reason the reason of disconnection; may be a generic message
	 */
	void onDisconnected(Text reason);

	default void method_59807(Packet packet, Exception exception) throws CrashException {
		throw NetworkThreadUtils.method_59854(exception, packet, this);
	}

	boolean isConnectionOpen();

	default boolean accepts(Packet<?> packet) {
		return this.isConnectionOpen();
	}

	default void fillCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Connection");
		crashReportSection.add("Protocol", (CrashCallable<String>)(() -> this.getPhase().getId()));
		crashReportSection.add("Flow", (CrashCallable<String>)(() -> this.getSide().toString()));
		this.addCustomCrashReportInfo(crashReportSection);
	}

	default void addCustomCrashReportInfo(CrashReportSection section) {
	}
}
