package net.minecraft.client.option;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class HotbarStorageEntry extends ForwardingList<ItemStack> {
	private final DefaultedList<ItemStack> delegate = DefaultedList.ofSize(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);

	@Override
	protected List<ItemStack> delegate() {
		return this.delegate;
	}

	public NbtList toListTag() {
		NbtList nbtList = new NbtList();

		for (ItemStack itemStack : this.delegate()) {
			nbtList.add(itemStack.writeNbt(new NbtCompound()));
		}

		return nbtList;
	}

	public void fromListTag(NbtList tag) {
		List<ItemStack> list = this.delegate();

		for (int i = 0; i < list.size(); i++) {
			list.set(i, ItemStack.fromNbt(tag.getCompound(i)));
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
