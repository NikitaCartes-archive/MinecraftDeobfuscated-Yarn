package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

enum ToolType {
	field_20340,
	field_20341,
	field_20342,
	field_20343,
	field_20344,
	field_20345;

	protected static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	protected static final UUID ATTACK_REACH_MODIFIER_ID = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

	public void initModifiers(ToolMaterial toolMaterial, Multimap<String, EntityAttributeModifier> multimap) {
		float f = this.getAttackSpeed(toolMaterial);
		float g = this.getAttackDamage(toolMaterial);
		float h = this.getAttackReach(toolMaterial);
		multimap.put(
			EntityAttributes.ATTACK_DAMAGE.getId(),
			new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)g, EntityAttributeModifier.Operation.field_6328)
		);
		multimap.put(
			EntityAttributes.ATTACK_SPEED.getId(),
			new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)f, EntityAttributeModifier.Operation.field_6328)
		);
		if (h != 0.0F) {
			multimap.put(
				EntityAttributes.ATTACK_REACH.getId(),
				new EntityAttributeModifier(ATTACK_REACH_MODIFIER_ID, "Weapon modifier", (double)h, EntityAttributeModifier.Operation.field_6328)
			);
		}
	}

	public float getAttackDamage(ToolMaterial toolMaterial) {
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

	public float getAttackSpeed(ToolMaterial toolMaterial) {
		switch (this) {
			case field_20340:
				return 0.5F;
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

	public float getAttackReach(ToolMaterial toolMaterial) {
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
