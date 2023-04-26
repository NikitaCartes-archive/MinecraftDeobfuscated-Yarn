package net.minecraft.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class EyeOfEnderEntity extends Entity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(EyeOfEnderEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private double targetX;
	private double targetY;
	private double targetZ;
	private int lifespan;
	private boolean dropsItem;

	public EyeOfEnderEntity(EntityType<? extends EyeOfEnderEntity> entityType, World world) {
		super(entityType, world);
	}

	public EyeOfEnderEntity(World world, double x, double y, double z) {
		this(EntityType.EYE_OF_ENDER, world);
		this.setPosition(x, y, z);
	}

	public void setItem(ItemStack stack) {
		if (!stack.isOf(Items.ENDER_EYE) || stack.hasNbt()) {
			this.getDataTracker().set(ITEM, stack.copyWithCount(1));
		}
	}

	private ItemStack getTrackedItem() {
		return this.getDataTracker().get(ITEM);
	}

	@Override
	public ItemStack getStack() {
		ItemStack itemStack = this.getTrackedItem();
		return itemStack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemStack;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(d)) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	/**
	 * Sets where the eye will fly towards.
	 * If close enough, it will fly directly towards it, otherwise, it will fly upwards, in the direction of the BlockPos.
	 * 
	 * @param pos the block the eye of ender is drawn towards
	 */
	public void initTargetPos(BlockPos pos) {
		double d = (double)pos.getX();
		int i = pos.getY();
		double e = (double)pos.getZ();
		double f = d - this.getX();
		double g = e - this.getZ();
		double h = Math.sqrt(f * f + g * g);
		if (h > 12.0) {
			this.targetX = this.getX() + f / h * 12.0;
			this.targetZ = this.getZ() + g / h * 12.0;
			this.targetY = this.getY() + 8.0;
		} else {
			this.targetX = d;
			this.targetY = (double)i;
			this.targetZ = e;
		}

		this.lifespan = 0;
		this.dropsItem = this.random.nextInt(5) > 0;
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.setVelocity(x, y, z);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			double d = Math.sqrt(x * x + z * z);
			this.setYaw((float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI));
			this.setPitch((float)(MathHelper.atan2(y, d) * 180.0F / (float)Math.PI));
			this.prevYaw = this.getYaw();
			this.prevPitch = this.getPitch();
		}
	}

	@Override
	public void tick() {
		super.tick();
		Vec3d vec3d = this.getVelocity();
		double d = this.getX() + vec3d.x;
		double e = this.getY() + vec3d.y;
		double f = this.getZ() + vec3d.z;
		double g = vec3d.horizontalLength();
		this.setPitch(ProjectileEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, g) * 180.0F / (float)Math.PI)));
		this.setYaw(ProjectileEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI)));
		if (!this.getWorld().isClient) {
			double h = this.targetX - d;
			double i = this.targetZ - f;
			float j = (float)Math.sqrt(h * h + i * i);
			float k = (float)MathHelper.atan2(i, h);
			double l = MathHelper.lerp(0.0025, g, (double)j);
			double m = vec3d.y;
			if (j < 1.0F) {
				l *= 0.8;
				m *= 0.8;
			}

			int n = this.getY() < this.targetY ? 1 : -1;
			vec3d = new Vec3d(Math.cos((double)k) * l, m + ((double)n - m) * 0.015F, Math.sin((double)k) * l);
			this.setVelocity(vec3d);
		}

		float o = 0.25F;
		if (this.isTouchingWater()) {
			for (int p = 0; p < 4; p++) {
				this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
			}
		} else {
			this.getWorld()
				.addParticle(
					ParticleTypes.PORTAL,
					d - vec3d.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
					e - vec3d.y * 0.25 - 0.5,
					f - vec3d.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
					vec3d.x,
					vec3d.y,
					vec3d.z
				);
		}

		if (!this.getWorld().isClient) {
			this.setPosition(d, e, f);
			this.lifespan++;
			if (this.lifespan > 80 && !this.getWorld().isClient) {
				this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
				this.discard();
				if (this.dropsItem) {
					this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.getStack()));
				} else {
					this.getWorld().syncWorldEvent(WorldEvents.EYE_OF_ENDER_BREAKS, this.getBlockPos(), 0);
				}
			}
		} else {
			this.setPos(d, e, f);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		ItemStack itemStack = this.getTrackedItem();
		if (!itemStack.isEmpty()) {
			nbt.put("Item", itemStack.writeNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("Item"));
		this.setItem(itemStack);
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}
}
