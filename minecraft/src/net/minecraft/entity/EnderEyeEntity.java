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
	private static final TrackedData<ItemStack> field_17080 = DataTracker.registerData(EnderEyeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private double field_7619;
	private double field_7618;
	private double field_7617;
	private int field_7620;
	private boolean field_7621;

	public EnderEyeEntity(EntityType<? extends EnderEyeEntity> entityType, World world) {
		super(entityType, world);
	}

	public EnderEyeEntity(World world, double d, double e, double f) {
		this(EntityType.EYE_OF_ENDER, world);
		this.field_7620 = 0;
		this.setPosition(d, e, f);
	}

	public void method_16933(ItemStack itemStack) {
		if (itemStack.getItem() != Items.field_8449 || itemStack.hasTag()) {
			this.getDataTracker().set(field_17080, SystemUtil.consume(itemStack.copy(), itemStackx -> itemStackx.setAmount(1)));
		}
	}

	private ItemStack method_16935() {
		return this.getDataTracker().get(field_17080);
	}

	@Override
	public ItemStack getItem() {
		ItemStack itemStack = this.method_16935();
		return itemStack.isEmpty() ? new ItemStack(Items.field_8449) : itemStack;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(field_17080, ItemStack.EMPTY);
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

	public void method_7478(BlockPos blockPos) {
		double d = (double)blockPos.getX();
		int i = blockPos.getY();
		double e = (double)blockPos.getZ();
		double f = d - this.x;
		double g = e - this.z;
		float h = MathHelper.sqrt(f * f + g * g);
		if (h > 12.0F) {
			this.field_7619 = this.x + f / (double)h * 12.0;
			this.field_7617 = this.z + g / (double)h * 12.0;
			this.field_7618 = this.y + 8.0;
		} else {
			this.field_7619 = d;
			this.field_7618 = (double)i;
			this.field_7617 = e;
		}

		this.field_7620 = 0;
		this.field_7621 = this.random.nextInt(5) > 0;
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
	public void update() {
		this.prevRenderX = this.x;
		this.prevRenderY = this.y;
		this.prevRenderZ = this.z;
		super.update();
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
			double d = this.field_7619 - this.x;
			double e = this.field_7617 - this.z;
			float g = (float)Math.sqrt(d * d + e * e);
			float h = (float)MathHelper.atan2(e, d);
			double i = MathHelper.lerp(0.0025, (double)f, (double)g);
			double j = vec3d.y;
			if (g < 1.0F) {
				i *= 0.8;
				j *= 0.8;
			}

			int k = this.y < this.field_7618 ? 1 : -1;
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
			this.field_7620++;
			if (this.field_7620 > 80 && !this.world.isClient) {
				this.playSound(SoundEvents.field_15210, 1.0F, 1.0F);
				this.invalidate();
				if (this.field_7621) {
					this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, this.getItem()));
				} else {
					this.world.playEvent(2003, new BlockPos(this), 0);
				}
			}
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		ItemStack itemStack = this.method_16935();
		if (!itemStack.isEmpty()) {
			compoundTag.put("Item", itemStack.toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("Item"));
		this.method_16933(itemStack);
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
