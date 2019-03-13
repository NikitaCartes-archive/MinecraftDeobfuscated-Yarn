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
	private StatusEffect stewEffect;
	private int stewEffectDuration;
	private UUID lightningId;

	public MooshroomEntity(EntityType<? extends MooshroomEntity> entityType, World world) {
		super(entityType, world);
		this.field_6746 = Blocks.field_10402;
	}

	@Override
	public void method_5800(LightningEntity lightningEntity) {
		UUID uUID = lightningEntity.getUuid();
		if (!uUID.equals(this.lightningId)) {
			this.setType(this.getType() == MooshroomEntity.Type.field_18109 ? MooshroomEntity.Type.field_18110 : MooshroomEntity.Type.field_18109);
			this.lightningId = uUID;
			this.method_5783(SoundEvents.field_18266, 2.0F, 1.0F);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_18105, MooshroomEntity.Type.field_18109.name);
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() == Items.field_8428 && this.getBreedingAge() >= 0 && !playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
			boolean bl = false;
			ItemStack itemStack2;
			if (this.stewEffect != null) {
				bl = true;
				itemStack2 = new ItemStack(Items.field_8766);
				SuspiciousStewItem.addEffectToStew(itemStack2, this.stewEffect, this.stewEffectDuration);
				this.stewEffect = null;
				this.stewEffectDuration = 0;
			} else {
				itemStack2 = new ItemStack(Items.field_8208);
			}

			if (itemStack.isEmpty()) {
				playerEntity.method_6122(hand, itemStack2);
			} else if (!playerEntity.inventory.method_7394(itemStack2)) {
				playerEntity.method_7328(itemStack2, false);
			}

			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.field_18269;
			} else {
				soundEvent = SoundEvents.field_18268;
			}

			this.method_5783(soundEvent, 1.0F, 1.0F);
			return true;
		} else if (itemStack.getItem() == Items.field_8868 && this.getBreedingAge() >= 0) {
			this.field_6002.method_8406(ParticleTypes.field_11236, this.x, this.y + (double)(this.getHeight() / 2.0F), this.z, 0.0, 0.0, 0.0);
			if (!this.field_6002.isClient) {
				this.invalidate();
				CowEntity cowEntity = EntityType.COW.method_5883(this.field_6002);
				cowEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
				cowEntity.setHealth(this.getHealth());
				cowEntity.field_6283 = this.field_6283;
				if (this.hasCustomName()) {
					cowEntity.method_5665(this.method_5797());
				}

				this.field_6002.spawnEntity(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.field_6002
						.spawnEntity(new ItemEntity(this.field_6002, this.x, this.y + (double)this.getHeight(), this.z, new ItemStack(this.getType().field_18112.getBlock())));
				}

				itemStack.applyDamage(1, playerEntity);
				this.method_5783(SoundEvents.field_14705, 1.0F, 1.0F);
			}

			return true;
		} else {
			if (this.getType() == MooshroomEntity.Type.field_18110 && itemStack.getItem().method_7855(ItemTags.field_15543)) {
				if (this.stewEffect != null) {
					for (int j = 0; j < 2; j++) {
						this.field_6002
							.method_8406(
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
						this.field_6002
							.method_8406(
								ParticleTypes.field_11245,
								this.x + (double)(this.random.nextFloat() / 2.0F),
								this.y + (double)(this.getHeight() / 2.0F),
								this.z + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}

					this.stewEffect = pair.getLeft();
					this.stewEffectDuration = pair.getRight();
					this.method_5783(SoundEvents.field_18267, 2.0F, 1.0F);
				}
			}

			return super.method_5992(playerEntity, hand);
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putString("Type", this.getType().name);
		if (this.stewEffect != null) {
			compoundTag.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
			compoundTag.putInt("EffectDuration", this.stewEffectDuration);
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setType(MooshroomEntity.Type.fromName(compoundTag.getString("Type")));
		if (compoundTag.containsKey("EffectId", 1)) {
			this.stewEffect = StatusEffect.byRawId(compoundTag.getByte("EffectId"));
		}

		if (compoundTag.containsKey("EffectDuration", 3)) {
			this.stewEffectDuration = compoundTag.getInt("EffectDuration");
		}
	}

	private Pair<StatusEffect, Integer> method_18436(ItemStack itemStack) {
		FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).method_7711();
		return Pair.of(flowerBlock.getEffectInStew(), flowerBlock.getEffectInStewDuration());
	}

	private void setType(MooshroomEntity.Type type) {
		this.field_6011.set(field_18105, type.name);
	}

	public MooshroomEntity.Type getType() {
		return MooshroomEntity.Type.fromName(this.field_6011.get(field_18105));
	}

	public MooshroomEntity method_6495(PassiveEntity passiveEntity) {
		MooshroomEntity mooshroomEntity = EntityType.MOOSHROOM.method_5883(this.field_6002);
		mooshroomEntity.setType(this.chooseBabyType((MooshroomEntity)passiveEntity));
		return mooshroomEntity;
	}

	private MooshroomEntity.Type chooseBabyType(MooshroomEntity mooshroomEntity) {
		MooshroomEntity.Type type = this.getType();
		MooshroomEntity.Type type2 = mooshroomEntity.getType();
		MooshroomEntity.Type type3;
		if (type == type2 && this.random.nextInt(1024) == 0) {
			type3 = type == MooshroomEntity.Type.field_18110 ? MooshroomEntity.Type.field_18109 : MooshroomEntity.Type.field_18110;
		} else {
			type3 = this.random.nextBoolean() ? type : type2;
		}

		return type3;
	}

	public static enum Type {
		field_18109("red", Blocks.field_10559.method_9564()),
		field_18110("brown", Blocks.field_10251.method_9564());

		private final String name;
		private final BlockState field_18112;

		private Type(String string2, BlockState blockState) {
			this.name = string2;
			this.field_18112 = blockState;
		}

		@Environment(EnvType.CLIENT)
		public BlockState method_18437() {
			return this.field_18112;
		}

		private static MooshroomEntity.Type fromName(String string) {
			for (MooshroomEntity.Type type : values()) {
				if (type.name.equals(string)) {
					return type;
				}
			}

			return field_18109;
		}
	}
}
