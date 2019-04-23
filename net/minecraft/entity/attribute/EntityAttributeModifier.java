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

    public EntityAttributeModifier(String string, double d, Operation operation) {
        this(MathHelper.randomUuid(ThreadLocalRandom.current()), () -> string, d, operation);
    }

    public EntityAttributeModifier(UUID uUID, String string, double d, Operation operation) {
        this(uUID, () -> string, d, operation);
    }

    public EntityAttributeModifier(UUID uUID, Supplier<String> supplier, double d, Operation operation) {
        this.uuid = uUID;
        this.nameGetter = supplier;
        this.amount = d;
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

    public EntityAttributeModifier setSerialize(boolean bl) {
        this.serialize = bl;
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)object;
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

        private Operation(int j) {
            this.id = j;
        }

        public int getId() {
            return this.id;
        }

        public static Operation fromId(int i) {
            if (i < 0 || i >= VALUES.length) {
                throw new IllegalArgumentException("No operation with value " + i);
            }
            return VALUES[i];
        }

        static {
            VALUES = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }
    }
}

