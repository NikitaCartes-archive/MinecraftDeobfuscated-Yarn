package net.minecraft.item;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AnimalArmorItem extends ArmorItem {
	private final Identifier entityTexture;
	private final AnimalArmorItem.Type type;

	public AnimalArmorItem(RegistryEntry<ArmorMaterial> material, AnimalArmorItem.Type type, Item.Settings settings) {
		super(material, ArmorItem.Type.BODY, settings);
		this.type = type;
		this.entityTexture = (Identifier)type.textureIdFunction.apply(((RegistryKey)material.getKey().orElseThrow()).getValue());
	}

	public Identifier getEntityTexture() {
		return this.entityTexture;
	}

	public AnimalArmorItem.Type getType() {
		return this.type;
	}

	public static enum Type {
		EQUESTRIAN(id -> id.withPath((UnaryOperator<String>)(path -> "textures/entity/horse/armor/horse_armor_" + path + ".png"))),
		CANINE(id -> id.withPath("textures/entity/wolf/wolf_armor.png"));

		final Function<Identifier, Identifier> textureIdFunction;

		private Type(Function<Identifier, Identifier> textureIdFunction) {
			this.textureIdFunction = textureIdFunction;
		}
	}
}
