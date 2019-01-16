package net.minecraft.text.event;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.text.TextComponent;

public class HoverEvent {
	private final HoverEvent.Action action;
	private final TextComponent value;

	public HoverEvent(HoverEvent.Action action, TextComponent textComponent) {
		this.action = action;
		this.value = textComponent;
	}

	public HoverEvent.Action getAction() {
		return this.action;
	}

	public TextComponent getValue() {
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
		SHOW_TEXT("show_text", true),
		SHOW_ITEM("show_item", true),
		SHOW_ENTITY("show_entity", true);

		private static final Map<String, HoverEvent.Action> field_11758 = (Map<String, HoverEvent.Action>)Arrays.stream(values())
			.collect(Collectors.toMap(HoverEvent.Action::getName, action -> action));
		private final boolean field_11759;
		private final String name;

		private Action(String string2, boolean bl) {
			this.name = string2;
			this.field_11759 = bl;
		}

		public boolean method_10895() {
			return this.field_11759;
		}

		public String getName() {
			return this.name;
		}

		public static HoverEvent.Action get(String string) {
			return (HoverEvent.Action)field_11758.get(string);
		}
	}
}
