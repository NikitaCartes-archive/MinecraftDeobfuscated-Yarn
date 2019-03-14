package net.minecraft.entity.attribute;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAttributes {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final EntityAttribute MAX_HEALTH = new ClampedEntityAttribute(null, "generic.maxHealth", 20.0, 0.0, 1024.0)
		.setName("Max Health")
		.setTracked(true);
	public static final EntityAttribute FOLLOW_RANGE = new ClampedEntityAttribute(null, "generic.followRange", 32.0, 0.0, 2048.0).setName("Follow Range");
	public static final EntityAttribute KNOCKBACK_RESISTANCE = new ClampedEntityAttribute(null, "generic.knockbackResistance", 0.0, 0.0, 1.0)
		.setName("Knockback Resistance");
	public static final EntityAttribute MOVEMENT_SPEED = new ClampedEntityAttribute(null, "generic.movementSpeed", 0.7F, 0.0, 1024.0)
		.setName("Movement Speed")
		.setTracked(true);
	public static final EntityAttribute FLYING_SPEED = new ClampedEntityAttribute(null, "generic.flyingSpeed", 0.4F, 0.0, 1024.0)
		.setName("Flying Speed")
		.setTracked(true);
	public static final EntityAttribute ATTACK_DAMAGE = new ClampedEntityAttribute(null, "generic.attackDamage", 2.0, 0.0, 2048.0);
	public static final EntityAttribute ATTACK_KNOCKBACK = new ClampedEntityAttribute(null, "generic.attackKnockback", 0.0, 0.0, 5.0);
	public static final EntityAttribute ATTACK_SPEED = new ClampedEntityAttribute(null, "generic.attackSpeed", 4.0, 0.0, 1024.0).setTracked(true);
	public static final EntityAttribute ARMOR = new ClampedEntityAttribute(null, "generic.armor", 0.0, 0.0, 30.0).setTracked(true);
	public static final EntityAttribute ARMOR_TOUGHNESS = new ClampedEntityAttribute(null, "generic.armorToughness", 0.0, 0.0, 20.0).setTracked(true);
	public static final EntityAttribute LUCK = new ClampedEntityAttribute(null, "generic.luck", 0.0, -1024.0, 1024.0).setTracked(true);

	public static ListTag toTag(AbstractEntityAttributeContainer abstractEntityAttributeContainer) {
		ListTag listTag = new ListTag();

		for (EntityAttributeInstance entityAttributeInstance : abstractEntityAttributeContainer.values()) {
			listTag.add(toTag(entityAttributeInstance));
		}

		return listTag;
	}

	private static CompoundTag toTag(EntityAttributeInstance entityAttributeInstance) {
		CompoundTag compoundTag = new CompoundTag();
		EntityAttribute entityAttribute = entityAttributeInstance.getAttribute();
		compoundTag.putString("Name", entityAttribute.getId());
		compoundTag.putDouble("Base", entityAttributeInstance.getBaseValue());
		Collection<EntityAttributeModifier> collection = entityAttributeInstance.getModifiers();
		if (collection != null && !collection.isEmpty()) {
			ListTag listTag = new ListTag();

			for (EntityAttributeModifier entityAttributeModifier : collection) {
				if (entityAttributeModifier.shouldSerialize()) {
					listTag.add(toTag(entityAttributeModifier));
				}
			}

			compoundTag.put("Modifiers", listTag);
		}

		return compoundTag;
	}

	public static CompoundTag toTag(EntityAttributeModifier entityAttributeModifier) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", entityAttributeModifier.getName());
		compoundTag.putDouble("Amount", entityAttributeModifier.getAmount());
		compoundTag.putInt("Operation", entityAttributeModifier.getOperation().getId());
		compoundTag.putUuid("UUID", entityAttributeModifier.getId());
		return compoundTag;
	}

	public static void fromTag(AbstractEntityAttributeContainer abstractEntityAttributeContainer, ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(compoundTag.getString("Name"));
			if (entityAttributeInstance == null) {
				LOGGER.warn("Ignoring unknown attribute '{}'", compoundTag.getString("Name"));
			} else {
				fromTag(entityAttributeInstance, compoundTag);
			}
		}
	}

	private static void fromTag(EntityAttributeInstance entityAttributeInstance, CompoundTag compoundTag) {
		entityAttributeInstance.setBaseValue(compoundTag.getDouble("Base"));
		if (compoundTag.containsKey("Modifiers", 9)) {
			ListTag listTag = compoundTag.getList("Modifiers", 10);

			for (int i = 0; i < listTag.size(); i++) {
				EntityAttributeModifier entityAttributeModifier = createFromTag(listTag.getCompoundTag(i));
				if (entityAttributeModifier != null) {
					EntityAttributeModifier entityAttributeModifier2 = entityAttributeInstance.getModifier(entityAttributeModifier.getId());
					if (entityAttributeModifier2 != null) {
						entityAttributeInstance.removeModifier(entityAttributeModifier2);
					}

					entityAttributeInstance.addModifier(entityAttributeModifier);
				}
			}
		}
	}

	@Nullable
	public static EntityAttributeModifier createFromTag(CompoundTag compoundTag) {
		UUID uUID = compoundTag.getUuid("UUID");

		try {
			EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(compoundTag.getInt("Operation"));
			return new EntityAttributeModifier(uUID, compoundTag.getString("Name"), compoundTag.getDouble("Amount"), operation);
		} catch (Exception var3) {
			LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
			return null;
		}
	}
}
