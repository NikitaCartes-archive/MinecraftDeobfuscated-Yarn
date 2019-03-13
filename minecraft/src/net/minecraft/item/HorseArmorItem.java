package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class HorseArmorItem extends Item {
	private final int field_18136;
	private final String texture;

	public HorseArmorItem(int i, String string, Item.Settings settings) {
		super(settings);
		this.field_18136 = i;
		this.texture = "textures/entity/horse/armor/horse_armor_" + string + ".png";
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_18454() {
		return new Identifier(this.texture);
	}

	public int method_18455() {
		return this.field_18136;
	}
}
