package net.minecraft.network.listener;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashCallable;
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

	boolean isConnectionOpen();

	default boolean accepts(Packet<?> packet) {
		return this.isConnectionOpen();
	}

	/**
	 * {@return whether uncaught exceptions in main thread should crash the game
	 * instead of logging and ignoring them}
	 * 
	 * @implNote This is {@code true} by default.
	 * 
	 * @apiNote This only affects the processing on the main thread done by calling
	 * methods in {@link net.minecraft.network.NetworkThreadUtils}. Uncaught exceptions
	 * in other threads or processing in the main thread using the {@code client.execute(() -> {})}
	 * code will be unaffected, and always gets logged without crashing.
	 * 
	 * @see ServerPacketListener
	 */
	default boolean shouldCrashOnException() {
		return true;
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
