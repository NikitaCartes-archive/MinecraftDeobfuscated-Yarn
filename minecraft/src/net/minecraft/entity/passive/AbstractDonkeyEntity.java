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
	private static final TrackedData<Boolean> field_6943 = DataTracker.registerData(AbstractDonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	protected AbstractDonkeyEntity(EntityType<? extends AbstractDonkeyEntity> entityType, World world) {
		super(entityType, world);
		this.field_6964 = false;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6943, false);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue((double)this.method_6754());
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.175F);
		this.method_5996(ATTR_JUMP_STRENGTH).setBaseValue(0.5);
	}

	public boolean hasChest() {
		return this.field_6011.get(field_6943);
	}

	public void setHasChest(boolean bl) {
		this.field_6011.set(field_6943, bl);
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
	protected SoundEvent method_6747() {
		super.method_6747();
		return SoundEvents.field_14661;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.hasChest()) {
			if (!this.field_6002.isClient) {
				this.method_5706(Blocks.field_10034);
			}

			this.setHasChest(false);
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putBoolean("ChestedHorse", this.hasChest());
		if (this.hasChest()) {
			ListTag listTag = new ListTag();

			for (int i = 2; i < this.decorationItem.getInvSize(); i++) {
				ItemStack itemStack = this.decorationItem.method_5438(i);
				if (!itemStack.isEmpty()) {
					CompoundTag compoundTag2 = new CompoundTag();
					compoundTag2.putByte("Slot", (byte)i);
					itemStack.method_7953(compoundTag2);
					listTag.add(compoundTag2);
				}
			}

			compoundTag.method_10566("Items", listTag);
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setHasChest(compoundTag.getBoolean("ChestedHorse"));
		if (this.hasChest()) {
			ListTag listTag = compoundTag.method_10554("Items", 10);
			this.method_6721();

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(i);
				int j = compoundTag2.getByte("Slot") & 255;
				if (j >= 2 && j < this.decorationItem.getInvSize()) {
					this.decorationItem.method_5447(j, ItemStack.method_7915(compoundTag2));
				}
			}
		}

		this.method_6731();
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (i == 499) {
			if (this.hasChest() && itemStack.isEmpty()) {
				this.setHasChest(false);
				this.method_6721();
				return true;
			}

			if (!this.hasChest() && itemStack.getItem() == Blocks.field_10034.getItem()) {
				this.setHasChest(true);
				this.method_6721();
				return true;
			}
		}

		return super.method_5758(i, itemStack);
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.method_5992(playerEntity, hand);
		} else {
			if (!this.isChild()) {
				if (this.isTame() && playerEntity.isSneaking()) {
					this.method_6722(playerEntity);
					return true;
				}

				if (this.hasPassengers()) {
					return super.method_5992(playerEntity, hand);
				}
			}

			if (!itemStack.isEmpty()) {
				boolean bl = this.method_6742(playerEntity, itemStack);
				if (!bl) {
					if (!this.isTame() || itemStack.getItem() == Items.field_8448) {
						if (itemStack.interactWithEntity(playerEntity, this, hand)) {
							return true;
						} else {
							this.method_6757();
							return true;
						}
					}

					if (!this.hasChest() && itemStack.getItem() == Blocks.field_10034.getItem()) {
						this.setHasChest(true);
						this.method_6705();
						bl = true;
						this.method_6721();
					}

					if (!this.isChild() && !this.isSaddled() && itemStack.getItem() == Items.field_8175) {
						this.method_6722(playerEntity);
						return true;
					}
				}

				if (bl) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					return true;
				}
			}

			if (this.isChild()) {
				return super.method_5992(playerEntity, hand);
			} else {
				this.method_6726(playerEntity);
				return true;
			}
		}
	}

	protected void method_6705() {
		this.method_5783(SoundEvents.field_14598, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	public int method_6702() {
		return 5;
	}
}
