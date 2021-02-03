package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;

public class SculkSensorBlockEntity extends BlockEntity implements SculkSensorListener.Listener {
	private final SculkSensorListener listener;
	private int lastVibrationFrequency;

	public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SCULK_SENSOR, pos, state);
		this.listener = new SculkSensorListener(new BlockPositionSource(this.pos), ((SculkSensorBlock)state.getBlock()).getRange(), this);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.lastVibrationFrequency = tag.getInt("last_vibration_frequency");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putInt("last_vibration_frequency", this.lastVibrationFrequency);
		return tag;
	}

	public SculkSensorListener getEventListener() {
		return this.listener;
	}

	public int getLastVibrationFrequency() {
		return this.lastVibrationFrequency;
	}

	@Override
	public boolean shouldListen(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
		boolean bl = event == GameEvent.BLOCK_DESTROY && pos.equals(this.getPos());
		boolean bl2 = event == GameEvent.BLOCK_PLACE && pos.equals(this.getPos());
		return !bl && !bl2 && SculkSensorBlock.isInactive(this.getCachedState());
	}

	@Override
	public void listen(World world, GameEventListener listener, GameEvent event, int i) {
		BlockState blockState = this.getCachedState();
		if (!world.isClient() && SculkSensorBlock.isInactive(blockState)) {
			this.lastVibrationFrequency = SculkSensorBlock.FREQUENCIES.getInt(event);
			SculkSensorBlock.setActive(world, this.pos, blockState, method_32910(i, listener.getRange()));
		}
	}

	public static int method_32910(int i, int range) {
		double d = (double)i / (double)range;
		return Math.max(1, 15 - MathHelper.floor(d * 15.0));
	}
}
