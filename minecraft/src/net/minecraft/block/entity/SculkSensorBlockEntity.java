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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import org.slf4j.Logger;

public class SculkSensorBlockEntity extends BlockEntity implements GameEventListener.Holder<Vibrations.VibrationListener>, Vibrations {
	private static final Logger LOGGER = LogUtils.getLogger();
	private Vibrations.ListenerData listenerData;
	private final Vibrations.VibrationListener listener;
	private final Vibrations.Callback callback = this.createCallback();
	private int lastVibrationFrequency;

	protected SculkSensorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		this.listenerData = new Vibrations.ListenerData();
		this.listener = new Vibrations.VibrationListener(this);
	}

	public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityType.SCULK_SENSOR, pos, state);
	}

	public Vibrations.Callback createCallback() {
		return new SculkSensorBlockEntity.VibrationCallback(this.getPos());
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
		if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
			Vibrations.ListenerData.CODEC
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
				.resultOrPartial(LOGGER::error)
				.ifPresent(listener -> this.listenerData = listener);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
		Vibrations.ListenerData.CODEC
			.encodeStart(NbtOps.INSTANCE, this.listenerData)
			.resultOrPartial(LOGGER::error)
			.ifPresent(listenerNbt -> nbt.put("listener", listenerNbt));
	}

	@Override
	public Vibrations.ListenerData getVibrationListenerData() {
		return this.listenerData;
	}

	@Override
	public Vibrations.Callback getVibrationCallback() {
		return this.callback;
	}

	public int getLastVibrationFrequency() {
		return this.lastVibrationFrequency;
	}

	public void setLastVibrationFrequency(int lastVibrationFrequency) {
		this.lastVibrationFrequency = lastVibrationFrequency;
	}

	public Vibrations.VibrationListener getEventListener() {
		return this.listener;
	}

	protected class VibrationCallback implements Vibrations.Callback {
		public static final int RANGE = 8;
		protected final BlockPos pos;
		private final PositionSource positionSource;

		public VibrationCallback(BlockPos pos) {
			this.pos = pos;
			this.positionSource = new BlockPositionSource(pos);
		}

		@Override
		public int getRange() {
			return 8;
		}

		@Override
		public PositionSource getPositionSource() {
			return this.positionSource;
		}

		@Override
		public boolean triggersAvoidCriterion() {
			return true;
		}

		@Override
		public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable GameEvent.Emitter emitter) {
			return !pos.equals(this.pos) || !event.method_55838(GameEvent.BLOCK_DESTROY) && !event.method_55838(GameEvent.BLOCK_PLACE)
				? SculkSensorBlock.isInactive(SculkSensorBlockEntity.this.getCachedState())
				: false;
		}

		@Override
		public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
			BlockState blockState = SculkSensorBlockEntity.this.getCachedState();
			if (SculkSensorBlock.isInactive(blockState)) {
				SculkSensorBlockEntity.this.setLastVibrationFrequency(Vibrations.method_55783(event));
				int i = Vibrations.getSignalStrength(distance, this.getRange());
				if (blockState.getBlock() instanceof SculkSensorBlock sculkSensorBlock) {
					sculkSensorBlock.setActive(sourceEntity, world, this.pos, blockState, i, SculkSensorBlockEntity.this.getLastVibrationFrequency());
				}
			}
		}

		@Override
		public void onListen() {
			SculkSensorBlockEntity.this.markDirty();
		}

		@Override
		public boolean requiresTickingChunksAround() {
			return true;
		}
	}
}
