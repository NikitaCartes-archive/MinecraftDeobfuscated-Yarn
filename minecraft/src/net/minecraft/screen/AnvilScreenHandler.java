package net.minecraft.screen;

import java.util.Map;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.world.WorldEvents;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilScreenHandler extends ForgingScreenHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final boolean field_30752 = false;
	public static final int field_30751 = 50;
	private int repairItemUsage;
	private String newItemName;
	private final Property levelCost = Property.create();
	private static final int field_30753 = 0;
	private static final int field_30754 = 1;
	private static final int field_30755 = 1;
	private static final int field_30747 = 1;
	private static final int field_30748 = 2;
	private static final int field_30749 = 1;
	private static final int field_30750 = 1;

	public AnvilScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	public AnvilScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.ANVIL, syncId, inventory, context);
		this.addProperty(this.levelCost);
	}

	@Override
	protected boolean canUse(BlockState state) {
		return state.isIn(BlockTags.ANVIL);
	}

	@Override
	protected boolean canTakeOutput(PlayerEntity player, boolean present) {
		return (player.getAbilities().creativeMode || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
	}

	@Override
	protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
		if (!player.getAbilities().creativeMode) {
			player.addExperienceLevels(-this.levelCost.get());
		}

		this.input.setStack(0, ItemStack.EMPTY);
		if (this.repairItemUsage > 0) {
			ItemStack itemStack = this.input.getStack(1);
			if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
				itemStack.decrement(this.repairItemUsage);
				this.input.setStack(1, itemStack);
			} else {
				this.input.setStack(1, ItemStack.EMPTY);
			}
		} else {
			this.input.setStack(1, ItemStack.EMPTY);
		}

		this.levelCost.set(0);
		this.context.run((world, pos) -> {
			BlockState blockState = world.getBlockState(pos);
			if (!player.getAbilities().creativeMode && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
				BlockState blockState2 = AnvilBlock.getLandingState(blockState);
				if (blockState2 == null) {
					world.removeBlock(pos, false);
					world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
				} else {
					world.setBlockState(pos, blockState2, Block.NOTIFY_LISTENERS);
					world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
				}
			} else {
				world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
			}
		});
	}

	@Override
	public void updateResult() {
		ItemStack itemStack = this.input.getStack(0);
		this.levelCost.set(1);
		int i = 0;
		int j = 0;
		int k = 0;
		if (itemStack.isEmpty()) {
			this.output.setStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		} else {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = this.input.getStack(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack2);
			j += itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
			this.repairItemUsage = 0;
			if (!itemStack3.isEmpty()) {
				boolean bl = itemStack3.isOf(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantmentNbt(itemStack3).isEmpty();
				if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					int l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					if (l <= 0) {
						this.output.setStack(0, ItemStack.EMPTY);
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
					if (!bl && (!itemStack2.isOf(itemStack3.getItem()) || !itemStack2.isDamageable())) {
						this.output.setStack(0, ItemStack.EMPTY);
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

					Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
					boolean bl2 = false;
					boolean bl3 = false;

					for (Enchantment enchantment : map2.keySet()) {
						if (enchantment != null) {
							int q = (Integer)map.getOrDefault(enchantment, 0);
							int r = (Integer)map2.get(enchantment);
							r = q == r ? r + 1 : Math.max(r, q);
							boolean bl4 = enchantment.isAcceptableItem(itemStack);
							if (this.player.getAbilities().creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
								bl4 = true;
							}

							for (Enchantment enchantment2 : map.keySet()) {
								if (enchantment2 != enchantment && !enchantment.canCombine(enchantment2)) {
									bl4 = false;
									i++;
								}
							}

							if (!bl4) {
								bl3 = true;
							} else {
								bl2 = true;
								if (r > enchantment.getMaxLevel()) {
									r = enchantment.getMaxLevel();
								}

								map.put(enchantment, r);
								int s = 0;
								switch (enchantment.getRarity()) {
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
						this.output.setStack(0, ItemStack.EMPTY);
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

			if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
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

			this.output.setStack(0, itemStack2);
			this.sendContentUpdates();
		}
	}

	public static int getNextCost(int cost) {
		return cost * 2 + 1;
	}

	public void setNewItemName(String newItemName) {
		this.newItemName = newItemName;
		if (this.getSlot(2).hasStack()) {
			ItemStack itemStack = this.getSlot(2).getStack();
			if (StringUtils.isBlank(newItemName)) {
				itemStack.removeCustomName();
			} else {
				itemStack.setCustomName(new LiteralText(this.newItemName));
			}
		}

		this.updateResult();
	}

	public int getLevelCost() {
		return this.levelCost.get();
	}
}
