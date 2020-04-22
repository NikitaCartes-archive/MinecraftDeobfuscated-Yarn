/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class EntityAttributeModifier {
    private static final Logger LOGGER = LogManager.getLogger();
    private final double value;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID uuid;

    public EntityAttributeModifier(String name, double value, Operation operation) {
        this(MathHelper.randomUuid(ThreadLocalRandom.current()), () -> name, value, operation);
    }

    public EntityAttributeModifier(UUID uuid, String name, double value, Operation operation) {
        this(uuid, () -> name, value, operation);
    }

    public EntityAttributeModifier(UUID uuid, Supplier<String> nameGetter, double value, Operation operation) {
        this.uuid = uuid;
        this.nameGetter = nameGetter;
        this.value = value;
        this.operation = operation;
    }

    public UUID getId() {
        return this.uuid;
    }

    public String getName() {
        return this.nameGetter.get();
    }

    public Operation getOperation() {
        return this.operation;
    }

    public double getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)o;
        return Objects.equals(this.uuid, entityAttributeModifier.uuid);
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.value + ", operation=" + (Object)((Object)this.operation) + ", name='" + this.nameGetter.get() + '\'' + ", id=" + this.uuid + '}';
    }

    public CompoundTag toTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Name", this.getName());
        compoundTag.putDouble("Amount", this.value);
        compoundTag.putInt("Operation", this.operation.getId());
        compoundTag.putUuidNew("UUID", this.uuid);
        return compoundTag;
    }

    @Nullable
    public static EntityAttributeModifier fromTag(CompoundTag tag) {
        try {
            UUID uUID = tag.getUuidNew("UUID");
            Operation operation = Operation.fromId(tag.getInt("Operation"));
            return new EntityAttributeModifier(uUID, tag.getString("Name"), tag.getDouble("Amount"), operation);
        } catch (Exception exception) {
            LOGGER.warn("Unable to create attribute: {}", (Object)exception.getMessage());
            return null;
        }
    }

    public static enum Operation {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);

        private static final Operation[] VALUES;
        private final int id;

        private Operation(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static Operation fromId(int id) {
            if (id < 0 || id >= VALUES.length) {
                throw new IllegalArgumentException("No operation with value " + id);
            }
            return VALUES[id];
        }

        static {
            VALUES = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }
    }
}

