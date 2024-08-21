package net.minecraft.client.render.model.json;

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
public class BlockPropertiesPredicate {
	private static final Splitter COMMA_SPLITTER = Splitter.on(',');
	private static final Splitter EQUAL_SIGN_SPLITTER = Splitter.on('=').limit(2);

	public static <O, S extends State<O, S>> Predicate<State<O, S>> parse(StateManager<O, S> stateManager, String string) {
		Map<Property<?>, Comparable<?>> map = new HashMap();

		for (String string2 : COMMA_SPLITTER.split(string)) {
			Iterator<String> iterator = EQUAL_SIGN_SPLITTER.split(string2).iterator();
			if (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				Property<?> property = stateManager.getProperty(string3);
				if (property != null && iterator.hasNext()) {
					String string4 = (String)iterator.next();
					Comparable<?> comparable = parse((Property<Comparable<?>>)property, string4);
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
	private static <T extends Comparable<T>> T parse(Property<T> property, String value) {
		return (T)property.parse(value).orElse(null);
	}
}
