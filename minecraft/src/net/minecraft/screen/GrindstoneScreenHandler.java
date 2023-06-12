package net.minecraft.screen;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class GrindstoneScreenHandler extends ScreenHandler {
	public static final int field_30793 = 35;
	public static final int INPUT_1_ID = 0;
	public static final int INPUT_2_ID = 1;
	public static final int OUTPUT_ID = 2;
	private static final int INVENTORY_START = 3;
	private static final int INVENTORY_END = 30;
	private static final int HOTBAR_START = 30;
	private static final int HOTBAR_END = 39;
	private final Inventory result = new CraftingResultInventory();
	final Inventory input = new SimpleInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			GrindstoneScreenHandler.this.onContentChanged(this);
		}
	};
	private final ScreenHandlerContext context;

	public GrindstoneScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public GrindstoneScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.GRINDSTONE, syncId);
		this.context = context;
		this.addSlot(new Slot(this.input, 0, 49, 19) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isDamageable() || stack.isOf(Items.ENCHANTED_BOOK) || stack.hasEnchantments();
			}
		});
		this.addSlot(new Slot(this.input, 1, 49, 40) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isDamageable() || stack.isOf(Items.ENCHANTED_BOOK) || stack.hasEnchantments();
			}
		});
		this.addSlot(new Slot(this.result, 2, 129, 34) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				context.run((world, pos) -> {
					if (world instanceof ServerWorld) {
						ExperienceOrbEntity.spawn((ServerWorld)world, Vec3d.ofCenter(pos), this.getExperience(world));
					}

					world.syncWorldEvent(WorldEvents.GRINDSTONE_USED, pos, 0);
				});
				GrindstoneScreenHandler.this.input.setStack(0, ItemStack.EMPTY);
				GrindstoneScreenHandler.this.input.setStack(1, ItemStack.EMPTY);
			}

			private int getExperience(World world) {
				int i = 0;
				i += this.getExperience(GrindstoneScreenHandler.this.input.getStack(0));
				i += this.getExperience(GrindstoneScreenHandler.this.input.getStack(1));
				if (i > 0) {
					int j = (int)Math.ceil((double)i / 2.0);
					return j + world.random.nextInt(j);
				} else {
					return 0;
				}
			}

			private int getExperience(ItemStack stack) {
				int i = 0;
				Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);

				for (Entry<Enchantment, Integer> entry : map.entrySet()) {
					Enchantment enchantment = (Enchantment)entry.getKey();
					Integer integer = (Integer)entry.getValue();
					if (!enchantment.isCursed()) {
						i += enchantment.getMinPower(integer);
					}
				}

				return i;
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

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.input) {
			this.updateResult();
		}
	}

	private void updateResult() {
		ItemStack itemStack = this.input.getStack(0);
		ItemStack itemStack2 = this.input.getStack(1);
		boolean bl = !itemStack.isEmpty() || !itemStack2.isEmpty();
		boolean bl2 = !itemStack.isEmpty() && !itemStack2.isEmpty();
		if (!bl) {
			this.result.setStack(0, ItemStack.EMPTY);
		} else {
			boolean bl3 = !itemStack.isEmpty() && !itemStack.isOf(Items.ENCHANTED_BOOK) && !itemStack.hasEnchantments()
				|| !itemStack2.isEmpty() && !itemStack2.isOf(Items.ENCHANTED_BOOK) && !itemStack2.hasEnchantments();
			if (itemStack.getCount() > 1 || itemStack2.getCount() > 1 || !bl2 && bl3) {
				this.result.setStack(0, ItemStack.EMPTY);
				this.sendContentUpdates();
				return;
			}

			int i = 1;
			int m;
			ItemStack itemStack3;
			if (bl2) {
				if (!itemStack.isOf(itemStack2.getItem())) {
					this.result.setStack(0, ItemStack.EMPTY);
					this.sendContentUpdates();
					return;
				}

				Item item = itemStack.getItem();
				int j = item.getMaxDamage() - itemStack.getDamage();
				int k = item.getMaxDamage() - itemStack2.getDamage();
				int l = j + k + item.getMaxDamage() * 5 / 100;
				m = Math.max(item.getMaxDamage() - l, 0);
				itemStack3 = this.transferEnchantments(itemStack, itemStack2);
				if (!itemStack3.isDamageable()) {
					if (!ItemStack.areEqual(itemStack, itemStack2)) {
						this.result.setStack(0, ItemStack.EMPTY);
						this.sendContentUpdates();
						return;
					}

					i = 2;
				}
			} else {
				boolean bl4 = !itemStack.isEmpty();
				m = bl4 ? itemStack.getDamage() : itemStack2.getDamage();
				itemStack3 = bl4 ? itemStack : itemStack2;
			}

			this.result.setStack(0, this.grind(itemStack3, m, i));
		}

		this.sendContentUpdates();
	}

	private ItemStack transferEnchantments(ItemStack target, ItemStack source) {
		ItemStack itemStack = target.copy();
		Map<Enchantment, Integer> map = EnchantmentHelper.get(source);

		for (Entry<Enchantment, Integer> entry : map.entrySet()) {
			Enchantment enchantment = (Enchantment)entry.getKey();
			if (!enchantment.isCursed() || EnchantmentHelper.getLevel(enchantment, itemStack) == 0) {
				itemStack.addEnchantment(enchantment, (Integer)entry.getValue());
			}
		}

		return itemStack;
	}

	private ItemStack grind(ItemStack item, int damage, int amount) {
		ItemStack itemStack = item.copyWithCount(amount);
		itemStack.removeSubNbt("Enchantments");
		itemStack.removeSubNbt("StoredEnchantments");
		if (damage > 0) {
			itemStack.setDamage(damage);
		} else {
			itemStack.removeSubNbt("Damage");
		}

		Map<Enchantment, Integer> map = (Map<Enchantment, Integer>)EnchantmentHelper.get(item)
			.entrySet()
			.stream()
			.filter(entry -> ((Enchantment)entry.getKey()).isCursed())
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		EnchantmentHelper.set(map, itemStack);
		itemStack.setRepairCost(0);
		if (itemStack.isOf(Items.ENCHANTED_BOOK) && map.size() == 0) {
			itemStack = new ItemStack(Items.BOOK);
			if (item.hasCustomName()) {
				itemStack.setCustomName(item.getName());
			}
		}

		for (int i = 0; i < map.size(); i++) {
			itemStack.setRepairCost(AnvilScreenHandler.getNextCost(itemStack.getRepairCost()));
		}

		return itemStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.input));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.GRINDSTONE);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			ItemStack itemStack3 = this.input.getStack(0);
			ItemStack itemStack4 = this.input.getStack(1);
			if (slot == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot != 0 && slot != 1) {
				if (!itemStack3.isEmpty() && !itemStack4.isEmpty()) {
					if (slot >= 3 && slot < 30) {
						if (!this.insertItem(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 0, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}
}
