package net.minecraft;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class class_8471 {
	private static final Logger field_44426 = LogUtils.getLogger();
	private static final class_8471.class_8473 field_44427 = new class_8471.class_8473(46, Style.EMPTY.withColor(Formatting.WHITE));
	private final Map<UUID, class_8471.class_8474> field_44428 = new HashMap();
	private final Int2ObjectMap<class_8471.class_8475> field_44429 = new Int2ObjectOpenHashMap<>();
	private int field_44430;

	private static StringVisitable method_51068(List<class_8471.class_8473> list) {
		return new StringVisitable() {
			@Override
			public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
				for (class_8471.class_8473 lv : list) {
					Optional<T> optional = visitor.accept(Character.toString(lv.codepoint));
					if (optional.isPresent()) {
						return optional;
					}
				}

				return Optional.empty();
			}

			@Override
			public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
				for (class_8471.class_8473 lv : list) {
					Optional<T> optional = styledVisitor.accept(style.withParent(lv.style), Character.toString(lv.codepoint));
					if (optional.isPresent()) {
						return optional;
					}
				}

				return Optional.empty();
			}
		};
	}

	public void method_51071(UUID uUID, class_8367 arg) {
		Text text = arg.header().displayName();
		List<List<class_8471.class_8473>> list = (List<List<class_8471.class_8473>>)arg.options()
			.values()
			.stream()
			.filter(argx -> !argx.irregular())
			.map(
				argx -> {
					List<class_8471.class_8473> listx = new ArrayList();
					Text.translatable("vote.option_display", text, argx.displayName())
						.asOrderedText()
						.accept((ix, style, jx) -> listx.add(new class_8471.class_8473(jx, style)));
					return listx;
				}
			)
			.collect(Collectors.toList());
		int i = Util.method_50030(list);
		StringVisitable stringVisitable;
		if (i == 0) {
			stringVisitable = StringVisitable.EMPTY;
		} else {
			List<class_8471.class_8473> list2 = new ArrayList((Collection)list.get(0));
			int j = i - 1;

			while (j >= 0 && Character.isSpaceChar(((class_8471.class_8473)list2.get(j)).codepoint)) {
				j--;
			}

			List<class_8471.class_8473> list3 = new ArrayList(list2.subList(0, j + 1));
			list3.add(field_44427);
			list3.add(field_44427);
			list3.add(field_44427);
			stringVisitable = method_51068(list3);
		}

		class_8471.class_8474 lv = new class_8471.class_8474(arg, stringVisitable);
		this.field_44428.put(uUID, lv);
	}

	public void method_51070(UUID uUID) {
		this.field_44428.remove(uUID);
	}

	public boolean method_51063() {
		return !this.field_44428.isEmpty();
	}

	public void method_51066(class_8373 arg, class_8375 arg2) {
		class_8471.class_8474 lv = (class_8471.class_8474)this.field_44428.get(arg.voteId());
		if (lv != null) {
			lv.method_51078(arg.index(), arg2);
		}
	}

	public void method_51072(BiConsumer<UUID, class_8471.class_8474> biConsumer) {
		this.field_44428.forEach(biConsumer);
	}

	@Nullable
	public class_8471.class_8474 method_51074(UUID uUID) {
		return (class_8471.class_8474)this.field_44428.get(uUID);
	}

	public int method_51067(class_8471.class_8475 arg) {
		int i = this.field_44430++;
		this.field_44429.put(i, arg);
		return i;
	}

	public void method_51064(int i, Optional<Text> optional) {
		class_8471.class_8475 lv = this.field_44429.remove(i);
		if (lv == null) {
			field_44426.warn("Received response for unknown vote: {}, {}", this.field_44430, optional.map(Text::getString).orElse("<none>"));
		} else {
			lv.run(i, optional);
		}
	}

	@Environment(EnvType.CLIENT)
	public static record class_8472(int count, OptionalInt limit) {
		public boolean method_51075() {
			return this.limit.isEmpty() || this.count < this.limit.getAsInt();
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_8473(int codepoint, Style style) {
	}

	@Environment(EnvType.CLIENT)
	public static class class_8474 {
		private final class_8367 field_44432;
		private final Int2ObjectMap<Map<UUID, class_8388>> field_44433 = new Int2ObjectOpenHashMap<>();
		private final StringVisitable field_44434;
		private final OptionalInt field_44435;
		private final OptionalInt field_44436;

		class_8474(class_8367 arg, StringVisitable stringVisitable) {
			this.field_44432 = arg;
			OptionalInt optionalInt = OptionalInt.empty();
			OptionalInt optionalInt2 = OptionalInt.empty();

			for (class_8390.class_8391 lv : arg.header().cost()) {
				if (lv.material() == class_8390.field_44032) {
					optionalInt2 = OptionalInt.of(lv.count());
				} else if (lv.material() == class_8390.field_44033) {
					optionalInt = OptionalInt.of(lv.count());
				}
			}

			this.field_44435 = optionalInt;
			this.field_44436 = optionalInt2;
			this.field_44434 = stringVisitable;
		}

		public void method_51078(int i, class_8375 arg) {
			this.field_44433.computeIfAbsent(i, (Int2ObjectFunction<? extends Map<UUID, class_8388>>)(ix -> new HashMap())).putAll(arg.voters());
		}

		public StringVisitable method_51076() {
			return this.field_44434;
		}

		public class_8367 method_51082() {
			return this.field_44432;
		}

		public long method_51079(long l) {
			long m = l - this.field_44432.header().start();
			return this.field_44432.header().duration() - m;
		}

		private int method_51084(UUID uUID, class_8373 arg) {
			Map<UUID, class_8388> map = this.field_44433.get(arg.index());
			if (map == null) {
				return 0;
			} else {
				class_8388 lv = (class_8388)map.get(uUID);
				return lv != null ? lv.voteCount() : 0;
			}
		}

		public class_8471.class_8472 method_51081(UUID uUID, class_8373 arg) {
			int i = this.method_51084(uUID, arg);
			return new class_8471.class_8472(i, this.field_44435);
		}

		public class_8471.class_8472 method_51080(UUID uUID) {
			int i = this.field_44432.options().keySet().stream().mapToInt(arg -> this.method_51084(uUID, arg)).sum();
			return new class_8471.class_8472(i, this.field_44436);
		}

		public boolean method_51083(UUID uUID) {
			if (!this.method_51080(uUID).method_51075()) {
				return false;
			} else {
				for (class_8373 lv : this.field_44432.options().keySet()) {
					if (this.method_51081(uUID, lv).method_51075()) {
						return true;
					}
				}

				return false;
			}
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface class_8475 {
		void run(int i, Optional<Text> optional);
	}
}
