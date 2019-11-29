package net.minecraft;

import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

public enum class_4741 {
	field_21807,
	field_21808,
	field_21809,
	field_21810,
	field_21811,
	field_21812;

	public static final UUID field_21813 = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID field_21814 = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	public static final UUID field_21815 = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

	public void method_24227(ToolMaterial toolMaterial, Multimap<String, EntityAttributeModifier> multimap) {
		float f = this.method_24228(toolMaterial);
		float g = this.method_24226(toolMaterial);
		float h = this.method_24229(toolMaterial);
		multimap.put(
			EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(field_21813, "Weapon modifier", (double)g, EntityAttributeModifier.Operation.ADDITION)
		);
		multimap.put(
			EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(field_21814, "Weapon modifier", (double)f, EntityAttributeModifier.Operation.ADDITION)
		);
		if (h != 0.0F) {
			multimap.put(
				EntityAttributes.field_21805.getId(), new EntityAttributeModifier(field_21815, "Weapon modifier", (double)h, EntityAttributeModifier.Operation.ADDITION)
			);
		}
	}

	public float method_24226(ToolMaterial toolMaterial) {
		switch (this) {
			case field_21807:
				return toolMaterial.getAttackDamage() + 3.0F;
			case field_21808:
				return toolMaterial.getAttackDamage() + 4.0F;
			case field_21809:
				return toolMaterial.getAttackDamage() + 1.0F;
			case field_21810:
				if (toolMaterial != ToolMaterials.IRON && toolMaterial != ToolMaterials.DIAMOND) {
					return 0.0F;
				}

				return 1.0F;
			case field_21811:
				return toolMaterial.getAttackDamage();
			case field_21812:
				return 6.0F;
			default:
				return 0.0F;
		}
	}

	public float method_24228(ToolMaterial toolMaterial) {
		switch (this) {
			case field_21807:
				return 0.5F;
			case field_21808:
			case field_21811:
			case field_21812:
				return -0.5F;
			case field_21809:
				return 0.0F;
			case field_21810:
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

	public float method_24229(ToolMaterial toolMaterial) {
		switch (this) {
			case field_21807:
				return 0.5F;
			case field_21808:
			case field_21809:
			case field_21811:
				return 0.0F;
			case field_21810:
			case field_21812:
				return 1.0F;
			default:
				return 0.0F;
		}
	}
}
