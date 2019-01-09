package net.minecraft;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public class class_2943 {
	private static final class_3513<class_2941<?>> field_13328 = new class_3513<>(16);
	public static final class_2941<Byte> field_13319 = new class_2941<Byte>() {
		public void method_12741(class_2540 arg, Byte byte_) {
			arg.writeByte(byte_);
		}

		public Byte method_12740(class_2540 arg) {
			return arg.readByte();
		}

		@Override
		public class_2940<Byte> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Byte method_12742(Byte byte_) {
			return byte_;
		}
	};
	public static final class_2941<Integer> field_13327 = new class_2941<Integer>() {
		public void method_12766(class_2540 arg, Integer integer) {
			arg.method_10804(integer);
		}

		public Integer method_12765(class_2540 arg) {
			return arg.method_10816();
		}

		@Override
		public class_2940<Integer> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Integer method_12767(Integer integer) {
			return integer;
		}
	};
	public static final class_2941<Float> field_13320 = new class_2941<Float>() {
		public void method_12769(class_2540 arg, Float float_) {
			arg.writeFloat(float_);
		}

		public Float method_12768(class_2540 arg) {
			return arg.readFloat();
		}

		@Override
		public class_2940<Float> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Float method_12770(Float float_) {
			return float_;
		}
	};
	public static final class_2941<String> field_13326 = new class_2941<String>() {
		public void method_12723(class_2540 arg, String string) {
			arg.method_10814(string);
		}

		public String method_12722(class_2540 arg) {
			return arg.method_10800(32767);
		}

		@Override
		public class_2940<String> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public String method_12724(String string) {
			return string;
		}
	};
	public static final class_2941<class_2561> field_13317 = new class_2941<class_2561>() {
		public void method_12727(class_2540 arg, class_2561 arg2) {
			arg.method_10805(arg2);
		}

		public class_2561 method_12725(class_2540 arg) {
			return arg.method_10808();
		}

		@Override
		public class_2940<class_2561> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_2561 method_12726(class_2561 arg) {
			return arg.method_10853();
		}
	};
	public static final class_2941<Optional<class_2561>> field_13325 = new class_2941<Optional<class_2561>>() {
		public void method_12728(class_2540 arg, Optional<class_2561> optional) {
			if (optional.isPresent()) {
				arg.writeBoolean(true);
				arg.method_10805((class_2561)optional.get());
			} else {
				arg.writeBoolean(false);
			}
		}

		public Optional<class_2561> method_12729(class_2540 arg) {
			return arg.readBoolean() ? Optional.of(arg.method_10808()) : Optional.empty();
		}

		@Override
		public class_2940<Optional<class_2561>> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Optional<class_2561> method_12730(Optional<class_2561> optional) {
			return optional.isPresent() ? Optional.of(((class_2561)optional.get()).method_10853()) : Optional.empty();
		}
	};
	public static final class_2941<class_1799> field_13322 = new class_2941<class_1799>() {
		public void method_12731(class_2540 arg, class_1799 arg2) {
			arg.method_10793(arg2);
		}

		public class_1799 method_12733(class_2540 arg) {
			return arg.method_10819();
		}

		@Override
		public class_2940<class_1799> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_1799 method_12732(class_1799 arg) {
			return arg.method_7972();
		}
	};
	public static final class_2941<Optional<class_2680>> field_13312 = new class_2941<Optional<class_2680>>() {
		public void method_12734(class_2540 arg, Optional<class_2680> optional) {
			if (optional.isPresent()) {
				arg.method_10804(class_2248.method_9507((class_2680)optional.get()));
			} else {
				arg.method_10804(0);
			}
		}

		public Optional<class_2680> method_12735(class_2540 arg) {
			int i = arg.method_10816();
			return i == 0 ? Optional.empty() : Optional.of(class_2248.method_9531(i));
		}

		@Override
		public class_2940<Optional<class_2680>> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Optional<class_2680> method_12736(Optional<class_2680> optional) {
			return optional;
		}
	};
	public static final class_2941<Boolean> field_13323 = new class_2941<Boolean>() {
		public void method_12738(class_2540 arg, Boolean boolean_) {
			arg.writeBoolean(boolean_);
		}

		public Boolean method_12737(class_2540 arg) {
			return arg.readBoolean();
		}

		@Override
		public class_2940<Boolean> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Boolean method_12739(Boolean boolean_) {
			return boolean_;
		}
	};
	public static final class_2941<class_2394> field_13314 = new class_2941<class_2394>() {
		public void method_12746(class_2540 arg, class_2394 arg2) {
			arg.method_10804(class_2378.field_11141.method_10249((class_2396<? extends class_2394>)arg2.method_10295()));
			arg2.method_10294(arg);
		}

		public class_2394 method_12743(class_2540 arg) {
			return this.method_12744(arg, class_2378.field_11141.method_10200(arg.method_10816()));
		}

		private <T extends class_2394> T method_12744(class_2540 arg, class_2396<T> arg2) {
			return arg2.method_10298().method_10297(arg2, arg);
		}

		@Override
		public class_2940<class_2394> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_2394 method_12745(class_2394 arg) {
			return arg;
		}
	};
	public static final class_2941<class_2379> field_13316 = new class_2941<class_2379>() {
		public void method_12747(class_2540 arg, class_2379 arg2) {
			arg.writeFloat(arg2.method_10256());
			arg.writeFloat(arg2.method_10257());
			arg.writeFloat(arg2.method_10258());
		}

		public class_2379 method_12748(class_2540 arg) {
			return new class_2379(arg.readFloat(), arg.readFloat(), arg.readFloat());
		}

		@Override
		public class_2940<class_2379> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_2379 method_12749(class_2379 arg) {
			return arg;
		}
	};
	public static final class_2941<class_2338> field_13324 = new class_2941<class_2338>() {
		public void method_12751(class_2540 arg, class_2338 arg2) {
			arg.method_10807(arg2);
		}

		public class_2338 method_12750(class_2540 arg) {
			return arg.method_10811();
		}

		@Override
		public class_2940<class_2338> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_2338 method_12752(class_2338 arg) {
			return arg;
		}
	};
	public static final class_2941<Optional<class_2338>> field_13315 = new class_2941<Optional<class_2338>>() {
		public void method_12753(class_2540 arg, Optional<class_2338> optional) {
			arg.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				arg.method_10807((class_2338)optional.get());
			}
		}

		public Optional<class_2338> method_12754(class_2540 arg) {
			return !arg.readBoolean() ? Optional.empty() : Optional.of(arg.method_10811());
		}

		@Override
		public class_2940<Optional<class_2338>> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Optional<class_2338> method_12755(Optional<class_2338> optional) {
			return optional;
		}
	};
	public static final class_2941<class_2350> field_13321 = new class_2941<class_2350>() {
		public void method_12757(class_2540 arg, class_2350 arg2) {
			arg.method_10817(arg2);
		}

		public class_2350 method_12756(class_2540 arg) {
			return arg.method_10818(class_2350.class);
		}

		@Override
		public class_2940<class_2350> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_2350 method_12758(class_2350 arg) {
			return arg;
		}
	};
	public static final class_2941<Optional<UUID>> field_13313 = new class_2941<Optional<UUID>>() {
		public void method_12759(class_2540 arg, Optional<UUID> optional) {
			arg.writeBoolean(optional.isPresent());
			if (optional.isPresent()) {
				arg.method_10797((UUID)optional.get());
			}
		}

		public Optional<UUID> method_12760(class_2540 arg) {
			return !arg.readBoolean() ? Optional.empty() : Optional.of(arg.method_10790());
		}

		@Override
		public class_2940<Optional<UUID>> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public Optional<UUID> method_12761(Optional<UUID> optional) {
			return optional;
		}
	};
	public static final class_2941<class_2487> field_13318 = new class_2941<class_2487>() {
		public void method_12763(class_2540 arg, class_2487 arg2) {
			arg.method_10794(arg2);
		}

		public class_2487 method_12764(class_2540 arg) {
			return arg.method_10798();
		}

		@Override
		public class_2940<class_2487> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_2487 method_12762(class_2487 arg) {
			return arg.method_10553();
		}
	};
	public static final class_2941<class_3850> field_17207 = new class_2941<class_3850>() {
		public void method_17197(class_2540 arg, class_3850 arg2) {
			arg.method_10804(class_2378.field_17166.method_10249(arg2.method_16919()));
			arg.method_10804(class_2378.field_17167.method_10249(arg2.method_16924()));
			arg.method_10804(arg2.method_16925());
		}

		public class_3850 method_17198(class_2540 arg) {
			return new class_3850(class_2378.field_17166.method_10200(arg.method_10816()), class_2378.field_17167.method_10200(arg.method_10816()), arg.method_10816());
		}

		@Override
		public class_2940<class_3850> method_12717(int i) {
			return new class_2940<>(i, this);
		}

		public class_3850 method_17196(class_3850 arg) {
			return arg;
		}
	};

	public static void method_12720(class_2941<?> arg) {
		field_13328.method_15225(arg);
	}

	@Nullable
	public static class_2941<?> method_12721(int i) {
		return field_13328.method_10200(i);
	}

	public static int method_12719(class_2941<?> arg) {
		return field_13328.method_15231(arg);
	}

	static {
		method_12720(field_13319);
		method_12720(field_13327);
		method_12720(field_13320);
		method_12720(field_13326);
		method_12720(field_13317);
		method_12720(field_13325);
		method_12720(field_13322);
		method_12720(field_13323);
		method_12720(field_13316);
		method_12720(field_13324);
		method_12720(field_13315);
		method_12720(field_13321);
		method_12720(field_13313);
		method_12720(field_13312);
		method_12720(field_13318);
		method_12720(field_13314);
		method_12720(field_17207);
	}
}
