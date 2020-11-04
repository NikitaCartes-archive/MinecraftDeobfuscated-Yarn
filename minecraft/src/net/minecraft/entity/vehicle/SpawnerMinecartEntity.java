package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(World world, BlockPos blockPos, int i) {
			world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)i);
		}
	};
	private final Runnable field_27012;

	public SpawnerMinecartEntity(EntityType<? extends SpawnerMinecartEntity> entityType, World world) {
		super(entityType, world);
		this.field_27012 = this.method_31553(world);
	}

	public SpawnerMinecartEntity(World world, double x, double y, double z) {
		super(EntityType.SPAWNER_MINECART, world, x, y, z);
		this.field_27012 = this.method_31553(world);
	}

	private Runnable method_31553(World world) {
		return world instanceof ServerWorld
			? () -> this.logic.method_31588((ServerWorld)world, this.getBlockPos())
			: () -> this.logic.method_31589(world, this.getBlockPos());
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.SPAWNER;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.SPAWNER.getDefaultState();
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.logic.fromTag(this.world, this.getBlockPos(), tag);
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		this.logic.toTag(this.world, this.getBlockPos(), tag);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		this.logic.method_8275(this.world, status);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_27012.run();
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}
}
