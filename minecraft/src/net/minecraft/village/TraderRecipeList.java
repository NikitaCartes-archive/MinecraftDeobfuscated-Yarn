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
		ListTag listTag = compoundTag.getList("Recipes", 10);

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

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TraderRecipe traderRecipe = (TraderRecipe)this.get(i);
			packetByteBuf.writeItemStack(traderRecipe.getDiscountedFirstBuyItem());
			packetByteBuf.writeItemStack(traderRecipe.getModifiableSellItem());
			ItemStack itemStack = traderRecipe.getSecondBuyItem();
			packetByteBuf.writeBoolean(!itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
				packetByteBuf.writeItemStack(itemStack);
			}

			packetByteBuf.writeBoolean(traderRecipe.isDisabled());
			packetByteBuf.writeInt(traderRecipe.getUses());
			packetByteBuf.writeInt(traderRecipe.getMaxUses());
			packetByteBuf.writeInt(traderRecipe.getRewardedExp());
			packetByteBuf.writeInt(traderRecipe.getTax());
			packetByteBuf.writeFloat(traderRecipe.getPriceMultiplier());
		}
	}

	public static TraderRecipeList fromPacket(PacketByteBuf packetByteBuf) {
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

			traderRecipe.setTax(n);
			traderRecipeList.add(traderRecipe);
		}

		return traderRecipeList;
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.size(); i++) {
			TraderRecipe traderRecipe = (TraderRecipe)this.get(i);
			listTag.add(traderRecipe.toTag());
		}

		compoundTag.put("Recipes", listTag);
		return compoundTag;
	}
}
