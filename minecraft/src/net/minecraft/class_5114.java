package net.minecraft;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

public class class_5114<T> extends SimpleRegistry<T> {
	private final IntFunction<T> field_23631;

	public class_5114(IntFunction<T> intFunction) {
		this.field_23631 = intFunction;
	}

	@Nullable
	@Override
	public T get(@Nullable Identifier id) {
		T object = super.get(id);
		if (object != null) {
			return object;
		} else if (id.getNamespace().equals("_generated")) {
			int i = Integer.parseInt(id.getPath());
			if (i < 0) {
				return null;
			} else {
				T object2 = (T)this.field_23631.apply(i);
				this.set(i, id, object2);
				return object2;
			}
		} else {
			return null;
		}
	}

	@Override
	public T get(int index) {
		T object = super.get(index);
		if (object != null) {
			return object;
		} else if (index < 0) {
			return null;
		} else {
			T object2 = (T)this.field_23631.apply(index);
			this.set(index, new Identifier("_generated", Integer.toString(index)), object2);
			return object2;
		}
	}
}
