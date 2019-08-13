package net.minecraft;

import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

public enum class_4465 {
	field_20340,
	field_20341,
	field_20342,
	field_20343,
	field_20344,
	field_20345;

	public static final UUID field_20346 = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID field_20347 = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	public static final UUID field_20348 = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

	public void method_21756(ToolMaterial toolMaterial, Multimap<String, EntityAttributeModifier> multimap) {
		float f = this.method_21757(toolMaterial);
		float g = this.method_21755(toolMaterial);
		float h = this.method_21758(toolMaterial);
		multimap.put(
			EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(field_20346, "Weapon modifier", (double)g, EntityAttributeModifier.Operation.field_6328)
		);
		multimap.put(
			EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(field_20347, "Weapon modifier", (double)f, EntityAttributeModifier.Operation.field_6328)
		);
		if (h != 0.0F) {
			multimap.put(
				EntityAttributes.field_20339.getId(), new EntityAttributeModifier(field_20348, "Weapon modifier", (double)h, EntityAttributeModifier.Operation.field_6328)
			);
		}
	}

	public float method_21755(ToolMaterial toolMaterial) {
		switch (this) {
			case field_20340:
				return toolMaterial.getAttackDamage() + 3.0F;
			case field_20341:
				return toolMaterial.getAttackDamage() + 4.0F;
			case field_20342:
				return toolMaterial.getAttackDamage() + 1.0F;
			case field_20343:
				if (toolMaterial != ToolMaterials.field_8923 && toolMaterial != ToolMaterials.field_8930) {
					return 0.0F;
				}

				return 1.0F;
			case field_20344:
				return toolMaterial.getAttackDamage();
			case field_20345:
				return 6.0F;
			default:
				return 0.0F;
		}
	}

	public float method_21757(ToolMaterial toolMaterial) {
		switch (this) {
			case field_20340:
				return 0.0F;
			case field_20341:
			case field_20344:
			case field_20345:
				return -0.5F;
			case field_20342:
				return 0.0F;
			case field_20343:
				if (toolMaterial == ToolMaterials.field_8922) {
					return -0.5F;
				} else if (toolMaterial == ToolMaterials.field_8923) {
					return 0.5F;
				} else if (toolMaterial == ToolMaterials.field_8930) {
					return 1.0F;
				} else {
					if (toolMaterial == ToolMaterials.field_8929) {
						return 1.0F;
					}

					return 0.0F;
				}
			default:
				return 0.0F;
		}
	}

	public float method_21758(ToolMaterial toolMaterial) {
		switch (this) {
			case field_20340:
				return 0.5F;
			case field_20341:
			case field_20342:
			case field_20344:
				return 0.0F;
			case field_20343:
			case field_20345:
				return 1.0F;
			default:
				return 0.0F;
		}
	}
}
