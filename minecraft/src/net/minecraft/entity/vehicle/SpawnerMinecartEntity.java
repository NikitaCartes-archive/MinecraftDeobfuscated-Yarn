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
	private final MobSpawnerLogic field_7746 = new MobSpawnerLogic() {
		@Override
		public void sendStatus(int i) {
			SpawnerMinecartEntity.this.field_6002.sendEntityStatus(SpawnerMinecartEntity.this, (byte)i);
		}

		@Override
		public World method_8271() {
			return SpawnerMinecartEntity.this.field_6002;
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
	public BlockState method_7517() {
		return Blocks.field_10260.method_9564();
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.field_7746.deserialize(compoundTag);
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		this.field_7746.serialize(compoundTag);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		this.field_7746.method_8275(b);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_7746.update();
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}
}
