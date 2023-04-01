package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_8293;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class TntEntity extends Entity implements Ownable {
	public static final String field_44107 = "block_state";
	private static final TrackedData<Integer> FUSE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<BlockState> field_44108 = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
	private static final int DEFAULT_FUSE = 80;
	@Nullable
	private LivingEntity causingEntity;

	public TntEntity(EntityType<? extends TntEntity> entityType, World world) {
		super(entityType, world);
		this.intersectionChecked = true;
	}

	public TntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
		this(EntityType.TNT, world);
		this.setPosition(x, y, z);
		double d = world.random.nextDouble() * (float) (Math.PI * 2);
		this.setVelocity(-Math.sin(d) * 0.02, 0.2F, -Math.cos(d) * 0.02);
		this.setFuse(80);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
		this.causingEntity = igniter;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(FUSE, 80);
		this.dataTracker.startTracking(field_44108, Blocks.TNT.getDefaultState());
	}

	public BlockState method_50691() {
		return this.dataTracker.get(field_44108);
	}

	public void method_50690(BlockState blockState) {
		this.dataTracker.set(field_44108, blockState);
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	public boolean canHit() {
		return !this.isRemoved();
	}

	@Override
	public void tick() {
		if (!this.hasNoGravity()) {
			this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
		}

		this.move(MovementType.SELF, this.getVelocity());
		this.setVelocity(this.getVelocity().multiply(0.98));
		if (this.onGround) {
			this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
		}

		int i = this.getFuse() - 1;
		this.setFuse(i);
		if (i <= 0) {
			this.discard();
			if (!this.world.isClient) {
				this.explode();
			}
		} else {
			this.updateWaterState();
			if (this.world.isClient) {
				this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	private void explode() {
		float f = 4.0F;
		this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), 4.0F, World.ExplosionSourceType.TNT);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("Fuse", (short)this.getFuse());
		this.method_50690(NbtHelper.toBlockState(this.world.createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("block_state")));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.setFuse(nbt.getShort("Fuse"));
		nbt.put("block_state", NbtHelper.fromBlockState(this.method_50691()));
	}

	@Nullable
	public LivingEntity getOwner() {
		return this.causingEntity;
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.15F;
	}

	public void setFuse(int fuse) {
		int i = class_8293.field_43671.method_50116() ? this.world.random.nextInt(399) + 1 : fuse;
		this.dataTracker.set(FUSE, i);
	}

	public int getFuse() {
		return this.dataTracker.get(FUSE);
	}

	@Override
	protected boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		return class_8293.field_43633.method_50116() ? true : bl;
	}
}
