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
	public static final EntityAttribute ATTACK_SPEED = new ClampedEntityAttribute(null, "generic.attackSpeed", 4.0, 0.1F, 1024.0).setTracked(true);
	public static final EntityAttribute ARMOR = new ClampedEntityAttribute(null, "generic.armor", 0.0, 0.0, 30.0).setTracked(true);
	public static final EntityAttribute ARMOR_TOUGHNESS = new ClampedEntityAttribute(null, "generic.armorToughness", 0.0, 0.0, 20.0).setTracked(true);
	public static final EntityAttribute LUCK = new ClampedEntityAttribute(null, "generic.luck", 0.0, -1024.0, 1024.0).setTracked(true);
	public static final EntityAttribute field_21805 = new ClampedEntityAttribute(null, "generic.attackReach", 2.5, 0.0, 6.0).setTracked(true);

	public static ListTag toTag(AbstractEntityAttributeContainer container) {
		ListTag listTag = new ListTag();

		for (EntityAttributeInstance entityAttributeInstance : container.values()) {
			listTag.add(toTag(entityAttributeInstance));
		}

		return listTag;
	}

	private static CompoundTag toTag(EntityAttributeInstance instance) {
		CompoundTag compoundTag = new CompoundTag();
		EntityAttribute entityAttribute = instance.getAttribute();
		compoundTag.putString("Name", entityAttribute.getId());
		compoundTag.putDouble("Base", instance.getBaseValue());
		Collection<EntityAttributeModifier> collection = instance.getModifiers();
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

	public static CompoundTag toTag(EntityAttributeModifier modifier) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", modifier.getName());
		compoundTag.putDouble("Amount", modifier.getAmount());
		compoundTag.putInt("Operation", modifier.getOperation().getId());
		compoundTag.putUuid("UUID", modifier.getId());
		return compoundTag;
	}

	public static void fromTag(AbstractEntityAttributeContainer container, ListTag tag) {
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag compoundTag = tag.getCompound(i);
			EntityAttributeInstance entityAttributeInstance = container.get(compoundTag.getString("Name"));
			if (entityAttributeInstance == null) {
				LOGGER.warn("Ignoring unknown attribute '{}'", compoundTag.getString("Name"));
			} else {
				fromTag(entityAttributeInstance, compoundTag);
			}
		}
	}

	private static void fromTag(EntityAttributeInstance instance, CompoundTag tag) {
		instance.setBaseValue(tag.getDouble("Base"));
		if (tag.contains("Modifiers", 9)) {
			ListTag listTag = tag.getList("Modifiers", 10);

			for (int i = 0; i < listTag.size(); i++) {
				EntityAttributeModifier entityAttributeModifier = createFromTag(listTag.getCompound(i));
				if (entityAttributeModifier != null) {
					EntityAttributeModifier entityAttributeModifier2 = instance.getModifier(entityAttributeModifier.getId());
					if (entityAttributeModifier2 != null) {
						instance.removeModifier(entityAttributeModifier2);
					}

					instance.addModifier(entityAttributeModifier);
				}
			}
		}
	}

	@Nullable
	public static EntityAttributeModifier createFromTag(CompoundTag tag) {
		UUID uUID = tag.getUuid("UUID");

		try {
			EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(tag.getInt("Operation"));
			return new EntityAttributeModifier(uUID, tag.getString("Name"), tag.getDouble("Amount"), operation);
		} catch (Exception var3) {
			LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
			return null;
		}
	}
}
