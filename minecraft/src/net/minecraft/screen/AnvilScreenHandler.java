package net.minecraft.screen;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldEvents;
import org.slf4j.Logger;

public class AnvilScreenHandler extends ForgingScreenHandler {
	public static final int INPUT_1_ID = 0;
	public static final int INPUT_2_ID = 1;
	public static final int OUTPUT_ID = 2;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final boolean field_30752 = false;
	public static final int MAX_NAME_LENGTH = 50;
	private int repairItemUsage;
	@Nullable
	private String newItemName;
	private final Property levelCost = Property.create();
	private static final int field_30753 = 0;
	private static final int field_30754 = 1;
	private static final int field_30755 = 1;
	private static final int field_30747 = 1;
	private static final int field_30748 = 2;
	private static final int field_30749 = 1;
	private static final int field_30750 = 1;
	private static final int INPUT_1_X = 27;
	private static final int INPUT_2_X = 76;
	private static final int OUTPUT_X = 134;
	private static final int SLOT_Y = 47;

	public AnvilScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	public AnvilScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.ANVIL, syncId, inventory, context);
		this.addProperty(this.levelCost);
	}

	@Override
	protected ForgingSlotsManager getForgingSlotsManager() {
		return ForgingSlotsManager.create().input(0, 27, 47, stack -> true).input(1, 76, 47, stack -> true).output(2, 134, 47).build();
	}

	@Override
	protected boolean canUse(BlockState state) {
		return state.isIn(BlockTags.ANVIL);
	}

	@Override
	protected boolean canTakeOutput(PlayerEntity player, boolean present) {
		return (player.isInCreativeMode() || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
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
			if (!player.isInCreativeMode() && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
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
		long l = 0L;
		int j = 0;
		if (!itemStack.isEmpty() && EnchantmentHelper.canHaveEnchantments(itemStack)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = this.input.getStack(1);
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(itemStack2));
			l += (long)itemStack.getOrDefault(DataComponentTypes.REPAIR_COST, Integer.valueOf(0)).intValue()
				+ (long)itemStack3.getOrDefault(DataComponentTypes.REPAIR_COST, Integer.valueOf(0)).intValue();
			this.repairItemUsage = 0;
			if (!itemStack3.isEmpty()) {
				boolean bl = itemStack3.contains(DataComponentTypes.STORED_ENCHANTMENTS);
				if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					int k = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					if (k <= 0) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					int m;
					for (m = 0; k > 0 && m < itemStack3.getCount(); m++) {
						int n = itemStack2.getDamage() - k;
						itemStack2.setDamage(n);
						i++;
						k = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					}

					this.repairItemUsage = m;
				} else {
					if (!bl && (!itemStack2.isOf(itemStack3.getItem()) || !itemStack2.isDamageable())) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					if (itemStack2.isDamageable() && !bl) {
						int kx = itemStack.getMaxDamage() - itemStack.getDamage();
						int m = itemStack3.getMaxDamage() - itemStack3.getDamage();
						int n = m + itemStack2.getMaxDamage() * 12 / 100;
						int o = kx + n;
						int p = itemStack2.getMaxDamage() - o;
						if (p < 0) {
							p = 0;
						}

						if (p < itemStack2.getDamage()) {
							itemStack2.setDamage(p);
							i += 2;
						}
					}

					ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStack3);
					boolean bl2 = false;
					boolean bl3 = false;

					for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
						RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
						int q = builder.getLevel(registryEntry);
						int r = entry.getIntValue();
						r = q == r ? r + 1 : Math.max(r, q);
						Enchantment enchantment = registryEntry.value();
						boolean bl4 = enchantment.isAcceptableItem(itemStack);
						if (this.player.getAbilities().creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
							bl4 = true;
						}

						for (RegistryEntry<Enchantment> registryEntry2 : builder.getEnchantments()) {
							if (!registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2)) {
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

							builder.set(registryEntry, r);
							int s = enchantment.getAnvilCost();
							if (bl) {
								s = Math.max(1, s / 2);
							}

							i += s * r;
							if (itemStack.getCount() > 1) {
								i = 40;
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

			if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
				if (!this.newItemName.equals(itemStack.getName().getString())) {
					j = 1;
					i += j;
					itemStack2.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
				}
			} else if (itemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
				j = 1;
				i += j;
				itemStack2.remove(DataComponentTypes.CUSTOM_NAME);
			}

			int t = (int)MathHelper.clamp(l + (long)i, 0L, 2147483647L);
			this.levelCost.set(t);
			if (i <= 0) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (j == i && j > 0 && this.levelCost.get() >= 40) {
				this.levelCost.set(39);
			}

			if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (!itemStack2.isEmpty()) {
				int kxx = itemStack2.getOrDefault(DataComponentTypes.REPAIR_COST, Integer.valueOf(0));
				if (kxx < itemStack3.getOrDefault(DataComponentTypes.REPAIR_COST, Integer.valueOf(0))) {
					kxx = itemStack3.getOrDefault(DataComponentTypes.REPAIR_COST, Integer.valueOf(0));
				}

				if (j != i || j == 0) {
					kxx = getNextCost(kxx);
				}

				itemStack2.set(DataComponentTypes.REPAIR_COST, kxx);
				EnchantmentHelper.set(itemStack2, builder.build());
			}

			this.output.setStack(0, itemStack2);
			this.sendContentUpdates();
		} else {
			this.output.setStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		}
	}

	public static int getNextCost(int cost) {
		return (int)Math.min((long)cost * 2L + 1L, 2147483647L);
	}

	public boolean setNewItemName(String newItemName) {
		String string = sanitize(newItemName);
		if (string != null && !string.equals(this.newItemName)) {
			this.newItemName = string;
			if (this.getSlot(2).hasStack()) {
				ItemStack itemStack = this.getSlot(2).getStack();
				if (StringHelper.isBlank(string)) {
					itemStack.remove(DataComponentTypes.CUSTOM_NAME);
				} else {
					itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(string));
				}
			}

			this.updateResult();
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	private static String sanitize(String name) {
		String string = StringHelper.stripInvalidChars(name);
		return string.length() <= 50 ? string : null;
	}

	public int getLevelCost() {
		return this.levelCost.get();
	}
}
