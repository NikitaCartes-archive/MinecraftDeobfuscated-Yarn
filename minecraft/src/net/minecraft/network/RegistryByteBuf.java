package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.registry.DynamicRegistryManager;

/**
 * A packet byte buffer bound to a particular {@link DynamicRegistryManager} instance.
 * 
 * <p>This is used during the {@link NetworkPhase#PLAY} phase only.
 */
public class RegistryByteBuf extends PacketByteBuf {
	private final DynamicRegistryManager registryManager;

	public RegistryByteBuf(ByteBuf buf, DynamicRegistryManager registryManager) {
		super(buf);
		this.registryManager = registryManager;
	}

	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}

	public static Function<ByteBuf, RegistryByteBuf> makeFactory(DynamicRegistryManager registryManager) {
		return buf -> new RegistryByteBuf(buf, registryManager);
	}
}
