package net.minecraft.entity.attribute;

import java.util.UUID;

public interface AttributeModifierCreator {
	UUID getUuid();

	EntityAttributeModifier createAttributeModifier(int amplifier);
}
