package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public class EnderEyeEntity extends Entity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(EnderEyeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private double velocityX;
	private double velocityY;
	private double velocityZ;
	private int useCount;
	private boolean dropsItem;

	public EnderEyeEntity(EntityType<? extends EnderEyeEntity> entityType, World world) {
		super(entityType, world);
	}

	public EnderEyeEntity(World world, double d, double e, double f) {
		this(EntityType.field_6061, world);
		this.useCount = 0;
		this.setPosition(d, e, f);
	}

	public void setItem(ItemStack itemStack) {
		if (itemStack.getItem() != Items.field_8449 || itemStack.hasTag()) {
			this.getDataTracker().set(ITEM, SystemUtil.consume(itemStack.copy(), itemStackx -> itemStackx.setAmount(1)));
		}
	}

	private ItemStack getTrackedItem() {
		return this.getDataTracker().get(ITEM);
	}

	@Override
	public ItemStack getStack() {
		ItemStack itemStack = this.getTrackedItem();
		return itemStack.isEmpty() ? new ItemStack(Items.field_8449) : itemStack;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	public void moveTowards(BlockPos blockPos) {
		double d = (double)blockPos.getX();
		int i = blockPos.getY();
		double e = (double)blockPos.getZ();
		double f = d - this.x;
		double g = e - this.z;
		float h = MathHelper.sqrt(f * f + g * g);
		if (h > 12.0F) {
			this.velocityX = this.x + f / (double)h * 12.0;
			this.velocityZ = this.z + g / (double)h * 12.0;
			this.velocityY = this.y + 8.0;
		} else {
			this.velocityX = d;
			this.velocityY = (double)i;
			this.velocityZ = e;
		}

		this.useCount = 0;
		this.dropsItem = this.random.nextInt(5) > 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}
	}

	@Override
	public void tick() {
		this.prevRenderX = this.x;
		this.prevRenderY = this.y;
		this.prevRenderZ = this.z;
		super.tick();
		Vec3d vec3d = this.getVelocity();
		this.x = this.x + vec3d.x;
		this.y = this.y + vec3d.y;
		this.z = this.z + vec3d.z;
		float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);

		while (this.pitch - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.pitch - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while (this.yaw - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.yaw - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		this.pitch = MathHelper.lerp(0.2F, this.prevPitch, this.pitch);
		this.yaw = MathHelper.lerp(0.2F, this.prevYaw, this.yaw);
		if (!this.world.isClient) {
			double d = this.velocityX - this.x;
			double e = this.velocityZ - this.z;
			float g = (float)Math.sqrt(d * d + e * e);
			float h = (float)MathHelper.atan2(e, d);
			double i = MathHelper.lerp(0.0025, (double)f, (double)g);
			double j = vec3d.y;
			if (g < 1.0F) {
				i *= 0.8;
				j *= 0.8;
			}

			int k = this.y < this.velocityY ? 1 : -1;
			vec3d = new Vec3d(Math.cos((double)h) * i, j + ((double)k - j) * 0.015F, Math.sin((double)h) * i);
			this.setVelocity(vec3d);
		}

		float l = 0.25F;
		if (this.isInsideWater()) {
			for (int m = 0; m < 4; m++) {
				this.world.addParticle(ParticleTypes.field_11247, this.x - vec3d.x * 0.25, this.y - vec3d.y * 0.25, this.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
			}
		} else {
			this.world
				.addParticle(
					ParticleTypes.field_11214,
					this.x - vec3d.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
					this.y - vec3d.y * 0.25 - 0.5,
					this.z - vec3d.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
					vec3d.x,
					vec3d.y,
					vec3d.z
				);
		}

		if (!this.world.isClient) {
			this.setPosition(this.x, this.y, this.z);
			this.useCount++;
			if (this.useCount > 80 && !this.world.isClient) {
				this.playSound(SoundEvents.field_15210, 1.0F, 1.0F);
				this.remove();
				if (this.dropsItem) {
					this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, this.getStack()));
				} else {
					this.world.playLevelEvent(2003, new BlockPos(this), 0);
				}
			}
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		ItemStack itemStack = this.getTrackedItem();
		if (!itemStack.isEmpty()) {
			compoundTag.put("Item", itemStack.toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("Item"));
		this.setItem(itemStack);
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public boolean canPlayerAttack() {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
