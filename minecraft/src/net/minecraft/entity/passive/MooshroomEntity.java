package net.minecraft.entity.passive;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.item.block.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity {
	private static final TrackedData<String> field_18105 = DataTracker.registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
	private StatusEffect field_18106;
	private int field_18107;
	private UUID field_18108;

	public MooshroomEntity(EntityType<? extends MooshroomEntity> entityType, World world) {
		super(entityType, world);
		this.spawningGround = Blocks.field_10402;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		UUID uUID = lightningEntity.getUuid();
		if (!uUID.equals(this.field_18108)) {
			this.method_18433(
				this.method_18435() == MooshroomEntity.class_4053.field_18109 ? MooshroomEntity.class_4053.field_18110 : MooshroomEntity.class_4053.field_18109
			);
			this.field_18108 = uUID;
			this.playSound(SoundEvents.field_18266, 2.0F, 1.0F);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_18105, MooshroomEntity.class_4053.field_18109.field_18111);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8428 && this.getBreedingAge() >= 0 && !playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
			boolean bl = false;
			ItemStack itemStack2;
			if (this.field_18106 != null) {
				bl = true;
				itemStack2 = new ItemStack(Items.field_8766);
				SuspiciousStewItem.addEffectToStew(itemStack2, this.field_18106, this.field_18107);
				this.field_18106 = null;
				this.field_18107 = 0;
			} else {
				itemStack2 = new ItemStack(Items.field_8208);
			}

			if (itemStack.isEmpty()) {
				playerEntity.setStackInHand(hand, itemStack2);
			} else if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.field_18269;
			} else {
				soundEvent = SoundEvents.field_18268;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
			return true;
		} else if (itemStack.getItem() == Items.field_8868 && this.getBreedingAge() >= 0) {
			this.world.addParticle(ParticleTypes.field_11236, this.x, this.y + (double)(this.getHeight() / 2.0F), this.z, 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.invalidate();
				CowEntity cowEntity = EntityType.COW.create(this.world);
				cowEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
				cowEntity.setHealth(this.getHealth());
				cowEntity.field_6283 = this.field_6283;
				if (this.hasCustomName()) {
					cowEntity.setCustomName(this.getCustomName());
				}

				this.world.spawnEntity(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.world
						.spawnEntity(new ItemEntity(this.world, this.x, this.y + (double)this.getHeight(), this.z, new ItemStack(this.method_18435().field_18112.getBlock())));
				}

				itemStack.applyDamage(1, playerEntity);
				this.playSound(SoundEvents.field_14705, 1.0F, 1.0F);
			}

			return true;
		} else {
			if (this.method_18435() == MooshroomEntity.class_4053.field_18110 && itemStack.getItem().matches(ItemTags.field_15543)) {
				if (this.field_18106 != null) {
					for (int j = 0; j < 2; j++) {
						this.world
							.addParticle(
								ParticleTypes.field_11251,
								this.x + (double)(this.random.nextFloat() / 2.0F),
								this.y + (double)(this.getHeight() / 2.0F),
								this.z + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}
				} else {
					Pair<StatusEffect, Integer> pair = this.method_18436(itemStack);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					for (int i = 0; i < 4; i++) {
						this.world
							.addParticle(
								ParticleTypes.field_11245,
								this.x + (double)(this.random.nextFloat() / 2.0F),
								this.y + (double)(this.getHeight() / 2.0F),
								this.z + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}

					this.field_18106 = pair.getLeft();
					this.field_18107 = pair.getRight();
					this.playSound(SoundEvents.field_18267, 2.0F, 1.0F);
				}
			}

			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putString("Type", this.method_18435().field_18111);
		if (this.field_18106 != null) {
			compoundTag.putByte("EffectId", (byte)StatusEffect.getRawId(this.field_18106));
			compoundTag.putInt("EffectDuration", this.field_18107);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_18433(MooshroomEntity.class_4053.method_18441(compoundTag.getString("Type")));
		if (compoundTag.containsKey("EffectId", 1)) {
			this.field_18106 = StatusEffect.byRawId(compoundTag.getByte("EffectId"));
		}

		if (compoundTag.containsKey("EffectDuration", 3)) {
			this.field_18107 = compoundTag.getInt("EffectDuration");
		}
	}

	private Pair<StatusEffect, Integer> method_18436(ItemStack itemStack) {
		FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
		return Pair.of(flowerBlock.getEffectInStew(), flowerBlock.getEffectInStewDuration());
	}

	private void method_18433(MooshroomEntity.class_4053 arg) {
		this.dataTracker.set(field_18105, arg.field_18111);
	}

	public MooshroomEntity.class_4053 method_18435() {
		return MooshroomEntity.class_4053.method_18441(this.dataTracker.get(field_18105));
	}

	public MooshroomEntity method_6495(PassiveEntity passiveEntity) {
		MooshroomEntity mooshroomEntity = EntityType.MOOSHROOM.create(this.world);
		mooshroomEntity.method_18433(this.method_18434((MooshroomEntity)passiveEntity));
		return mooshroomEntity;
	}

	private MooshroomEntity.class_4053 method_18434(MooshroomEntity mooshroomEntity) {
		MooshroomEntity.class_4053 lv = this.method_18435();
		MooshroomEntity.class_4053 lv2 = mooshroomEntity.method_18435();
		MooshroomEntity.class_4053 lv3;
		if (lv == lv2 && this.random.nextInt(1024) == 0) {
			lv3 = lv == MooshroomEntity.class_4053.field_18110 ? MooshroomEntity.class_4053.field_18109 : MooshroomEntity.class_4053.field_18110;
		} else {
			lv3 = this.random.nextBoolean() ? lv : lv2;
		}

		return lv3;
	}

	public static enum class_4053 {
		field_18109("red", Blocks.field_10559.getDefaultState()),
		field_18110("brown", Blocks.field_10251.getDefaultState());

		private final String field_18111;
		private final BlockState field_18112;

		private class_4053(String string2, BlockState blockState) {
			this.field_18111 = string2;
			this.field_18112 = blockState;
		}

		@Environment(EnvType.CLIENT)
		public BlockState method_18437() {
			return this.field_18112;
		}

		private static MooshroomEntity.class_4053 method_18441(String string) {
			for (MooshroomEntity.class_4053 lv : values()) {
				if (lv.field_18111.equals(string)) {
					return lv;
				}
			}

			return field_18109;
		}
	}
}
