package net.minecraft.network;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.packet.Packet;

/**
 * A set of callbacks for sending a packet.
 */
public interface PacketCallbacks {
	/**
	 * {@return a callback that always runs {@code runnable}}
	 */
	static PacketCallbacks always(Runnable runnable) {
		return new PacketCallbacks() {
			@Override
			public void onSuccess() {
				runnable.run();
			}

			@Nullable
			@Override
			public Packet<?> getFailurePacket() {
				runnable.run();
				return null;
			}
		};
	}

	/**
	 * {@return a callback that sends {@code failurePacket} when failed}
	 */
	static PacketCallbacks of(Supplier<Packet<?>> failurePacket) {
		return new PacketCallbacks() {
			@Nullable
			@Override
			public Packet<?> getFailurePacket() {
				return (Packet<?>)failurePacket.get();
			}
		};
	}

	/**
	 * Called when packet is sent successfully.
	 */
	default void onSuccess() {
	}

	/**
	 * {@return the packet to send on failure, or {@code null} if there is none}
	 */
	@Nullable
	default Packet<?> getFailurePacket() {
		return null;
	}
}
