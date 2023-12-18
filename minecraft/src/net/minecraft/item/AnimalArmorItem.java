package net.minecraft.item;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class AnimalArmorItem extends Item {
	private final int bonus;
	private final Identifier entityTexture;
	private final AnimalArmorItem.Type type;

	public AnimalArmorItem(int bonus, AnimalArmorItem.Type type, @Nullable String name, Item.Settings settings) {
		super(settings);
		this.bonus = bonus;
		this.type = type;
		this.entityTexture = (Identifier)type.textureIdFunction.apply(name);
	}

	public Identifier getEntityTexture() {
		return this.entityTexture;
	}

	public int getBonus() {
		return this.bonus;
	}

	public AnimalArmorItem.Type getType() {
		return this.type;
	}

	public static enum Type {
		EQUESTRIAN(name -> new Identifier("textures/entity/horse/armor/horse_armor_" + name + ".png")),
		CANINE(name -> new Identifier("textures/entity/wolf/wolf_armor.png"));

		final Function<String, Identifier> textureIdFunction;

		private Type(Function<String, Identifier> textureIdFunction) {
			this.textureIdFunction = textureIdFunction;
		}
	}
}
