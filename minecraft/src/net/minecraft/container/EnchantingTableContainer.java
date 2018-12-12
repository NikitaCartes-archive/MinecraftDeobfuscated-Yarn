package net.minecraft.container;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class EnchantingTableContainer extends Container {
	public Inventory inventory = new BasicInventory(new StringTextComponent("Enchant"), 2) {
		@Override
		public void markDirty() {
			super.markDirty();
			EnchantingTableContainer.this.onContentChanged(this);
		}
	};
	private final World world;
	private final BlockPos pos;
	private final Random random = new Random();
	public int enchantmentSeed;
	public int[] enchantmentPower = new int[3];
	public int[] enchantmentId = new int[]{-1, -1, -1};
	public int[] enchantmentLevel = new int[]{-1, -1, -1};

	@Environment(EnvType.CLIENT)
	public EnchantingTableContainer(PlayerInventory playerInventory, World world) {
		this(playerInventory, world, BlockPos.ORIGIN);
	}

	public EnchantingTableContainer(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		this.world = world;
		this.pos = blockPos;
		this.enchantmentSeed = playerInventory.player.getEnchantmentTableSeed();
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

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	protected void senDataToListener(ContainerListener containerListener) {
		containerListener.onContainerPropertyUpdate(this, 0, this.enchantmentPower[0]);
		containerListener.onContainerPropertyUpdate(this, 1, this.enchantmentPower[1]);
		containerListener.onContainerPropertyUpdate(this, 2, this.enchantmentPower[2]);
		containerListener.onContainerPropertyUpdate(this, 3, this.enchantmentSeed & -16);
		containerListener.onContainerPropertyUpdate(this, 4, this.enchantmentId[0]);
		containerListener.onContainerPropertyUpdate(this, 5, this.enchantmentId[1]);
		containerListener.onContainerPropertyUpdate(this, 6, this.enchantmentId[2]);
		containerListener.onContainerPropertyUpdate(this, 7, this.enchantmentLevel[0]);
		containerListener.onContainerPropertyUpdate(this, 8, this.enchantmentLevel[1]);
		containerListener.onContainerPropertyUpdate(this, 9, this.enchantmentLevel[2]);
	}

	@Override
	public void addListener(ContainerListener containerListener) {
		super.addListener(containerListener);
		this.senDataToListener(containerListener);
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		for (int i = 0; i < this.listeners.size(); i++) {
			ContainerListener containerListener = (ContainerListener)this.listeners.get(i);
			this.senDataToListener(containerListener);
		}
	}

	@Override
	public void setProperty(int i, int j) {
		if (i >= 0 && i <= 2) {
			this.enchantmentPower[i] = j;
		} else if (i == 3) {
			this.enchantmentSeed = j;
		} else if (i >= 4 && i <= 6) {
			this.enchantmentId[i - 4] = j;
		} else if (i >= 7 && i <= 9) {
			this.enchantmentLevel[i - 7] = j;
		} else {
			super.setProperty(i, j);
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (inventory == this.inventory) {
			ItemStack itemStack = inventory.getInvStack(0);
			if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
				if (!this.world.isClient) {
					int i = 0;

					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							if ((j != 0 || k != 0) && this.world.isAir(this.pos.add(k, 0, j)) && this.world.isAir(this.pos.add(k, 1, j))) {
								if (this.world.getBlockState(this.pos.add(k * 2, 0, j * 2)).getBlock() == Blocks.field_10504) {
									i++;
								}

								if (this.world.getBlockState(this.pos.add(k * 2, 1, j * 2)).getBlock() == Blocks.field_10504) {
									i++;
								}

								if (k != 0 && j != 0) {
									if (this.world.getBlockState(this.pos.add(k * 2, 0, j)).getBlock() == Blocks.field_10504) {
										i++;
									}

									if (this.world.getBlockState(this.pos.add(k * 2, 1, j)).getBlock() == Blocks.field_10504) {
										i++;
									}

									if (this.world.getBlockState(this.pos.add(k, 0, j * 2)).getBlock() == Blocks.field_10504) {
										i++;
									}

									if (this.world.getBlockState(this.pos.add(k, 1, j * 2)).getBlock() == Blocks.field_10504) {
										i++;
									}
								}
							}
						}
					}

					this.random.setSeed((long)this.enchantmentSeed);

					for (int j = 0; j < 3; j++) {
						this.enchantmentPower[j] = EnchantmentHelper.calculateEnchantmentPower(this.random, j, i, itemStack);
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
				}
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
		} else if (this.enchantmentPower[i] > 0
			&& !itemStack.isEmpty()
			&& (playerEntity.experience >= j && playerEntity.experience >= this.enchantmentPower[i] || playerEntity.abilities.creativeMode)) {
			if (!this.world.isClient) {
				List<InfoEnchantment> list = this.getRandomEnchantments(itemStack, i, this.enchantmentPower[i]);
				if (!list.isEmpty()) {
					playerEntity.method_7286(itemStack, j);
					boolean bl = itemStack.getItem() == Items.field_8529;
					if (bl) {
						itemStack = new ItemStack(Items.field_8598);
						this.inventory.setInvStack(0, itemStack);
					}

					for (int k = 0; k < list.size(); k++) {
						InfoEnchantment infoEnchantment = (InfoEnchantment)list.get(k);
						if (bl) {
							EnchantedBookItem.addEnchantment(itemStack, infoEnchantment);
						} else {
							itemStack.addEnchantment(infoEnchantment.enchantment, infoEnchantment.level);
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
						Criterions.ENCHANTED_ITEM.handle((ServerPlayerEntity)playerEntity, itemStack, j);
					}

					this.inventory.markDirty();
					this.enchantmentSeed = playerEntity.getEnchantmentTableSeed();
					this.onContentChanged(this.inventory);
					this.world.playSound(null, this.pos, SoundEvents.field_15119, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private List<InfoEnchantment> getRandomEnchantments(ItemStack itemStack, int i, int j) {
		this.random.setSeed((long)(this.enchantmentSeed + i));
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

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		if (!this.world.isClient) {
			this.method_7607(playerEntity, playerEntity.world, this.inventory);
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.world.getBlockState(this.pos).getBlock() != Blocks.field_10485
			? false
			: !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
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
