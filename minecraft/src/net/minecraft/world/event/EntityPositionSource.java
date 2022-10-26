package net.minecraft.world.event;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPositionSource implements PositionSource {
	public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Uuids.INT_STREAM_CODEC.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid),
					Codec.FLOAT.fieldOf("y_offset").orElse(0.0F).forGetter(entityPositionSource -> entityPositionSource.yOffset)
				)
				.apply(instance, (uuid, yOffset) -> new EntityPositionSource(Either.right(Either.left(uuid)), yOffset))
	);
	private Either<Entity, Either<UUID, Integer>> source;
	final float yOffset;

	public EntityPositionSource(Entity entity, float yOffset) {
		this(Either.left(entity), yOffset);
	}

	EntityPositionSource(Either<Entity, Either<UUID, Integer>> source, float yOffset) {
		this.source = source;
		this.yOffset = yOffset;
	}

	@Override
	public Optional<Vec3d> getPos(World world) {
		if (this.source.left().isEmpty()) {
			this.findEntityInWorld(world);
		}

		return this.source.left().map(entity -> entity.getPos().add(0.0, (double)this.yOffset, 0.0));
	}

	private void findEntityInWorld(World world) {
		this.source
			.<Optional>map(
				Optional::of,
				entityId -> Optional.ofNullable(
						(Entity)entityId.map(uuid -> world instanceof ServerWorld serverWorld ? serverWorld.getEntity(uuid) : null, world::getEntityById)
					)
			)
			.ifPresent(entity -> this.source = Either.left(entity));
	}

	private UUID getUuid() {
		return this.source.map(Entity::getUuid, entityId -> entityId.map(Function.identity(), entityIdx -> {
				throw new RuntimeException("Unable to get entityId from uuid");
			}));
	}

	int getEntityId() {
		return this.source.<Integer>map(Entity::getId, entityId -> entityId.map(uuid -> {
				throw new IllegalStateException("Unable to get entityId from uuid");
			}, Function.identity()));
	}

	@Override
	public PositionSourceType<?> getType() {
		return PositionSourceType.ENTITY;
	}

	public static class Type implements PositionSourceType<EntityPositionSource> {
		public EntityPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
			return new EntityPositionSource(Either.right(Either.right(packetByteBuf.readVarInt())), packetByteBuf.readFloat());
		}

		public void writeToBuf(PacketByteBuf packetByteBuf, EntityPositionSource entityPositionSource) {
			packetByteBuf.writeVarInt(entityPositionSource.getEntityId());
			packetByteBuf.writeFloat(entityPositionSource.yOffset);
		}

		@Override
		public Codec<EntityPositionSource> getCodec() {
			return EntityPositionSource.CODEC;
		}
	}
}
