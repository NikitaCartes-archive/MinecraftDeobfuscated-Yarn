package net.minecraft.text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HoverEvent {
	private final HoverEvent.Action action;
	private final Text value;

	public HoverEvent(HoverEvent.Action action, Text value) {
		this.action = action;
		this.value = value;
	}

	public HoverEvent.Action getAction() {
		return this.action;
	}

	public Text getValue() {
		return this.value;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			HoverEvent hoverEvent = (HoverEvent)obj;
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
		SHOW_TEXT("show_text", true),
		SHOW_ITEM("show_item", true),
		SHOW_ENTITY("show_entity", true);

		private static final Map<String, HoverEvent.Action> BY_NAME = (Map<String, HoverEvent.Action>)Arrays.stream(values())
			.collect(Collectors.toMap(HoverEvent.Action::getName, a -> a));
		private final boolean userDefinable;
		private final String name;

		private Action(String name, boolean userDefinable) {
			this.name = name;
			this.userDefinable = userDefinable;
		}

		public boolean isUserDefinable() {
			return this.userDefinable;
		}

		public String getName() {
			return this.name;
		}

		public static HoverEvent.Action byName(String name) {
			return (HoverEvent.Action)BY_NAME.get(name);
		}
	}
}
