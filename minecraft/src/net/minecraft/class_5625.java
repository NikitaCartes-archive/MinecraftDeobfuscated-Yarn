package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Util;

public class class_5625 implements class_5627 {
	private static final Map<String, List<String>> field_27820 = Util.make(Maps.<String, List<String>>newHashMap(), hashMap -> {
		hashMap.put("{}", Lists.newArrayList("DataVersion", "author", "size", "data", "entities", "palette", "palettes"));
		hashMap.put("{}.data.[].{}", Lists.newArrayList("pos", "state", "nbt"));
		hashMap.put("{}.entities.[].{}", Lists.newArrayList("blockPos", "pos"));
	});
	private static final Set<String> field_27821 = Sets.<String>newHashSet("{}.size.[]", "{}.data.[].{}", "{}.palette.[].{}", "{}.entities.[].{}");
	private static final Pattern field_27822 = Pattern.compile("[A-Za-z0-9._+-]+");
	private static final String field_27823 = String.valueOf(':');
	private static final String field_27824 = String.valueOf(',');
	private final String field_27825;
	private final int field_27826;
	private final List<String> field_27827;
	private String field_27828;

	public class_5625() {
		this("    ", 0, Lists.<String>newArrayList());
	}

	public class_5625(String string, int i, List<String> list) {
		this.field_27825 = string;
		this.field_27826 = i;
		this.field_27827 = list;
	}

	public String method_32283(Tag tag) {
		tag.method_32289(this);
		return this.field_27828;
	}

	@Override
	public void method_32302(StringTag stringTag) {
		this.field_27828 = StringTag.escape(stringTag.asString());
	}

	@Override
	public void method_32291(ByteTag byteTag) {
		this.field_27828 = byteTag.getNumber() + "b";
	}

	@Override
	public void method_32301(ShortTag shortTag) {
		this.field_27828 = shortTag.getNumber() + "s";
	}

	@Override
	public void method_32297(IntTag intTag) {
		this.field_27828 = String.valueOf(intTag.getNumber());
	}

	@Override
	public void method_32300(LongTag longTag) {
		this.field_27828 = longTag.getNumber() + "L";
	}

	@Override
	public void method_32295(FloatTag floatTag) {
		this.field_27828 = floatTag.getFloat() + "f";
	}

	@Override
	public void method_32293(DoubleTag doubleTag) {
		this.field_27828 = doubleTag.getDouble() + "d";
	}

	@Override
	public void method_32290(ByteArrayTag byteArrayTag) {
		StringBuilder stringBuilder = new StringBuilder("[").append("B").append(";");
		byte[] bs = byteArrayTag.getByteArray();

		for (int i = 0; i < bs.length; i++) {
			stringBuilder.append(" ").append(bs[i]).append("B");
			if (i != bs.length - 1) {
				stringBuilder.append(field_27824);
			}
		}

		stringBuilder.append("]");
		this.field_27828 = stringBuilder.toString();
	}

	@Override
	public void method_32296(IntArrayTag intArrayTag) {
		StringBuilder stringBuilder = new StringBuilder("[").append("I").append(";");
		int[] is = intArrayTag.getIntArray();

		for (int i = 0; i < is.length; i++) {
			stringBuilder.append(" ").append(is[i]);
			if (i != is.length - 1) {
				stringBuilder.append(field_27824);
			}
		}

		stringBuilder.append("]");
		this.field_27828 = stringBuilder.toString();
	}

	@Override
	public void method_32299(LongArrayTag longArrayTag) {
		String string = "L";
		StringBuilder stringBuilder = new StringBuilder("[").append("L").append(";");
		long[] ls = longArrayTag.getLongArray();

		for (int i = 0; i < ls.length; i++) {
			stringBuilder.append(" ").append(ls[i]).append("L");
			if (i != ls.length - 1) {
				stringBuilder.append(field_27824);
			}
		}

		stringBuilder.append("]");
		this.field_27828 = stringBuilder.toString();
	}

	@Override
	public void method_32298(ListTag listTag) {
		if (listTag.isEmpty()) {
			this.field_27828 = "[]";
		} else {
			StringBuilder stringBuilder = new StringBuilder("[");
			this.method_32285("[]");
			String string = field_27821.contains(this.method_32280()) ? "" : this.field_27825;
			if (!string.isEmpty()) {
				stringBuilder.append("\n");
			}

			for (int i = 0; i < listTag.size(); i++) {
				stringBuilder.append(Strings.repeat(string, this.field_27826 + 1));
				stringBuilder.append(new class_5625(string, this.field_27826 + 1, this.field_27827).method_32283(listTag.get(i)));
				if (i != listTag.size() - 1) {
					stringBuilder.append(field_27824).append(string.isEmpty() ? " " : "\n");
				}
			}

			if (!string.isEmpty()) {
				stringBuilder.append("\n").append(Strings.repeat(string, this.field_27826));
			}

			stringBuilder.append("]");
			this.field_27828 = stringBuilder.toString();
			this.method_32284();
		}
	}

	@Override
	public void method_32292(CompoundTag compoundTag) {
		if (compoundTag.isEmpty()) {
			this.field_27828 = "{}";
		} else {
			StringBuilder stringBuilder = new StringBuilder("{");
			this.method_32285("{}");
			String string = field_27821.contains(this.method_32280()) ? "" : this.field_27825;
			if (!string.isEmpty()) {
				stringBuilder.append("\n");
			}

			Collection<String> collection = this.method_32286(compoundTag);
			Iterator<String> iterator = collection.iterator();

			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				Tag tag = compoundTag.get(string2);
				this.method_32285(string2);
				stringBuilder.append(Strings.repeat(string, this.field_27826 + 1))
					.append(method_32281(string2))
					.append(field_27823)
					.append(" ")
					.append(new class_5625(string, this.field_27826 + 1, this.field_27827).method_32283(tag));
				this.method_32284();
				if (iterator.hasNext()) {
					stringBuilder.append(field_27824).append(string.isEmpty() ? " " : "\n");
				}
			}

			if (!string.isEmpty()) {
				stringBuilder.append("\n").append(Strings.repeat(string, this.field_27826));
			}

			stringBuilder.append("}");
			this.field_27828 = stringBuilder.toString();
			this.method_32284();
		}
	}

	private void method_32284() {
		this.field_27827.remove(this.field_27827.size() - 1);
	}

	private void method_32285(String string) {
		this.field_27827.add(string);
	}

	protected List<String> method_32286(CompoundTag compoundTag) {
		Set<String> set = Sets.<String>newHashSet(compoundTag.getKeys());
		List<String> list = Lists.<String>newArrayList();
		List<String> list2 = (List<String>)field_27820.get(this.method_32280());
		if (list2 != null) {
			for (String string : list2) {
				if (set.remove(string)) {
					list.add(string);
				}
			}

			if (!set.isEmpty()) {
				set.stream().sorted().forEach(list::add);
			}
		} else {
			list.addAll(set);
			Collections.sort(list);
		}

		return list;
	}

	public String method_32280() {
		return String.join(".", this.field_27827);
	}

	protected static String method_32281(String string) {
		return field_27822.matcher(string).matches() ? string : StringTag.escape(string);
	}

	@Override
	public void method_32294(EndTag endTag) {
	}
}
