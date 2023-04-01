package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class class_8314 extends class_8275<Integer, class_8314.class_8315> {
	private static final String field_43768 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String field_43769 = "!,-.0123456789?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private final Int2ObjectMap<class_8314.class_8315> field_43770 = new Int2ObjectOpenHashMap<>();

	public class_8314() {
		super(Codec.INT, class_8314.class_8315.field_43771);
	}

	@Nullable
	public class_8314.class_8315 method_50328(int i) {
		return this.field_43770.get(i);
	}

	protected Text method_50162(Integer integer, class_8314.class_8315 arg) {
		return Text.translatable(arg.field_43773, Character.toString(integer));
	}

	protected void method_50138(Integer integer, class_8314.class_8315 arg) {
		this.field_43770.put(integer.intValue(), arg);
	}

	protected void method_50136(Integer integer) {
		this.field_43770.remove(integer.intValue());
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43770.entrySet().stream().map(entry -> new class_8275.class_8276((Integer)entry.getKey(), (class_8314.class_8315)entry.getValue()));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		int j = random.nextBetween(32, 126);
		ObjectArrayList<class_8314.class_8315> objectArrayList = new ObjectArrayList<>(class_8314.class_8315.values());
		Util.shuffle(objectArrayList, random);
		if ("!,-.0123456789?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".indexOf(j) == -1) {
			objectArrayList.remove(class_8314.class_8315.ILLAGER);
		}

		if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".indexOf(j) == -1) {
			objectArrayList.remove(class_8314.class_8315.SGA);
		}

		class_8314.class_8315 lv = this.field_43770.get(j);
		if (lv != null) {
			objectArrayList.remove(lv);
		}

		return objectArrayList.stream().limit((long)i).map(arg -> new class_8275.class_8276(j, arg));
	}

	public static enum class_8315 implements StringIdentifiable {
		HIDE("hide"),
		BLANK("blank"),
		BLACK("black"),
		DARK_BLUE("dark_blue"),
		DARK_GREEN("dark_green"),
		DARK_AQUA("dark_aqua"),
		DARK_RED("dark_red"),
		DARK_PURPLE("dark_purple"),
		GOLD("gold"),
		GRAY("gray"),
		DARK_GRAY("dark_gray"),
		BLUE("blue"),
		GREEN("green"),
		AQUA("aqua"),
		RED("red"),
		LIGHT_PURPLE("light_purple"),
		YELLOW("yellow"),
		WHITE("white"),
		OBFUSCATED("obfuscated"),
		BOLD("bold"),
		STRIKETHROUGH("strikethrough"),
		UNDERLINE("underline"),
		ITALIC("italic"),
		THIN("thin"),
		SGA("sga"),
		ILLAGER("illager");

		public static final com.mojang.serialization.Codec<class_8314.class_8315> field_43771 = StringIdentifiable.createCodec(class_8314.class_8315::values);
		private final String field_43772;
		final String field_43773;

		private class_8315(String string2) {
			this.field_43772 = string2;
			this.field_43773 = "rule.text_style." + string2;
		}

		@Override
		public String asString() {
			return this.field_43772;
		}
	}
}
