package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(int i) {
			SpawnerMinecartEntity.this.world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)i);
		}

		@Override
		public World getWorld() {
			return SpawnerMinecartEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos(SpawnerMinecartEntity.this);
		}
	};

	public SpawnerMinecartEntity(EntityType<? extends SpawnerMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public SpawnerMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.field_6142, world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7680;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.field_10260.getDefaultState();
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.logic.deserialize(compoundTag);
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		this.logic.serialize(compoundTag);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		this.logic.method_8275(b);
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
