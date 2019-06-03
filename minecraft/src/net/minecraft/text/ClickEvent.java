package net.minecraft.text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickEvent {
	private final ClickEvent.Action action;
	private final String value;

	public ClickEvent(ClickEvent.Action action, String string) {
		this.action = action;
		this.value = string;
	}

	public ClickEvent.Action getAction() {
		return this.action;
	}

	public String getValue() {
		return this.value;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			ClickEvent clickEvent = (ClickEvent)object;
			if (this.action != clickEvent.action) {
				return false;
			} else {
				return this.value != null ? this.value.equals(clickEvent.value) : clickEvent.value == null;
			}
		} else {
			return false;
		}
	}

	public String toString() {
		return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
	}

	public int hashCode() {
		int i = this.action.hashCode();
		return 31 * i + (this.value != null ? this.value.hashCode() : 0);
	}

	public static enum Action {
		field_11749("open_url", true),
		field_11746("open_file", false),
		field_11750("run_command", true),
		field_11745("suggest_command", true),
		field_11748("change_page", true);

		private static final Map<String, ClickEvent.Action> BY_NAME = (Map<String, ClickEvent.Action>)Arrays.stream(values())
			.collect(Collectors.toMap(ClickEvent.Action::getName, action -> action));
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

		public static ClickEvent.Action byName(String string) {
			return (ClickEvent.Action)BY_NAME.get(string);
		}
	}
}
