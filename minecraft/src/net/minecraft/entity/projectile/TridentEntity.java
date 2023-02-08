package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentEntity extends PersistentProjectileEntity {
	private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private ItemStack tridentStack = new ItemStack(Items.TRIDENT);
	private boolean dealtDamage;
	public int returnTimer;

	public TridentEntity(EntityType<? extends TridentEntity> entityType, World world) {
		super(entityType, world);
	}

	public TridentEntity(World world, LivingEntity owner, ItemStack stack) {
		super(EntityType.TRIDENT, owner, world);
		this.tridentStack = stack.copy();
		this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(stack));
		this.dataTracker.set(ENCHANTED, stack.hasGlint());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(LOYALTY, (byte)0);
		this.dataTracker.startTracking(ENCHANTED, false);
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		int i = this.dataTracker.get(LOYALTY);
		if (i > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
			if (!this.isOwnerAlive()) {
				if (!this.world.isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.discard();
			} else {
				this.setNoClip(true);
				Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
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

	public boolean isEnchanted() {
		return this.dataTracker.get(ENCHANTED);
	}

	@Nullable
	@Override
	protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		float f = 8.0F;
		if (entity instanceof LivingEntity livingEntity) {
			f += EnchantmentHelper.getAttackDamage(this.tridentStack, livingEntity.getGroup());
		}

		Entity entity2 = this.getOwner();
		DamageSource damageSource = this.getDamageSources().trident(this, (Entity)(entity2 == null ? this : entity2));
		this.dealtDamage = true;
		SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
		if (entity.damage(damageSource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (entity instanceof LivingEntity livingEntity2) {
				if (entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
				}

				this.onHit(livingEntity2);
			}
		}

		this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
		float g = 1.0F;
		if (this.world instanceof ServerWorld && this.world.isThundering() && this.hasChanneling()) {
			BlockPos blockPos = entity.getBlockPos();
			if (this.world.isSkyVisible(blockPos)) {
				LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.world);
				if (lightningEntity != null) {
					lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
					lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
					this.world.spawnEntity(lightningEntity);
					soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
					g = 5.0F;
				}
			}
		}

		this.playSound(soundEvent, g, 1.0F);
	}

	public boolean hasChanneling() {
		return EnchantmentHelper.hasChanneling(this.tridentStack);
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
	}

	@Override
	protected SoundEvent getHitSound() {
		return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (this.isOwner(player) || this.getOwner() == null) {
			super.onPlayerCollision(player);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Trident", NbtElement.COMPOUND_TYPE)) {
			this.tridentStack = ItemStack.fromNbt(nbt.getCompound("Trident"));
		}

		this.dealtDamage = nbt.getBoolean("DealtDamage");
		this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("Trident", this.tridentStack.writeNbt(new NbtCompound()));
		nbt.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	public void age() {
		int i = this.dataTracker.get(LOYALTY);
		if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
			super.age();
		}
	}

	@Override
	protected float getDragInWater() {
		return 0.99F;
	}

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}
}
