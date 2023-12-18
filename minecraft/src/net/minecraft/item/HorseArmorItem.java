package net.minecraft.item;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class HorseArmorItem extends Item {
	private final int bonus;
	private final Identifier field_47823;
	private final HorseArmorItem.class_9076 field_47824;

	public HorseArmorItem(int bonus, HorseArmorItem.class_9076 arg, @Nullable String string, Item.Settings settings) {
		super(settings);
		this.bonus = bonus;
		this.field_47824 = arg;
		this.field_47823 = (Identifier)arg.field_47827.apply(string);
	}

	public Identifier getEntityTexture() {
		return this.field_47823;
	}

	public int getBonus() {
		return this.bonus;
	}

	public HorseArmorItem.class_9076 method_55756() {
		return this.field_47824;
	}

	public static enum class_9076 {
		EQUESTRIAN(string -> new Identifier("textures/entity/horse/armor/horse_armor_" + string + ".png")),
		CANINE(string -> new Identifier("textures/entity/wolf/wolf_armor.png"));

		final Function<String, Identifier> field_47827;

		private class_9076(Function<String, Identifier> function) {
			this.field_47827 = function;
		}
	}
}
