package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
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
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
		RegistryOps<NbtElement> registryOps = registries.getOps(NbtOps.INSTANCE);
		if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
			Vibrations.ListenerData.CODEC
				.parse(registryOps, nbt.getCompound("listener"))
				.resultOrPartial(string -> LOGGER.error("Failed to parse vibration listener for Sculk Sensor: '{}'", string))
				.ifPresent(listener -> this.listenerData = listener);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
		RegistryOps<NbtElement> registryOps = registries.getOps(NbtOps.INSTANCE);
		Vibrations.ListenerData.CODEC
			.encodeStart(registryOps, this.listenerData)
			.resultOrPartial(string -> LOGGER.error("Failed to encode vibration listener for Sculk Sensor: '{}'", string))
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

		public VibrationCallback(final BlockPos pos) {
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
			return !pos.equals(this.pos) || !event.matches(GameEvent.BLOCK_DESTROY) && !event.matches(GameEvent.BLOCK_PLACE)
				? SculkSensorBlock.isInactive(SculkSensorBlockEntity.this.getCachedState())
				: false;
		}

		@Override
		public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
			BlockState blockState = SculkSensorBlockEntity.this.getCachedState();
			if (SculkSensorBlock.isInactive(blockState)) {
				SculkSensorBlockEntity.this.setLastVibrationFrequency(Vibrations.getFrequency(event));
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
