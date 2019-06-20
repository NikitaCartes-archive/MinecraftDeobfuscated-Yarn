package net.minecraft;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class class_1799 {
	private static final Logger field_8033 = LogManager.getLogger();
	public static final class_1799 field_8037 = new class_1799((class_1792)null);
	public static final DecimalFormat field_8029 = method_7931();
	private int field_8031;
	private int field_8030;
	@Deprecated
	private final class_1792 field_8038;
	private class_2487 field_8040;
	private boolean field_8036;
	private class_1533 field_8041;
	private class_2694 field_8039;
	private boolean field_8035;
	private class_2694 field_8032;
	private boolean field_8034;

	private static DecimalFormat method_7931() {
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
		return decimalFormat;
	}

	public class_1799(class_1935 arg) {
		this(arg, 1);
	}

	public class_1799(class_1935 arg, int i) {
		this.field_8038 = arg == null ? null : arg.method_8389();
		this.field_8031 = i;
		this.method_7957();
	}

	private void method_7957() {
		this.field_8036 = false;
		this.field_8036 = this.method_7960();
	}

	private class_1799(class_2487 arg) {
		this.field_8038 = class_2378.field_11142.method_10223(new class_2960(arg.method_10558("id")));
		this.field_8031 = arg.method_10571("Count");
		if (arg.method_10573("tag", 10)) {
			this.field_8040 = arg.method_10562("tag");
			this.method_7909().method_7860(arg);
		}

		if (this.method_7909().method_7846()) {
			this.method_7974(this.method_7919());
		}

		this.method_7957();
	}

	public static class_1799 method_7915(class_2487 arg) {
		try {
			return new class_1799(arg);
		} catch (RuntimeException var2) {
			field_8033.debug("Tried to load invalid item: {}", arg, var2);
			return field_8037;
		}
	}

	public boolean method_7960() {
		if (this == field_8037) {
			return true;
		} else {
			return this.method_7909() == null || this.method_7909() == class_1802.field_8162 ? true : this.field_8031 <= 0;
		}
	}

	public class_1799 method_7971(int i) {
		int j = Math.min(i, this.field_8031);
		class_1799 lv = this.method_7972();
		lv.method_7939(j);
		this.method_7934(j);
		return lv;
	}

	public class_1792 method_7909() {
		return this.field_8036 ? class_1802.field_8162 : this.field_8038;
	}

	public class_1269 method_7981(class_1838 arg) {
		class_1657 lv = arg.method_8036();
		class_2338 lv2 = arg.method_8037();
		class_2694 lv3 = new class_2694(arg.method_8045(), lv2, false);
		if (lv != null && !lv.field_7503.field_7476 && !this.method_7944(arg.method_8045().method_8514(), lv3)) {
			return class_1269.field_5811;
		} else {
			class_1792 lv4 = this.method_7909();
			class_1269 lv5 = lv4.method_7884(arg);
			if (lv != null && lv5 == class_1269.field_5812) {
				lv.method_7259(class_3468.field_15372.method_14956(lv4));
			}

			return lv5;
		}
	}

	public float method_7924(class_2680 arg) {
		return this.method_7909().method_7865(this, arg);
	}

	public class_1271<class_1799> method_7913(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		return this.method_7909().method_7836(arg, arg2, arg3);
	}

	public class_1799 method_7910(class_1937 arg, class_1309 arg2) {
		return this.method_7909().method_7861(this, arg, arg2);
	}

	public class_2487 method_7953(class_2487 arg) {
		class_2960 lv = class_2378.field_11142.method_10221(this.method_7909());
		arg.method_10582("id", lv == null ? "minecraft:air" : lv.toString());
		arg.method_10567("Count", (byte)this.field_8031);
		if (this.field_8040 != null) {
			arg.method_10566("tag", this.field_8040);
		}

		return arg;
	}

	public int method_7914() {
		return this.method_7909().method_7882();
	}

	public boolean method_7946() {
		return this.method_7914() > 1 && (!this.method_7963() || !this.method_7986());
	}

	public boolean method_7963() {
		if (!this.field_8036 && this.method_7909().method_7841() > 0) {
			class_2487 lv = this.method_7969();
			return lv == null || !lv.method_10577("Unbreakable");
		} else {
			return false;
		}
	}

	public boolean method_7986() {
		return this.method_7963() && this.method_7919() > 0;
	}

	public int method_7919() {
		return this.field_8040 == null ? 0 : this.field_8040.method_10550("Damage");
	}

	public void method_7974(int i) {
		this.method_7948().method_10569("Damage", Math.max(0, i));
	}

	public int method_7936() {
		return this.method_7909().method_7841();
	}

	public boolean method_7970(int i, Random random, @Nullable class_3222 arg) {
		if (!this.method_7963()) {
			return false;
		} else {
			if (i > 0) {
				int j = class_1890.method_8225(class_1893.field_9119, this);
				int k = 0;

				for (int l = 0; j > 0 && l < i; l++) {
					if (class_1885.method_8176(this, j, random)) {
						k++;
					}
				}

				i -= k;
				if (i <= 0) {
					return false;
				}
			}

			if (arg != null && i != 0) {
				class_174.field_1185.method_8960(arg, this, this.method_7919() + i);
			}

			int j = this.method_7919() + i;
			this.method_7974(j);
			return j >= this.method_7936();
		}
	}

	public <T extends class_1309> void method_7956(int i, T arg, Consumer<T> consumer) {
		if (!arg.field_6002.field_9236 && (!(arg instanceof class_1657) || !((class_1657)arg).field_7503.field_7477)) {
			if (this.method_7963()) {
				if (this.method_7970(i, arg.method_6051(), arg instanceof class_3222 ? (class_3222)arg : null)) {
					consumer.accept(arg);
					class_1792 lv = this.method_7909();
					this.method_7934(1);
					if (arg instanceof class_1657) {
						((class_1657)arg).method_7259(class_3468.field_15383.method_14956(lv));
					}

					this.method_7974(0);
				}
			}
		}
	}

	public void method_7979(class_1309 arg, class_1657 arg2) {
		class_1792 lv = this.method_7909();
		if (lv.method_7873(this, arg, arg2)) {
			arg2.method_7259(class_3468.field_15372.method_14956(lv));
		}
	}

	public void method_7952(class_1937 arg, class_2680 arg2, class_2338 arg3, class_1657 arg4) {
		class_1792 lv = this.method_7909();
		if (lv.method_7879(this, arg, arg2, arg3, arg4)) {
			arg4.method_7259(class_3468.field_15372.method_14956(lv));
		}
	}

	public boolean method_7951(class_2680 arg) {
		return this.method_7909().method_7856(arg);
	}

	public boolean method_7920(class_1657 arg, class_1309 arg2, class_1268 arg3) {
		return this.method_7909().method_7847(this, arg, arg2, arg3);
	}

	public class_1799 method_7972() {
		class_1799 lv = new class_1799(this.method_7909(), this.field_8031);
		lv.method_7912(this.method_7965());
		if (this.field_8040 != null) {
			lv.field_8040 = this.field_8040.method_10553();
		}

		return lv;
	}

	public static boolean method_7975(class_1799 arg, class_1799 arg2) {
		if (arg.method_7960() && arg2.method_7960()) {
			return true;
		} else if (arg.method_7960() || arg2.method_7960()) {
			return false;
		} else {
			return arg.field_8040 == null && arg2.field_8040 != null ? false : arg.field_8040 == null || arg.field_8040.equals(arg2.field_8040);
		}
	}

	public static boolean method_7973(class_1799 arg, class_1799 arg2) {
		if (arg.method_7960() && arg2.method_7960()) {
			return true;
		} else {
			return !arg.method_7960() && !arg2.method_7960() ? arg.method_7968(arg2) : false;
		}
	}

	private boolean method_7968(class_1799 arg) {
		if (this.field_8031 != arg.field_8031) {
			return false;
		} else if (this.method_7909() != arg.method_7909()) {
			return false;
		} else {
			return this.field_8040 == null && arg.field_8040 != null ? false : this.field_8040 == null || this.field_8040.equals(arg.field_8040);
		}
	}

	public static boolean method_7984(class_1799 arg, class_1799 arg2) {
		if (arg == arg2) {
			return true;
		} else {
			return !arg.method_7960() && !arg2.method_7960() ? arg.method_7962(arg2) : false;
		}
	}

	public static boolean method_7987(class_1799 arg, class_1799 arg2) {
		if (arg == arg2) {
			return true;
		} else {
			return !arg.method_7960() && !arg2.method_7960() ? arg.method_7929(arg2) : false;
		}
	}

	public boolean method_7962(class_1799 arg) {
		return !arg.method_7960() && this.method_7909() == arg.method_7909();
	}

	public boolean method_7929(class_1799 arg) {
		return !this.method_7963() ? this.method_7962(arg) : !arg.method_7960() && this.method_7909() == arg.method_7909();
	}

	public String method_7922() {
		return this.method_7909().method_7866(this);
	}

	public String toString() {
		return this.field_8031 + " " + this.method_7909();
	}

	public void method_7917(class_1937 arg, class_1297 arg2, int i, boolean bl) {
		if (this.field_8030 > 0) {
			this.field_8030--;
		}

		if (this.method_7909() != null) {
			this.method_7909().method_7888(this, arg, arg2, i, bl);
		}
	}

	public void method_7982(class_1937 arg, class_1657 arg2, int i) {
		arg2.method_7342(class_3468.field_15370.method_14956(this.method_7909()), i);
		this.method_7909().method_7843(this, arg, arg2);
	}

	public int method_7935() {
		return this.method_7909().method_7881(this);
	}

	public class_1839 method_7976() {
		return this.method_7909().method_7853(this);
	}

	public void method_7930(class_1937 arg, class_1309 arg2, int i) {
		this.method_7909().method_7840(this, arg, arg2, i);
	}

	public boolean method_7967() {
		return this.method_7909().method_7838(this);
	}

	public boolean method_7985() {
		return !this.field_8036 && this.field_8040 != null && !this.field_8040.isEmpty();
	}

	@Nullable
	public class_2487 method_7969() {
		return this.field_8040;
	}

	public class_2487 method_7948() {
		if (this.field_8040 == null) {
			this.method_7980(new class_2487());
		}

		return this.field_8040;
	}

	public class_2487 method_7911(String string) {
		if (this.field_8040 != null && this.field_8040.method_10573(string, 10)) {
			return this.field_8040.method_10562(string);
		} else {
			class_2487 lv = new class_2487();
			this.method_7959(string, lv);
			return lv;
		}
	}

	@Nullable
	public class_2487 method_7941(String string) {
		return this.field_8040 != null && this.field_8040.method_10573(string, 10) ? this.field_8040.method_10562(string) : null;
	}

	public void method_7983(String string) {
		if (this.field_8040 != null && this.field_8040.method_10545(string)) {
			this.field_8040.method_10551(string);
			if (this.field_8040.isEmpty()) {
				this.field_8040 = null;
			}
		}
	}

	public class_2499 method_7921() {
		return this.field_8040 != null ? this.field_8040.method_10554("Enchantments", 10) : new class_2499();
	}

	public void method_7980(@Nullable class_2487 arg) {
		this.field_8040 = arg;
	}

	public class_2561 method_7964() {
		class_2487 lv = this.method_7941("display");
		if (lv != null && lv.method_10573("Name", 8)) {
			try {
				class_2561 lv2 = class_2561.class_2562.method_10877(lv.method_10558("Name"));
				if (lv2 != null) {
					return lv2;
				}

				lv.method_10551("Name");
			} catch (JsonParseException var3) {
				lv.method_10551("Name");
			}
		}

		return this.method_7909().method_7864(this);
	}

	public class_1799 method_7977(@Nullable class_2561 arg) {
		class_2487 lv = this.method_7911("display");
		if (arg != null) {
			lv.method_10582("Name", class_2561.class_2562.method_10867(arg));
		} else {
			lv.method_10551("Name");
		}

		return this;
	}

	public void method_7925() {
		class_2487 lv = this.method_7941("display");
		if (lv != null) {
			lv.method_10551("Name");
			if (lv.isEmpty()) {
				this.method_7983("display");
			}
		}

		if (this.field_8040 != null && this.field_8040.isEmpty()) {
			this.field_8040 = null;
		}
	}

	public boolean method_7938() {
		class_2487 lv = this.method_7941("display");
		return lv != null && lv.method_10573("Name", 8);
	}

	@Environment(EnvType.CLIENT)
	public List<class_2561> method_7950(@Nullable class_1657 arg, class_1836 arg2) {
		List<class_2561> list = Lists.<class_2561>newArrayList();
		class_2561 lv = new class_2585("").method_10852(this.method_7964()).method_10854(this.method_7932().field_8908);
		if (this.method_7938()) {
			lv.method_10854(class_124.field_1056);
		}

		list.add(lv);
		if (!arg2.method_8035() && !this.method_7938() && this.method_7909() == class_1802.field_8204) {
			list.add(new class_2585("#" + class_1806.method_8003(this)).method_10854(class_124.field_1080));
		}

		int i = 0;
		if (this.method_7985() && this.field_8040.method_10573("HideFlags", 99)) {
			i = this.field_8040.method_10550("HideFlags");
		}

		if ((i & 32) == 0) {
			this.method_7909().method_7851(this, arg == null ? null : arg.field_6002, list, arg2);
		}

		if (this.method_7985()) {
			if ((i & 1) == 0) {
				method_17870(list, this.method_7921());
			}

			if (this.field_8040.method_10573("display", 10)) {
				class_2487 lv2 = this.field_8040.method_10562("display");
				if (lv2.method_10573("color", 3)) {
					if (arg2.method_8035()) {
						list.add(new class_2588("item.color", String.format("#%06X", lv2.method_10550("color"))).method_10854(class_124.field_1080));
					} else {
						list.add(new class_2588("item.dyed").method_10856(new class_124[]{class_124.field_1080, class_124.field_1056}));
					}
				}

				if (lv2.method_10540("Lore") == 9) {
					class_2499 lv3 = lv2.method_10554("Lore", 8);

					for (int j = 0; j < lv3.size(); j++) {
						String string = lv3.method_10608(j);

						try {
							class_2561 lv4 = class_2561.class_2562.method_10877(string);
							if (lv4 != null) {
								list.add(class_2564.method_10889(lv4, new class_2583().method_10977(class_124.field_1064).method_10978(true)));
							}
						} catch (JsonParseException var19) {
							lv2.method_10551("Lore");
						}
					}
				}
			}
		}

		for (class_1304 lv5 : class_1304.values()) {
			Multimap<String, class_1322> multimap = this.method_7926(lv5);
			if (!multimap.isEmpty() && (i & 2) == 0) {
				list.add(new class_2585(""));
				list.add(new class_2588("item.modifiers." + lv5.method_5923()).method_10854(class_124.field_1080));

				for (Entry<String, class_1322> entry : multimap.entries()) {
					class_1322 lv6 = (class_1322)entry.getValue();
					double d = lv6.method_6186();
					boolean bl = false;
					if (arg != null) {
						if (lv6.method_6189() == class_5117.field_23651) {
							d += arg.method_5996(class_1612.field_7363).method_6201();
							d += (double)class_1890.method_8218(this, class_1310.field_6290);
							bl = true;
						} else if (lv6.method_6189() == class_5117.field_23652) {
							d += arg.method_5996(class_1612.field_7356).method_6201() - 1.5;
							bl = true;
						} else if (lv6.method_6189() == class_5117.field_23653) {
							d += arg.method_5996(class_1612.field_23644).method_6201();
							bl = true;
						}
					}

					double e;
					if (lv6.method_6182() != class_1322.class_1323.field_6330 && lv6.method_6182() != class_1322.class_1323.field_6331) {
						e = d;
					} else {
						e = d * 100.0;
					}

					if (bl) {
						list.add(
							new class_2585(" ")
								.method_10852(
									new class_2588(
										"attribute.modifier.equals." + lv6.method_6182().method_6191(), field_8029.format(e), new class_2588("attribute.name." + (String)entry.getKey())
									)
								)
								.method_10854(class_124.field_1077)
						);
					} else if (d > 0.0) {
						list.add(
							new class_2588(
									"attribute.modifier.plus." + lv6.method_6182().method_6191(), field_8029.format(e), new class_2588("attribute.name." + (String)entry.getKey())
								)
								.method_10854(class_124.field_1078)
						);
					} else if (d < 0.0) {
						e *= -1.0;
						list.add(
							new class_2588(
									"attribute.modifier.take." + lv6.method_6182().method_6191(), field_8029.format(e), new class_2588("attribute.name." + (String)entry.getKey())
								)
								.method_10854(class_124.field_1061)
						);
					}
				}
			}
		}

		if (this.method_7985() && this.method_7969().method_10577("Unbreakable") && (i & 4) == 0) {
			list.add(new class_2588("item.unbreakable").method_10854(class_124.field_1078));
		}

		if (this.method_7985() && this.field_8040.method_10573("CanDestroy", 9) && (i & 8) == 0) {
			class_2499 lv7 = this.field_8040.method_10554("CanDestroy", 8);
			if (!lv7.isEmpty()) {
				list.add(new class_2585(""));
				list.add(new class_2588("item.canBreak").method_10854(class_124.field_1080));

				for (int k = 0; k < lv7.size(); k++) {
					list.addAll(method_7937(lv7.method_10608(k)));
				}
			}
		}

		if (this.method_7985() && this.field_8040.method_10573("CanPlaceOn", 9) && (i & 16) == 0) {
			class_2499 lv7 = this.field_8040.method_10554("CanPlaceOn", 8);
			if (!lv7.isEmpty()) {
				list.add(new class_2585(""));
				list.add(new class_2588("item.canPlace").method_10854(class_124.field_1080));

				for (int k = 0; k < lv7.size(); k++) {
					list.addAll(method_7937(lv7.method_10608(k)));
				}
			}
		}

		if (arg2.method_8035()) {
			if (this.method_7986()) {
				list.add(new class_2588("item.durability", this.method_7936() - this.method_7919(), this.method_7936()));
			}

			list.add(new class_2585(class_2378.field_11142.method_10221(this.method_7909()).toString()).method_10854(class_124.field_1063));
			if (this.method_7985()) {
				list.add(new class_2588("item.nbt_tags", this.method_7969().method_10541().size()).method_10854(class_124.field_1063));
			}
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public static void method_17870(List<class_2561> list, class_2499 arg) {
		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			class_2378.field_11160.method_17966(class_2960.method_12829(lv.method_10558("id"))).ifPresent(arg2 -> list.add(arg2.method_8179(lv.method_10550("lvl"))));
		}
	}

	@Environment(EnvType.CLIENT)
	private static Collection<class_2561> method_7937(String string) {
		try {
			class_2259 lv = new class_2259(new StringReader(string), true).method_9678(true);
			class_2680 lv2 = lv.method_9669();
			class_2960 lv3 = lv.method_9664();
			boolean bl = lv2 != null;
			boolean bl2 = lv3 != null;
			if (bl || bl2) {
				if (bl) {
					return Lists.<class_2561>newArrayList(lv2.method_11614().method_9518().method_10854(class_124.field_1063));
				}

				class_3494<class_2248> lv4 = class_3481.method_15073().method_15193(lv3);
				if (lv4 != null) {
					Collection<class_2248> collection = lv4.method_15138();
					if (!collection.isEmpty()) {
						return (Collection<class_2561>)collection.stream()
							.map(class_2248::method_9518)
							.map(arg -> arg.method_10854(class_124.field_1063))
							.collect(Collectors.toList());
					}
				}
			}
		} catch (CommandSyntaxException var8) {
		}

		return Lists.<class_2561>newArrayList(new class_2585("missingno").method_10854(class_124.field_1063));
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7958() {
		return this.method_7909().method_7886(this);
	}

	public class_1814 method_7932() {
		return this.method_7909().method_7862(this);
	}

	public boolean method_7923() {
		return !this.method_7909().method_7870(this) ? false : !this.method_7942();
	}

	public void method_7978(class_1887 arg, int i) {
		this.method_7948();
		if (!this.field_8040.method_10573("Enchantments", 9)) {
			this.field_8040.method_10566("Enchantments", new class_2499());
		}

		class_2499 lv = this.field_8040.method_10554("Enchantments", 10);
		class_2487 lv2 = new class_2487();
		lv2.method_10582("id", String.valueOf(class_2378.field_11160.method_10221(arg)));
		lv2.method_10575("lvl", (short)((byte)i));
		lv.add(lv2);
	}

	public boolean method_7942() {
		return this.field_8040 != null && this.field_8040.method_10573("Enchantments", 9) ? !this.field_8040.method_10554("Enchantments", 10).isEmpty() : false;
	}

	public void method_7959(String string, class_2520 arg) {
		this.method_7948().method_10566(string, arg);
	}

	public boolean method_7961() {
		return this.field_8041 != null;
	}

	public void method_7943(@Nullable class_1533 arg) {
		this.field_8041 = arg;
	}

	@Nullable
	public class_1533 method_7945() {
		return this.field_8036 ? null : this.field_8041;
	}

	public int method_7928() {
		return this.method_7985() && this.field_8040.method_10573("RepairCost", 3) ? this.field_8040.method_10550("RepairCost") : 0;
	}

	public void method_7927(int i) {
		this.method_7948().method_10569("RepairCost", i);
	}

	public Multimap<String, class_1322> method_7926(class_1304 arg) {
		Multimap<String, class_1322> multimap;
		if (this.method_7985() && this.field_8040.method_10573("AttributeModifiers", 9)) {
			multimap = HashMultimap.create();
			class_2499 lv = this.field_8040.method_10554("AttributeModifiers", 10);

			for (int i = 0; i < lv.size(); i++) {
				class_2487 lv2 = lv.method_10602(i);
				class_1322 lv3 = class_1612.method_7133(lv2);
				if (lv3 != null
					&& (!lv2.method_10573("Slot", 8) || lv2.method_10558("Slot").equals(arg.method_5923()))
					&& lv3.method_6189().getLeastSignificantBits() != 0L
					&& lv3.method_6189().getMostSignificantBits() != 0L) {
					multimap.put(lv2.method_10558("AttributeName"), lv3);
				}
			}
		} else {
			multimap = this.method_7909().method_7844(arg);
		}

		return multimap;
	}

	public void method_7916(String string, class_1322 arg, @Nullable class_1304 arg2) {
		this.method_7948();
		if (!this.field_8040.method_10573("AttributeModifiers", 9)) {
			this.field_8040.method_10566("AttributeModifiers", new class_2499());
		}

		class_2499 lv = this.field_8040.method_10554("AttributeModifiers", 10);
		class_2487 lv2 = class_1612.method_7135(arg);
		lv2.method_10582("AttributeName", string);
		if (arg2 != null) {
			lv2.method_10582("Slot", arg2.method_5923());
		}

		lv.add(lv2);
	}

	public class_2561 method_7954() {
		class_2561 lv = new class_2585("").method_10852(this.method_7964());
		if (this.method_7938()) {
			lv.method_10854(class_124.field_1056);
		}

		class_2561 lv2 = class_2564.method_10885(lv);
		if (!this.field_8036) {
			class_2487 lv3 = this.method_7953(new class_2487());
			lv2.method_10854(this.method_7932().field_8908)
				.method_10859(arg2 -> arg2.method_10949(new class_2568(class_2568.class_2569.field_11757, new class_2585(lv3.toString()))));
		}

		return lv2;
	}

	private static boolean method_7918(class_2694 arg, @Nullable class_2694 arg2) {
		if (arg2 == null || arg.method_11681() != arg2.method_11681()) {
			return false;
		} else if (arg.method_11680() == null && arg2.method_11680() == null) {
			return true;
		} else {
			return arg.method_11680() != null && arg2.method_11680() != null
				? Objects.equals(arg.method_11680().method_11007(new class_2487()), arg2.method_11680().method_11007(new class_2487()))
				: false;
		}
	}

	public boolean method_7940(class_3505 arg, class_2694 arg2) {
		if (method_7918(arg2, this.field_8039)) {
			return this.field_8035;
		} else {
			this.field_8039 = arg2;
			if (this.method_7985() && this.field_8040.method_10573("CanDestroy", 9)) {
				class_2499 lv = this.field_8040.method_10554("CanDestroy", 8);

				for (int i = 0; i < lv.size(); i++) {
					String string = lv.method_10608(i);

					try {
						Predicate<class_2694> predicate = class_2252.method_9645().method_9642(new StringReader(string)).create(arg);
						if (predicate.test(arg2)) {
							this.field_8035 = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.field_8035 = false;
			return false;
		}
	}

	public boolean method_7944(class_3505 arg, class_2694 arg2) {
		if (method_7918(arg2, this.field_8032)) {
			return this.field_8034;
		} else {
			this.field_8032 = arg2;
			if (this.method_7985() && this.field_8040.method_10573("CanPlaceOn", 9)) {
				class_2499 lv = this.field_8040.method_10554("CanPlaceOn", 8);

				for (int i = 0; i < lv.size(); i++) {
					String string = lv.method_10608(i);

					try {
						Predicate<class_2694> predicate = class_2252.method_9645().method_9642(new StringReader(string)).create(arg);
						if (predicate.test(arg2)) {
							this.field_8034 = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.field_8034 = false;
			return false;
		}
	}

	public int method_7965() {
		return this.field_8030;
	}

	public void method_7912(int i) {
		this.field_8030 = i;
	}

	public int method_7947() {
		return this.field_8036 ? 0 : this.field_8031;
	}

	public void method_7939(int i) {
		this.field_8031 = i;
		this.method_7957();
	}

	public void method_7933(int i) {
		this.method_7939(this.field_8031 + i);
	}

	public void method_7934(int i) {
		this.method_7933(-i);
	}

	public void method_7949(class_1937 arg, class_1309 arg2, int i) {
		this.method_7909().method_7852(arg, arg2, this, i);
	}

	public boolean method_19267() {
		return this.method_7909().method_19263();
	}
}
