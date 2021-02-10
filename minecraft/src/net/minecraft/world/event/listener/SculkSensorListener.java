package net.minecraft.world.event.listener;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;

public class SculkSensorListener implements GameEventListener {
	protected final PositionSource positionSource;
	protected final int range;
	protected final SculkSensorListener.Callback callback;
	protected Optional<GameEvent> event = Optional.empty();
	protected int distance;
	protected int delay = 0;

	public SculkSensorListener(PositionSource positionSource, int range, SculkSensorListener.Callback listener) {
		this.positionSource = positionSource;
		this.range = range;
		this.callback = listener;
	}

	public void tick(World world) {
		if (this.event.isPresent()) {
			this.delay--;
			if (this.delay <= 0) {
				this.delay = 0;
				this.callback.accept(world, this, (GameEvent)this.event.get(), this.distance);
				this.event = Optional.empty();
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
	public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
		if (!this.shouldActivate(event, entity)) {
			return false;
		} else {
			Optional<BlockPos> optional = this.positionSource.getPos(world);
			if (!optional.isPresent()) {
				return false;
			} else {
				BlockPos blockPos = (BlockPos)optional.get();
				if (!this.callback.accepts(world, this, pos, event, entity)) {
					return false;
				} else if (this.isOccluded(world, pos, blockPos)) {
					return false;
				} else {
					this.listen(world, event, pos, blockPos);
					return true;
				}
			}
		}
	}

	private boolean shouldActivate(GameEvent event, @Nullable Entity entity) {
		if (this.event.isPresent()) {
			return false;
		} else if (!GameEventTags.VIBRATIONS.contains(event)) {
			return false;
		} else {
			if (entity != null) {
				if (GameEventTags.IGNORE_VIBRATIONS_SNEAKING.contains(event) && entity.bypassesSteppingEffects()) {
					return false;
				}

				if (entity.occludeVibrationSignals()) {
					return false;
				}
			}

			return entity == null || !entity.isSpectator();
		}
	}

	private void listen(World world, GameEvent event, BlockPos pos, BlockPos sourcePos) {
		this.event = Optional.of(event);
		if (world instanceof ServerWorld) {
			this.distance = MathHelper.floor(MathHelper.sqrt(pos.getSquaredDistance(sourcePos, false)));
			this.delay = this.distance;
			((ServerWorld)world).sendVibrationPacket(new Vibration(pos, this.positionSource, this.delay));
		}
	}

	private boolean isOccluded(World world, BlockPos pos, BlockPos sourcePos) {
		return world.raycast(new BlockStateRaycastContext(Vec3d.ofCenter(pos), Vec3d.ofCenter(sourcePos), state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)))
				.getType()
			== HitResult.Type.BLOCK;
	}

	public interface Callback {
		/**
		 * Returns whether the callback wants to accept this event.
		 */
		boolean accepts(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity);

		/**
		 * Accepts a game event after delay.
		 */
		void accept(World world, GameEventListener listener, GameEvent event, int distance);
	}
}
