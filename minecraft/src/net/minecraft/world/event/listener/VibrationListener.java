package net.minecraft.world.event.listener;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
	protected int distance;
	protected int delay;

	public VibrationListener(PositionSource positionSource, int range, VibrationListener.Callback listener) {
		this.positionSource = positionSource;
		this.range = range;
		this.callback = listener;
	}

	public void tick(World world) {
		if (this.vibration != null) {
			this.delay--;
			if (this.delay <= 0) {
				this.delay = 0;
				this.callback.accept(world, this, new BlockPos(this.vibration.pos), this.vibration.gameEvent, this.vibration.sourceEntity, this.distance);
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
	public boolean listen(World world, GameEvent event, @Nullable Entity sourceEntity, Vec3d pos) {
		if (this.vibration != null) {
			return false;
		} else if (!this.callback.canAccept(event, sourceEntity)) {
			return false;
		} else {
			Optional<Vec3d> optional = this.positionSource.getPos(world);
			if (optional.isEmpty()) {
				return false;
			} else {
				Vec3d vec3d = (Vec3d)optional.get();
				BlockPos blockPos = new BlockPos(pos);
				if (!this.callback.accepts(world, this, blockPos, event, sourceEntity)) {
					return false;
				} else if (this.callback.isNotOccluded(world, this, blockPos, event, sourceEntity) && isOccluded(world, pos, vec3d)) {
					return false;
				} else {
					if (world instanceof ServerWorld serverWorld) {
						this.listen(serverWorld, event, sourceEntity, pos, vec3d);
					}

					return true;
				}
			}
		}
	}

	private void listen(ServerWorld world, GameEvent event, @Nullable Entity sourceEntity, Vec3d sourcePos, Vec3d listenerPos) {
		this.distance = MathHelper.floor(sourcePos.distanceTo(listenerPos));
		this.vibration = new VibrationListener.Vibration(event, this.distance, sourcePos, sourceEntity);
		this.delay = this.distance;
		world.spawnParticles(new VibrationParticleEffect(this.positionSource, this.delay), sourcePos.x, sourcePos.y, sourcePos.z, 1, 0.0, 0.0, 0.0, 0.0);
	}

	private static boolean isOccluded(World world, Vec3d sourcePos, Vec3d listenerPos) {
		Vec3d vec3d = new Vec3d((double)MathHelper.floor(sourcePos.x) + 0.5, (double)MathHelper.floor(sourcePos.y) + 0.5, (double)MathHelper.floor(sourcePos.z) + 0.5);
		Vec3d vec3d2 = new Vec3d(
			(double)MathHelper.floor(listenerPos.x) + 0.5, (double)MathHelper.floor(listenerPos.y) + 0.5, (double)MathHelper.floor(listenerPos.z) + 0.5
		);
		return world.raycast(new BlockStateRaycastContext(vec3d, vec3d2, state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType()
			== HitResult.Type.BLOCK;
	}

	public interface Callback {
		default Tag.Identified<GameEvent> getTag() {
			return GameEventTags.VIBRATIONS;
		}

		default boolean canAccept(GameEvent event, @Nullable Entity sourceEntity) {
			if (!this.getTag().contains(event)) {
				return false;
			} else {
				if (sourceEntity != null) {
					if (sourceEntity.isSpectator()) {
						return false;
					}

					if (sourceEntity.bypassesSteppingEffects() && GameEventTags.IGNORE_VIBRATIONS_SNEAKING.contains(event)) {
						return false;
					}

					if (sourceEntity.occludeVibrationSignals()) {
						return false;
					}
				}

				return true;
			}
		}

		/**
		 * Returns whether the callback wants to accept this event.
		 */
		boolean accepts(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity);

		default boolean isNotOccluded(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity) {
			if (event == GameEvent.BLOCK_PLACE) {
				BlockState blockState = world.getBlockState(pos);
				if (blockState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
					BlockPos blockPos = pos.down();
					BlockState blockState2 = world.getBlockState(blockPos);
					return !blockState2.isIn(BlockTags.SKIP_OCCLUDE_VIBRATION_WHEN_ABOVE);
				}
			}

			return true;
		}

		/**
		 * Accepts a game event after delay.
		 */
		void accept(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, int delay);
	}

	public static record Vibration(GameEvent gameEvent, int distance, Vec3d pos, @Nullable Entity sourceEntity) {
	}
}
