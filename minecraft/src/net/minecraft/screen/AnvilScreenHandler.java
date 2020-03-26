package net.minecraft.screen;

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

public class AnvilScreenHandler extends ForgingScreenHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private int repairItemUsage;
	private String newItemName;
	private final Property levelCost = Property.create();

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
		return (player.abilities.creativeMode || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
	}

	@Override
	protected ItemStack onTakeOutput(PlayerEntity player, ItemStack stack) {
		if (!player.abilities.creativeMode) {
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
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
			BlockState blockState = world.getBlockState(blockPos);
			if (!player.abilities.creativeMode && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
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
				boolean bl = itemStack3.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantmentTag(itemStack3).isEmpty();
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
					if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.isDamageable())) {
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
							int q = map.containsKey(enchantment) ? (Integer)map.get(enchantment) : 0;
							int r = (Integer)map2.get(enchantment);
							r = q == r ? r + 1 : Math.max(r, q);
							boolean bl4 = enchantment.isAcceptableItem(itemStack);
							if (this.player.abilities.creativeMode || itemStack.getItem() == Items.ENCHANTED_BOOK) {
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
								if (r > enchantment.getMaximumLevel()) {
									r = enchantment.getMaximumLevel();
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

			this.output.setStack(0, itemStack2);
			this.sendContentUpdates();
		}
	}

	public static int getNextCost(int cost) {
		return cost * 2 + 1;
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
