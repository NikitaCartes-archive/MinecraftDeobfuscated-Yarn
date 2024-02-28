package net.minecraft.item;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class AnimalArmorItem extends ArmorItem {
	private final Identifier entityTexture;
	@Nullable
	private final Identifier overlayTexture;
	private final AnimalArmorItem.Type type;

	public AnimalArmorItem(RegistryEntry<ArmorMaterial> material, AnimalArmorItem.Type type, boolean hasOverlay, Item.Settings settings) {
		super(material, ArmorItem.Type.BODY, settings);
		this.type = type;
		Identifier identifier = (Identifier)type.textureIdFunction.apply(((RegistryKey)material.getKey().orElseThrow()).getValue());
		this.entityTexture = identifier.withSuffixedPath(".png");
		if (hasOverlay) {
			this.overlayTexture = identifier.withSuffixedPath("_overlay.png");
		} else {
			this.overlayTexture = null;
		}
	}

	public Identifier getEntityTexture() {
		return this.entityTexture;
	}

	@Nullable
	public Identifier getOverlayTexture() {
		return this.overlayTexture;
	}

	public AnimalArmorItem.Type getType() {
		return this.type;
	}

	@Override
	public SoundEvent getBreakSound() {
		return this.type.breakSound;
	}

	public static enum Type {
		EQUESTRIAN(id -> id.withPath((UnaryOperator<String>)(path -> "textures/entity/horse/armor/horse_armor_" + path)), SoundEvents.ENTITY_ITEM_BREAK),
		CANINE(id -> id.withPath("textures/entity/wolf/wolf_armor"), SoundEvents.ITEM_WOLF_ARMOR_BREAK);

		final Function<Identifier, Identifier> textureIdFunction;
		final SoundEvent breakSound;

		private Type(Function<Identifier, Identifier> textureIdFunction, SoundEvent breakSound) {
			this.textureIdFunction = textureIdFunction;
			this.breakSound = breakSound;
		}
	}
}
