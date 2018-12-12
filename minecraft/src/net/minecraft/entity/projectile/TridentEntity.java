package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentEntity extends ProjectileEntity {
	private static final TrackedData<Byte> field_7647 = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
	private ItemStack tridentStack = new ItemStack(Items.field_8547);
	private boolean dealtDamage;
	public int field_7649;

	public TridentEntity(World world) {
		super(EntityType.TRIDENT, world);
	}

	public TridentEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
		super(EntityType.TRIDENT, livingEntity, world);
		this.tridentStack = itemStack.copy();
		this.dataTracker.set(field_7647, (byte)EnchantmentHelper.getLoyalty(itemStack));
	}

	@Environment(EnvType.CLIENT)
	public TridentEntity(World world, double d, double e, double f) {
		super(EntityType.TRIDENT, d, e, f, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_7647, (byte)0);
	}

	@Override
	public void update() {
		if (this.field_7576 > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		if ((this.dealtDamage || this.isNoClip()) && entity != null) {
			int i = this.dataTracker.get(field_7647);
			if (i > 0 && !this.method_7493()) {
				if (!this.world.isClient && this.pickupType == ProjectileEntity.PickupType.PICKUP) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.invalidate();
			} else if (i > 0) {
				this.setNoClip(true);
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y + (double)entity.getEyeHeight() - this.y, entity.z - this.z);
				this.y = this.y + vec3d.y * 0.015 * (double)i;
				if (this.world.isClient) {
					this.prevRenderY = this.y;
				}

				vec3d = vec3d.normalize();
				double d = 0.05 * (double)i;
				this.velocityX = this.velocityX + (vec3d.x * d - this.velocityX * 0.05);
				this.velocityY = this.velocityY + (vec3d.y * d - this.velocityY * 0.05);
				this.velocityZ = this.velocityZ + (vec3d.z * d - this.velocityZ * 0.05);
				if (this.field_7649 == 0) {
					this.playSoundAtEntity(SoundEvents.field_14698, 10.0F, 1.0F);
				}

				this.field_7649++;
			}
		}

		super.update();
	}

	private boolean method_7493() {
		Entity entity = this.getOwner();
		return entity == null || !entity.isValid() ? false : !(entity instanceof ServerPlayerEntity) || !((ServerPlayerEntity)entity).isSpectator();
	}

	@Override
	protected ItemStack asItemStack() {
		return this.tridentStack.copy();
	}

	@Nullable
	@Override
	protected Entity method_7434(Vec3d vec3d, Vec3d vec3d2) {
		return this.dealtDamage ? null : super.method_7434(vec3d, vec3d2);
	}

	@Override
	protected void method_7454(HitResult hitResult) {
		Entity entity = hitResult.entity;
		float f = 8.0F;
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			f += EnchantmentHelper.getAttackDamage(this.tridentStack, livingEntity.getGroup());
		}

		Entity entity2 = this.getOwner();
		DamageSource damageSource = DamageSource.trident(this, (Entity)(entity2 == null ? this : entity2));
		this.dealtDamage = true;
		SoundEvent soundEvent = SoundEvents.field_15213;
		if (entity.damage(damageSource, f) && entity instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity)entity;
			if (entity2 instanceof LivingEntity) {
				EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
				EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
			}

			this.onHit(livingEntity2);
		}

		this.velocityX *= -0.01F;
		this.velocityY *= -0.1F;
		this.velocityZ *= -0.01F;
		float g = 1.0F;
		if (this.world.isThundering() && EnchantmentHelper.hasChanneling(this.tridentStack)) {
			BlockPos blockPos = entity.getPos();
			if (this.world.isSkyVisible(blockPos)) {
				LightningEntity lightningEntity = new LightningEntity(
					this.world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, false
				);
				lightningEntity.method_6961(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
				this.world.addGlobalEntity(lightningEntity);
				soundEvent = SoundEvents.field_14896;
				g = 5.0F;
			}
		}

		this.playSoundAtEntity(soundEvent, g, 1.0F);
	}

	@Override
	protected SoundEvent getSound() {
		return SoundEvents.field_15104;
	}

	@Override
	public void onPlayerCollision(PlayerEntity playerEntity) {
		Entity entity = this.getOwner();
		if (entity == null || entity.getUuid() == playerEntity.getUuid()) {
			super.onPlayerCollision(playerEntity);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Trident", 10)) {
			this.tridentStack = ItemStack.fromTag(compoundTag.getCompound("Trident"));
		}

		this.dealtDamage = compoundTag.getBoolean("DealtDamage");
		this.dataTracker.set(field_7647, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.put("Trident", this.tridentStack.toTag(new CompoundTag()));
		compoundTag.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	protected void method_7446() {
		int i = this.dataTracker.get(field_7647);
		if (this.pickupType != ProjectileEntity.PickupType.PICKUP || i <= 0) {
			super.method_7446();
		}
	}

	@Override
	protected float method_7436() {
		return 0.99F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderFrom(double d, double e, double f) {
		return true;
	}
}
