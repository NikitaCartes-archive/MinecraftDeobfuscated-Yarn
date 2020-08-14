package net.minecraft;

import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

public enum class_5510 {
	field_26791,
	field_26792,
	field_26793,
	field_26794,
	field_26795,
	field_26796;

	public static final UUID field_26797 = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID field_26798 = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	public static final UUID field_26799 = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

	public void method_31245(ToolMaterial toolMaterial, Builder<EntityAttribute, EntityAttributeModifier> builder) {
		float f = this.method_31246(toolMaterial);
		float g = this.method_31244(toolMaterial);
		float h = this.method_31247(toolMaterial);
		builder.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(field_26797, "Weapon modifier", (double)g, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(field_26798, "Weapon modifier", (double)f, EntityAttributeModifier.Operation.ADDITION)
		);
		if (h != 0.0F) {
			builder.put(
				EntityAttributes.GENERIC_ATTACK_REACH, new EntityAttributeModifier(field_26799, "Weapon modifier", (double)h, EntityAttributeModifier.Operation.ADDITION)
			);
		}
	}

	public float method_31244(ToolMaterial toolMaterial) {
		switch (this) {
			case field_26791:
				return toolMaterial.getAttackDamage() + 2.0F;
			case field_26792:
				return toolMaterial.getAttackDamage() + 3.0F;
			case field_26793:
				return toolMaterial.getAttackDamage() + 1.0F;
			case field_26794:
				if (toolMaterial != ToolMaterials.IRON && toolMaterial != ToolMaterials.DIAMOND) {
					if (toolMaterial == ToolMaterials.NETHERITE) {
						return 2.0F;
					}

					return 0.0F;
				}

				return 1.0F;
			case field_26795:
				return toolMaterial.getAttackDamage();
			case field_26796:
				return 5.0F;
			default:
				return 0.0F;
		}
	}

	public float method_31246(ToolMaterial toolMaterial) {
		switch (this) {
			case field_26791:
				return 0.5F;
			case field_26792:
			case field_26795:
			case field_26796:
				return -0.5F;
			case field_26793:
				return 0.0F;
			case field_26794:
				if (toolMaterial == ToolMaterials.WOOD) {
					return -0.5F;
				} else if (toolMaterial == ToolMaterials.IRON) {
					return 0.5F;
				} else if (toolMaterial == ToolMaterials.DIAMOND) {
					return 1.0F;
				} else if (toolMaterial == ToolMaterials.GOLD) {
					return 1.0F;
				} else {
					if (toolMaterial == ToolMaterials.NETHERITE) {
						return 1.0F;
					}

					return 0.0F;
				}
			default:
				return 0.0F;
		}
	}

	public float method_31247(ToolMaterial toolMaterial) {
		switch (this) {
			case field_26791:
				return 0.5F;
			case field_26792:
			case field_26793:
			case field_26795:
				return 0.0F;
			case field_26794:
			case field_26796:
				return 1.0F;
			default:
				return 0.0F;
		}
	}
}
