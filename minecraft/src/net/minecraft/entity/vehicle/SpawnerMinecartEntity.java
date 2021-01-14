package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(int status) {
			SpawnerMinecartEntity.this.world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)status);
		}

		@Override
		public World getWorld() {
			return SpawnerMinecartEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return SpawnerMinecartEntity.this.getBlockPos();
		}
	};

	public SpawnerMinecartEntity(EntityType<? extends SpawnerMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public SpawnerMinecartEntity(World world, double x, double y, double z) {
		super(EntityType.SPAWNER_MINECART, world, x, y, z);
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
		this.logic.fromTag(nbt);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.logic.toTag(nbt);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		this.logic.method_8275(status);
	}

	@Override
	public void tick() {
		super.tick();
		this.logic.update();
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}
}
