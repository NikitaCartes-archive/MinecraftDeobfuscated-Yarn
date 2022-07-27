package net.minecraft.server.filter;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.message.FilterMask;

/**
 * A message from the {@link TextFilterer}.
 */
public record FilteredMessage(String raw, FilterMask mask) {
	public static final FilteredMessage EMPTY = permitted("");

	public static FilteredMessage permitted(String raw) {
		return new FilteredMessage(raw, FilterMask.PASS_THROUGH);
	}

	public static FilteredMessage censored(String raw) {
		return new FilteredMessage(raw, FilterMask.FULLY_FILTERED);
	}

	@Nullable
	public String filter() {
		return this.mask.filter(this.raw);
	}

	public String getString() {
		return (String)Objects.requireNonNullElse(this.filter(), "");
	}

	public boolean isFiltered() {
		return !this.mask.isPassThrough();
	}
}
