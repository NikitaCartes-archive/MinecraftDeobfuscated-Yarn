package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public class VillagerRecipeList extends ArrayList<VillagerRecipe> {
	public VillagerRecipeList() {
	}

	public VillagerRecipeList(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("Recipes", 10);

		for (int i = 0; i < listTag.size(); i++) {
			this.add(new VillagerRecipe(listTag.getCompoundTag(i)));
		}
	}

	@Nullable
	public VillagerRecipe getValidRecipe(ItemStack itemStack, ItemStack itemStack2, int i) {
		if (i > 0 && i < this.size()) {
			VillagerRecipe villagerRecipe = (VillagerRecipe)this.get(i);
			return villagerRecipe.matchesBuyItems(itemStack, itemStack2) ? villagerRecipe : null;
		} else {
			for (int j = 0; j < this.size(); j++) {
				VillagerRecipe villagerRecipe2 = (VillagerRecipe)this.get(j);
				if (villagerRecipe2.matchesBuyItems(itemStack, itemStack2)) {
					return villagerRecipe2;
				}
			}

			return null;
		}
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			VillagerRecipe villagerRecipe = (VillagerRecipe)this.get(i);
			packetByteBuf.writeItemStack(villagerRecipe.getFirstBuyItem());
			packetByteBuf.writeItemStack(villagerRecipe.getModifiableSellItem());
			ItemStack itemStack = villagerRecipe.getSecondBuyItem();
			packetByteBuf.writeBoolean(!itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
				packetByteBuf.writeItemStack(itemStack);
			}

			packetByteBuf.writeBoolean(villagerRecipe.isDisabled());
			packetByteBuf.writeInt(villagerRecipe.getUses());
			packetByteBuf.writeInt(villagerRecipe.getMaxUses());
		}
	}

	public static VillagerRecipeList fromPacket(PacketByteBuf packetByteBuf) {
		VillagerRecipeList villagerRecipeList = new VillagerRecipeList();
		int i = packetByteBuf.readByte() & 255;

		for (int j = 0; j < i; j++) {
			ItemStack itemStack = packetByteBuf.readItemStack();
			ItemStack itemStack2 = packetByteBuf.readItemStack();
			ItemStack itemStack3 = ItemStack.EMPTY;
			if (packetByteBuf.readBoolean()) {
				itemStack3 = packetByteBuf.readItemStack();
			}

			boolean bl = packetByteBuf.readBoolean();
			int k = packetByteBuf.readInt();
			int l = packetByteBuf.readInt();
			VillagerRecipe villagerRecipe = new VillagerRecipe(itemStack, itemStack3, itemStack2, k, l);
			if (bl) {
				villagerRecipe.clearUses();
			}

			villagerRecipeList.add(villagerRecipe);
		}

		return villagerRecipeList;
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.size(); i++) {
			VillagerRecipe villagerRecipe = (VillagerRecipe)this.get(i);
			listTag.add(villagerRecipe.deserialize());
		}

		compoundTag.put("Recipes", listTag);
		return compoundTag;
	}
}
