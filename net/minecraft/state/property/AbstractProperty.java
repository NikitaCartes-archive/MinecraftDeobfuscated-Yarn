/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import net.minecraft.state.property.Property;

public abstract class AbstractProperty<T extends Comparable<T>>
implements Property<T> {
    private final Class<T> valueClass;
    private final String name;
    private Integer computedHashCode;

    protected AbstractProperty(String string, Class<T> class_) {
        this.valueClass = class_;
        this.name = string;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<T> getValueClass() {
        return this.valueClass;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).add("values", this.getValues()).toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof AbstractProperty) {
            AbstractProperty abstractProperty = (AbstractProperty)object;
            return this.valueClass.equals(abstractProperty.valueClass) && this.name.equals(abstractProperty.name);
        }
        return false;
    }

    public final int hashCode() {
        if (this.computedHashCode == null) {
            this.computedHashCode = this.computeHashCode();
        }
        return this.computedHashCode;
    }

    public int computeHashCode() {
        return 31 * this.valueClass.hashCode() + this.name.hashCode();
    }
}

