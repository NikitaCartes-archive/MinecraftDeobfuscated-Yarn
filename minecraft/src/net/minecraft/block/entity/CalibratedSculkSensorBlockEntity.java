package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;

public class CalibratedSculkSensorBlockEntity extends SculkSensorBlockEntity {
	public CalibratedSculkSensorBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.CALIBRATED_SCULK_SENSOR, blockPos, blockState);
	}

	@Override
	public VibrationListener.Callback createCallback() {
		return new CalibratedSculkSensorBlockEntity.Callback(this);
	}

	public static class Callback extends SculkSensorBlockEntity.Callback {
		public Callback(SculkSensorBlockEntity sculkSensorBlockEntity) {
			super(sculkSensorBlockEntity);
		}

		@Override
		public int getRange() {
			return 16;
		}

		@Override
		public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable GameEvent.Emitter emitter) {
			BlockPos blockPos = this.blockEntity.getPos();
			int i = this.getCalibrationFrequency(world, blockPos, this.blockEntity.getCachedState());
			return i != 0 && VibrationListener.getFrequency(event) != i ? false : super.accepts(world, listener, pos, event, emitter);
		}

		private int getCalibrationFrequency(World world, BlockPos pos, BlockState state) {
			Direction direction = ((Direction)state.get(CalibratedSculkSensorBlock.FACING)).getOpposite();
			return world.getEmittedRedstonePower(pos.offset(direction), direction);
		}
	}
}
