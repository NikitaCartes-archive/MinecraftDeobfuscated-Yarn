package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public class TraderRecipeList extends ArrayList<TraderRecipe> {
	public TraderRecipeList() {
	}

	public TraderRecipeList(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.method_10554("Recipes", 10);

		for (int i = 0; i < listTag.size(); i++) {
			this.add(new TraderRecipe(listTag.getCompoundTag(i)));
		}
	}

	@Nullable
	public TraderRecipe getValidRecipe(ItemStack itemStack, ItemStack itemStack2, int i) {
		if (i > 0 && i < this.size()) {
			TraderRecipe traderRecipe = (TraderRecipe)this.get(i);
			return traderRecipe.matchesBuyItems(itemStack, itemStack2) ? traderRecipe : null;
		} else {
			for (int j = 0; j < this.size(); j++) {
				TraderRecipe traderRecipe2 = (TraderRecipe)this.get(j);
				if (traderRecipe2.matchesBuyItems(itemStack, itemStack2)) {
					return traderRecipe2;
				}
			}

			return null;
		}
	}

	public void method_8270(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TraderRecipe traderRecipe = (TraderRecipe)this.get(i);
			packetByteBuf.writeItemStack(traderRecipe.method_19272());
			packetByteBuf.writeItemStack(traderRecipe.getModifiableSellItem());
			ItemStack itemStack = traderRecipe.getSecondBuyItem();
			packetByteBuf.writeBoolean(!itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
				packetByteBuf.writeItemStack(itemStack);
			}

			packetByteBuf.writeBoolean(traderRecipe.isDisabled());
			packetByteBuf.writeInt(traderRecipe.getUses());
			packetByteBuf.writeInt(traderRecipe.getMaxUses());
			packetByteBuf.writeInt(traderRecipe.method_19279());
			packetByteBuf.writeInt(traderRecipe.method_19277());
			packetByteBuf.writeFloat(traderRecipe.method_19278());
		}
	}

	public static TraderRecipeList method_8265(PacketByteBuf packetByteBuf) {
		TraderRecipeList traderRecipeList = new TraderRecipeList();
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
			int m = packetByteBuf.readInt();
			int n = packetByteBuf.readInt();
			float f = packetByteBuf.readFloat();
			TraderRecipe traderRecipe = new TraderRecipe(itemStack, itemStack3, itemStack2, k, l, m, f);
			if (bl) {
				traderRecipe.clearUses();
			}

			traderRecipe.method_19273(n);
			traderRecipeList.add(traderRecipe);
		}

		return traderRecipeList;
	}

	public CompoundTag method_8268() {
		CompoundTag compoundTag = new CompoundTag();
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.size(); i++) {
			TraderRecipe traderRecipe = (TraderRecipe)this.get(i);
			listTag.add(traderRecipe.method_8251());
		}

		compoundTag.method_10566("Recipes", listTag);
		return compoundTag;
	}
}
