package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(World world, BlockPos pos, int i) {
			world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)i);
		}
	};
	private final Runnable ticker;

	public SpawnerMinecartEntity(EntityType<? extends SpawnerMinecartEntity> entityType, World world) {
		super(entityType, world);
		this.ticker = this.getTicker(world);
	}

	public SpawnerMinecartEntity(World world, double x, double y, double z) {
		super(EntityType.SPAWNER_MINECART, world, x, y, z);
		this.ticker = this.getTicker(world);
	}

	private Runnable getTicker(World world) {
		return world instanceof ServerWorld
			? () -> this.logic.serverTick((ServerWorld)world, this.getBlockPos())
			: () -> this.logic.clientTick(world, this.getBlockPos());
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
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.logic.readNbt(this.world, this.getBlockPos(), nbt);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.logic.writeNbt(this.world, this.getBlockPos(), nbt);
	}

	@Override
	public void handleStatus(byte status) {
		this.logic.method_8275(this.world, status);
	}

	@Override
	public void tick() {
		super.tick();
		this.ticker.run();
	}

	public MobSpawnerLogic getLogic() {
		return this.logic;
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}
}
