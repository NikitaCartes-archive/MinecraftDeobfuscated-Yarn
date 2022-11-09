package net.minecraft.world.event.listener;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;

public class VibrationListener implements GameEventListener {
	@VisibleForTesting
	public static final Object2IntMap<GameEvent> FREQUENCIES = Object2IntMaps.unmodifiable(Util.make(new Object2IntOpenHashMap<>(), frequencies -> {
		frequencies.put(GameEvent.STEP, 1);
		frequencies.put(GameEvent.FLAP, 2);
		frequencies.put(GameEvent.SWIM, 3);
		frequencies.put(GameEvent.ELYTRA_GLIDE, 4);
		frequencies.put(GameEvent.HIT_GROUND, 5);
		frequencies.put(GameEvent.TELEPORT, 5);
		frequencies.put(GameEvent.SPLASH, 6);
		frequencies.put(GameEvent.ENTITY_SHAKE, 6);
		frequencies.put(GameEvent.BLOCK_CHANGE, 6);
		frequencies.put(GameEvent.NOTE_BLOCK_PLAY, 6);
		frequencies.put(GameEvent.PROJECTILE_SHOOT, 7);
		frequencies.put(GameEvent.DRINK, 7);
		frequencies.put(GameEvent.PRIME_FUSE, 7);
		frequencies.put(GameEvent.PROJECTILE_LAND, 8);
		frequencies.put(GameEvent.EAT, 8);
		frequencies.put(GameEvent.ENTITY_INTERACT, 8);
		frequencies.put(GameEvent.ENTITY_DAMAGE, 8);
		frequencies.put(GameEvent.EQUIP, 9);
		frequencies.put(GameEvent.SHEAR, 9);
		frequencies.put(GameEvent.ENTITY_ROAR, 9);
		frequencies.put(GameEvent.BLOCK_CLOSE, 10);
		frequencies.put(GameEvent.BLOCK_DEACTIVATE, 10);
		frequencies.put(GameEvent.BLOCK_DETACH, 10);
		frequencies.put(GameEvent.DISPENSE_FAIL, 10);
		frequencies.put(GameEvent.BLOCK_OPEN, 11);
		frequencies.put(GameEvent.BLOCK_ACTIVATE, 11);
		frequencies.put(GameEvent.BLOCK_ATTACH, 11);
		frequencies.put(GameEvent.ENTITY_PLACE, 12);
		frequencies.put(GameEvent.BLOCK_PLACE, 12);
		frequencies.put(GameEvent.FLUID_PLACE, 12);
		frequencies.put(GameEvent.ENTITY_DIE, 13);
		frequencies.put(GameEvent.BLOCK_DESTROY, 13);
		frequencies.put(GameEvent.FLUID_PICKUP, 13);
		frequencies.put(GameEvent.ITEM_INTERACT_FINISH, 14);
		frequencies.put(GameEvent.CONTAINER_CLOSE, 14);
		frequencies.put(GameEvent.PISTON_CONTRACT, 14);
		frequencies.put(GameEvent.PISTON_EXTEND, 15);
		frequencies.put(GameEvent.CONTAINER_OPEN, 15);
		frequencies.put(GameEvent.EXPLODE, 15);
		frequencies.put(GameEvent.LIGHTNING_STRIKE, 15);
		frequencies.put(GameEvent.INSTRUMENT_PLAY, 15);
	}));
	protected final PositionSource positionSource;
	protected final int range;
	protected final VibrationListener.Callback callback;
	@Nullable
	protected Vibration vibration;
	protected int delay;
	private final VibrationSelector selector;

	public static Codec<VibrationListener> createCodec(VibrationListener.Callback callback) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						PositionSource.CODEC.fieldOf("source").forGetter(listener -> listener.positionSource),
						Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(listener -> listener.range),
						Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> Optional.ofNullable(listener.vibration)),
						VibrationSelector.CODEC.fieldOf("selector").forGetter(listener -> listener.selector),
						Codecs.NONNEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(listener -> listener.delay)
					)
					.apply(
						instance,
						(positionSource, range, vibration, selector, delay) -> new VibrationListener(
								positionSource, range, callback, (Vibration)vibration.orElse(null), selector, delay
							)
					)
		);
	}

	private VibrationListener(
		PositionSource positionSource, int range, VibrationListener.Callback callback, @Nullable Vibration vibration, VibrationSelector selector, int delay
	) {
		this.positionSource = positionSource;
		this.range = range;
		this.callback = callback;
		this.vibration = vibration;
		this.delay = delay;
		this.selector = selector;
	}

	public VibrationListener(PositionSource positionSource, int range, VibrationListener.Callback callback) {
		this(positionSource, range, callback, null, new VibrationSelector(), 0);
	}

	public static int getFrequency(GameEvent event) {
		return FREQUENCIES.getOrDefault(event, 0);
	}

	public void tick(World world) {
		if (world instanceof ServerWorld serverWorld) {
			if (this.vibration == null) {
				this.selector.getVibrationToTick(serverWorld.getTime()).ifPresent(vibration -> {
					this.vibration = vibration;
					Vec3d vec3d = this.vibration.pos();
					this.delay = MathHelper.floor(this.vibration.distance());
					serverWorld.spawnParticles(new VibrationParticleEffect(this.positionSource, this.delay), vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
					this.callback.onListen();
					this.selector.clear();
				});
			}

			if (this.vibration != null) {
				this.delay--;
				if (this.delay <= 0) {
					this.delay = 0;
					this.callback
						.accept(
							serverWorld,
							this,
							new BlockPos(this.vibration.pos()),
							this.vibration.gameEvent(),
							(Entity)this.vibration.getEntity(serverWorld).orElse(null),
							(Entity)this.vibration.getOwner(serverWorld).orElse(null),
							this.vibration.distance()
						);
					this.vibration = null;
				}
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
	public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
		if (this.vibration != null) {
			return false;
		} else if (!this.callback.canAccept(event, emitter)) {
			return false;
		} else {
			Optional<Vec3d> optional = this.positionSource.getPos(world);
			if (optional.isEmpty()) {
				return false;
			} else {
				Vec3d vec3d = (Vec3d)optional.get();
				if (!this.callback.accepts(world, this, new BlockPos(emitterPos), event, emitter)) {
					return false;
				} else if (isOccluded(world, emitterPos, vec3d)) {
					return false;
				} else {
					this.trySelect(world, event, emitter, emitterPos, vec3d);
					return true;
				}
			}
		}
	}

	public void forceListen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
		this.positionSource.getPos(world).ifPresent(listenerPos -> this.trySelect(world, event, emitter, emitterPos, listenerPos));
	}

	public void trySelect(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos, Vec3d listenerPos) {
		this.selector.tryAccept(new Vibration(event, (float)emitterPos.distanceTo(listenerPos), emitterPos, emitter.sourceEntity()), world.getTime());
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
}
