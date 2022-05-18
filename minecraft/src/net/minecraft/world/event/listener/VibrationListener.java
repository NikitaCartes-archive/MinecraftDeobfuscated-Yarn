package net.minecraft.world.event.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;

public class VibrationListener implements GameEventListener {
	protected final PositionSource positionSource;
	protected final int range;
	protected final VibrationListener.Callback callback;
	@Nullable
	protected VibrationListener.Vibration vibration;
	protected float distance;
	protected int delay;

	public static Codec<VibrationListener> createCodec(VibrationListener.Callback callback) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						PositionSource.CODEC.fieldOf("source").forGetter(listener -> listener.positionSource),
						Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(listener -> listener.range),
						VibrationListener.Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> Optional.ofNullable(listener.vibration)),
						Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("event_distance").orElse(0.0F).forGetter(listener -> listener.distance),
						Codecs.NONNEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(listener -> listener.delay)
					)
					.apply(
						instance,
						(positionSource, range, vibration, float_, delay) -> new VibrationListener(
								positionSource, range, callback, (VibrationListener.Vibration)vibration.orElse(null), float_, delay
							)
					)
		);
	}

	public VibrationListener(
		PositionSource positionSource, int range, VibrationListener.Callback callback, @Nullable VibrationListener.Vibration vibration, float distance, int delay
	) {
		this.positionSource = positionSource;
		this.range = range;
		this.callback = callback;
		this.vibration = vibration;
		this.distance = distance;
		this.delay = delay;
	}

	public void tick(World world) {
		if (world instanceof ServerWorld serverWorld && this.vibration != null) {
			this.delay--;
			if (this.delay <= 0) {
				this.delay = 0;
				this.callback
					.accept(
						serverWorld,
						this,
						new BlockPos(this.vibration.pos),
						this.vibration.gameEvent,
						(Entity)this.vibration.getEntity(serverWorld).orElse(null),
						(Entity)this.vibration.getOwner(serverWorld).orElse(null),
						this.distance
					);
				this.vibration = null;
			}
		}
	}

	@Override
	public PositionSource getPositionSource() {
		return this.positionSource;
	}

	@Override
	public int getRange() {
		return this.range;
	}

	@Override
	public boolean listen(ServerWorld world, GameEvent.Message event) {
		if (this.vibration != null) {
			return false;
		} else {
			GameEvent gameEvent = event.getEvent();
			GameEvent.Emitter emitter = event.getEmitter();
			if (!this.callback.canAccept(gameEvent, emitter)) {
				return false;
			} else {
				Optional<Vec3d> optional = this.positionSource.getPos(world);
				if (optional.isEmpty()) {
					return false;
				} else {
					Vec3d vec3d = event.getEmitterPos();
					Vec3d vec3d2 = (Vec3d)optional.get();
					if (!this.callback.accepts(world, this, new BlockPos(vec3d), gameEvent, emitter)) {
						return false;
					} else if (isOccluded(world, vec3d, vec3d2)) {
						return false;
					} else {
						this.listen(world, gameEvent, emitter, vec3d, vec3d2);
						return true;
					}
				}
			}
		}
	}

	private void listen(ServerWorld world, GameEvent gameEvent, GameEvent.Emitter emitter, Vec3d start, Vec3d end) {
		this.distance = (float)start.distanceTo(end);
		this.vibration = new VibrationListener.Vibration(gameEvent, this.distance, start, emitter.sourceEntity());
		this.delay = MathHelper.floor(this.distance);
		world.spawnParticles(new VibrationParticleEffect(this.positionSource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
		this.callback.onListen();
	}

	private static boolean isOccluded(World world, Vec3d start, Vec3d end) {
		Vec3d vec3d = new Vec3d((double)MathHelper.floor(start.x) + 0.5, (double)MathHelper.floor(start.y) + 0.5, (double)MathHelper.floor(start.z) + 0.5);
		Vec3d vec3d2 = new Vec3d((double)MathHelper.floor(end.x) + 0.5, (double)MathHelper.floor(end.y) + 0.5, (double)MathHelper.floor(end.z) + 0.5);

		for (Direction direction : Direction.values()) {
			Vec3d vec3d3 = vec3d.withBias(direction, 1.0E-5F);
			if (world.raycast(new BlockStateRaycastContext(vec3d3, vec3d2, state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() != HitResult.Type.BLOCK
				)
			 {
				return false;
			}
		}

		return true;
	}

	public interface Callback {
		default TagKey<GameEvent> getTag() {
			return GameEventTags.VIBRATIONS;
		}

		default boolean triggersAvoidCriterion() {
			return false;
		}

		default boolean canAccept(GameEvent gameEvent, GameEvent.Emitter emitter) {
			if (!gameEvent.isIn(this.getTag())) {
				return false;
			} else {
				Entity entity = emitter.sourceEntity();
				if (entity != null) {
					if (entity.isSpectator()) {
						return false;
					}

					if (entity.bypassesSteppingEffects() && gameEvent.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
						if (this.triggersAvoidCriterion() && entity instanceof ServerPlayerEntity serverPlayerEntity) {
							Criteria.AVOID_VIBRATION.trigger(serverPlayerEntity);
						}

						return false;
					}

					if (entity.occludeVibrationSignals()) {
						return false;
					}
				}

				return emitter.affectedState() != null ? !emitter.affectedState().isIn(BlockTags.DAMPENS_VIBRATIONS) : true;
			}
		}

		/**
		 * Returns whether the callback wants to accept this event.
		 */
		boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter);

		/**
		 * Accepts a game event after delay.
		 */
		void accept(
			ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance
		);

		default void onListen() {
		}
	}

	public static record Vibration(
		GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity
	) {
		public static final Codec<VibrationListener.Vibration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registry.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(VibrationListener.Vibration::gameEvent),
						Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationListener.Vibration::distance),
						Vec3d.CODEC.fieldOf("pos").forGetter(VibrationListener.Vibration::pos),
						Codecs.UUID.optionalFieldOf("source").forGetter(vibration -> Optional.ofNullable(vibration.uuid())),
						Codecs.UUID.optionalFieldOf("projectile_owner").forGetter(vibration -> Optional.ofNullable(vibration.projectileOwnerUuid()))
					)
					.apply(
						instance,
						(event, distance, pos, uuid, projectileOwnerUuid) -> new VibrationListener.Vibration(
								event, distance, pos, (UUID)uuid.orElse(null), (UUID)projectileOwnerUuid.orElse(null)
							)
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
}
