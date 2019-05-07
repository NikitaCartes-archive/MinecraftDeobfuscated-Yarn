package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

public class PlayerInventory implements Inventory, Nameable {
	public final DefaultedList<ItemStack> main = DefaultedList.create(36, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> armor = DefaultedList.create(4, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> offHand = DefaultedList.create(1, ItemStack.EMPTY);
	private final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
	public int selectedSlot;
	public final PlayerEntity player;
	private ItemStack cursorStack = ItemStack.EMPTY;
	private int changeCount;

	public PlayerInventory(PlayerEntity playerEntity) {
		this.player = playerEntity;
	}

	public ItemStack getMainHandStack() {
		return isValidHotbarIndex(this.selectedSlot) ? this.main.get(this.selectedSlot) : ItemStack.EMPTY;
	}

	public static int getHotbarSize() {
		return 9;
	}

	private boolean canStackAddMore(ItemStack itemStack, ItemStack itemStack2) {
		return !itemStack.isEmpty()
			&& this.areItemsEqual(itemStack, itemStack2)
			&& itemStack.canStack()
			&& itemStack.getAmount() < itemStack.getMaxAmount()
			&& itemStack.getAmount() < this.getInvMaxStackAmount();
	}

	private boolean areItemsEqual(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
	}

	public int getEmptySlot() {
		for (int i = 0; i < this.main.size(); i++) {
			if (this.main.get(i).isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	@Environment(EnvType.CLIENT)
	public void addPickBlock(ItemStack itemStack) {
		int i = this.getSlotWithStack(itemStack);
		if (isValidHotbarIndex(i)) {
			this.selectedSlot = i;
		} else {
			if (i == -1) {
				this.selectedSlot = this.getSwappableHotbarSlot();
				if (!this.main.get(this.selectedSlot).isEmpty()) {
					int j = this.getEmptySlot();
					if (j != -1) {
						this.main.set(j, this.main.get(this.selectedSlot));
					}
				}

				this.main.set(this.selectedSlot, itemStack);
			} else {
				this.swapSlotWithHotbar(i);
			}
		}
	}

	public void swapSlotWithHotbar(int i) {
		this.selectedSlot = this.getSwappableHotbarSlot();
		ItemStack itemStack = this.main.get(this.selectedSlot);
		this.main.set(this.selectedSlot, this.main.get(i));
		this.main.set(i, itemStack);
	}

	public static boolean isValidHotbarIndex(int i) {
		return i >= 0 && i < 9;
	}

	@Environment(EnvType.CLIENT)
	public int getSlotWithStack(ItemStack itemStack) {
		for (int i = 0; i < this.main.size(); i++) {
			if (!this.main.get(i).isEmpty() && this.areItemsEqual(itemStack, this.main.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public int method_7371(ItemStack itemStack) {
		for (int i = 0; i < this.main.size(); i++) {
			ItemStack itemStack2 = this.main.get(i);
			if (!this.main.get(i).isEmpty()
				&& this.areItemsEqual(itemStack, this.main.get(i))
				&& !this.main.get(i).isDamaged()
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
			if (this.main.get(j).isEmpty()) {
				return j;
			}
		}

		for (int ix = 0; ix < 9; ix++) {
			int j = (this.selectedSlot + ix) % 9;
			if (!this.main.get(j).hasEnchantments()) {
				return j;
			}
		}

		return this.selectedSlot;
	}

	@Environment(EnvType.CLIENT)
	public void scrollInHotbar(double d) {
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
			ItemStack itemStack = this.getInvStack(k);
			if (!itemStack.isEmpty() && predicate.test(itemStack)) {
				int l = i <= 0 ? itemStack.getAmount() : Math.min(i - j, itemStack.getAmount());
				j += l;
				if (i != 0) {
					itemStack.subtractAmount(l);
					if (itemStack.isEmpty()) {
						this.setInvStack(k, ItemStack.EMPTY);
					}

					if (i > 0 && j >= i) {
						return j;
					}
				}
			}
		}

		if (!this.cursorStack.isEmpty() && predicate.test(this.cursorStack)) {
			int kx = i <= 0 ? this.cursorStack.getAmount() : Math.min(i - j, this.cursorStack.getAmount());
			j += kx;
			if (i != 0) {
				this.cursorStack.subtractAmount(kx);
				if (this.cursorStack.isEmpty()) {
					this.cursorStack = ItemStack.EMPTY;
				}

				if (i > 0 && j >= i) {
					return j;
				}
			}
		}

		return j;
	}

	private int addStack(ItemStack itemStack) {
		int i = this.getOccupiedSlotWithRoomForStack(itemStack);
		if (i == -1) {
			i = this.getEmptySlot();
		}

		return i == -1 ? itemStack.getAmount() : this.addStack(i, itemStack);
	}

	private int addStack(int i, ItemStack itemStack) {
		Item item = itemStack.getItem();
		int j = itemStack.getAmount();
		ItemStack itemStack2 = this.getInvStack(i);
		if (itemStack2.isEmpty()) {
			itemStack2 = new ItemStack(item, 0);
			if (itemStack.hasTag()) {
				itemStack2.setTag(itemStack.getTag().method_10553());
			}

			this.setInvStack(i, itemStack2);
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

	public int getOccupiedSlotWithRoomForStack(ItemStack itemStack) {
		if (this.canStackAddMore(this.getInvStack(this.selectedSlot), itemStack)) {
			return this.selectedSlot;
		} else if (this.canStackAddMore(this.getInvStack(40), itemStack)) {
			return 40;
		} else {
			for (int i = 0; i < this.main.size(); i++) {
				if (this.canStackAddMore(this.main.get(i), itemStack)) {
					return i;
				}
			}

			return -1;
		}
	}

	public void updateItems() {
		for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
			for (int i = 0; i < defaultedList.size(); i++) {
				if (!defaultedList.get(i).isEmpty()) {
					defaultedList.get(i).update(this.player.world, this.player, i, this.selectedSlot == i);
				}
			}
		}
	}

	public boolean insertStack(ItemStack itemStack) {
		return this.insertStack(-1, itemStack);
	}

	public boolean insertStack(int i, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		} else {
			try {
				if (itemStack.isDamaged()) {
					if (i == -1) {
						i = this.getEmptySlot();
					}

					if (i >= 0) {
						this.main.set(i, itemStack.copy());
						this.main.get(i).setUpdateCooldown(5);
						itemStack.setAmount(0);
						return true;
					} else if (this.player.abilities.creativeMode) {
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
							itemStack.setAmount(this.addStack(itemStack));
						} else {
							itemStack.setAmount(this.addStack(i, itemStack));
						}
					} while (!itemStack.isEmpty() && itemStack.getAmount() < j);

					if (itemStack.getAmount() == j && this.player.abilities.creativeMode) {
						itemStack.setAmount(0);
						return true;
					} else {
						return itemStack.getAmount() < j;
					}
				}
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
				CrashReportSection crashReportSection = crashReport.addElement("Item being added");
				crashReportSection.add("Item ID", Item.getRawIdByItem(itemStack.getItem()));
				crashReportSection.add("Item data", itemStack.getDamage());
				crashReportSection.method_577("Item name", () -> itemStack.getDisplayName().getString());
				throw new CrashException(crashReport);
			}
		}
	}

	public void offerOrDrop(World world, ItemStack itemStack) {
		if (!world.isClient) {
			while (!itemStack.isEmpty()) {
				int i = this.getOccupiedSlotWithRoomForStack(itemStack);
				if (i == -1) {
					i = this.getEmptySlot();
				}

				if (i == -1) {
					this.player.dropItem(itemStack, false);
					break;
				}

				int j = itemStack.getMaxAmount() - this.getInvStack(i).getAmount();
				if (this.insertStack(i, itemStack.split(j))) {
					((ServerPlayerEntity)this.player).networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-2, i, this.getInvStack(i)));
				}
			}
		}
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
			if (i < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			i -= defaultedList.size();
		}

		return list != null && !((ItemStack)list.get(i)).isEmpty() ? Inventories.splitStack(list, i, j) : ItemStack.EMPTY;
	}

	public void removeOne(ItemStack itemStack) {
		for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
			for (int i = 0; i < defaultedList.size(); i++) {
				if (defaultedList.get(i) == itemStack) {
					defaultedList.set(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

	@Override
	public ItemStack removeInvStack(int i) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
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
	public void setInvStack(int i, ItemStack itemStack) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
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

	public float getBlockBreakingSpeed(BlockState blockState) {
		return this.main.get(this.selectedSlot).getBlockBreakingSpeed(blockState);
	}

	public ListTag serialize(ListTag listTag) {
		for (int i = 0; i < this.main.size(); i++) {
			if (!this.main.get(i).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)i);
				this.main.get(i).toTag(compoundTag);
				listTag.add(compoundTag);
			}
		}

		for (int ix = 0; ix < this.armor.size(); ix++) {
			if (!this.armor.get(ix).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)(ix + 100));
				this.armor.get(ix).toTag(compoundTag);
				listTag.add(compoundTag);
			}
		}

		for (int ixx = 0; ixx < this.offHand.size(); ixx++) {
			if (!this.offHand.get(ixx).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)(ixx + 150));
				this.offHand.get(ixx).toTag(compoundTag);
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}

	public void deserialize(ListTag listTag) {
		this.main.clear();
		this.armor.clear();
		this.offHand.clear();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			int j = compoundTag.getByte("Slot") & 255;
			ItemStack itemStack = ItemStack.fromTag(compoundTag);
			if (!itemStack.isEmpty()) {
				if (j >= 0 && j < this.main.size()) {
					this.main.set(j, itemStack);
				} else if (j >= 100 && j < this.armor.size() + 100) {
					this.armor.set(j - 100, itemStack);
				} else if (j >= 150 && j < this.offHand.size() + 150) {
					this.offHand.set(j - 150, itemStack);
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.main.size() + this.armor.size() + this.offHand.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.main) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemStackx : this.armor) {
			if (!itemStackx.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemStackxx : this.offHand) {
			if (!itemStackxx.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int i) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
			if (i < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			i -= defaultedList.size();
		}

		return list == null ? ItemStack.EMPTY : (ItemStack)list.get(i);
	}

	@Override
	public Component getName() {
		return new TranslatableComponent("container.inventory");
	}

	public boolean isUsingEffectiveTool(BlockState blockState) {
		return this.getInvStack(this.selectedSlot).isEffectiveOn(blockState);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getArmorStack(int i) {
		return this.armor.get(i);
	}

	public void damageArmor(float f) {
		if (!(f <= 0.0F)) {
			f /= 4.0F;
			if (f < 1.0F) {
				f = 1.0F;
			}

			for (int i = 0; i < this.armor.size(); i++) {
				ItemStack itemStack = this.armor.get(i);
				if (itemStack.getItem() instanceof ArmorItem) {
					int j = i;
					itemStack.applyDamage(
						(int)f, this.player, playerEntity -> playerEntity.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.field_6178, j))
					);
				}
			}
		}
	}

	public void dropAll() {
		for (List<ItemStack> list : this.combinedInventory) {
			for (int i = 0; i < list.size(); i++) {
				ItemStack itemStack = (ItemStack)list.get(i);
				if (!itemStack.isEmpty()) {
					this.player.dropItem(itemStack, true, false);
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

	public void setCursorStack(ItemStack itemStack) {
		this.cursorStack = itemStack;
	}

	public ItemStack getCursorStack() {
		return this.cursorStack;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.player.removed ? false : !(playerEntity.squaredDistanceTo(this.player) > 64.0);
	}

	public boolean contains(ItemStack itemStack) {
		for (List<ItemStack> list : this.combinedInventory) {
			for (ItemStack itemStack2 : list) {
				if (!itemStack2.isEmpty() && itemStack2.isEqualIgnoreTags(itemStack)) {
					return true;
				}
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public boolean contains(Tag<Item> tag) {
		for (List<ItemStack> list : this.combinedInventory) {
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
			this.setInvStack(i, playerInventory.getInvStack(i));
		}

		this.selectedSlot = playerInventory.selectedSlot;
	}

	@Override
	public void clear() {
		for (List<ItemStack> list : this.combinedInventory) {
			list.clear();
		}
	}

	public void populateRecipeFinder(RecipeFinder recipeFinder) {
		for (ItemStack itemStack : this.main) {
			recipeFinder.addNormalItem(itemStack);
		}
	}
}
