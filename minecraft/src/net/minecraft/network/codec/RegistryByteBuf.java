package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;

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
