package net.minecraft;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

@Environment(EnvType.CLIENT)
public class class_9980 {
	private static final Splitter field_53163 = Splitter.on(',');
	private static final Splitter field_53164 = Splitter.on('=').limit(2);

	public static <O, S extends State<O, S>> Predicate<State<O, S>> method_62334(StateManager<O, S> stateManager, String string) {
		Map<Property<?>, Comparable<?>> map = new HashMap();

		for (String string2 : field_53163.split(string)) {
			Iterator<String> iterator = field_53164.split(string2).iterator();
			if (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				Property<?> property = stateManager.getProperty(string3);
				if (property != null && iterator.hasNext()) {
					String string4 = (String)iterator.next();
					Comparable<?> comparable = method_62335((Property<Comparable<?>>)property, string4);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + property.getValues());
					}

					map.put(property, comparable);
				} else if (!string3.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
				}
			}
		}

		return state -> {
			for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
				if (!Objects.equals(state.get((Property)entry.getKey()), entry.getValue())) {
					return false;
				}
			}

			return true;
		};
	}

	@Nullable
	private static <T extends Comparable<T>> T method_62335(Property<T> property, String string) {
		return (T)property.parse(string).orElse(null);
	}
}
