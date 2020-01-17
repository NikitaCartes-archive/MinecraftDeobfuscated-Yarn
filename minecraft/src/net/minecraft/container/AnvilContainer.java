package net.minecraft.container;

import java.util.Map;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilContainer extends Container {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Inventory result = new CraftingResultInventory();
	private final Inventory inventory = new BasicInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			AnvilContainer.this.onContentChanged(this);
		}
	};
	private final Property levelCost = Property.create();
	private final BlockContext context;
	private int repairItemUsage;
	private String newItemName;
	private final PlayerEntity player;

	public AnvilContainer(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, BlockContext.EMPTY);
	}

	public AnvilContainer(int syncId, PlayerInventory inventory, BlockContext blockContext) {
		super(ContainerType.ANVIL, syncId);
		this.context = blockContext;
		this.player = inventory.player;
		this.addProperty(this.levelCost);
		this.addSlot(new Slot(this.inventory, 0, 27, 47));
		this.addSlot(new Slot(this.inventory, 1, 76, 47));
		this.addSlot(
			new Slot(this.result, 2, 134, 47) {
				@Override
				public boolean canInsert(ItemStack stack) {
					return false;
				}

				@Override
				public boolean canTakeItems(PlayerEntity playerEntity) {
					return (playerEntity.abilities.creativeMode || playerEntity.experienceLevel >= AnvilContainer.this.levelCost.get())
						&& AnvilContainer.this.levelCost.get() > 0
						&& this.hasStack();
				}

				@Override
				public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
					if (!player.abilities.creativeMode) {
						player.addExperienceLevels(-AnvilContainer.this.levelCost.get());
					}

					AnvilContainer.this.inventory.setInvStack(0, ItemStack.EMPTY);
					if (AnvilContainer.this.repairItemUsage > 0) {
						ItemStack itemStack = AnvilContainer.this.inventory.getInvStack(1);
						if (!itemStack.isEmpty() && itemStack.getCount() > AnvilContainer.this.repairItemUsage) {
							itemStack.decrement(AnvilContainer.this.repairItemUsage);
							AnvilContainer.this.inventory.setInvStack(1, itemStack);
						} else {
							AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
						}
					} else {
						AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
					}

					AnvilContainer.this.levelCost.set(0);
					blockContext.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
						BlockState blockState = world.getBlockState(blockPos);
						if (!player.abilities.creativeMode && blockState.matches(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
							BlockState blockState2 = AnvilBlock.getLandingState(blockState);
							if (blockState2 == null) {
								world.removeBlock(blockPos, false);
								world.playLevelEvent(1029, blockPos, 0);
							} else {
								world.setBlockState(blockPos, blockState2, 2);
								world.playLevelEvent(1030, blockPos, 0);
							}
						} else {
							world.playLevelEvent(1030, blockPos, 0);
						}
					}));
					return stack;
				}
			}
		);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.inventory) {
			this.updateResult();
		}
	}

	public void updateResult() {
		ItemStack itemStack = this.inventory.getInvStack(0);
		this.levelCost.set(1);
		int i = 0;
		int j = 0;
		int k = 0;
		if (itemStack.isEmpty()) {
			this.result.setInvStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		} else {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = this.inventory.getInvStack(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack2);
			j += itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
			this.repairItemUsage = 0;
			if (!itemStack3.isEmpty()) {
				boolean bl = itemStack3.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantmentTag(itemStack3).isEmpty();
				if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					int l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					if (l <= 0) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					int m;
					for (m = 0; l > 0 && m < itemStack3.getCount(); m++) {
						int n = itemStack2.getDamage() - l;
						itemStack2.setDamage(n);
						i++;
						l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					}

					this.repairItemUsage = m;
				} else {
					if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.isDamageable())) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					if (itemStack2.isDamageable() && !bl) {
						int lx = itemStack.getMaxDamage() - itemStack.getDamage();
						int m = itemStack3.getMaxDamage() - itemStack3.getDamage();
						int n = m + itemStack2.getMaxDamage() * 12 / 100;
						int o = lx + n;
						int p = itemStack2.getMaxDamage() - o;
						if (p < 0) {
							p = 0;
						}

						if (p < itemStack2.getDamage()) {
							itemStack2.setDamage(p);
							i += 2;
						}
					}

					Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(itemStack3);
					boolean bl2 = false;
					boolean bl3 = false;

					for (Enchantment enchantment : map2.keySet()) {
						if (enchantment != null) {
							int q = map.containsKey(enchantment) ? (Integer)map.get(enchantment) : 0;
							int r = (Integer)map2.get(enchantment);
							r = q == r ? r + 1 : Math.max(r, q);
							boolean bl4 = enchantment.isAcceptableItem(itemStack);
							if (this.player.abilities.creativeMode || itemStack.getItem() == Items.ENCHANTED_BOOK) {
								bl4 = true;
							}

							for (Enchantment enchantment2 : map.keySet()) {
								if (enchantment2 != enchantment && !enchantment.isDifferent(enchantment2)) {
									bl4 = false;
									i++;
								}
							}

							if (!bl4) {
								bl3 = true;
							} else {
								bl2 = true;
								if (r > enchantment.getMaximumLevel()) {
									r = enchantment.getMaximumLevel();
								}

								map.put(enchantment, r);
								int s = 0;
								switch (enchantment.getWeight()) {
									case COMMON:
										s = 1;
										break;
									case UNCOMMON:
										s = 2;
										break;
									case RARE:
										s = 4;
										break;
									case VERY_RARE:
										s = 8;
								}

								if (bl) {
									s = Math.max(1, s / 2);
								}

								i += s * r;
								if (itemStack.getCount() > 1) {
									i = 40;
								}
							}
						}
					}

					if (bl3 && !bl2) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}
				}
			}

			if (StringUtils.isBlank(this.newItemName)) {
				if (itemStack.hasCustomName()) {
					k = 1;
					i += k;
					itemStack2.removeCustomName();
				}
			} else if (!this.newItemName.equals(itemStack.getName().getString())) {
				k = 1;
				i += k;
				itemStack2.setCustomName(new LiteralText(this.newItemName));
			}

			this.levelCost.set(j + i);
			if (i <= 0) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (k == i && k > 0 && this.levelCost.get() >= 40) {
				this.levelCost.set(39);
			}

			if (this.levelCost.get() >= 40 && !this.player.abilities.creativeMode) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (!itemStack2.isEmpty()) {
				int t = itemStack2.getRepairCost();
				if (!itemStack3.isEmpty() && t < itemStack3.getRepairCost()) {
					t = itemStack3.getRepairCost();
				}

				if (k != i || k == 0) {
					t = getNextCost(t);
				}

				itemStack2.setRepairCost(t);
				EnchantmentHelper.set(map, itemStack2);
			}

			this.result.setInvStack(0, itemStack2);
			this.sendContentUpdates();
		}
	}

	public static int getNextCost(int cost) {
		return cost * 2 + 1;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(player, world, this.inventory)));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.context
			.run(
				(world, blockPos) -> !world.getBlockState(blockPos).matches(BlockTags.ANVIL)
						? false
						: player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0,
				true
			);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot != 0 && invSlot != 1) {
				if (invSlot >= 3 && invSlot < 39 && !this.insertItem(itemStack2, 0, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	public void setNewItemName(String string) {
		this.newItemName = string;
		if (this.getSlot(2).hasStack()) {
			ItemStack itemStack = this.getSlot(2).getStack();
			if (StringUtils.isBlank(string)) {
				itemStack.removeCustomName();
			} else {
				itemStack.setCustomName(new LiteralText(this.newItemName));
			}
		}

		this.updateResult();
	}

	@Environment(EnvType.CLIENT)
	public int getLevelCost() {
		return this.levelCost.get();
	}
}
