package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum HorseArmorType {
	NONE(0),
	IRON(5, "iron", "meo"),
	GOLD(7, "gold", "goo"),
	DIAMOND(11, "diamond", "dio");

	private final String texturePath;
	private final String id;
	private final int protection;

	private HorseArmorType(int j) {
		this.protection = j;
		this.texturePath = null;
		this.id = "";
	}

	private HorseArmorType(int j, String string2, String string3) {
		this.protection = j;
		this.texturePath = "textures/entity/horse/armor/horse_armor_" + string2 + ".png";
		this.id = string3;
	}

	public int getOrdinal() {
		return this.ordinal();
	}

	@Environment(EnvType.CLIENT)
	public String getId() {
		return this.id;
	}

	public int getProtectionAmount() {
		return this.protection;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String getTexturePath() {
		return this.texturePath;
	}

	public static HorseArmorType getByOrdinal(int i) {
		return values()[i];
	}

	public static HorseArmorType getByStack(ItemStack itemStack) {
		return itemStack.isEmpty() ? NONE : getByItem(itemStack.getItem());
	}

	public static HorseArmorType getByItem(Item item) {
		if (item == Items.field_8578) {
			return IRON;
		} else if (item == Items.field_8560) {
			return GOLD;
		} else {
			return item == Items.field_8807 ? DIAMOND : NONE;
		}
	}

	public static boolean isHorseArmor(Item item) {
		return getByItem(item) != NONE;
	}
}
