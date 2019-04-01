package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_818 implements class_815 {
	private static final Splitter field_4334 = Splitter.on('|').omitEmptyStrings();
	private final String field_4333;
	private final String field_4332;

	public class_818(String string, String string2) {
		this.field_4333 = string;
		this.field_4332 = string2;
	}

	@Override
	public Predicate<class_2680> getPredicate(class_2689<class_2248, class_2680> arg) {
		class_2769<?> lv = arg.method_11663(this.field_4333);
		if (lv == null) {
			throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.field_4333, arg.method_11660().toString()));
		} else {
			String string = this.field_4332;
			boolean bl = !string.isEmpty() && string.charAt(0) == '!';
			if (bl) {
				string = string.substring(1);
			}

			List<String> list = field_4334.splitToList(string);
			if (list.isEmpty()) {
				throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.field_4332, this.field_4333, arg.method_11660().toString()));
			} else {
				Predicate<class_2680> predicate;
				if (list.size() == 1) {
					predicate = this.method_3525(arg, lv, string);
				} else {
					List<Predicate<class_2680>> list2 = (List<Predicate<class_2680>>)list.stream()
						.map(stringx -> this.method_3525(arg, lv, stringx))
						.collect(Collectors.toList());
					predicate = argx -> list2.stream().anyMatch(predicatex -> predicatex.test(argx));
				}

				return bl ? predicate.negate() : predicate;
			}
		}
	}

	private Predicate<class_2680> method_3525(class_2689<class_2248, class_2680> arg, class_2769<?> arg2, String string) {
		Optional<?> optional = arg2.method_11900(string);
		if (!optional.isPresent()) {
			throw new RuntimeException(
				String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", string, this.field_4333, arg.method_11660().toString(), this.field_4332)
			);
		} else {
			return arg2x -> arg2x.method_11654(arg2).equals(optional.get());
		}
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("key", this.field_4333).add("value", this.field_4332).toString();
	}
}
