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
import net.minecraft.text.StringTextComponent;
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
	private int field_7776;
	private String newItemName;
	private final PlayerEntity player;

	public AnvilContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public AnvilContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.ANVIL, i);
		this.context = blockContext;
		this.player = playerInventory.player;
		this.addProperty(this.levelCost);
		this.addSlot(new Slot(this.inventory, 0, 27, 47));
		this.addSlot(new Slot(this.inventory, 1, 76, 47));
		this.addSlot(
			new Slot(this.result, 2, 134, 47) {
				@Override
				public boolean canInsert(ItemStack itemStack) {
					return false;
				}

				@Override
				public boolean canTakeItems(PlayerEntity playerEntity) {
					return (playerEntity.abilities.creativeMode || playerEntity.experience >= AnvilContainer.this.levelCost.get())
						&& AnvilContainer.this.levelCost.get() > 0
						&& this.hasStack();
				}

				@Override
				public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
					if (!playerEntity.abilities.creativeMode) {
						playerEntity.method_7316(-AnvilContainer.this.levelCost.get());
					}

					AnvilContainer.this.inventory.setInvStack(0, ItemStack.EMPTY);
					if (AnvilContainer.this.field_7776 > 0) {
						ItemStack itemStack2 = AnvilContainer.this.inventory.getInvStack(1);
						if (!itemStack2.isEmpty() && itemStack2.getAmount() > AnvilContainer.this.field_7776) {
							itemStack2.subtractAmount(AnvilContainer.this.field_7776);
							AnvilContainer.this.inventory.setInvStack(1, itemStack2);
						} else {
							AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
						}
					} else {
						AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
					}

					AnvilContainer.this.levelCost.set(0);
					blockContext.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
						BlockState blockState = world.getBlockState(blockPos);
						if (!playerEntity.abilities.creativeMode && blockState.matches(BlockTags.field_15486) && playerEntity.getRand().nextFloat() < 0.12F) {
							BlockState blockState2 = AnvilBlock.getLandingState(blockState);
							if (blockState2 == null) {
								world.clearBlockState(blockPos);
								world.playEvent(1029, blockPos, 0);
							} else {
								world.setBlockState(blockPos, blockState2, 2);
								world.playEvent(1030, blockPos, 0);
							}
						} else {
							world.playEvent(1030, blockPos, 0);
						}
					}));
					return itemStack;
				}
			}
		);

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.inventory) {
			this.method_7628();
		}
	}

	public void method_7628() {
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
			this.field_7776 = 0;
			if (!itemStack3.isEmpty()) {
				boolean bl = itemStack3.getItem() == Items.field_8598 && !EnchantedBookItem.getEnchantmentTag(itemStack3).isEmpty();
				if (itemStack2.hasDurability() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					int l = Math.min(itemStack2.getDamage(), itemStack2.getDurability() / 4);
					if (l <= 0) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					int m;
					for (m = 0; l > 0 && m < itemStack3.getAmount(); m++) {
						int n = itemStack2.getDamage() - l;
						itemStack2.setDamage(n);
						i++;
						l = Math.min(itemStack2.getDamage(), itemStack2.getDurability() / 4);
					}

					this.field_7776 = m;
				} else {
					if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.hasDurability())) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					if (itemStack2.hasDurability() && !bl) {
						int lx = itemStack.getDurability() - itemStack.getDamage();
						int m = itemStack3.getDurability() - itemStack3.getDamage();
						int n = m + itemStack2.getDurability() * 12 / 100;
						int o = lx + n;
						int p = itemStack2.getDurability() - o;
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
							if (this.player.abilities.creativeMode || itemStack.getItem() == Items.field_8598) {
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
									case LEGENDARY:
										s = 8;
								}

								if (bl) {
									s = Math.max(1, s / 2);
								}

								i += s * r;
								if (itemStack.getAmount() > 1) {
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
				if (itemStack.hasDisplayName()) {
					k = 1;
					i += k;
					itemStack2.removeDisplayName();
				}
			} else if (!this.newItemName.equals(itemStack.getDisplayName().getString())) {
				k = 1;
				i += k;
				itemStack2.setDisplayName(new StringTextComponent(this.newItemName));
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
					t = t * 2 + 1;
				}

				itemStack2.setRepairCost(t);
				EnchantmentHelper.set(map, itemStack2);
			}

			this.result.setInvStack(0, itemStack2);
			this.sendContentUpdates();
		}
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(playerEntity, world, this.inventory)));
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.context
			.run(
				(world, blockPos) -> !world.getBlockState(blockPos).matches(BlockTags.field_15486)
						? false
						: playerEntity.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0,
				true
			);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 39 && !this.insertItem(itemStack2, 0, 2, false)) {
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

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}

	public void setNewItemName(String string) {
		this.newItemName = string;
		if (this.getSlot(2).hasStack()) {
			ItemStack itemStack = this.getSlot(2).getStack();
			if (StringUtils.isBlank(string)) {
				itemStack.removeDisplayName();
			} else {
				itemStack.setDisplayName(new StringTextComponent(this.newItemName));
			}
		}

		this.method_7628();
	}

	@Environment(EnvType.CLIENT)
	public int getLevelCost() {
		return this.levelCost.get();
	}
}
