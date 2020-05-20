package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.state.State;

public abstract class Property<T extends Comparable<T>> {
	private final Class<T> field_24742;
	private final String field_24743;
	private Integer field_24744;
	private final Codec<T> field_24745 = Codec.STRING
		.comapFlatMap(
			stringx -> (DataResult)this.parse(stringx)
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error("Unable to read property: " + this + " with value: " + stringx)),
			this::name
		);

	protected Property(String string, Class<T> class_) {
		this.field_24742 = class_;
		this.field_24743 = string;
	}

	public String getName() {
		return this.field_24743;
	}

	public Class<T> getType() {
		return this.field_24742;
	}

	public abstract Collection<T> getValues();

	public abstract String name(T value);

	public abstract Optional<T> parse(String name);

	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", this.field_24743).add("clazz", this.field_24742).add("values", this.getValues()).toString();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Property)) {
			return false;
		} else {
			Property<?> property = (Property<?>)object;
			return this.field_24742.equals(property.field_24742) && this.field_24743.equals(property.field_24743);
		}
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

	public <U, S extends State<?, S>> DataResult<S> method_28503(DynamicOps<U> dynamicOps, S state, U object) {
		DataResult<T> dataResult = this.field_24745.parse(dynamicOps, object);
		return dataResult.map(comparable -> state.with(this, comparable)).setPartial(state);
	}
}
