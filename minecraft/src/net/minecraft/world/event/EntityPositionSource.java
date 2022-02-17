package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPositionSource implements PositionSource {
	public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("source_entity_id").forGetter(positionSource -> positionSource.entityId),
					Codec.FLOAT.fieldOf("y_offset").forGetter(positionSource -> positionSource.yOffset)
				)
				.apply(instance, EntityPositionSource::new)
	);
	final int entityId;
	private Optional<Entity> entity = Optional.empty();
	final float yOffset;

	public EntityPositionSource(Entity entity, float yOffset) {
		this(entity.getId(), yOffset);
	}

	EntityPositionSource(int entityId, float yOffset) {
		this.entityId = entityId;
		this.yOffset = yOffset;
	}

	@Override
	public Optional<Vec3d> getPos(World world) {
		if (this.entity.isEmpty()) {
			this.entity = Optional.ofNullable(world.getEntityById(this.entityId));
		}

		return this.entity.map(entity -> entity.getPos().add(0.0, (double)this.yOffset, 0.0));
	}

	@Override
	public PositionSourceType<?> getType() {
		return PositionSourceType.ENTITY;
	}

	public static class Type implements PositionSourceType<EntityPositionSource> {
		public EntityPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
			return new EntityPositionSource(packetByteBuf.readVarInt(), packetByteBuf.readFloat());
		}

		public void writeToBuf(PacketByteBuf packetByteBuf, EntityPositionSource entityPositionSource) {
			packetByteBuf.writeVarInt(entityPositionSource.entityId);
			packetByteBuf.writeFloat(entityPositionSource.yOffset);
		}

		@Override
		public Codec<EntityPositionSource> getCodec() {
			return EntityPositionSource.CODEC;
		}
	}
}
