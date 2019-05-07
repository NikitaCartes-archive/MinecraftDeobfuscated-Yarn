package net.minecraft.container;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrindstoneContainer extends Container {
	private final Inventory resultInventory = new CraftingResultInventory();
	private final Inventory craftingInventory = new BasicInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			GrindstoneContainer.this.onContentChanged(this);
		}
	};
	private final BlockContext context;

	public GrindstoneContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public GrindstoneContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.field_17336, i);
		this.context = blockContext;
		this.addSlot(new Slot(this.craftingInventory, 0, 49, 19) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.hasDurability() || itemStack.getItem() == Items.field_8598 || itemStack.hasEnchantments();
			}
		});
		this.addSlot(new Slot(this.craftingInventory, 1, 49, 40) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.hasDurability() || itemStack.getItem() == Items.field_8598 || itemStack.hasEnchantments();
			}
		});
		this.addSlot(new Slot(this.resultInventory, 2, 129, 34) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
				blockContext.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
					int i = this.getExperience(world);

					while (i > 0) {
						int j = ExperienceOrbEntity.roundToOrbSize(i);
						i -= j;
						world.spawnEntity(new ExperienceOrbEntity(world, (double)blockPos.getX(), (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, j));
					}

					world.playLevelEvent(1042, blockPos, 0);
				}));
				GrindstoneContainer.this.craftingInventory.setInvStack(0, ItemStack.EMPTY);
				GrindstoneContainer.this.craftingInventory.setInvStack(1, ItemStack.EMPTY);
				return itemStack;
			}

			private int getExperience(World world) {
				int i = 0;
				i += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(0));
				i += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(1));
				if (i > 0) {
					int j = (int)Math.ceil((double)i / 2.0);
					return j + world.random.nextInt(j);
				} else {
					return 0;
				}
			}

			private int getExperience(ItemStack itemStack) {
				int i = 0;
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);

				for (Entry<Enchantment, Integer> entry : map.entrySet()) {
					Enchantment enchantment = (Enchantment)entry.getKey();
					Integer integer = (Integer)entry.getValue();
					if (!enchantment.isCursed()) {
						i += enchantment.getMinimumPower(integer);
					}
				}

				return i;
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
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.craftingInventory) {
			this.updateResult();
		}
	}

	private void updateResult() {
		ItemStack itemStack = this.craftingInventory.getInvStack(0);
		ItemStack itemStack2 = this.craftingInventory.getInvStack(1);
		boolean bl = !itemStack.isEmpty() || !itemStack2.isEmpty();
		boolean bl2 = !itemStack.isEmpty() && !itemStack2.isEmpty();
		if (!bl) {
			this.resultInventory.setInvStack(0, ItemStack.EMPTY);
		} else {
			boolean bl3 = !itemStack.isEmpty() && itemStack.getItem() != Items.field_8598 && !itemStack.hasEnchantments()
				|| !itemStack2.isEmpty() && itemStack2.getItem() != Items.field_8598 && !itemStack2.hasEnchantments();
			if (itemStack.getAmount() > 1 || itemStack2.getAmount() > 1 || !bl2 && bl3) {
				this.resultInventory.setInvStack(0, ItemStack.EMPTY);
				this.sendContentUpdates();
				return;
			}

			int i = 1;
			int m;
			ItemStack itemStack3;
			if (bl2) {
				if (itemStack.getItem() != itemStack2.getItem()) {
					this.resultInventory.setInvStack(0, ItemStack.EMPTY);
					this.sendContentUpdates();
					return;
				}

				Item item = itemStack.getItem();
				int j = item.getDurability() - itemStack.getDamage();
				int k = item.getDurability() - itemStack2.getDamage();
				int l = j + k + item.getDurability() * 5 / 100;
				m = Math.max(item.getDurability() - l, 0);
				itemStack3 = this.transferEnchantments(itemStack, itemStack2);
				if (!itemStack3.hasDurability()) {
					if (!ItemStack.areEqual(itemStack, itemStack2)) {
						this.resultInventory.setInvStack(0, ItemStack.EMPTY);
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

			this.resultInventory.setInvStack(0, this.grind(itemStack3, m, i));
		}

		this.sendContentUpdates();
	}

	private ItemStack transferEnchantments(ItemStack itemStack, ItemStack itemStack2) {
		ItemStack itemStack3 = itemStack.copy();
		Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack2);

		for (Entry<Enchantment, Integer> entry : map.entrySet()) {
			Enchantment enchantment = (Enchantment)entry.getKey();
			if (!enchantment.isCursed() || EnchantmentHelper.getLevel(enchantment, itemStack3) == 0) {
				itemStack3.addEnchantment(enchantment, (Integer)entry.getValue());
			}
		}

		return itemStack3;
	}

	private ItemStack grind(ItemStack itemStack, int i, int j) {
		ItemStack itemStack2 = itemStack.copy();
		itemStack2.removeSubTag("Enchantments");
		itemStack2.removeSubTag("StoredEnchantments");
		if (i > 0) {
			itemStack2.setDamage(i);
		} else {
			itemStack2.removeSubTag("Damage");
		}

		itemStack2.setAmount(j);
		Map<Enchantment, Integer> map = (Map<Enchantment, Integer>)EnchantmentHelper.getEnchantments(itemStack)
			.entrySet()
			.stream()
			.filter(entry -> ((Enchantment)entry.getKey()).isCursed())
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		EnchantmentHelper.set(map, itemStack2);
		itemStack2.setRepairCost(0);
		if (itemStack2.getItem() == Items.field_8598 && map.size() == 0) {
			itemStack2 = new ItemStack(Items.field_8529);
			itemStack2.setDisplayName(itemStack.getDisplayName());
		}

		for (int k = 0; k < map.size(); k++) {
			itemStack2.setRepairCost(AnvilContainer.getNextCost(itemStack2.getRepairCost()));
		}

		return itemStack2;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(playerEntity, world, this.craftingInventory)));
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.context, playerEntity, Blocks.field_16337);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			ItemStack itemStack3 = this.craftingInventory.getInvStack(0);
			ItemStack itemStack4 = this.craftingInventory.getInvStack(1);
			if (i == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i != 0 && i != 1) {
				if (!itemStack3.isEmpty() && !itemStack4.isEmpty()) {
					if (i >= 3 && i < 30) {
						if (!this.insertItem(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (i >= 30 && i < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 0, 2, false)) {
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
}
