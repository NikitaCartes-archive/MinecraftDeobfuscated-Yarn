package net.minecraft.text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HoverEvent {
	private final HoverEvent.Action action;
	private final Text value;

	public HoverEvent(HoverEvent.Action action, Text text) {
		this.action = action;
		this.value = text;
	}

	public HoverEvent.Action getAction() {
		return this.action;
	}

	public Text getValue() {
		return this.value;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			HoverEvent hoverEvent = (HoverEvent)object;
			if (this.action != hoverEvent.action) {
				return false;
			} else {
				return this.value != null ? this.value.equals(hoverEvent.value) : hoverEvent.value == null;
			}
		} else {
			return false;
		}
	}

	public String toString() {
		return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
	}

	public int hashCode() {
		int i = this.action.hashCode();
		return 31 * i + (this.value != null ? this.value.hashCode() : 0);
	}

	public static enum Action {
		field_11762("show_text", true),
		field_11757("show_item", true),
		field_11761("show_entity", true);

		private static final Map<String, HoverEvent.Action> BY_NAME = (Map<String, HoverEvent.Action>)Arrays.stream(values())
			.collect(Collectors.toMap(HoverEvent.Action::getName, action -> action));
		private final boolean userDefinable;
		private final String name;

		private Action(String string2, boolean bl) {
			this.name = string2;
			this.userDefinable = bl;
		}

		public boolean isUserDefinable() {
			return this.userDefinable;
		}

		public String getName() {
			return this.name;
		}

		public static HoverEvent.Action byName(String string) {
			return (HoverEvent.Action)BY_NAME.get(string);
		}
	}
}
