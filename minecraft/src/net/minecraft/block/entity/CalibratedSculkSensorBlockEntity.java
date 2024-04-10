package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

public class CalibratedSculkSensorBlockEntity extends SculkSensorBlockEntity {
	public CalibratedSculkSensorBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.CALIBRATED_SCULK_SENSOR, blockPos, blockState);
	}

	@Override
	public Vibrations.Callback createCallback() {
		return new CalibratedSculkSensorBlockEntity.Callback(this.getPos());
	}

	protected class Callback extends SculkSensorBlockEntity.VibrationCallback {
		public Callback(final BlockPos pos) {
			super(pos);
		}

		@Override
		public int getRange() {
			return 16;
		}

		@Override
		public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable GameEvent.Emitter emitter) {
			int i = this.getCalibrationFrequency(world, this.pos, CalibratedSculkSensorBlockEntity.this.getCachedState());
			return i != 0 && Vibrations.getFrequency(event) != i ? false : super.accepts(world, pos, event, emitter);
		}

		private int getCalibrationFrequency(World world, BlockPos pos, BlockState state) {
			Direction direction = ((Direction)state.get(CalibratedSculkSensorBlock.FACING)).getOpposite();
			return world.getEmittedRedstonePower(pos.offset(direction), direction);
		}
	}
}
