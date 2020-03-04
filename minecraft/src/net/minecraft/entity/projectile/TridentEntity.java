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
	private static final TrackedData<Boolean> field_21514 = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private ItemStack tridentStack = new ItemStack(Items.TRIDENT);
	private boolean dealtDamage;
	public int returnTimer;

	public TridentEntity(EntityType<? extends TridentEntity> entityType, World world) {
		super(entityType, world);
	}

	public TridentEntity(World world, LivingEntity owner, ItemStack item) {
		super(EntityType.TRIDENT, owner, world);
		this.tridentStack = item.copy();
		this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(item));
		this.dataTracker.set(field_21514, item.hasEnchantmentGlint());
	}

	@Environment(EnvType.CLIENT)
	public TridentEntity(World world, double x, double y, double z) {
		super(EntityType.TRIDENT, x, y, z, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(LOYALTY, (byte)0);
		this.dataTracker.startTracking(field_21514, false);
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
				if (!this.world.isClient && this.pickupType == ProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.remove();
			} else if (i > 0) {
				this.setNoClip(true);
				Vec3d vec3d = new Vec3d(entity.getX() - this.getX(), entity.getEyeY() - this.getY(), entity.getZ() - this.getZ());
				this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double)i, this.getZ());
				if (this.world.isClient) {
					this.lastRenderY = this.getY();
				}

				double d = 0.05 * (double)i;
				this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
				if (this.returnTimer == 0) {
					this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
				}

				this.returnTimer++;
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

	@Environment(EnvType.CLIENT)
	public boolean method_23751() {
		return this.dataTracker.get(field_21514);
	}

	@Nullable
	@Override
	protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity entity = entityHitResult.getEntity();
		float f = 8.0F;
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			f += EnchantmentHelper.getAttackDamage(this.tridentStack, livingEntity.getGroup());
		}

		Entity entity2 = this.getOwner();
		DamageSource damageSource = DamageSource.trident(this, (Entity)(entity2 == null ? this : entity2));
		this.dealtDamage = true;
		SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
		if (entity.damage(damageSource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)entity;
				if (entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
				}

				this.onHit(livingEntity2);
			}
		}

		this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
		float g = 1.0F;
		if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.tridentStack)) {
			BlockPos blockPos = entity.getSenseCenterPos();
			if (this.world.isSkyVisible(blockPos)) {
				LightningEntity lightningEntity = new LightningEntity(
					this.world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, false
				);
				lightningEntity.setChanneller(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
				((ServerWorld)this.world).addLightning(lightningEntity);
				soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
				g = 5.0F;
			}
		}

		this.playSound(soundEvent, g, 1.0F);
	}

	@Override
	protected SoundEvent getHitSound() {
		return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		Entity entity = this.getOwner();
		if (entity == null || entity.getUuid() == player.getUuid()) {
			super.onPlayerCollision(player);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("Trident", 10)) {
			this.tridentStack = ItemStack.fromTag(tag.getCompound("Trident"));
		}

		this.dealtDamage = tag.getBoolean("DealtDamage");
		this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.put("Trident", this.tridentStack.toTag(new CompoundTag()));
		tag.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	public void age() {
		int i = this.dataTracker.get(LOYALTY);
		if (this.pickupType != ProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
			super.age();
		}
	}

	@Override
	protected float getDragInWater() {
		return 0.99F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}
}
