package net.minecraft;

import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

public enum class_5508 {
	field_26763,
	field_26764,
	field_26765,
	field_26766,
	field_26767,
	field_26768;

	public static final UUID field_26769 = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID field_26770 = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	public static final UUID field_26771 = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

	public void method_31214(ToolMaterial toolMaterial, Builder<EntityAttribute, EntityAttributeModifier> builder) {
		float f = this.method_31215(toolMaterial);
		float g = this.method_31213(toolMaterial);
		float h = this.method_31216(toolMaterial);
		builder.put(EntityAttributes.field_23721, new EntityAttributeModifier(field_26769, "Weapon modifier", (double)g, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.field_23723, new EntityAttributeModifier(field_26770, "Weapon modifier", (double)f, EntityAttributeModifier.Operation.ADDITION));
		if (h != 0.0F) {
			builder.put(EntityAttributes.field_26761, new EntityAttributeModifier(field_26771, "Weapon modifier", (double)h, EntityAttributeModifier.Operation.ADDITION));
		}
	}

	public float method_31213(ToolMaterial toolMaterial) {
		switch (this) {
			case field_26763:
				return toolMaterial.getAttackDamage() + 2.0F;
			case field_26764:
				return toolMaterial.getAttackDamage() + 3.0F;
			case field_26765:
				return toolMaterial.getAttackDamage() + 1.0F;
			case field_26766:
				if (toolMaterial != ToolMaterials.field_8923 && toolMaterial != ToolMaterials.field_8930) {
					return 0.0F;
				}

				return 1.0F;
			case field_26767:
				return toolMaterial.getAttackDamage();
			case field_26768:
				return 5.0F;
			default:
				return 0.0F;
		}
	}

	public float method_31215(ToolMaterial toolMaterial) {
		switch (this) {
			case field_26763:
				return 0.5F;
			case field_26764:
			case field_26767:
			case field_26768:
				return -0.5F;
			case field_26765:
				return 0.0F;
			case field_26766:
				if (toolMaterial == ToolMaterials.field_8922) {
					return -0.5F;
				} else if (toolMaterial == ToolMaterials.field_8923) {
					return 0.5F;
				} else if (toolMaterial == ToolMaterials.field_8930) {
					return 1.0F;
				} else if (toolMaterial == ToolMaterials.field_8929) {
					return 1.0F;
				} else {
					if (toolMaterial == ToolMaterials.field_22033) {
						return 1.0F;
					}

					return 0.0F;
				}
			default:
				return 0.0F;
		}
	}

	public float method_31216(ToolMaterial toolMaterial) {
		switch (this) {
			case field_26763:
				return 0.5F;
			case field_26764:
			case field_26765:
			case field_26767:
				return 0.0F;
			case field_26766:
			case field_26768:
				return 1.0F;
			default:
				return 0.0F;
		}
	}
}
