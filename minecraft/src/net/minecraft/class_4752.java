package net.minecraft;

import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

public enum class_4752 {
	field_21870,
	field_21871,
	field_21872,
	field_21873,
	field_21874,
	field_21875;

	public static final UUID field_21876 = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID field_21877 = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	public static final UUID field_21878 = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

	public void method_24324(ToolMaterial toolMaterial, Multimap<String, EntityAttributeModifier> multimap) {
		float f = this.method_24325(toolMaterial);
		float g = this.method_24323(toolMaterial);
		float h = this.method_24326(toolMaterial);
		multimap.put(
			EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(field_21876, "Weapon modifier", (double)g, EntityAttributeModifier.Operation.ADDITION)
		);
		multimap.put(
			EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(field_21877, "Weapon modifier", (double)f, EntityAttributeModifier.Operation.ADDITION)
		);
		if (h != 0.0F) {
			multimap.put(
				EntityAttributes.field_21868.getId(), new EntityAttributeModifier(field_21878, "Weapon modifier", (double)h, EntityAttributeModifier.Operation.ADDITION)
			);
		}
	}

	public float method_24323(ToolMaterial toolMaterial) {
		switch (this) {
			case field_21870:
				return toolMaterial.getAttackDamage() + 2.0F;
			case field_21871:
				return toolMaterial.getAttackDamage() + 3.0F;
			case field_21872:
				return toolMaterial.getAttackDamage() + 1.0F;
			case field_21873:
				if (toolMaterial != ToolMaterials.IRON && toolMaterial != ToolMaterials.DIAMOND) {
					return 0.0F;
				}

				return 1.0F;
			case field_21874:
				return toolMaterial.getAttackDamage();
			case field_21875:
				return 5.0F;
			default:
				return 0.0F;
		}
	}

	public float method_24325(ToolMaterial toolMaterial) {
		switch (this) {
			case field_21870:
				return 0.5F;
			case field_21871:
			case field_21874:
			case field_21875:
				return -0.5F;
			case field_21872:
				return 0.0F;
			case field_21873:
				if (toolMaterial == ToolMaterials.WOOD) {
					return -0.5F;
				} else if (toolMaterial == ToolMaterials.IRON) {
					return 0.5F;
				} else if (toolMaterial == ToolMaterials.DIAMOND) {
					return 1.0F;
				} else {
					if (toolMaterial == ToolMaterials.GOLD) {
						return 1.0F;
					}

					return 0.0F;
				}
			default:
				return 0.0F;
		}
	}

	public float method_24326(ToolMaterial toolMaterial) {
		switch (this) {
			case field_21870:
				return 0.5F;
			case field_21871:
			case field_21872:
			case field_21874:
				return 0.0F;
			case field_21873:
			case field_21875:
				return 1.0F;
			default:
				return 0.0F;
		}
	}
}
