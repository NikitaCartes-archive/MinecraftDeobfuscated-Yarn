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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentEntity extends ProjectileEntity {
	private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
	private ItemStack tridentStack = new ItemStack(Items.field_8547);
	private boolean dealtDamage;
	public int field_7649;

	public TridentEntity(EntityType<? extends TridentEntity> entityType, World world) {
		super(entityType, world);
	}

	public TridentEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
		super(EntityType.field_6127, livingEntity, world);
		this.tridentStack = itemStack.copy();
		this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(itemStack));
	}

	@Environment(EnvType.CLIENT)
	public TridentEntity(World world, double d, double e, double f) {
		super(EntityType.field_6127, d, e, f, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(LOYALTY, (byte)0);
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		if ((this.dealtDamage || this.isNoClip()) && entity != null) {
			int i = this.dataTracker.get(LOYALTY);
			if (i > 0 && !this.isOwnerAlive()) {
				if (!this.world.isClient && this.pickupType == ProjectileEntity.PickupPermission.field_7593) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.remove();
			} else if (i > 0) {
				this.setNoClip(true);
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y + (double)entity.getStandingEyeHeight() - this.y, entity.z - this.z);
				this.y = this.y + vec3d.y * 0.015 * (double)i;
				if (this.world.isClient) {
					this.prevRenderY = this.y;
				}

				double d = 0.05 * (double)i;
				this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
				if (this.field_7649 == 0) {
					this.playSound(SoundEvents.field_14698, 10.0F, 1.0F);
				}

				this.field_7649++;
			}
		}

		super.tick();
	}

	private boolean isOwnerAlive() {
		Entity entity = this.getOwner();
		return entity == null || !entity.isAlive() ? false : !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
	}

	@Override
	protected ItemStack asItemStack() {
		return this.tridentStack.copy();
	}

	@Nullable
	@Override
	protected EntityHitResult getEntityCollision(Vec3d vec3d, Vec3d vec3d2) {
		return this.dealtDamage ? null : super.getEntityCollision(vec3d, vec3d2);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
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

		this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
		float g = 1.0F;
		if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.tridentStack)) {
			BlockPos blockPos = entity.getBlockPos();
			if (this.world.isSkyVisible(blockPos)) {
				LightningEntity lightningEntity = new LightningEntity(
					this.world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, false
				);
				lightningEntity.setChanneller(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
				((ServerWorld)this.world).addLightning(lightningEntity);
				soundEvent = SoundEvents.field_14896;
				g = 5.0F;
			}
		}

		this.playSound(soundEvent, g, 1.0F);
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
		this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.put("Trident", this.tridentStack.toTag(new CompoundTag()));
		compoundTag.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	protected void age() {
		int i = this.dataTracker.get(LOYALTY);
		if (this.pickupType != ProjectileEntity.PickupPermission.field_7593 || i <= 0) {
			super.age();
		}
	}

	@Override
	protected float getDragInWater() {
		return 0.99F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderFrom(double d, double e, double f) {
		return true;
	}
}
