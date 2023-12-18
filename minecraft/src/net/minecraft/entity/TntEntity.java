package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class TntEntity extends Entity implements Ownable {
	private static final TrackedData<Integer> FUSE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
	private static final int DEFAULT_FUSE = 80;
	private static final String BLOCK_STATE_NBT_KEY = "block_state";
	public static final String FUSE_NBT_KEY = "fuse";
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
		this.dataTracker.startTracking(BLOCK_STATE, Blocks.TNT.getDefaultState());
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
		if (this.isOnGround()) {
			this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
		}

		int i = this.getFuse() - 1;
		this.setFuse(i);
		if (i <= 0) {
			this.discard();
			if (!this.getWorld().isClient) {
				this.explode();
			}
		} else {
			this.updateWaterState();
			if (this.getWorld().isClient) {
				this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	private void explode() {
		float f = 4.0F;
		this.getWorld().createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), 4.0F, World.ExplosionSourceType.TNT);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("fuse", (short)this.getFuse());
		nbt.put("block_state", NbtHelper.fromBlockState(this.getBlockState()));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.setFuse(nbt.getShort("fuse"));
		if (nbt.contains("block_state", NbtElement.COMPOUND_TYPE)) {
			this.setBlockState(NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("block_state")));
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		return this.causingEntity;
	}

	@Override
	public void copyFrom(Entity original) {
		super.copyFrom(original);
		if (original instanceof TntEntity tntEntity) {
			this.causingEntity = tntEntity.causingEntity;
		}
	}

	public void setFuse(int fuse) {
		this.dataTracker.set(FUSE, fuse);
	}

	public int getFuse() {
		return this.dataTracker.get(FUSE);
	}

	public void setBlockState(BlockState state) {
		this.dataTracker.set(BLOCK_STATE, state);
	}

	public BlockState getBlockState() {
		return this.dataTracker.get(BLOCK_STATE);
	}
}
