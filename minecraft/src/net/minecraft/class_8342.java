package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.ArrayUtils;

public class class_8342 extends class_8266.class_8268 {
	private static final List<String> field_43913 = List.of(
		"Super", "Hyper", "Opti", "Extra", "Extreme", "Incredible", "Beyond", "Ultra", "Atomic", "Warp", "Performance", "Realistic", "Future", "Quantum", "Quad"
	);
	private static final Int2ObjectMap<String> field_43914 = new Int2ObjectArrayMap<>();

	public class_8342() {
		super(0);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		int[] is = IntStream.range(-3, i + 5).filter(ix -> ix == 0).toArray();
		ArrayUtils.shuffle(is);
		return IntStream.of(is).limit((long)i).mapToObj(ix -> this.method_50125(ix));
	}

	@Override
	public Text method_50133(int i) {
		return Text.translatable("rule.optimize.set", method_50408(i));
	}

	@Override
	public Text method_50132(int i, int j) {
		return Text.translatable("rule.optimize.change", method_50408(i), method_50408(j));
	}

	private static String method_50409(int i) {
		if (i < 0) {
			return "Abysmal";
		} else if (i == Integer.MAX_VALUE) {
			return "L0L N00B";
		} else {
			Random random = Random.create((long)i * 2L);
			List<String> list = new ArrayList(field_43913);
			StringBuilder stringBuilder = new StringBuilder();
			int j = Math.min(Math.max(MathHelper.floorLog2(i) - 1, 1), list.size());

			for (int k = 0; k < j; k++) {
				int l = random.nextInt(list.size());
				String string = (String)list.remove(l);
				stringBuilder.append(string);
			}

			return stringBuilder.append(" ").append(i + 1).append("000").toString();
		}
	}

	public static String method_50408(int i) {
		return field_43914.computeIfAbsent(i, class_8342::method_50409);
	}
}
