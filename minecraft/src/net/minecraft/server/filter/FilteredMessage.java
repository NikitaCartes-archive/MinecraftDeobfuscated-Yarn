package net.minecraft.server.filter;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.class_7649;

/**
 * A message from the {@link TextFilterer}.
 */
public record FilteredMessage(String raw, class_7649 mask) {
	public static final FilteredMessage EMPTY = method_45060("");

	public static FilteredMessage method_45060(String string) {
		return new FilteredMessage(string, class_7649.field_39942);
	}

	public static FilteredMessage method_45062(String string) {
		return new FilteredMessage(string, class_7649.field_39941);
	}

	@Nullable
	public String method_45059() {
		return this.mask.method_45089(this.raw);
	}

	public String method_45061() {
		return (String)Objects.requireNonNullElse(this.method_45059(), "");
	}

	public boolean method_45063() {
		return !this.mask.method_45087();
	}
}
