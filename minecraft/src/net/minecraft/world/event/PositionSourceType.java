package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface PositionSourceType<T extends PositionSource> {
	PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
	PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

	T readFromBuf(PacketByteBuf buf);

	void writeToBuf(PacketByteBuf buf, T positionSource);

	Codec<T> getCodec();

	static <S extends PositionSourceType<T>, T extends PositionSource> S register(String id, S positionSourceType) {
		return Registry.register(Registries.POSITION_SOURCE_TYPE, id, positionSourceType);
	}

	static PositionSource read(PacketByteBuf buf) {
		PositionSourceType<?> positionSourceType = buf.readRegistryValue(Registries.POSITION_SOURCE_TYPE);
		if (positionSourceType == null) {
			throw new IllegalArgumentException("Unknown position source type");
		} else {
			return positionSourceType.readFromBuf(buf);
		}
	}

	static <T extends PositionSource> void write(T positionSource, PacketByteBuf buf) {
		buf.writeRegistryValue(Registries.POSITION_SOURCE_TYPE, positionSource.getType());
		((PositionSourceType<T>)positionSource.getType()).writeToBuf(buf, positionSource);
	}
}
