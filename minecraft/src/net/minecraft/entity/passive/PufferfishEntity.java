package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity extends FishEntity {
	private static final TrackedData<Integer> field_6835 = DataTracker.registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int field_6833;
	private int field_6832;
	private static final Predicate<LivingEntity> field_6834 = livingEntity -> {
		if (livingEntity == null) {
			return false;
		} else {
			return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative()
				? livingEntity.method_6046() != EntityGroup.AQUATIC
				: false;
		}
	};

	public PufferfishEntity(EntityType<? extends PufferfishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6835, 0);
	}

	public int method_6594() {
		return this.field_6011.get(field_6835);
	}

	public void method_6596(int i) {
		this.field_6011.set(field_6835, i);
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_6835.equals(trackedData)) {
			this.refreshSize();
		}

		super.method_5674(trackedData);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("PuffState", this.method_6594());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.method_6596(compoundTag.getInt("PuffState"));
	}

	@Override
	protected ItemStack method_6452() {
		return new ItemStack(Items.field_8108);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.field_6201.add(1, new PufferfishEntity.class_1455(this));
	}

	@Override
	public void update() {
		if (this.isValid() && !this.field_6002.isClient) {
			if (this.field_6833 > 0) {
				if (this.method_6594() == 0) {
					this.method_5783(SoundEvents.field_15235, this.getSoundVolume(), this.getSoundPitch());
					this.method_6596(1);
				} else if (this.field_6833 > 40 && this.method_6594() == 1) {
					this.method_5783(SoundEvents.field_15235, this.getSoundVolume(), this.getSoundPitch());
					this.method_6596(2);
				}

				this.field_6833++;
			} else if (this.method_6594() != 0) {
				if (this.field_6832 > 60 && this.method_6594() == 2) {
					this.method_5783(SoundEvents.field_15133, this.getSoundVolume(), this.getSoundPitch());
					this.method_6596(1);
				} else if (this.field_6832 > 100 && this.method_6594() == 1) {
					this.method_5783(SoundEvents.field_15133, this.getSoundVolume(), this.getSoundPitch());
					this.method_6596(0);
				}

				this.field_6832++;
			}
		}

		super.update();
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.isValid() && this.method_6594() > 0) {
			for (MobEntity mobEntity : this.field_6002.method_8390(MobEntity.class, this.method_5829().expand(0.3), field_6834)) {
				if (mobEntity.isValid()) {
					this.method_6593(mobEntity);
				}
			}
		}
	}

	private void method_6593(MobEntity mobEntity) {
		int i = this.method_6594();
		if (mobEntity.damage(DamageSource.method_5511(this), (float)(1 + i))) {
			mobEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 60 * i, 0));
			this.method_5783(SoundEvents.field_14848, 1.0F, 1.0F);
		}
	}

	@Override
	public void method_5694(PlayerEntity playerEntity) {
		int i = this.method_6594();
		if (playerEntity instanceof ServerPlayerEntity && i > 0 && playerEntity.damage(DamageSource.method_5511(this), (float)(1 + i))) {
			((ServerPlayerEntity)playerEntity).field_13987.sendPacket(new GameStateChangeS2CPacket(9, 0.0F));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 60 * i, 0));
		}
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14553;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14888;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14748;
	}

	@Override
	protected SoundEvent method_6457() {
		return SoundEvents.field_15004;
	}

	@Override
	public EntitySize method_18377(EntityPose entityPose) {
		return super.method_18377(entityPose).scaled(method_6592(this.method_6594()));
	}

	private static float method_6592(int i) {
		switch (i) {
			case 0:
				return 0.5F;
			case 1:
				return 0.7F;
			default:
				return 1.0F;
		}
	}

	static class class_1455 extends Goal {
		private final PufferfishEntity field_6836;

		public class_1455(PufferfishEntity pufferfishEntity) {
			this.field_6836 = pufferfishEntity;
		}

		@Override
		public boolean canStart() {
			List<LivingEntity> list = this.field_6836.field_6002.method_8390(LivingEntity.class, this.field_6836.method_5829().expand(2.0), PufferfishEntity.field_6834);
			return !list.isEmpty();
		}

		@Override
		public void start() {
			this.field_6836.field_6833 = 1;
			this.field_6836.field_6832 = 0;
		}

		@Override
		public void onRemove() {
			this.field_6836.field_6833 = 0;
		}

		@Override
		public boolean shouldContinue() {
			List<LivingEntity> list = this.field_6836.field_6002.method_8390(LivingEntity.class, this.field_6836.method_5829().expand(2.0), PufferfishEntity.field_6834);
			return !list.isEmpty();
		}
	}
}
