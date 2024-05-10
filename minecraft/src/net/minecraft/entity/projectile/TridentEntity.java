package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentEntity extends PersistentProjectileEntity {
	private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private boolean dealtDamage;
	public int returnTimer;

	public TridentEntity(EntityType<? extends TridentEntity> entityType, World world) {
		super(entityType, world);
	}

	public TridentEntity(World world, LivingEntity owner, ItemStack stack) {
		super(EntityType.TRIDENT, owner, world, stack, null);
		this.dataTracker.set(LOYALTY, this.getLoyalty(stack));
		this.dataTracker.set(ENCHANTED, stack.hasGlint());
	}

	public TridentEntity(World world, double x, double y, double z, ItemStack stack) {
		super(EntityType.TRIDENT, x, y, z, world, stack, stack);
		this.dataTracker.set(LOYALTY, this.getLoyalty(stack));
		this.dataTracker.set(ENCHANTED, stack.hasGlint());
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(LOYALTY, (byte)0);
		builder.add(ENCHANTED, false);
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
				if (!this.getWorld().isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.discard();
			} else {
				this.setNoClip(true);
				Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
				this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double)i, this.getZ());
				if (this.getWorld().isClient) {
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
		Entity entity2 = this.getOwner();
		DamageSource damageSource = this.getDamageSources().trident(this, (Entity)(entity2 == null ? this : entity2));
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			f = EnchantmentHelper.getDamage(serverWorld, this.getItemStack(), entity, damageSource, f);
		}

		this.dealtDamage = true;
		if (entity.damage(damageSource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (this.getWorld() instanceof ServerWorld serverWorld) {
				EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
			}

			if (entity instanceof LivingEntity livingEntity) {
				this.knockback(livingEntity, damageSource);
				this.onHit(livingEntity);
			}
		}

		this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
		this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0F, 1.0F);
	}

	@Override
	protected void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack shotFromStack) {
		EnchantmentHelper.onHitBlock(
			world, shotFromStack, this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null, this, null, blockHitResult.getPos(), this::kill
		);
	}

	@Override
	protected ItemStack getWeaponStack() {
		return this.getItemStack();
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
	}

	@Override
	protected ItemStack getDefaultItemStack() {
		return new ItemStack(Items.TRIDENT);
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
		this.dealtDamage = nbt.getBoolean("DealtDamage");
		this.dataTracker.set(LOYALTY, this.getLoyalty(this.getItemStack()));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("DealtDamage", this.dealtDamage);
	}

	private byte getLoyalty(ItemStack stack) {
		return this.getWorld() instanceof ServerWorld serverWorld
			? (byte)MathHelper.clamp(EnchantmentHelper.getTridentReturnAcceleration(serverWorld, stack, this), 0, 127)
			: 0;
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
