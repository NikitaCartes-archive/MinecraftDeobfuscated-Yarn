package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_1310;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
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
			return !(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator() && !((PlayerEntity)livingEntity).isCreative()
				? livingEntity.method_6046() != class_1310.field_6292
				: false;
		}
	};
	private float field_6831 = -1.0F;
	private float field_6830;

	public PufferfishEntity(World world) {
		super(EntityType.PUFFERFISH, world);
		this.setSize(0.7F, 0.7F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_6835, 0);
	}

	public int method_6594() {
		return this.dataTracker.get(field_6835);
	}

	public void method_6596(int i) {
		this.dataTracker.set(field_6835, i);
		this.method_6592(i);
	}

	private void method_6592(int i) {
		float f = 1.0F;
		if (i == 1) {
			f = 0.7F;
		} else if (i == 0) {
			f = 0.5F;
		}

		this.method_6595(f);
	}

	@Override
	protected final void setSize(float f, float g) {
		boolean bl = this.field_6831 > 0.0F;
		this.field_6831 = f;
		this.field_6830 = g;
		if (!bl) {
			this.method_6595(1.0F);
		}
	}

	private void method_6595(float f) {
		super.setSize(this.field_6831 * f, this.field_6830 * f);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		this.method_6592(this.method_6594());
		super.onTrackedDataSet(trackedData);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("PuffState", this.method_6594());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_6596(compoundTag.getInt("PuffState"));
	}

	@Override
	protected ItemStack method_6452() {
		return new ItemStack(Items.field_8108);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(1, new PufferfishEntity.class_1455(this));
	}

	@Override
	public void update() {
		if (this.isValid() && !this.world.isRemote) {
			if (this.field_6833 > 0) {
				if (this.method_6594() == 0) {
					this.playSoundAtEntity(SoundEvents.field_15235, this.getSoundVolume(), this.getSoundPitch());
					this.method_6596(1);
				} else if (this.field_6833 > 40 && this.method_6594() == 1) {
					this.playSoundAtEntity(SoundEvents.field_15235, this.getSoundVolume(), this.getSoundPitch());
					this.method_6596(2);
				}

				this.field_6833++;
			} else if (this.method_6594() != 0) {
				if (this.field_6832 > 60 && this.method_6594() == 2) {
					this.playSoundAtEntity(SoundEvents.field_15133, this.getSoundVolume(), this.getSoundPitch());
					this.method_6596(1);
				} else if (this.field_6832 > 100 && this.method_6594() == 1) {
					this.playSoundAtEntity(SoundEvents.field_15133, this.getSoundVolume(), this.getSoundPitch());
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
		if (this.method_6594() > 0) {
			for (MobEntity mobEntity : this.world.getEntities(MobEntity.class, this.getBoundingBox().expand(0.3), field_6834)) {
				if (mobEntity.isValid()) {
					this.method_6593(mobEntity);
				}
			}
		}
	}

	private void method_6593(MobEntity mobEntity) {
		int i = this.method_6594();
		if (mobEntity.damage(DamageSource.mob(this), (float)(1 + i))) {
			mobEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 60 * i, 0));
			this.playSoundAtEntity(SoundEvents.field_14848, 1.0F, 1.0F);
		}
	}

	@Override
	public void method_5694(PlayerEntity playerEntity) {
		int i = this.method_6594();
		if (playerEntity instanceof ServerPlayerEntity && i > 0 && playerEntity.damage(DamageSource.mob(this), (float)(1 + i))) {
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new GameStateChangeClientPacket(9, 0.0F));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 60 * i, 0));
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14553;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14888;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14748;
	}

	@Override
	protected SoundEvent method_6457() {
		return SoundEvents.field_15004;
	}

	static class class_1455 extends Goal {
		private final PufferfishEntity field_6836;

		public class_1455(PufferfishEntity pufferfishEntity) {
			this.field_6836 = pufferfishEntity;
		}

		@Override
		public boolean canStart() {
			List<LivingEntity> list = this.field_6836.world.getEntities(LivingEntity.class, this.field_6836.getBoundingBox().expand(2.0), PufferfishEntity.field_6834);
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
			List<LivingEntity> list = this.field_6836.world.getEntities(LivingEntity.class, this.field_6836.getBoundingBox().expand(2.0), PufferfishEntity.field_6834);
			return !list.isEmpty();
		}
	}
}
