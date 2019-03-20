package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.MobSpawnerLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobSpawnerMinecartEntity extends AbstractMinecartEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void method_8273(int i) {
			MobSpawnerMinecartEntity.this.world.summonParticle(MobSpawnerMinecartEntity.this, (byte)i);
		}

		@Override
		public World getWorld() {
			return MobSpawnerMinecartEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos(MobSpawnerMinecartEntity.this);
		}
	};

	public MobSpawnerMinecartEntity(EntityType<? extends MobSpawnerMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public MobSpawnerMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.SPAWNER_MINECART, world, d, e, f);
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
	public void method_5711(byte b) {
		this.logic.method_8275(b);
	}

	@Override
	public void tick() {
		super.tick();
		this.logic.update();
	}

	@Override
	public boolean method_5833() {
		return true;
	}
}
