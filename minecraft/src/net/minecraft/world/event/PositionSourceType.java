package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface PositionSourceType<T extends PositionSource> {
	PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
	PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

	Codec<T> getCodec();

	PacketCodec<RegistryByteBuf, T> getPacketCodec();

	static <S extends PositionSourceType<T>, T extends PositionSource> S register(String id, S positionSourceType) {
		return Registry.register(Registries.POSITION_SOURCE_TYPE, id, positionSourceType);
	}
}
