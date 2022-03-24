package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkCatalystBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
	private final BlockPositionSource positionSource = new BlockPositionSource(this.pos);
	private final SculkSpreadManager spreadManager = SculkSpreadManager.create();

	public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SCULK_CATALYST, pos, state);
	}

	@Override
	public PositionSource getPositionSource() {
		return this.positionSource;
	}

	@Override
	public int getRange() {
		return 8;
	}

	@Override
	public boolean listen(ServerWorld world, GameEvent event, @Nullable Entity entity, Vec3d pos) {
		if (event == GameEvent.ENTITY_DYING && entity instanceof LivingEntity livingEntity) {
			if (!livingEntity.isExperienceDroppingDisabled()) {
				this.spreadManager.spread(new BlockPos(pos), livingEntity.getXpToDrop());
				livingEntity.disableExperienceDropping();
				SculkCatalystBlock.bloom(world, this.pos, this.getCachedState(), world.getRandom());
			}

			return true;
		} else {
			return false;
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity) {
		blockEntity.spreadManager.tick(world, pos, world.getRandom(), true);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.spreadManager.readNbt(nbt);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		this.spreadManager.writeNbt(nbt);
		super.writeNbt(nbt);
	}

	@VisibleForTesting
	public SculkSpreadManager getSpreadManager() {
		return this.spreadManager;
	}
}
