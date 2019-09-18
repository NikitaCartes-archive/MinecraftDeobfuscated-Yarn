package net.minecraft.state.property;

import com.google.common.base.MoreObjects;

public abstract class AbstractProperty<T extends Comparable<T>> implements Property<T> {
	private final Class<T> type;
	private final String name;
	private Integer computedHashCode;

	protected AbstractProperty(String string, Class<T> class_) {
		this.type = class_;
		this.name = string;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class<T> getType() {
		return this.type;
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.type).add("values", this.getValues()).toString();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof AbstractProperty)) {
			return false;
		} else {
			AbstractProperty<?> abstractProperty = (AbstractProperty<?>)object;
			return this.type.equals(abstractProperty.type) && this.name.equals(abstractProperty.name);
		}
	}

	public final int hashCode() {
		if (this.computedHashCode == null) {
			this.computedHashCode = this.computeHashCode();
		}

		return this.computedHashCode;
	}

	public int computeHashCode() {
		return 31 * this.type.hashCode() + this.name.hashCode();
	}
}
