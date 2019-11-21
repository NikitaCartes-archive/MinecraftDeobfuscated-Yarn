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
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

public class PlayerInventory implements Inventory, Nameable {
	public final DefaultedList<ItemStack> main = DefaultedList.ofSize(36, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
	private final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
	public int selectedSlot;
	public final PlayerEntity player;
	private ItemStack cursorStack = ItemStack.EMPTY;
	private int changeCount;

	public PlayerInventory(PlayerEntity player) {
		this.player = player;
	}

	public ItemStack getMainHandStack() {
		return isValidHotbarIndex(this.selectedSlot) ? this.main.get(this.selectedSlot) : ItemStack.EMPTY;
	}

	public static int getHotbarSize() {
		return 9;
	}

	private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
		return !existingStack.isEmpty()
			&& this.areItemsEqual(existingStack, stack)
			&& existingStack.isStackable()
			&& existingStack.getCount() < existingStack.getMaxCount()
			&& existingStack.getCount() < this.getInvMaxStackAmount();
	}

	private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
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

	public void swapSlotWithHotbar(int hotbarSlot) {
		this.selectedSlot = this.getSwappableHotbarSlot();
		ItemStack itemStack = this.main.get(this.selectedSlot);
		this.main.set(this.selectedSlot, this.main.get(hotbarSlot));
		this.main.set(hotbarSlot, itemStack);
	}

	public static boolean isValidHotbarIndex(int slot) {
		return slot >= 0 && slot < 9;
	}

