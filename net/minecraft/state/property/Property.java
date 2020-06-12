/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.state.State;

public abstract class Property<T extends Comparable<T>> {
    private final Class<T> field_24742;
    private final String field_24743;
    private Integer field_24744;
    private final Codec<T> field_24745 = Codec.STRING.comapFlatMap(string -> this.parse((String)string).map(DataResult::success).orElseGet(() -> DataResult.error("Unable to read property: " + this + " with value: " + string)), this::name);
    private final Codec<class_4933<T>> field_25670 = this.field_24745.xmap(this::method_30042, class_4933::method_30045);

    protected Property(String string2, Class<T> class_) {
        this.field_24742 = class_;
        this.field_24743 = string2;
    }

    public class_4933<T> method_30042(T comparable) {
        return new class_4933(this, (Comparable)comparable, null);
    }

    public class_4933<T> method_30041(State<?, ?> state) {
        return new class_4933(this, (Comparable)state.get(this), null);
    }

    public Stream<class_4933<T>> method_30043() {
        return this.getValues().stream().map(this::method_30042);
    }

    public Codec<class_4933<T>> method_30044() {
        return this.field_25670;
    }

    public String getName() {
        return this.field_24743;
    }

    public Class<T> getType() {
        return this.field_24742;
    }

    public abstract Collection<T> getValues();

    public abstract String name(T var1);

    public abstract Optional<T> parse(String var1);

    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.field_24743).add("clazz", this.field_24742).add("values", this.getValues()).toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Property) {
            Property property = (Property)object;
            return this.field_24742.equals(property.field_24742) && this.field_24743.equals(property.field_24743);
        }
        return false;
    }

    public final int hashCode() {
        if (this.field_24744 == null) {
            this.field_24744 = this.computeHashCode();
        }
        return this.field_24744;
    }

    public int computeHashCode() {
        return 31 * this.field_24742.hashCode() + this.field_24743.hashCode();
    }

    public static final class class_4933<T extends Comparable<T>> {
        private final Property<T> field_22879;
        private final T field_22880;

        private class_4933(Property<T> property, T comparable) {
            if (!property.getValues().contains(comparable)) {
                throw new IllegalArgumentException("Value " + comparable + " does not belong to property " + property);
            }
            this.field_22879 = property;
            this.field_22880 = comparable;
        }

        public Property<T> method_25815() {
            return this.field_22879;
        }

        public T method_30045() {
            return this.field_22880;
        }

        public String toString() {
            return this.field_22879.getName() + "=" + this.field_22879.name(this.field_22880);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof class_4933)) {
                return false;
            }
            class_4933 lv = (class_4933)object;
            return this.field_22879 == lv.field_22879 && this.field_22880.equals(lv.field_22880);
        }

        public int hashCode() {
            int i = this.field_22879.hashCode();
            i = 31 * i + this.field_22880.hashCode();
            return i;
        }

        /* synthetic */ class_4933(Property property, Comparable comparable, _1 arg) {
            this(property, comparable);
        }
    }
}

