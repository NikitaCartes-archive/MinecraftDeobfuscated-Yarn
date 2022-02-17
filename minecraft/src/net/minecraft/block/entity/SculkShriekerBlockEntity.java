package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;

public class SculkShriekerBlockEntity extends BlockEntity implements VibrationListener.Callback {
	public static final int RANGE = 8;
	private final VibrationListener vibrationListener = new VibrationListener(new BlockPositionSource(this.pos), 8, this);

	public SculkShriekerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SCULK_SHRIEKER, pos, state);
	}

	public VibrationListener getVibrationListener() {
		return this.vibrationListener;
	}

	@Override
	public boolean canAccept(GameEvent event, @Nullable Entity sourceEntity) {
		return true;
	}

	@Override
	public boolean accepts(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity) {
		if (sourceEntity instanceof ProjectileEntity projectileEntity) {
			sourceEntity = projectileEntity.getOwner();
		}

		return sourceEntity instanceof PlayerEntity
			&& event == GameEvent.SCULK_SENSOR_TENDRILS_CLICKING
			&& SculkShriekerBlock.method_40795((ServerWorld)world, this.getPos(), this.getCachedState());
	}

	@Override
	public void accept(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, int delay) {
		BlockState blockState = this.getCachedState();
		if (!world.isClient() && SculkShriekerBlock.method_40795((ServerWorld)world, this.getPos(), blockState)) {
			SculkShriekerBlock.method_40792((ServerWorld)world, blockState, this.getPos());
		}
	}
}
