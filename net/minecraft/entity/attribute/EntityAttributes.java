/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class EntityAttributes {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityAttribute MAX_HEALTH = new ClampedEntityAttribute(null, "generic.maxHealth", 20.0, 0.0, 1024.0).setName("Max Health").setTracked(true);
    public static final EntityAttribute FOLLOW_RANGE = new ClampedEntityAttribute(null, "generic.followRange", 32.0, 0.0, 2048.0).setName("Follow Range");
    public static final EntityAttribute KNOCKBACK_RESISTANCE = new ClampedEntityAttribute(null, "generic.knockbackResistance", 0.0, 0.0, 1.0).setName("Knockback Resistance");
    public static final EntityAttribute MOVEMENT_SPEED = new ClampedEntityAttribute(null, "generic.movementSpeed", 0.7f, 0.0, 1024.0).setName("Movement Speed").setTracked(true);
    public static final EntityAttribute FLYING_SPEED = new ClampedEntityAttribute(null, "generic.flyingSpeed", 0.4f, 0.0, 1024.0).setName("Flying Speed").setTracked(true);
    public static final EntityAttribute ATTACK_DAMAGE = new ClampedEntityAttribute(null, "generic.attackDamage", 2.0, 0.0, 2048.0);
    public static final EntityAttribute ATTACK_KNOCKBACK = new ClampedEntityAttribute(null, "generic.attackKnockback", 0.0, 0.0, 5.0);
    public static final EntityAttribute ATTACK_SPEED = new ClampedEntityAttribute(null, "generic.attackSpeed", 4.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute ARMOR = new ClampedEntityAttribute(null, "generic.armor", 0.0, 0.0, 30.0).setTracked(true);
    public static final EntityAttribute ARMOR_TOUGHNESS = new ClampedEntityAttribute(null, "generic.armorToughness", 0.0, 0.0, 20.0).setTracked(true);
    public static final EntityAttribute LUCK = new ClampedEntityAttribute(null, "generic.luck", 0.0, -1024.0, 1024.0).setTracked(true);

    public static ListTag toTag(AbstractEntityAttributeContainer container) {
        ListTag listTag = new ListTag();
        for (EntityAttributeInstance entityAttributeInstance : container.values()) {
            listTag.add(EntityAttributes.toTag(entityAttributeInstance));
        }
        return listTag;
    }

    private static CompoundTag toTag(EntityAttributeInstance instance) {
        CompoundTag compoundTag = new CompoundTag();
        EntityAttribute entityAttribute = instance.getAttribute();
        compoundTag.putString("Name", entityAttribute.getId());
        compoundTag.putDouble("Base", instance.getBaseValue());
        Set<EntityAttributeModifier> collection = instance.getModifiers();
        if (collection != null && !collection.isEmpty()) {
            ListTag listTag = new ListTag();
            for (EntityAttributeModifier entityAttributeModifier : collection) {
                if (!entityAttributeModifier.shouldSerialize()) continue;
                listTag.add(EntityAttributes.toTag(entityAttributeModifier));
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
        compoundTag.putUuidOld("UUID", modifier.getId());
        return compoundTag;
    }

    public static void fromTag(AbstractEntityAttributeContainer container, ListTag tag) {
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            EntityAttributeInstance entityAttributeInstance = container.get(compoundTag.getString("Name"));
            if (entityAttributeInstance == null) {
                LOGGER.warn("Ignoring unknown attribute '{}'", (Object)compoundTag.getString("Name"));
                continue;
            }
            EntityAttributes.fromTag(entityAttributeInstance, compoundTag);
        }
    }

    private static void fromTag(EntityAttributeInstance instance, CompoundTag tag) {
        instance.setBaseValue(tag.getDouble("Base"));
        if (tag.contains("Modifiers", 9)) {
            ListTag listTag = tag.getList("Modifiers", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                EntityAttributeModifier entityAttributeModifier = EntityAttributes.createFromTag(listTag.getCompound(i));
                if (entityAttributeModifier == null) continue;
                EntityAttributeModifier entityAttributeModifier2 = instance.getModifier(entityAttributeModifier.getId());
                if (entityAttributeModifier2 != null) {
                    instance.removeModifier(entityAttributeModifier2);
                }
                instance.addModifier(entityAttributeModifier);
            }
        }
    }

    @Nullable
    public static EntityAttributeModifier createFromTag(CompoundTag tag) {
        UUID uUID = tag.getUuidOld("UUID");
        try {
            EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(tag.getInt("Operation"));
            return new EntityAttributeModifier(uUID, tag.getString("Name"), tag.getDouble("Amount"), operation);
        } catch (Exception exception) {
            LOGGER.warn("Unable to create attribute: {}", (Object)exception.getMessage());
            return null;
        }
    }
}

