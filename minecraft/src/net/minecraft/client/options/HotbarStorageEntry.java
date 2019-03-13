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
	private final DefaultedList<ItemStack> field_3948 = DefaultedList.create(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);

	@Override
	protected List<ItemStack> delegate() {
		return this.field_3948;
	}

	public ListTag method_3153() {
		ListTag listTag = new ListTag();

		for (ItemStack itemStack : this.delegate()) {
			listTag.add(itemStack.method_7953(new CompoundTag()));
		}

		return listTag;
	}

	public void method_3152(ListTag listTag) {
		List<ItemStack> list = this.delegate();

		for (int i = 0; i < list.size(); i++) {
			list.set(i, ItemStack.method_7915(listTag.getCompoundTag(i)));
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
