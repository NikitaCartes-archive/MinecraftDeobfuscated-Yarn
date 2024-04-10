package net.minecraft.world.event;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPositionSource implements PositionSource {
	public static final MapCodec<EntityPositionSource> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Uuids.INT_STREAM_CODEC.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid),
					Codec.FLOAT.fieldOf("y_offset").orElse(0.0F).forGetter(entityPositionSource -> entityPositionSource.yOffset)
				)
				.apply(instance, (uuid, yOffset) -> new EntityPositionSource(Either.right(Either.left(uuid)), yOffset))
	);
	public static final PacketCodec<ByteBuf, EntityPositionSource> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		EntityPositionSource::getEntityId,
		PacketCodecs.FLOAT,
		source -> source.yOffset,
		(entityId, yOffset) -> new EntityPositionSource(Either.right(Either.right(entityId)), yOffset)
	);
	private Either<Entity, Either<UUID, Integer>> source;
	private final float yOffset;

	public EntityPositionSource(Entity entity, float yOffset) {
		this(Either.left(entity), yOffset);
	}

	private EntityPositionSource(Either<Entity, Either<UUID, Integer>> source, float yOffset) {
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

	private int getEntityId() {
		return this.source.<Integer>map(Entity::getId, entityId -> entityId.map(uuid -> {
				throw new IllegalStateException("Unable to get entityId from uuid");
			}, Function.identity()));
	}

	@Override
	public PositionSourceType<EntityPositionSource> getType() {
		return PositionSourceType.ENTITY;
	}

	public static class Type implements PositionSourceType<EntityPositionSource> {
		@Override
		public MapCodec<EntityPositionSource> getCodec() {
			return EntityPositionSource.CODEC;
		}

		@Override
		public PacketCodec<ByteBuf, EntityPositionSource> getPacketCodec() {
			return EntityPositionSource.PACKET_CODEC;
		}
	}
}
