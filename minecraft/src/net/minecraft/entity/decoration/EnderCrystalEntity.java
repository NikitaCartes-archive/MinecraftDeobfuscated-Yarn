package net.minecraft.entity.decoration;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.explosion.Explosion;

public class EnderCrystalEntity extends Entity {
	private static final TrackedData<Optional<BlockPos>> field_7033 = DataTracker.registerData(
		EnderCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS
	);
	private static final TrackedData<Boolean> field_7035 = DataTracker.registerData(EnderCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public int field_7034;

	public EnderCrystalEntity(EntityType<? extends EnderCrystalEntity> entityType, World world) {
		super(entityType, world);
		this.field_6033 = true;
		this.field_7034 = this.random.nextInt(100000);
	}

	public EnderCrystalEntity(World world, double d, double e, double f) {
		this(EntityType.END_CRYSTAL, world);
		this.setPosition(d, e, f);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.method_5841().startTracking(field_7033, Optional.empty());
		this.method_5841().startTracking(field_7035, true);
	}

	@Override
	public void update() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.field_7034++;
		if (!this.field_6002.isClient) {
			BlockPos blockPos = new BlockPos(this);
			if (this.field_6002.field_9247 instanceof TheEndDimension && this.field_6002.method_8320(blockPos).isAir()) {
				this.field_6002.method_8501(blockPos, Blocks.field_10036.method_9564());
			}
		}
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		if (this.method_6838() != null) {
			compoundTag.method_10566("BeamTarget", TagHelper.serializeBlockPos(this.method_6838()));
		}

		compoundTag.putBoolean("ShowBottom", this.method_6836());
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		if (compoundTag.containsKey("BeamTarget", 10)) {
			this.method_6837(TagHelper.deserializeBlockPos(compoundTag.getCompound("BeamTarget")));
		}

		if (compoundTag.containsKey("ShowBottom", 1)) {
			this.setShowBottom(compoundTag.getBoolean("ShowBottom"));
		}
	}

	@Override
	public boolean doesCollide() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (damageSource.method_5529() instanceof EnderDragonEntity) {
			return false;
		} else {
			if (!this.invalid && !this.field_6002.isClient) {
				this.invalidate();
				if (!this.field_6002.isClient) {
					if (!damageSource.isExplosive()) {
						this.field_6002.createExplosion(null, this.x, this.y, this.z, 6.0F, Explosion.class_4179.field_18687);
					}

					this.crystalDestroyed(damageSource);
				}
			}

			return true;
		}
	}

	@Override
	public void kill() {
		this.crystalDestroyed(DamageSource.GENERIC);
		super.kill();
	}

	private void crystalDestroyed(DamageSource damageSource) {
		if (this.field_6002.field_9247 instanceof TheEndDimension) {
			TheEndDimension theEndDimension = (TheEndDimension)this.field_6002.field_9247;
			EnderDragonFight enderDragonFight = theEndDimension.method_12513();
			if (enderDragonFight != null) {
				enderDragonFight.crystalDestroyed(this, damageSource);
			}
		}
	}

	public void method_6837(@Nullable BlockPos blockPos) {
		this.method_5841().set(field_7033, Optional.ofNullable(blockPos));
	}

	@Nullable
	public BlockPos method_6838() {
		return (BlockPos)this.method_5841().get(field_7033).orElse(null);
	}

	public void setShowBottom(boolean bl) {
		this.method_5841().set(field_7035, bl);
	}

	public boolean method_6836() {
		return this.method_5841().get(field_7035);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		return super.shouldRenderAtDistance(d) || this.method_6838() != null;
	}

	@Override
	public Packet<?> method_18002() {
		return new EntitySpawnS2CPacket(this);
	}
}
