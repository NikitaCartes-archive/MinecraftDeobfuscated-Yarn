package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_377 implements AutoCloseable {
	private static final Logger field_2255 = LogManager.getLogger();
	private static final class_384 field_2250 = new class_384();
	private static final class_379 field_2251 = () -> 4.0F;
	private static final Random field_2252 = new Random();
	private final class_1060 field_2248;
	private final class_2960 field_2246;
	private class_382 field_2256;
	private final List<class_390> field_2247 = Lists.<class_390>newArrayList();
	private final Char2ObjectMap<class_382> field_2253 = new Char2ObjectOpenHashMap<>();
	private final Char2ObjectMap<class_379> field_2257 = new Char2ObjectOpenHashMap<>();
	private final Int2ObjectMap<CharList> field_2249 = new Int2ObjectOpenHashMap<>();
	private final List<class_380> field_2254 = Lists.<class_380>newArrayList();

	public class_377(class_1060 arg, class_2960 arg2) {
		this.field_2248 = arg;
		this.field_2246 = arg2;
	}

	public void method_2004(List<class_390> list) {
		for (class_390 lv : this.field_2247) {
			lv.close();
		}

		this.field_2247.clear();
		this.method_2010();
		this.field_2254.clear();
		this.field_2253.clear();
		this.field_2257.clear();
		this.field_2249.clear();
		this.field_2256 = this.method_2012(class_385.field_2283);
		Set<class_390> set = Sets.<class_390>newHashSet();

		for (char c = 0; c < '\uffff'; c++) {
			for (class_390 lv2 : list) {
				class_379 lv3 = (class_379)(c == ' ' ? field_2251 : lv2.method_2040(c));
				if (lv3 != null) {
					set.add(lv2);
					if (lv3 != class_385.field_2283) {
						this.field_2249.computeIfAbsent(class_3532.method_15386(lv3.method_16798(false)), i -> new CharArrayList()).add(c);
					}
					break;
				}
			}
		}

		list.stream().filter(set::contains).forEach(this.field_2247::add);
	}

	public void close() {
		this.method_2010();
	}

	public void method_2010() {
		for (class_380 lv : this.field_2254) {
			lv.close();
		}
	}

	public class_379 method_2011(char c) {
		return this.field_2257.computeIfAbsent(c, i -> (class_379)(i == 32 ? field_2251 : this.method_2008((char)i)));
	}

	private class_383 method_2008(char c) {
		for (class_390 lv : this.field_2247) {
			class_383 lv2 = lv.method_2040(c);
			if (lv2 != null) {
				return lv2;
			}
		}

		return class_385.field_2283;
	}

	public class_382 method_2014(char c) {
		return this.field_2253.computeIfAbsent(c, i -> (class_382)(i == 32 ? field_2250 : this.method_2012(this.method_2008((char)i))));
	}

	private class_382 method_2012(class_383 arg) {
		for (class_380 lv : this.field_2254) {
			class_382 lv2 = lv.method_2022(arg);
			if (lv2 != null) {
				return lv2;
			}
		}

		class_380 lv3 = new class_380(
			new class_2960(this.field_2246.method_12836(), this.field_2246.method_12832() + "/" + this.field_2254.size()), arg.method_2033()
		);
		this.field_2254.add(lv3);
		this.field_2248.method_4616(lv3.method_2023(), lv3);
		class_382 lv4 = lv3.method_2022(arg);
		return lv4 == null ? this.field_2256 : lv4;
	}

	public class_382 method_2013(class_379 arg) {
		CharList charList = this.field_2249.get(class_3532.method_15386(arg.method_16798(false)));
		return charList != null && !charList.isEmpty() ? this.method_2014(charList.get(field_2252.nextInt(charList.size()))) : this.field_2256;
	}
}
