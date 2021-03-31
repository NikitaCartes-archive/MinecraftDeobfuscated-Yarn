package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;

public class SculkSensorBlockEntity extends BlockEntity implements SculkSensorListener.Callback {
	private final SculkSensorListener listener;
	private int lastVibrationFrequency;

	public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SCULK_SENSOR, pos, state);
		this.listener = new SculkSensorListener(new BlockPositionSource(this.pos), ((SculkSensorBlock)state.getBlock()).getRange(), this);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
		return nbt;
	}

	public SculkSensorListener getEventListener() {
		return this.listener;
	}

	public int getLastVibrationFrequency() {
		return this.lastVibrationFrequency;
	}

	@Override
	public boolean accepts(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
		boolean bl = event == GameEvent.BLOCK_DESTROY && pos.equals(this.getPos());
		boolean bl2 = event == GameEvent.BLOCK_PLACE && pos.equals(this.getPos());
		return !bl && !bl2 && SculkSensorBlock.isInactive(this.getCachedState());
	}

	@Override
	public void accept(World world, GameEventListener listener, GameEvent event, int distance) {
		BlockState blockState = this.getCachedState();
		if (!world.isClient() && SculkSensorBlock.isInactive(blockState)) {
			this.lastVibrationFrequency = SculkSensorBlock.FREQUENCIES.getInt(event);
			SculkSensorBlock.setActive(world, this.pos, blockState, getPower(distance, listener.getRange()));
		}
	}

	public static int getPower(int distance, int range) {
		double d = (double)distance / (double)range;
		return Math.max(1, 15 - MathHelper.floor(d * 15.0));
	}
}
