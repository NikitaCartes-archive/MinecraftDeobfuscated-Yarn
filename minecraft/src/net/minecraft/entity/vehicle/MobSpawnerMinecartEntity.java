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
	private final MobSpawnerLogic field_7746 = new MobSpawnerLogic() {
		@Override
		public void method_8273(int i) {
			MobSpawnerMinecartEntity.this.field_6002.summonParticle(MobSpawnerMinecartEntity.this, (byte)i);
		}

		@Override
		public World method_8271() {
			return MobSpawnerMinecartEntity.this.field_6002;
		}

		@Override
		public BlockPos method_8276() {
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
	public BlockState method_7517() {
		return Blocks.field_10260.method_9564();
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.field_7746.method_8280(compoundTag);
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		this.field_7746.method_8272(compoundTag);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		this.field_7746.method_8275(b);
	}

	@Override
	public void update() {
		super.update();
		this.field_7746.update();
	}

	@Override
	public boolean method_5833() {
		return true;
	}
}
