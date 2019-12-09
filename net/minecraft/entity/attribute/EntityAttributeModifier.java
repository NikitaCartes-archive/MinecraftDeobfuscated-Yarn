/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.util.math.MathHelper;

public class EntityAttributeModifier {
    private final double amount;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID uuid;
    private boolean serialize = true;

    public EntityAttributeModifier(String name, double amount, Operation operation) {
        this(MathHelper.randomUuid(ThreadLocalRandom.current()), () -> name, amount, operation);
    }

    public EntityAttributeModifier(UUID uuid, String name, double amount, Operation operation) {
        this(uuid, () -> name, amount, operation);
    }

    public EntityAttributeModifier(UUID uuid, Supplier<String> nameGetter, double amount, Operation operation) {
        this.uuid = uuid;
        this.nameGetter = nameGetter;
        this.amount = amount;
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

    public double getAmount() {
        return this.amount;
    }

    public boolean shouldSerialize() {
        return this.serialize;
    }

    public EntityAttributeModifier setSerialize(boolean serialize) {
        this.serialize = serialize;
        return this;
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
        return this.uuid != null ? this.uuid.hashCode() : 0;
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + (Object)((Object)this.operation) + ", name='" + this.nameGetter.get() + '\'' + ", id=" + this.uuid + ", serialize=" + this.serialize + '}';
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

