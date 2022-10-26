package net.minecraft.world.event.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public record Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
	public static final Codec<Vibration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(Vibration::gameEvent),
					Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(Vibration::distance),
					Vec3d.CODEC.fieldOf("pos").forGetter(Vibration::pos),
					Uuids.INT_STREAM_CODEC.optionalFieldOf("source").forGetter(vibration -> Optional.ofNullable(vibration.uuid())),
					Uuids.INT_STREAM_CODEC.optionalFieldOf("projectile_owner").forGetter(vibration -> Optional.ofNullable(vibration.projectileOwnerUuid()))
				)
				.apply(
					instance,
					(event, distance, pos, uuid, projectileOwnerUuid) -> new Vibration(event, distance, pos, (UUID)uuid.orElse(null), (UUID)projectileOwnerUuid.orElse(null))
				)
	);

	public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid) {
		this(gameEvent, distance, pos, uuid, projectileOwnerUuid, null);
	}

	public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable Entity entity) {
		this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), getOwnerUuid(entity), entity);
	}

	@Nullable
	private static UUID getOwnerUuid(@Nullable Entity entity) {
		if (entity instanceof ProjectileEntity projectileEntity && projectileEntity.getOwner() != null) {
			return projectileEntity.getOwner().getUuid();
		}

		return null;
	}

	public Optional<Entity> getEntity(ServerWorld world) {
		return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(world::getEntity));
	}

	public Optional<Entity> getOwner(ServerWorld world) {
		return this.getEntity(world)
			.filter(entity -> entity instanceof ProjectileEntity)
			.map(entity -> (ProjectileEntity)entity)
			.map(ProjectileEntity::getOwner)
			.or(() -> Optional.ofNullable(this.projectileOwnerUuid).map(world::getEntity));
	}
}
