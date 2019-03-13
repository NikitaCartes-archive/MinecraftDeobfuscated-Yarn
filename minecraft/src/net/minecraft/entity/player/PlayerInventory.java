package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

public class PlayerInventory implements Inventory, Nameable {
	public final DefaultedList<ItemStack> field_7547 = DefaultedList.create(36, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> field_7548 = DefaultedList.create(4, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> field_7544 = DefaultedList.create(1, ItemStack.EMPTY);
	private final List<DefaultedList<ItemStack>> field_7543 = ImmutableList.of(this.field_7547, this.field_7548, this.field_7544);
	public int selectedSlot;
	public final PlayerEntity field_7546;
	private ItemStack field_7549 = ItemStack.EMPTY;
	private int changeCount;

	public PlayerInventory(PlayerEntity playerEntity) {
		this.field_7546 = playerEntity;
	}

	public ItemStack method_7391() {
		return isValidHotbarIndex(this.selectedSlot) ? this.field_7547.get(this.selectedSlot) : ItemStack.EMPTY;
	}

	public static int getHotbarSize() {
		return 9;
	}

	private boolean method_7393(ItemStack itemStack, ItemStack itemStack2) {
		return !itemStack.isEmpty()
			&& this.method_7392(itemStack, itemStack2)
			&& itemStack.canStack()
			&& itemStack.getAmount() < itemStack.getMaxAmount()
			&& itemStack.getAmount() < this.getInvMaxStackAmount();
	}

	private boolean method_7392(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
	}

	public int getEmptySlot() {
		for (int i = 0; i < this.field_7547.size(); i++) {
			if (this.field_7547.get(i).isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	@Environment(EnvType.CLIENT)
	public void method_7374(ItemStack itemStack) {
		int i = this.method_7395(itemStack);
		if (isValidHotbarIndex(i)) {
			this.selectedSlot = i;
		} else {
			if (i == -1) {
				this.selectedSlot = this.getSwappableHotbarSlot();
				if (!this.field_7547.get(this.selectedSlot).isEmpty()) {
					int j = this.getEmptySlot();
					if (j != -1) {
						this.field_7547.set(j, this.field_7547.get(this.selectedSlot));
					}
				}

				this.field_7547.set(this.selectedSlot, itemStack);
			} else {
				this.swapSlotWithHotbar(i);
			}
		}
	}

	public void swapSlotWithHotbar(int i) {
		this.selectedSlot = this.getSwappableHotbarSlot();
		ItemStack itemStack = this.field_7547.get(this.selectedSlot);
		this.field_7547.set(this.selectedSlot, this.field_7547.get(i));
		this.field_7547.set(i, itemStack);
	}

	public static boolean isValidHotbarIndex(int i) {
		return i >= 0 && i < 9;
	}

	@Environment(EnvType.CLIENT)
	public int method_7395(ItemStack itemStack) {
		for (int i = 0; i < this.field_7547.size(); i++) {
			if (!this.field_7547.get(i).isEmpty() && this.method_7392(itemStack, this.field_7547.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public int method_7371(ItemStack itemStack) {
		for (int i = 0; i < this.field_7547.size(); i++) {
			ItemStack itemStack2 = this.field_7547.get(i);
			if (!this.field_7547.get(i).isEmpty()
				&& this.method_7392(itemStack, this.field_7547.get(i))
				&& !this.field_7547.get(i).isDamaged()
				&& !itemStack2.hasEnchantments()
				&& !itemStack2.hasDisplayName()) {
				return i;
			}
		}

		return -1;
	}

	public int getSwappableHotbarSlot() {
		for (int i = 0; i < 9; i++) {
			int j = (this.selectedSlot + i) % 9;
			if (this.field_7547.get(j).isEmpty()) {
				return j;
			}
		}

		for (int ix = 0; ix < 9; ix++) {
			int j = (this.selectedSlot + ix) % 9;
			if (!this.field_7547.get(j).hasEnchantments()) {
				return j;
			}
		}

		return this.selectedSlot;
	}

	@Environment(EnvType.CLIENT)
	public void method_7373(double d) {
		if (d > 0.0) {
			d = 1.0;
		}

		if (d < 0.0) {
			d = -1.0;
		}

		this.selectedSlot = (int)((double)this.selectedSlot - d);

		while (this.selectedSlot < 0) {
			this.selectedSlot += 9;
		}

		while (this.selectedSlot >= 9) {
			this.selectedSlot -= 9;
		}
	}

	public int method_7369(Predicate<ItemStack> predicate, int i) {
		int j = 0;

		for (int k = 0; k < this.getInvSize(); k++) {
			ItemStack itemStack = this.method_5438(k);
			if (!itemStack.isEmpty() && predicate.test(itemStack)) {
				int l = i <= 0 ? itemStack.getAmount() : Math.min(i - j, itemStack.getAmount());
				j += l;
				if (i != 0) {
					itemStack.subtractAmount(l);
					if (itemStack.isEmpty()) {
						this.method_5447(k, ItemStack.EMPTY);
					}

					if (i > 0 && j >= i) {
						return j;
					}
				}
			}
		}

		if (!this.field_7549.isEmpty() && predicate.test(this.field_7549)) {
			int kx = i <= 0 ? this.field_7549.getAmount() : Math.min(i - j, this.field_7549.getAmount());
			j += kx;
			if (i != 0) {
				this.field_7549.subtractAmount(kx);
				if (this.field_7549.isEmpty()) {
					this.field_7549 = ItemStack.EMPTY;
				}

				if (i > 0 && j >= i) {
					return j;
				}
			}
		}

		return j;
	}

	private int method_7366(ItemStack itemStack) {
		int i = this.method_7390(itemStack);
		if (i == -1) {
			i = this.getEmptySlot();
		}

		return i == -1 ? itemStack.getAmount() : this.method_7385(i, itemStack);
	}

	private int method_7385(int i, ItemStack itemStack) {
		Item item = itemStack.getItem();
		int j = itemStack.getAmount();
		ItemStack itemStack2 = this.method_5438(i);
		if (itemStack2.isEmpty()) {
			itemStack2 = new ItemStack(item, 0);
			if (itemStack.hasTag()) {
				itemStack2.method_7980(itemStack.method_7969().method_10553());
			}

			this.method_5447(i, itemStack2);
		}

		int k = j;
		if (j > itemStack2.getMaxAmount() - itemStack2.getAmount()) {
			k = itemStack2.getMaxAmount() - itemStack2.getAmount();
		}

		if (k > this.getInvMaxStackAmount() - itemStack2.getAmount()) {
			k = this.getInvMaxStackAmount() - itemStack2.getAmount();
		}

		if (k == 0) {
			return j;
		} else {
			j -= k;
			itemStack2.addAmount(k);
			itemStack2.setUpdateCooldown(5);
			return j;
		}
	}

	public int method_7390(ItemStack itemStack) {
		if (this.method_7393(this.method_5438(this.selectedSlot), itemStack)) {
			return this.selectedSlot;
		} else if (this.method_7393(this.method_5438(40), itemStack)) {
			return 40;
		} else {
			for (int i = 0; i < this.field_7547.size(); i++) {
				if (this.method_7393(this.field_7547.get(i), itemStack)) {
					return i;
				}
			}

			return -1;
		}
	}

	public void updateItems() {
		for (DefaultedList<ItemStack> defaultedList : this.field_7543) {
			for (int i = 0; i < defaultedList.size(); i++) {
				if (!defaultedList.get(i).isEmpty()) {
					defaultedList.get(i).method_7917(this.field_7546.field_6002, this.field_7546, i, this.selectedSlot == i);
				}
			}
		}
	}

	public boolean method_7394(ItemStack itemStack) {
		return this.method_7367(-1, itemStack);
	}

	public boolean method_7367(int i, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		} else {
			try {
				if (itemStack.isDamaged()) {
					if (i == -1) {
						i = this.getEmptySlot();
					}

					if (i >= 0) {
						this.field_7547.set(i, itemStack.copy());
						this.field_7547.get(i).setUpdateCooldown(5);
						itemStack.setAmount(0);
						return true;
					} else if (this.field_7546.abilities.creativeMode) {
						itemStack.setAmount(0);
						return true;
					} else {
						return false;
					}
				} else {
					int j;
					do {
						j = itemStack.getAmount();
						if (i == -1) {
							itemStack.setAmount(this.method_7366(itemStack));
						} else {
							itemStack.setAmount(this.method_7385(i, itemStack));
						}
					} while (!itemStack.isEmpty() && itemStack.getAmount() < j);

					if (itemStack.getAmount() == j && this.field_7546.abilities.creativeMode) {
						itemStack.setAmount(0);
						return true;
					} else {
						return itemStack.getAmount() < j;
					}
				}
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
				CrashReportSection crashReportSection = crashReport.method_562("Item being added");
				crashReportSection.add("Item ID", Item.getRawIdByItem(itemStack.getItem()));
				crashReportSection.add("Item data", itemStack.getDamage());
				crashReportSection.method_577("Item name", () -> itemStack.method_7964().getString());
				throw new CrashException(crashReport);
			}
		}
	}

	public void method_7398(World world, ItemStack itemStack) {
		if (!world.isClient) {
			while (!itemStack.isEmpty()) {
				int i = this.method_7390(itemStack);
				if (i == -1) {
					i = this.getEmptySlot();
				}

				if (i == -1) {
					this.field_7546.method_7328(itemStack, false);
					break;
				}

				int j = itemStack.getMaxAmount() - this.method_5438(i).getAmount();
				if (this.method_7367(i, itemStack.split(j))) {
					((ServerPlayerEntity)this.field_7546).field_13987.sendPacket(new GuiSlotUpdateS2CPacket(-2, i, this.method_5438(i)));
				}
			}
		}
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.field_7543) {
			if (i < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			i -= defaultedList.size();
		}

		return list != null && !((ItemStack)list.get(i)).isEmpty() ? Inventories.method_5430(list, i, j) : ItemStack.EMPTY;
	}

	public void method_7378(ItemStack itemStack) {
		for (DefaultedList<ItemStack> defaultedList : this.field_7543) {
			for (int i = 0; i < defaultedList.size(); i++) {
				if (defaultedList.get(i) == itemStack) {
					defaultedList.set(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

	@Override
	public ItemStack method_5441(int i) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.field_7543) {
			if (i < defaultedList2.size()) {
				defaultedList = defaultedList2;
				break;
			}

			i -= defaultedList2.size();
		}

		if (defaultedList != null && !defaultedList.get(i).isEmpty()) {
			ItemStack itemStack = defaultedList.get(i);
			defaultedList.set(i, ItemStack.EMPTY);
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.field_7543) {
			if (i < defaultedList2.size()) {
				defaultedList = defaultedList2;
				break;
			}

			i -= defaultedList2.size();
		}

		if (defaultedList != null) {
			defaultedList.set(i, itemStack);
		}
	}

	public float method_7370(BlockState blockState) {
		return this.field_7547.get(this.selectedSlot).method_7924(blockState);
	}

	public ListTag method_7384(ListTag listTag) {
		for (int i = 0; i < this.field_7547.size(); i++) {
			if (!this.field_7547.get(i).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)i);
				this.field_7547.get(i).method_7953(compoundTag);
				listTag.add(compoundTag);
			}
		}

		for (int ix = 0; ix < this.field_7548.size(); ix++) {
			if (!this.field_7548.get(ix).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)(ix + 100));
				this.field_7548.get(ix).method_7953(compoundTag);
				listTag.add(compoundTag);
			}
		}

		for (int ixx = 0; ixx < this.field_7544.size(); ixx++) {
			if (!this.field_7544.get(ixx).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)(ixx + 150));
				this.field_7544.get(ixx).method_7953(compoundTag);
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}

	public void method_7397(ListTag listTag) {
		this.field_7547.clear();
		this.field_7548.clear();
		this.field_7544.clear();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			int j = compoundTag.getByte("Slot") & 255;
			ItemStack itemStack = ItemStack.method_7915(compoundTag);
			if (!itemStack.isEmpty()) {
				if (j >= 0 && j < this.field_7547.size()) {
					this.field_7547.set(j, itemStack);
				} else if (j >= 100 && j < this.field_7548.size() + 100) {
					this.field_7548.set(j - 100, itemStack);
				} else if (j >= 150 && j < this.field_7544.size() + 150) {
					this.field_7544.set(j - 150, itemStack);
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.field_7547.size() + this.field_7548.size() + this.field_7544.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_7547) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemStackx : this.field_7548) {
			if (!itemStackx.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemStackxx : this.field_7544) {
			if (!itemStackxx.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack method_5438(int i) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.field_7543) {
			if (i < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			i -= defaultedList.size();
		}

		return list == null ? ItemStack.EMPTY : (ItemStack)list.get(i);
	}

	@Override
	public TextComponent method_5477() {
		return new TranslatableTextComponent("container.inventory");
	}

	public boolean method_7383(BlockState blockState) {
		return this.method_5438(this.selectedSlot).method_7951(blockState);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack method_7372(int i) {
		return this.field_7548.get(i);
	}

	public void method_7375(float f) {
		if (!(f <= 0.0F)) {
			f /= 4.0F;
			if (f < 1.0F) {
				f = 1.0F;
			}

			for (int i = 0; i < this.field_7548.size(); i++) {
				ItemStack itemStack = this.field_7548.get(i);
				if (itemStack.getItem() instanceof ArmorItem) {
					itemStack.applyDamage((int)f, this.field_7546);
				}
			}
		}
	}

	public void dropAll() {
		for (List<ItemStack> list : this.field_7543) {
			for (int i = 0; i < list.size(); i++) {
				ItemStack itemStack = (ItemStack)list.get(i);
				if (!itemStack.isEmpty()) {
					this.field_7546.method_7329(itemStack, true, false);
					list.set(i, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public void markDirty() {
		this.changeCount++;
	}

	@Environment(EnvType.CLIENT)
	public int getChangeCount() {
		return this.changeCount;
	}

	public void method_7396(ItemStack itemStack) {
		this.field_7549 = itemStack;
	}

	public ItemStack method_7399() {
		return this.field_7549;
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return this.field_7546.invalid ? false : !(playerEntity.squaredDistanceTo(this.field_7546) > 64.0);
	}

	public boolean method_7379(ItemStack itemStack) {
		for (List<ItemStack> list : this.field_7543) {
			for (ItemStack itemStack2 : list) {
				if (!itemStack2.isEmpty() && itemStack2.isEqualIgnoreTags(itemStack)) {
					return true;
				}
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7382(Tag<Item> tag) {
		for (List<ItemStack> list : this.field_7543) {
			for (ItemStack itemStack : list) {
				if (!itemStack.isEmpty() && tag.contains(itemStack.getItem())) {
					return true;
				}
			}
		}

		return false;
	}

	public void clone(PlayerInventory playerInventory) {
		for (int i = 0; i < this.getInvSize(); i++) {
			this.method_5447(i, playerInventory.method_5438(i));
		}

		this.selectedSlot = playerInventory.selectedSlot;
	}

	@Override
	public void clear() {
		for (List<ItemStack> list : this.field_7543) {
			list.clear();
		}
	}

	public void method_7387(RecipeFinder recipeFinder) {
		for (ItemStack itemStack : this.field_7547) {
			recipeFinder.method_7404(itemStack);
		}
	}
}
