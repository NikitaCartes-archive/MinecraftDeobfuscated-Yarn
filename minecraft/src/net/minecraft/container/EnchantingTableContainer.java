package net.minecraft.container;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3914;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.registry.Registry;

public class EnchantingTableContainer extends Container {
	private final Inventory inventory = new BasicInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			EnchantingTableContainer.this.onContentChanged(this);
		}
	};
	private final class_3914 pos;
	private final Random random = new Random();
	private final Property world = Property.create();
	public final int[] enchantmentPower = new int[3];
	public final int[] enchantmentId = new int[]{-1, -1, -1};
	public final int[] enchantmentLevel = new int[]{-1, -1, -1};

	public EnchantingTableContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, class_3914.field_17304);
	}

	public EnchantingTableContainer(int i, PlayerInventory playerInventory, class_3914 arg) {
		super(ContainerType.ENCHANTMENT, i);
		this.pos = arg;
		this.addSlot(new Slot(this.inventory, 0, 15, 47) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return true;
			}

			@Override
			public int getMaxStackAmount() {
				return 1;
			}
		});
		this.addSlot(new Slot(this.inventory, 1, 35, 47) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() == Items.field_8759;
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}

		this.method_17362(Property.create(this.enchantmentPower, 0));
		this.method_17362(Property.create(this.enchantmentPower, 1));
		this.method_17362(Property.create(this.enchantmentPower, 2));
		this.method_17362(this.world).set(playerInventory.player.getEnchantmentTableSeed());
		this.method_17362(Property.create(this.enchantmentId, 0));
		this.method_17362(Property.create(this.enchantmentId, 1));
		this.method_17362(Property.create(this.enchantmentId, 2));
		this.method_17362(Property.create(this.enchantmentLevel, 0));
		this.method_17362(Property.create(this.enchantmentLevel, 1));
		this.method_17362(Property.create(this.enchantmentLevel, 2));
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (inventory == this.inventory) {
			ItemStack itemStack = inventory.getInvStack(0);
			if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
				this.pos.method_17393((world, blockPos) -> {
					int ix = 0;

					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							if ((j != 0 || k != 0) && world.isAir(blockPos.add(k, 0, j)) && world.isAir(blockPos.add(k, 1, j))) {
								if (world.getBlockState(blockPos.add(k * 2, 0, j * 2)).getBlock() == Blocks.field_10504) {
									ix++;
								}

								if (world.getBlockState(blockPos.add(k * 2, 1, j * 2)).getBlock() == Blocks.field_10504) {
									ix++;
								}

								if (k != 0 && j != 0) {
									if (world.getBlockState(blockPos.add(k * 2, 0, j)).getBlock() == Blocks.field_10504) {
										ix++;
									}

									if (world.getBlockState(blockPos.add(k * 2, 1, j)).getBlock() == Blocks.field_10504) {
										ix++;
									}

									if (world.getBlockState(blockPos.add(k, 0, j * 2)).getBlock() == Blocks.field_10504) {
										ix++;
									}

									if (world.getBlockState(blockPos.add(k, 1, j * 2)).getBlock() == Blocks.field_10504) {
										ix++;
									}
								}
							}
						}
					}

					this.random.setSeed((long)this.world.get());

					for (int j = 0; j < 3; j++) {
						this.enchantmentPower[j] = EnchantmentHelper.calculateEnchantmentPower(this.random, j, ix, itemStack);
						this.enchantmentId[j] = -1;
						this.enchantmentLevel[j] = -1;
						if (this.enchantmentPower[j] < j + 1) {
							this.enchantmentPower[j] = 0;
						}
					}

					for (int jx = 0; jx < 3; jx++) {
						if (this.enchantmentPower[jx] > 0) {
							List<InfoEnchantment> list = this.getRandomEnchantments(itemStack, jx, this.enchantmentPower[jx]);
							if (list != null && !list.isEmpty()) {
								InfoEnchantment infoEnchantment = (InfoEnchantment)list.get(this.random.nextInt(list.size()));
								this.enchantmentId[jx] = Registry.ENCHANTMENT.getRawId(infoEnchantment.enchantment);
								this.enchantmentLevel[jx] = infoEnchantment.level;
							}
						}
					}

					this.sendContentUpdates();
				});
			} else {
				for (int i = 0; i < 3; i++) {
					this.enchantmentPower[i] = 0;
					this.enchantmentId[i] = -1;
					this.enchantmentLevel[i] = -1;
				}
			}
		}
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = this.inventory.getInvStack(0);
		ItemStack itemStack2 = this.inventory.getInvStack(1);
		int j = i + 1;
		if ((itemStack2.isEmpty() || itemStack2.getAmount() < j) && !playerEntity.abilities.creativeMode) {
			return false;
		} else if (this.enchantmentPower[i] <= 0
			|| itemStack.isEmpty()
			|| (playerEntity.experience < j || playerEntity.experience < this.enchantmentPower[i]) && !playerEntity.abilities.creativeMode) {
			return false;
		} else {
			this.pos.method_17393((world, blockPos) -> {
				ItemStack itemStack3 = itemStack;
				List<InfoEnchantment> list = this.getRandomEnchantments(itemStack, i, this.enchantmentPower[i]);
				if (!list.isEmpty()) {
					playerEntity.method_7286(itemStack, j);
					boolean bl = itemStack.getItem() == Items.field_8529;
					if (bl) {
						itemStack3 = new ItemStack(Items.field_8598);
						this.inventory.setInvStack(0, itemStack3);
					}

					for (int k = 0; k < list.size(); k++) {
						InfoEnchantment infoEnchantment = (InfoEnchantment)list.get(k);
						if (bl) {
							EnchantedBookItem.addEnchantment(itemStack3, infoEnchantment);
						} else {
							itemStack3.addEnchantment(infoEnchantment.enchantment, infoEnchantment.level);
						}
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack2.subtractAmount(j);
						if (itemStack2.isEmpty()) {
							this.inventory.setInvStack(1, ItemStack.EMPTY);
						}
					}

					playerEntity.increaseStat(Stats.field_15420);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.ENCHANTED_ITEM.handle((ServerPlayerEntity)playerEntity, itemStack3, j);
					}

					this.inventory.markDirty();
					this.world.set(playerEntity.getEnchantmentTableSeed());
					this.onContentChanged(this.inventory);
					world.playSound(null, blockPos, SoundEvents.field_15119, SoundCategory.field_15245, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
				}
			});
			return true;
		}
	}

	private List<InfoEnchantment> getRandomEnchantments(ItemStack itemStack, int i, int j) {
		this.random.setSeed((long)(this.world.get() + i));
		List<InfoEnchantment> list = EnchantmentHelper.getEnchantments(this.random, itemStack, j, false);
		if (itemStack.getItem() == Items.field_8529 && list.size() > 1) {
			list.remove(this.random.nextInt(list.size()));
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public int method_7638() {
		ItemStack itemStack = this.inventory.getInvStack(1);
		return itemStack.isEmpty() ? 0 : itemStack.getAmount();
	}

	@Environment(EnvType.CLIENT)
	public int method_17413() {
		return this.world.get();
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.pos.method_17393((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inventory));
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.pos, playerEntity, Blocks.field_10485);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 0) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (i == 1) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (itemStack2.getItem() == Items.field_8759) {
				if (!this.insertItem(itemStack2, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (((Slot)this.slotList.get(0)).hasStack() || !((Slot)this.slotList.get(0)).canInsert(itemStack2)) {
					return ItemStack.EMPTY;
				}

				if (itemStack2.hasTag() && itemStack2.getAmount() == 1) {
					((Slot)this.slotList.get(0)).setStack(itemStack2.copy());
					itemStack2.setAmount(0);
				} else if (!itemStack2.isEmpty()) {
					((Slot)this.slotList.get(0)).setStack(new ItemStack(itemStack2.getItem()));
					itemStack2.subtractAmount(1);
				}
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}
}
