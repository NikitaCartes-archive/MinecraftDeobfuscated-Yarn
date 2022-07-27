/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import java.util.function.Supplier;
import net.minecraft.network.Packet;
import org.jetbrains.annotations.Nullable;

/**
 * A set of callbacks for sending a packet.
 */
public interface PacketCallbacks {
    /**
     * {@return a callback that always runs {@code runnable}}
     */
    public static PacketCallbacks always(final Runnable runnable) {
        return new PacketCallbacks(){

            @Override
            public void onSuccess() {
                runnable.run();
            }

            @Override
            @Nullable
            public Packet<?> getFailurePacket() {
                runnable.run();
                return null;
            }
        };
    }

    /**
     * {@return a callback that sends {@code failurePacket} when failed}
     */
    public static PacketCallbacks of(final Supplier<Packet<?>> failurePacket) {
        return new PacketCallbacks(){

            @Override
            @Nullable
            public Packet<?> getFailurePacket() {
                return (Packet)failurePacket.get();
            }
        };
    }

    /**
     * Called when packet is sent successfully.
     */
    default public void onSuccess() {
    }

    /**
     * {@return the packet to send on failure, or {@code null} if there is none}
     */
    @Nullable
    default public Packet<?> getFailurePacket() {
        return null;
    }
}

