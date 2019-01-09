package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2487 implements class_2520 {
	private static final Logger field_11514 = LogManager.getLogger();
	private static final Pattern field_11516 = Pattern.compile("[A-Za-z0-9._+-]+");
	private final Map<String, class_2520> field_11515 = Maps.<String, class_2520>newHashMap();

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		for (String string : this.field_11515.keySet()) {
			class_2520 lv = (class_2520)this.field_11515.get(string);
			method_10555(string, lv, dataOutput);
		}

		dataOutput.writeByte(0);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(384L);
		if (i > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		} else {
			this.field_11515.clear();

			byte b;
			while ((b = method_10542(dataInput, arg)) != 0) {
				String string = method_10552(dataInput, arg);
				arg.method_10623((long)(224 + 16 * string.length()));
				class_2520 lv = method_10581(b, string, dataInput, i + 1, arg);
				if (this.field_11515.put(string, lv) != null) {
					arg.method_10623(288L);
				}
			}
		}
	}

	public Set<String> method_10541() {
		return this.field_11515.keySet();
	}

	@Override
	public byte method_10711() {
		return 10;
	}

	public int method_10546() {
		return this.field_11515.size();
	}

	public void method_10566(String string, class_2520 arg) {
		this.field_11515.put(string, arg);
	}

	public void method_10567(String string, byte b) {
		this.field_11515.put(string, new class_2481(b));
	}

	public void method_10575(String string, short s) {
		this.field_11515.put(string, new class_2516(s));
	}

	public void method_10569(String string, int i) {
		this.field_11515.put(string, new class_2497(i));
	}

	public void method_10544(String string, long l) {
		this.field_11515.put(string, new class_2503(l));
	}

	public void method_10560(String string, UUID uUID) {
		this.method_10544(string + "Most", uUID.getMostSignificantBits());
		this.method_10544(string + "Least", uUID.getLeastSignificantBits());
	}

	public UUID method_10584(String string) {
		return new UUID(this.method_10537(string + "Most"), this.method_10537(string + "Least"));
	}

	public boolean method_10576(String string) {
		return this.method_10573(string + "Most", 99) && this.method_10573(string + "Least", 99);
	}

	public void method_10548(String string, float f) {
		this.field_11515.put(string, new class_2494(f));
	}

	public void method_10549(String string, double d) {
		this.field_11515.put(string, new class_2489(d));
	}

	public void method_10582(String string, String string2) {
		this.field_11515.put(string, new class_2519(string2));
	}

	public void method_10570(String string, byte[] bs) {
		this.field_11515.put(string, new class_2479(bs));
	}

	public void method_10539(String string, int[] is) {
		this.field_11515.put(string, new class_2495(is));
	}

	public void method_10572(String string, List<Integer> list) {
		this.field_11515.put(string, new class_2495(list));
	}

	public void method_10564(String string, long[] ls) {
		this.field_11515.put(string, new class_2501(ls));
	}

	public void method_10538(String string, List<Long> list) {
		this.field_11515.put(string, new class_2501(list));
	}

	public void method_10556(String string, boolean bl) {
		this.method_10567(string, (byte)(bl ? 1 : 0));
	}

	@Nullable
	public class_2520 method_10580(String string) {
		return (class_2520)this.field_11515.get(string);
	}

	public byte method_10540(String string) {
		class_2520 lv = (class_2520)this.field_11515.get(string);
		return lv == null ? 0 : lv.method_10711();
	}

	public boolean method_10545(String string) {
		return this.field_11515.containsKey(string);
	}

	public boolean method_10573(String string, int i) {
		int j = this.method_10540(string);
		if (j == i) {
			return true;
		} else {
			return i != 99 ? false : j == 1 || j == 2 || j == 3 || j == 4 || j == 5 || j == 6;
		}
	}

	public byte method_10571(String string) {
		try {
			if (this.method_10573(string, 99)) {
				return ((class_2514)this.field_11515.get(string)).method_10698();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public short method_10568(String string) {
		try {
			if (this.method_10573(string, 99)) {
				return ((class_2514)this.field_11515.get(string)).method_10696();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public int method_10550(String string) {
		try {
			if (this.method_10573(string, 99)) {
				return ((class_2514)this.field_11515.get(string)).method_10701();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public long method_10537(String string) {
		try {
			if (this.method_10573(string, 99)) {
				return ((class_2514)this.field_11515.get(string)).method_10699();
			}
		} catch (ClassCastException var3) {
		}

		return 0L;
	}

	public float method_10583(String string) {
		try {
			if (this.method_10573(string, 99)) {
				return ((class_2514)this.field_11515.get(string)).method_10700();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0F;
	}

	public double method_10574(String string) {
		try {
			if (this.method_10573(string, 99)) {
				return ((class_2514)this.field_11515.get(string)).method_10697();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0;
	}

	public String method_10558(String string) {
		try {
			if (this.method_10573(string, 8)) {
				return ((class_2520)this.field_11515.get(string)).method_10714();
			}
		} catch (ClassCastException var3) {
		}

		return "";
	}

	public byte[] method_10547(String string) {
		try {
			if (this.method_10573(string, 7)) {
				return ((class_2479)this.field_11515.get(string)).method_10521();
			}
		} catch (ClassCastException var3) {
			throw new class_148(this.method_10559(string, 7, var3));
		}

		return new byte[0];
	}

	public int[] method_10561(String string) {
		try {
			if (this.method_10573(string, 11)) {
				return ((class_2495)this.field_11515.get(string)).method_10588();
			}
		} catch (ClassCastException var3) {
			throw new class_148(this.method_10559(string, 11, var3));
		}

		return new int[0];
	}

	public long[] method_10565(String string) {
		try {
			if (this.method_10573(string, 12)) {
				return ((class_2501)this.field_11515.get(string)).method_10615();
			}
		} catch (ClassCastException var3) {
			throw new class_148(this.method_10559(string, 12, var3));
		}

		return new long[0];
	}

	public class_2487 method_10562(String string) {
		try {
			if (this.method_10573(string, 10)) {
				return (class_2487)this.field_11515.get(string);
			}
		} catch (ClassCastException var3) {
			throw new class_148(this.method_10559(string, 10, var3));
		}

		return new class_2487();
	}

	public class_2499 method_10554(String string, int i) {
		try {
			if (this.method_10540(string) == 9) {
				class_2499 lv = (class_2499)this.field_11515.get(string);
				if (!lv.isEmpty() && lv.method_10601() != i) {
					return new class_2499();
				}

				return lv;
			}
		} catch (ClassCastException var4) {
			throw new class_148(this.method_10559(string, 9, var4));
		}

		return new class_2499();
	}

	public boolean method_10577(String string) {
		return this.method_10571(string) != 0;
	}

	public void method_10551(String string) {
		this.field_11515.remove(string);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");
		Collection<String> collection = this.field_11515.keySet();
		if (field_11514.isDebugEnabled()) {
			List<String> list = Lists.<String>newArrayList(this.field_11515.keySet());
			Collections.sort(list);
			collection = list;
		}

		for (String string : collection) {
			if (stringBuilder.length() != 1) {
				stringBuilder.append(',');
			}

			stringBuilder.append(method_10578(string)).append(':').append(this.field_11515.get(string));
		}

		return stringBuilder.append('}').toString();
	}

	public boolean isEmpty() {
		return this.field_11515.isEmpty();
	}

	private class_128 method_10559(String string, int i, ClassCastException classCastException) {
		class_128 lv = class_128.method_560(classCastException, "Reading NBT data");
		class_129 lv2 = lv.method_556("Corrupt NBT tag", 1);
		lv2.method_577("Tag type found", () -> field_11592[((class_2520)this.field_11515.get(string)).method_10711()]);
		lv2.method_577("Tag type expected", () -> field_11592[i]);
		lv2.method_578("Tag name", string);
		return lv;
	}

	public class_2487 method_10553() {
		class_2487 lv = new class_2487();

		for (String string : this.field_11515.keySet()) {
			lv.method_10566(string, ((class_2520)this.field_11515.get(string)).method_10707());
		}

		return lv;
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2487 && Objects.equals(this.field_11515, ((class_2487)object).field_11515);
	}

	public int hashCode() {
		return this.field_11515.hashCode();
	}

	private static void method_10555(String string, class_2520 arg, DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(arg.method_10711());
		if (arg.method_10711() != 0) {
			dataOutput.writeUTF(string);
			arg.method_10713(dataOutput);
		}
	}

	private static byte method_10542(DataInput dataInput, class_2505 arg) throws IOException {
		return dataInput.readByte();
	}

	private static String method_10552(DataInput dataInput, class_2505 arg) throws IOException {
		return dataInput.readUTF();
	}

	static class_2520 method_10581(byte b, String string, DataInput dataInput, int i, class_2505 arg) throws IOException {
		class_2520 lv = class_2520.method_10708(b);

		try {
			lv.method_10709(dataInput, i, arg);
			return lv;
		} catch (IOException var9) {
			class_128 lv2 = class_128.method_560(var9, "Loading NBT data");
			class_129 lv3 = lv2.method_562("NBT Tag");
			lv3.method_578("Tag name", string);
			lv3.method_578("Tag type", b);
			throw new class_148(lv2);
		}
	}

	public class_2487 method_10543(class_2487 arg) {
		for (String string : arg.field_11515.keySet()) {
			class_2520 lv = (class_2520)arg.field_11515.get(string);
			if (lv.method_10711() == 10) {
				if (this.method_10573(string, 10)) {
					class_2487 lv2 = this.method_10562(string);
					lv2.method_10543((class_2487)lv);
				} else {
					this.method_10566(string, lv.method_10707());
				}
			} else {
				this.method_10566(string, lv.method_10707());
			}
		}

		return this;
	}

	protected static String method_10578(String string) {
		return field_11516.matcher(string).matches() ? string : class_2519.method_10706(string, true);
	}

	protected static class_2561 method_10557(String string) {
		if (field_11516.matcher(string).matches()) {
			return new class_2585(string).method_10854(field_11591);
		} else {
			class_2561 lv = new class_2585(class_2519.method_10706(string, false)).method_10854(field_11591);
			return new class_2585("\"").method_10852(lv).method_10864("\"");
		}
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		if (this.field_11515.isEmpty()) {
			return new class_2585("{}");
		} else {
			class_2561 lv = new class_2585("{");
			Collection<String> collection = this.field_11515.keySet();
			if (field_11514.isDebugEnabled()) {
				List<String> list = Lists.<String>newArrayList(this.field_11515.keySet());
				Collections.sort(list);
				collection = list;
			}

			if (!string.isEmpty()) {
				lv.method_10864("\n");
			}

			Iterator<String> iterator = collection.iterator();

			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				class_2561 lv2 = new class_2585(Strings.repeat(string, i + 1))
					.method_10852(method_10557(string2))
					.method_10864(String.valueOf(':'))
					.method_10864(" ")
					.method_10852(((class_2520)this.field_11515.get(string2)).method_10710(string, i + 1));
				if (iterator.hasNext()) {
					lv2.method_10864(String.valueOf(',')).method_10864(string.isEmpty() ? " " : "\n");
				}

				lv.method_10852(lv2);
			}

			if (!string.isEmpty()) {
				lv.method_10864("\n").method_10864(Strings.repeat(string, i));
			}

			lv.method_10864("}");
			return lv;
		}
	}
}
