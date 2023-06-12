package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;

public class OptionsKeyLwjgl3Fix extends DataFix {
	public static final String KEY_UNKNOWN = "key.unknown";
	private static final Int2ObjectMap<String> NUMERICAL_KEY_IDS_TO_KEY_NAMES = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), map -> {
		map.put(0, "key.unknown");
		map.put(11, "key.0");
		map.put(2, "key.1");
		map.put(3, "key.2");
		map.put(4, "key.3");
		map.put(5, "key.4");
		map.put(6, "key.5");
		map.put(7, "key.6");
		map.put(8, "key.7");
		map.put(9, "key.8");
		map.put(10, "key.9");
		map.put(30, "key.a");
		map.put(40, "key.apostrophe");
		map.put(48, "key.b");
		map.put(43, "key.backslash");
		map.put(14, "key.backspace");
		map.put(46, "key.c");
		map.put(58, "key.caps.lock");
		map.put(51, "key.comma");
		map.put(32, "key.d");
		map.put(211, "key.delete");
		map.put(208, "key.down");
		map.put(18, "key.e");
		map.put(207, "key.end");
		map.put(28, "key.enter");
		map.put(13, "key.equal");
		map.put(1, "key.escape");
		map.put(33, "key.f");
		map.put(59, "key.f1");
		map.put(68, "key.f10");
		map.put(87, "key.f11");
		map.put(88, "key.f12");
		map.put(100, "key.f13");
		map.put(101, "key.f14");
		map.put(102, "key.f15");
		map.put(103, "key.f16");
		map.put(104, "key.f17");
		map.put(105, "key.f18");
		map.put(113, "key.f19");
		map.put(60, "key.f2");
		map.put(61, "key.f3");
		map.put(62, "key.f4");
		map.put(63, "key.f5");
		map.put(64, "key.f6");
		map.put(65, "key.f7");
		map.put(66, "key.f8");
		map.put(67, "key.f9");
		map.put(34, "key.g");
		map.put(41, "key.grave.accent");
		map.put(35, "key.h");
		map.put(199, "key.home");
		map.put(23, "key.i");
		map.put(210, "key.insert");
		map.put(36, "key.j");
		map.put(37, "key.k");
		map.put(82, "key.keypad.0");
		map.put(79, "key.keypad.1");
		map.put(80, "key.keypad.2");
		map.put(81, "key.keypad.3");
		map.put(75, "key.keypad.4");
		map.put(76, "key.keypad.5");
		map.put(77, "key.keypad.6");
		map.put(71, "key.keypad.7");
		map.put(72, "key.keypad.8");
		map.put(73, "key.keypad.9");
		map.put(78, "key.keypad.add");
		map.put(83, "key.keypad.decimal");
		map.put(181, "key.keypad.divide");
		map.put(156, "key.keypad.enter");
		map.put(141, "key.keypad.equal");
		map.put(55, "key.keypad.multiply");
		map.put(74, "key.keypad.subtract");
		map.put(38, "key.l");
		map.put(203, "key.left");
		map.put(56, "key.left.alt");
		map.put(26, "key.left.bracket");
		map.put(29, "key.left.control");
		map.put(42, "key.left.shift");
		map.put(219, "key.left.win");
		map.put(50, "key.m");
		map.put(12, "key.minus");
		map.put(49, "key.n");
		map.put(69, "key.num.lock");
		map.put(24, "key.o");
		map.put(25, "key.p");
		map.put(209, "key.page.down");
		map.put(201, "key.page.up");
		map.put(197, "key.pause");
		map.put(52, "key.period");
		map.put(183, "key.print.screen");
		map.put(16, "key.q");
		map.put(19, "key.r");
		map.put(205, "key.right");
		map.put(184, "key.right.alt");
		map.put(27, "key.right.bracket");
		map.put(157, "key.right.control");
		map.put(54, "key.right.shift");
		map.put(220, "key.right.win");
		map.put(31, "key.s");
		map.put(70, "key.scroll.lock");
		map.put(39, "key.semicolon");
		map.put(53, "key.slash");
		map.put(57, "key.space");
		map.put(20, "key.t");
		map.put(15, "key.tab");
		map.put(22, "key.u");
		map.put(200, "key.up");
		map.put(47, "key.v");
		map.put(17, "key.w");
		map.put(45, "key.x");
		map.put(21, "key.y");
		map.put(44, "key.z");
	});

	public OptionsKeyLwjgl3Fix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsKeyLwjgl3Fix",
			this.getInputSchema().getType(TypeReferences.OPTIONS),
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> (Dynamic)dynamic.getMapValues()
							.map(map -> dynamic.createMap((Map<? extends Dynamic<?>, ? extends Dynamic<?>>)map.entrySet().stream().map(entry -> {
									if (((Dynamic)entry.getKey()).asString("").startsWith("key_")) {
										int i = Integer.parseInt(((Dynamic)entry.getValue()).asString(""));
										if (i < 0) {
											int j = i + 100;
											String string;
											if (j == 0) {
												string = "key.mouse.left";
											} else if (j == 1) {
												string = "key.mouse.right";
											} else if (j == 2) {
												string = "key.mouse.middle";
											} else {
												string = "key.mouse." + (j + 1);
											}

											return Pair.of((Dynamic)entry.getKey(), ((Dynamic)entry.getValue()).createString(string));
										} else {
											String string2 = NUMERICAL_KEY_IDS_TO_KEY_NAMES.getOrDefault(i, "key.unknown");
											return Pair.of((Dynamic)entry.getKey(), ((Dynamic)entry.getValue()).createString(string2));
										}
									} else {
										return Pair.of((Dynamic)entry.getKey(), (Dynamic)entry.getValue());
									}
								}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))
							.result()
							.orElse(dynamic)
				)
		);
	}
}
