package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class AbstractDonkeyEntity extends HorseBaseEntity {
	private static final TrackedData<Boolean> CHEST = DataTracker.registerData(AbstractDonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	protected AbstractDonkeyEntity(EntityType<? extends AbstractDonkeyEntity> entityType, World world) {
		super(entityType, world);
		this.field_6964 = false;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CHEST, false);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue((double)this.getChildHealthBonus());
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.175F);
		this.getAttributeInstance(JUMP_STRENGTH).setBaseValue(0.5);
	}

	public boolean hasChest() {
		return this.dataTracker.get(CHEST);
	}

	public void setHasChest(boolean bl) {
		this.dataTracker.set(CHEST, bl);
	}

	@Override
	protected int getInventorySize() {
		return this.hasChest() ? 17 : super.getInventorySize();
	}

	@Override
	public double getMountedHeightOffset() {
		return super.getMountedHeightOffset() - 0.25;
	}

	@Override
	protected SoundEvent getAngrySound() {
		super.getAngrySound();
		return SoundEvents.field_14661;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.hasChest()) {
			if (!this.world.isClient) {
				this.dropItem(Blocks.field_10034);
			}

			this.setHasChest(false);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("ChestedHorse", this.hasChest());
		if (this.hasChest()) {
			ListTag listTag = new ListTag();

			for (int i = 2; i < this.items.getInvSize(); i++) {
				ItemStack itemStack = this.items.getInvStack(i);
				if (!itemStack.isEmpty()) {
					CompoundTag compoundTag2 = new CompoundTag();
					compoundTag2.putByte("Slot", (byte)i);
					itemStack.toTag(compoundTag2);
					listTag.add(compoundTag2);
				}
			}

			compoundTag.put("Items", listTag);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setHasChest(compoundTag.getBoolean("ChestedHorse"));
		if (this.hasChest()) {
			ListTag listTag = compoundTag.getList("Items", 10);
			this.method_6721();

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(i);
				int j = compoundTag2.getByte("Slot") & 255;
				if (j >= 2 && j < this.items.getInvSize()) {
					this.items.setInvStack(j, ItemStack.fromTag(compoundTag2));
				}
			}
		}

		this.updateSaddle();
	}

	@Override
	public boolean equip(int i, ItemStack itemStack) {
		if (i == 499) {
			if (this.hasChest() && itemStack.isEmpty()) {
				this.setHasChest(false);
				this.method_6721();
				return true;
			}

			if (!this.hasChest() && itemStack.getItem() == Blocks.field_10034.asItem()) {
				this.setHasChest(true);
				this.method_6721();
				return true;
			}
		}

		return super.equip(i, itemStack);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(playerEntity, hand);
		} else {
			if (!this.isBaby()) {
				if (this.isTame() && playerEntity.isSneaking()) {
					this.openInventory(playerEntity);
					return true;
				}

				if (this.hasPassengers()) {
					return super.interactMob(playerEntity, hand);
				}
			}

			if (!itemStack.isEmpty()) {
				boolean bl = this.receiveFood(playerEntity, itemStack);
				if (!bl) {
					if (!this.isTame() || itemStack.getItem() == Items.field_8448) {
						if (itemStack.useOnEntity(playerEntity, this, hand)) {
							return true;
						} else {
							this.playAngrySound();
							return true;
						}
					}

					if (!this.hasChest() && itemStack.getItem() == Blocks.field_10034.asItem()) {
						this.setHasChest(true);
						this.playAddChestSound();
						bl = true;
						this.method_6721();
					}

					if (!this.isBaby() && !this.isSaddled() && itemStack.getItem() == Items.field_8175) {
						this.openInventory(playerEntity);
						return true;
					}
				}

				if (bl) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return true;
				}
			}

			if (this.isBaby()) {
				return super.interactMob(playerEntity, hand);
			} else {
				this.putPlayerOnBack(playerEntity);
				return true;
			}
		}
	}

	protected void playAddChestSound() {
		this.playSound(SoundEvents.field_14598, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	public int method_6702() {
		return 5;
	}
}
