package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;
import org.slf4j.Logger;

public class SculkSensorBlockEntity extends BlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private VibrationListener listener;
	private final VibrationListener.Callback callback = this.createCallback();
	private int lastVibrationFrequency;

	protected SculkSensorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		this.listener = new VibrationListener(new BlockPositionSource(this.pos), this.callback);
	}

	public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityType.SCULK_SENSOR, pos, state);
	}

	public VibrationListener.Callback createCallback() {
		return new SculkSensorBlockEntity.Callback(this);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
		if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
			VibrationListener.createCodec(this.callback)
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
				.resultOrPartial(LOGGER::error)
				.ifPresent(listener -> this.listener = listener);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
		VibrationListener.createCodec(this.callback)
			.encodeStart(NbtOps.INSTANCE, this.listener)
			.resultOrPartial(LOGGER::error)
			.ifPresent(listenerNbt -> nbt.put("listener", listenerNbt));
	}

	public VibrationListener getEventListener() {
		return this.listener;
	}

	public int getLastVibrationFrequency() {
		return this.lastVibrationFrequency;
	}

	public void setLastVibrationFrequency(int lastVibrationFrequency) {
		this.lastVibrationFrequency = lastVibrationFrequency;
	}

	public static class Callback implements VibrationListener.Callback {
		public static final int field_43292 = 8;
		protected final SculkSensorBlockEntity blockEntity;

		public Callback(SculkSensorBlockEntity blockEntity) {
			this.blockEntity = blockEntity;
		}

		@Override
		public boolean triggersAvoidCriterion() {
			return true;
		}

		@Override
		public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable GameEvent.Emitter emitter) {
			return !pos.equals(this.blockEntity.getPos()) || event != GameEvent.BLOCK_DESTROY && event != GameEvent.BLOCK_PLACE
				? SculkSensorBlock.isInactive(this.blockEntity.getCachedState())
				: false;
		}

		@Override
		public void accept(
			ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance
		) {
			BlockState blockState = this.blockEntity.getCachedState();
			BlockPos blockPos = this.blockEntity.getPos();
			if (SculkSensorBlock.isInactive(blockState)) {
				this.blockEntity.setLastVibrationFrequency(VibrationListener.getFrequency(event));
				int i = getPower(distance, listener.getRange());
				if (blockState.getBlock() instanceof SculkSensorBlock sculkSensorBlock) {
					sculkSensorBlock.setActive(entity, world, blockPos, blockState, i, this.blockEntity.getLastVibrationFrequency());
				}
			}
		}

		@Override
		public void onListen() {
			this.blockEntity.markDirty();
		}

		@Override
		public int getRange() {
			return 8;
		}

		public static int getPower(float distance, int range) {
			double d = 15.0 / (double)range;
			return Math.max(1, 15 - MathHelper.floor(d * (double)distance));
		}
	}
}
