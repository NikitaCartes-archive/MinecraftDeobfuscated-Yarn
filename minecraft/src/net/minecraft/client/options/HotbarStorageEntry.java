package net.minecraft.client.options;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class HotbarStorageEntry extends ForwardingList<ItemStack> {
	private final DefaultedList<ItemStack> delegate = DefaultedList.create(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);

	@Override
	protected List<ItemStack> delegate() {
		return this.delegate;
	}

	public ListTag toListTag() {
		ListTag listTag = new ListTag();

		for (ItemStack itemStack : this.delegate()) {
			listTag.add(itemStack.toTag(new CompoundTag()));
		}

		return listTag;
	}

	public void fromListTag(ListTag listTag) {
		List<ItemStack> list = this.delegate();

		for (int i = 0; i < list.size(); i++) {
			list.set(i, ItemStack.fromTag(listTag.getCompoundTag(i)));
		}
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.delegate()) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
