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

	public void setHasChest(boolean hasChest) {
		this.dataTracker.set(CHEST, hasChest);
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
		return SoundEvents.ENTITY_DONKEY_ANGRY;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.hasChest()) {
			if (!this.world.isClient) {
				this.dropItem(Blocks.CHEST);
			}

			this.setHasChest(false);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("ChestedHorse", this.hasChest());
		if (this.hasChest()) {
			ListTag listTag = new ListTag();

			for (int i = 2; i < this.items.size(); i++) {
				ItemStack itemStack = this.items.getStack(i);
				if (!itemStack.isEmpty()) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.putByte("Slot", (byte)i);
					itemStack.toTag(compoundTag);
					listTag.add(compoundTag);
				}
			}

			tag.put("Items", listTag);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setHasChest(tag.getBoolean("ChestedHorse"));
		if (this.hasChest()) {
			ListTag listTag = tag.getList("Items", 10);
			this.method_6721();

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompound(i);
				int j = compoundTag.getByte("Slot") & 255;
				if (j >= 2 && j < this.items.size()) {
					this.items.setStack(j, ItemStack.fromTag(compoundTag));
				}
			}
		}

		this.updateSaddle();
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (slot == 499) {
			if (this.hasChest() && item.isEmpty()) {
				this.setHasChest(false);
				this.method_6721();
				return true;
			}

			if (!this.hasChest() && item.getItem() == Blocks.CHEST.asItem()) {
				this.setHasChest(true);
				this.method_6721();
				return true;
			}
		}

		return super.equip(slot, item);
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(player, hand);
		} else {
			if (!this.isBaby()) {
				if (this.isTame() && player.shouldCancelInteraction()) {
					this.openInventory(player);
					return true;
				}

				if (this.hasPassengers()) {
					return super.interactMob(player, hand);
				}
			}

			if (!itemStack.isEmpty()) {
				boolean bl = this.receiveFood(player, itemStack);
				if (!bl) {
					if (!this.isTame() || itemStack.getItem() == Items.NAME_TAG) {
						if (itemStack.useOnEntity(player, this, hand)) {
							return true;
						} else {
							this.playAngrySound();
							return true;
						}
					}

					if (!this.hasChest() && itemStack.getItem() == Blocks.CHEST.asItem()) {
						this.setHasChest(true);
						this.playAddChestSound();
						bl = true;
						this.method_6721();
					}

					if (!this.isBaby() && !this.isSaddled() && itemStack.getItem() == Items.SADDLE) {
						this.openInventory(player);
						return true;
					}
				}

				if (bl) {
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return true;
				}
			}

			if (this.isBaby()) {
				return super.interactMob(player, hand);
			} else {
				this.putPlayerOnBack(player);
				return true;
			}
		}
	}

	protected void playAddChestSound() {
		this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	public int method_6702() {
		return 5;
	}
}
