package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;

public class ClickEvent {
	public static final Codec<ClickEvent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(ClickEvent.Action.CODEC.forGetter(event -> event.action), Codec.STRING.fieldOf("value").forGetter(event -> event.value))
				.apply(instance, ClickEvent::new)
	);
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

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			ClickEvent clickEvent = (ClickEvent)o;
			return this.action == clickEvent.action && this.value.equals(clickEvent.value);
		} else {
			return false;
		}
	}

	public String toString() {
		return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
	}

	public int hashCode() {
		int i = this.action.hashCode();
		return 31 * i + this.value.hashCode();
	}

	public static enum Action implements StringIdentifiable {
		OPEN_URL("open_url", true),
		OPEN_FILE("open_file", false),
		RUN_COMMAND("run_command", true),
		SUGGEST_COMMAND("suggest_command", true),
		CHANGE_PAGE("change_page", true),
		COPY_TO_CLIPBOARD("copy_to_clipboard", true);

		public static final MapCodec<ClickEvent.Action> UNVALIDATED_CODEC = StringIdentifiable.createCodec(ClickEvent.Action::values).fieldOf("action");
		public static final MapCodec<ClickEvent.Action> CODEC = UNVALIDATED_CODEC.validate(ClickEvent.Action::validate);
		private final boolean userDefinable;
		private final String name;

		private Action(final String name, final boolean userDefinable) {
			this.name = name;
			this.userDefinable = userDefinable;
		}

		public boolean isUserDefinable() {
			return this.userDefinable;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public static DataResult<ClickEvent.Action> validate(ClickEvent.Action action) {
			return !action.isUserDefinable() ? DataResult.error(() -> "Action not allowed: " + action) : DataResult.success(action, Lifecycle.stable());
		}
	}
}
