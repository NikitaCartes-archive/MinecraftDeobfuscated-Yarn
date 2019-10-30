package net.minecraft.text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickEvent {
	private final ClickEvent.Action action;
	private final String value;

	public ClickEvent(ClickEvent.Action action, String value) {
		this.action = action;
		this.value = value;
	}

	public ClickEvent.Action getAction() {
		return this.action;
	}

	public String getValue() {
		return this.value;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			ClickEvent clickEvent = (ClickEvent)obj;
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
		OPEN_URL("open_url", true),
		OPEN_FILE("open_file", false),
		RUN_COMMAND("run_command", true),
		SUGGEST_COMMAND("suggest_command", true),
		CHANGE_PAGE("change_page", true),
		COPY_TO_CLIPBOARD("copy_to_clipboard", true);

		private static final Map<String, ClickEvent.Action> BY_NAME = (Map<String, ClickEvent.Action>)Arrays.stream(values())
			.collect(Collectors.toMap(ClickEvent.Action::getName, a -> a));
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

		public static ClickEvent.Action byName(String name) {
			return (ClickEvent.Action)BY_NAME.get(name);
		}
	}
}
