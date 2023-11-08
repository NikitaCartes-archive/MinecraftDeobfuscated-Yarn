package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(World world, BlockPos pos, int status) {
			world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)status);
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

	@Override
	protected Item asItem() {
		return Items.MINECART;
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
		this.logic.readNbt(this.getWorld(), this.getBlockPos(), nbt);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.logic.writeNbt(nbt);
	}

	@Override
	public void handleStatus(byte status) {
		this.logic.handleStatus(this.getWorld(), status);
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
