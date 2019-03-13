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
	private static final TrackedData<Byte> field_7647 = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
	private ItemStack field_7650 = new ItemStack(Items.field_8547);
	private boolean dealtDamage;
	public int field_7649;

	public TridentEntity(EntityType<? extends TridentEntity> entityType, World world) {
		super(entityType, world);
	}

	public TridentEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
		super(EntityType.TRIDENT, livingEntity, world);
		this.field_7650 = itemStack.copy();
		this.field_6011.set(field_7647, (byte)EnchantmentHelper.getLoyalty(itemStack));
	}

	@Environment(EnvType.CLIENT)
	public TridentEntity(World world, double d, double e, double f) {
		super(EntityType.TRIDENT, d, e, f, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7647, (byte)0);
	}

	@Override
	public void update() {
		if (this.field_7576 > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		if ((this.dealtDamage || this.isNoClip()) && entity != null) {
			int i = this.field_6011.get(field_7647);
			if (i > 0 && !this.method_7493()) {
				if (!this.field_6002.isClient && this.pickupType == ProjectileEntity.PickupType.PICKUP) {
					this.method_5699(this.method_7445(), 0.1F);
				}

				this.invalidate();
			} else if (i > 0) {
				this.setNoClip(true);
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y + (double)entity.getStandingEyeHeight() - this.y, entity.z - this.z);
				this.y = this.y + vec3d.y * 0.015 * (double)i;
				if (this.field_6002.isClient) {
					this.prevRenderY = this.y;
				}

				double d = 0.05 * (double)i;
				this.method_18799(this.method_18798().multiply(0.95).add(vec3d.normalize().multiply(d)));
				if (this.field_7649 == 0) {
					this.method_5783(SoundEvents.field_14698, 10.0F, 1.0F);
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
	protected ItemStack method_7445() {
		return this.field_7650.copy();
	}

	@Nullable
	@Override
	protected EntityHitResult method_7434(Vec3d vec3d, Vec3d vec3d2) {
		return this.dealtDamage ? null : super.method_7434(vec3d, vec3d2);
	}

	@Override
	protected void method_7454(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		float f = 8.0F;
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			f += EnchantmentHelper.getAttackDamage(this.field_7650, livingEntity.method_6046());
		}

		Entity entity2 = this.getOwner();
		DamageSource damageSource = DamageSource.method_5520(this, (Entity)(entity2 == null ? this : entity2));
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

		this.method_18799(this.method_18798().multiply(-0.01, -0.1, -0.01));
		float g = 1.0F;
		if (this.field_6002 instanceof ServerWorld && this.field_6002.isThundering() && EnchantmentHelper.hasChanneling(this.field_7650)) {
			BlockPos blockPos = entity.method_5704();
			if (this.field_6002.method_8311(blockPos)) {
				LightningEntity lightningEntity = new LightningEntity(
					this.field_6002, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, false
				);
				lightningEntity.method_6961(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
				((ServerWorld)this.field_6002).addLightning(lightningEntity);
				soundEvent = SoundEvents.field_14896;
				g = 5.0F;
			}
		}

		this.method_5783(soundEvent, g, 1.0F);
	}

	@Override
	protected SoundEvent method_7440() {
		return SoundEvents.field_15104;
	}

	@Override
	public void method_5694(PlayerEntity playerEntity) {
		Entity entity = this.getOwner();
		if (entity == null || entity.getUuid() == playerEntity.getUuid()) {
			super.method_5694(playerEntity);
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("Trident", 10)) {
			this.field_7650 = ItemStack.method_7915(compoundTag.getCompound("Trident"));
		}

		this.dealtDamage = compoundTag.getBoolean("DealtDamage");
		this.field_6011.set(field_7647, (byte)EnchantmentHelper.getLoyalty(this.field_7650));
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.method_10566("Trident", this.field_7650.method_7953(new CompoundTag()));
		compoundTag.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	protected void method_7446() {
		int i = this.field_6011.get(field_7647);
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
