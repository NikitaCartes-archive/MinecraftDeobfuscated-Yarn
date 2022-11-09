package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

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
		Identifier identifier = buf.readIdentifier();
		return ((PositionSourceType)Registries.POSITION_SOURCE_TYPE
				.getOrEmpty(identifier)
				.orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + identifier)))
			.readFromBuf(buf);
	}

	static <T extends PositionSource> void write(T positionSource, PacketByteBuf buf) {
		buf.writeIdentifier(Registries.POSITION_SOURCE_TYPE.getId(positionSource.getType()));
		((PositionSourceType<T>)positionSource.getType()).writeToBuf(buf, positionSource);
	}
}