	@Environment(EnvType.CLIENT)
	public int getSlotWithStack(ItemStack stack) {
		for (int i = 0; i < this.main.size(); i++) {
			if (!this.main.get(i).isEmpty() && this.areItemsEqual(stack, this.main.get(i))) {
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
				&& !itemStack2.hasCustomName()) {
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
	public void scrollInHotbar(double scrollAmount) {
		if (scrollAmount > 0.0) {
			scrollAmount = 1.0;
		}

		if (scrollAmount < 0.0) {
			scrollAmount = -1.0;
		}

		this.selectedSlot = (int)((double)this.selectedSlot - scrollAmount);

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
				int l = i <= 0 ? itemStack.getCount() : Math.min(i - j, itemStack.getCount());
				j += l;
				if (i != 0) {
					itemStack.decrement(l);
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
			int kx = i <= 0 ? this.cursorStack.getCount() : Math.min(i - j, this.cursorStack.getCount());
			j += kx;
			if (i != 0) {
				this.cursorStack.decrement(kx);
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

	private int addStack(ItemStack stack) {
		int i = this.getOccupiedSlotWithRoomForStack(stack);
		if (i == -1) {
			i = this.getEmptySlot();
		}

		return i == -1 ? stack.getCount() : this.addStack(i, stack);
	}

	private int addStack(int slot, ItemStack stack) {
		Item item = stack.getItem();
		int i = stack.getCount();
		ItemStack itemStack = this.getInvStack(slot);
		if (itemStack.isEmpty()) {
			itemStack = new ItemStack(item, 0);
			if (stack.hasTag()) {
				itemStack.setTag(stack.getTag().copy());
			}

			this.setInvStack(slot, itemStack);
		}

		int j = i;
		if (i > itemStack.getMaxCount() - itemStack.getCount()) {
			j = itemStack.getMaxCount() - itemStack.getCount();
		}

		if (j > this.getInvMaxStackAmount() - itemStack.getCount()) {
			j = this.getInvMaxStackAmount() - itemStack.getCount();
		}

		if (j == 0) {
			return i;
		} else {
			i -= j;
			itemStack.increment(j);
			itemStack.setCooldown(5);
			return i;
		}
	}

	public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
		if (this.canStackAddMore(this.getInvStack(this.selectedSlot), stack)) {
			return this.selectedSlot;
		} else if (this.canStackAddMore(this.getInvStack(40), stack)) {
			return 40;
		} else {
			for (int i = 0; i < this.main.size(); i++) {
				if (this.canStackAddMore(this.main.get(i), stack)) {
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
					defaultedList.get(i).inventoryTick(this.player.world, this.player, i, this.selectedSlot == i);
				}
			}
		}
	}

	public boolean insertStack(ItemStack stack) {
		return this.insertStack(-1, stack);
	}

	public boolean insertStack(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		} else {
			try {
				if (stack.isDamaged()) {
					if (slot == -1) {
						slot = this.getEmptySlot();
					}

					if (slot >= 0) {
						this.main.set(slot, stack.copy());
						this.main.get(slot).setCooldown(5);
						stack.setCount(0);
						return true;
					} else if (this.player.abilities.creativeMode) {
						stack.setCount(0);
						return true;
					} else {
						return false;
					}
				} else {
					int i;
					do {
						i = stack.getCount();
						if (slot == -1) {
							stack.setCount(this.addStack(stack));
						} else {
							stack.setCount(this.addStack(slot, stack));
						}
					} while (!stack.isEmpty() && stack.getCount() < i);

					if (stack.getCount() == i && this.player.abilities.creativeMode) {
						stack.setCount(0);
						return true;
					} else {
						return stack.getCount() < i;
					}
				}
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
				CrashReportSection crashReportSection = crashReport.addElement("Item being added");
				crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
				crashReportSection.add("Item data", stack.getDamage());
				crashReportSection.add("Item name", (CrashCallable<String>)(() -> stack.getName().getString()));
				throw new CrashException(crashReport);
			}
		}
	}

	public void offerOrDrop(World world, ItemStack stack) {
		if (!world.isClient) {
			while (!stack.isEmpty()) {
				int i = this.getOccupiedSlotWithRoomForStack(stack);
				if (i == -1) {
					i = this.getEmptySlot();
				}

				if (i == -1) {
					this.player.dropItem(stack, false);
					break;
				}

				int j = stack.getMaxCount() - this.getInvStack(i).getCount();
				if (this.insertStack(i, stack.split(j))) {
					((ServerPlayerEntity)this.player).networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-2, i, this.getInvStack(i)));
				}
			}
		}
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
			if (slot < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			slot -= defaultedList.size();
		}

		return list != null && !((ItemStack)list.get(slot)).isEmpty() ? Inventories.splitStack(list, slot, amount) : ItemStack.EMPTY;
	}

	public void removeOne(ItemStack stack) {
		for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
			for (int i = 0; i < defaultedList.size(); i++) {
				if (defaultedList.get(i) == stack) {
					defaultedList.set(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
			if (slot < defaultedList2.size()) {
				defaultedList = defaultedList2;
				break;
			}

			slot -= defaultedList2.size();
		}

		if (defaultedList != null && !defaultedList.get(slot).isEmpty()) {
			ItemStack itemStack = defaultedList.get(slot);
			defaultedList.set(slot, ItemStack.EMPTY);
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
			if (slot < defaultedList2.size()) {
				defaultedList = defaultedList2;
				break;
			}

			slot -= defaultedList2.size();
		}

		if (defaultedList != null) {
			defaultedList.set(slot, stack);
		}
	}

	public float getBlockBreakingSpeed(BlockState block) {
		return this.main.get(this.selectedSlot).getMiningSpeed(block);
	}

	public ListTag serialize(ListTag tag) {
		for (int i = 0; i < this.main.size(); i++) {
			if (!this.main.get(i).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)i);
				this.main.get(i).toTag(compoundTag);
				tag.add(compoundTag);
			}
		}

		for (int ix = 0; ix < this.armor.size(); ix++) {
			if (!this.armor.get(ix).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)(ix + 100));
				this.armor.get(ix).toTag(compoundTag);
				tag.add(compoundTag);
			}
		}

		for (int ixx = 0; ixx < this.offHand.size(); ixx++) {
			if (!this.offHand.get(ixx).isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)(ixx + 150));
				this.offHand.get(ixx).toTag(compoundTag);
				tag.add(compoundTag);
			}
		}

		return tag;
	}

	public void deserialize(ListTag tag) {
		this.main.clear();
		this.armor.clear();
		this.offHand.clear();

		for (int i = 0; i < tag.size(); i++) {
			CompoundTag compoundTag = tag.getCompound(i);
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
	public ItemStack getInvStack(int slot) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
			if (slot < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			slot -= defaultedList.size();
		}

		return list == null ? ItemStack.EMPTY : (ItemStack)list.get(slot);
	}

	@Override
	public Text getName() {
		return new TranslatableText("container.inventory");
	}

	public boolean isUsingEffectiveTool(BlockState blockState) {
		return this.getInvStack(this.selectedSlot).isEffectiveOn(blockState);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getArmorStack(int slot) {
		return this.armor.get(slot);
	}

	public void damageArmor(float armor) {
		if (!(armor <= 0.0F)) {
			armor /= 4.0F;
			if (armor < 1.0F) {
				armor = 1.0F;
			}

			for (int i = 0; i < this.armor.size(); i++) {
				ItemStack itemStack = this.armor.get(i);
				if (itemStack.getItem() instanceof ArmorItem) {
					int j = i;
					itemStack.damage((int)armor, this.player, playerEntity -> playerEntity.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, j)));
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

	public void setCursorStack(ItemStack stack) {
		this.cursorStack = stack;
	}

	public ItemStack getCursorStack() {
		return this.cursorStack;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.player.removed ? false : !(player.squaredDistanceTo(this.player) > 64.0);
	}

	public boolean contains(ItemStack stack) {
		for (List<ItemStack> list : this.combinedInventory) {
			for (ItemStack itemStack : list) {
				if (!itemStack.isEmpty() && itemStack.isItemEqualIgnoreDamage(stack)) {
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

	public void clone(PlayerInventory other) {
		for (int i = 0; i < this.getInvSize(); i++) {
			this.setInvStack(i, other.getInvStack(i));
		}

		this.selectedSlot = other.selectedSlot;
	}

	@Override
	public void clear() {
		for (List<ItemStack> list : this.combinedInventory) {
			list.clear();
		}
	}

	public void populateRecipeFinder(RecipeFinder finder) {
		for (ItemStack itemStack : this.main) {
			finder.addNormalItem(itemStack);
		}
	}
}
