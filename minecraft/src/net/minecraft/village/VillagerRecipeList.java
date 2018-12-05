package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TagHelper;

public class VillagerRecipeList extends ArrayList<VillagerRecipe> {
	public VillagerRecipeList() {
	}

	public VillagerRecipeList(CompoundTag compoundTag) {
		this.serialize(compoundTag);
	}

	@Nullable
	public VillagerRecipe getValidRecipe(ItemStack itemStack, ItemStack itemStack2, int i) {
		if (i > 0 && i < this.size()) {
			VillagerRecipe villagerRecipe = (VillagerRecipe)this.get(i);
			return !this.itemsAreEqual(itemStack, villagerRecipe.getBuyItem())
					|| (!itemStack2.isEmpty() || villagerRecipe.hasSecondBuyItem())
						&& (!villagerRecipe.hasSecondBuyItem() || !this.itemsAreEqual(itemStack2, villagerRecipe.getSecondBuyItem()))
					|| itemStack.getAmount() < villagerRecipe.getBuyItem().getAmount()
					|| villagerRecipe.hasSecondBuyItem() && itemStack2.getAmount() < villagerRecipe.getSecondBuyItem().getAmount()
				? null
				: villagerRecipe;
		} else {
			for (int j = 0; j < this.size(); j++) {
				VillagerRecipe villagerRecipe2 = (VillagerRecipe)this.get(j);
				if (this.itemsAreEqual(itemStack, villagerRecipe2.getBuyItem())
					&& itemStack.getAmount() >= villagerRecipe2.getBuyItem().getAmount()
					&& (
						!villagerRecipe2.hasSecondBuyItem() && itemStack2.isEmpty()
							|| villagerRecipe2.hasSecondBuyItem()
								&& this.itemsAreEqual(itemStack2, villagerRecipe2.getSecondBuyItem())
								&& itemStack2.getAmount() >= villagerRecipe2.getSecondBuyItem().getAmount()
					)) {
					return villagerRecipe2;
				}
			}

			return null;
		}
	}

	private boolean itemsAreEqual(ItemStack itemStack, ItemStack itemStack2) {
		ItemStack itemStack3 = itemStack.copy();
		if (itemStack3.getItem().canDamage()) {
			itemStack3.setDamage(itemStack3.getDamage());
		}

		return ItemStack.areEqualIgnoreTags(itemStack3, itemStack2)
			&& (!itemStack2.hasTag() || itemStack3.hasTag() && TagHelper.areTagsEqual(itemStack2.getTag(), itemStack3.getTag(), false));
	}

	public void writeToBuf(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			VillagerRecipe villagerRecipe = (VillagerRecipe)this.get(i);
			packetByteBuf.writeItemStack(villagerRecipe.getBuyItem());
			packetByteBuf.writeItemStack(villagerRecipe.getSellItem());
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

	@Environment(EnvType.CLIENT)
	public static VillagerRecipeList readFromBuf(PacketByteBuf packetByteBuf) throws IOException {
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

	public void serialize(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("Recipes", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			this.add(new VillagerRecipe(compoundTag2));
		}
	}

	public CompoundTag deserialize() {
		CompoundTag compoundTag = new CompoundTag();
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.size(); i++) {
			VillagerRecipe villagerRecipe = (VillagerRecipe)this.get(i);
			listTag.add((Tag)villagerRecipe.deserialize());
		}

		compoundTag.put("Recipes", listTag);
		return compoundTag;
	}
}
