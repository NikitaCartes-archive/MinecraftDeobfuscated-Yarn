package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2945 {
	private static final Logger field_13334 = LogManager.getLogger();
	private static final Map<Class<? extends class_1297>, Integer> field_13332 = Maps.<Class<? extends class_1297>, Integer>newHashMap();
	private final class_1297 field_13333;
	private final Map<Integer, class_2945.class_2946<?>> field_13331 = Maps.<Integer, class_2945.class_2946<?>>newHashMap();
	private final ReadWriteLock field_13335 = new ReentrantReadWriteLock();
	private boolean field_13330 = true;
	private boolean field_13329;

	public class_2945(class_1297 arg) {
		this.field_13333 = arg;
	}

	public static <T> class_2940<T> method_12791(Class<? extends class_1297> class_, class_2941<T> arg) {
		if (field_13334.isDebugEnabled()) {
			try {
				Class<?> class2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
				if (!class2.equals(class_)) {
					field_13334.debug("defineId called for: {} from {}", class_, class2, new RuntimeException());
				}
			} catch (ClassNotFoundException var5) {
			}
		}

		int i;
		if (field_13332.containsKey(class_)) {
			i = (Integer)field_13332.get(class_) + 1;
		} else {
			int j = 0;
			Class<?> class3 = class_;

			while (class3 != class_1297.class) {
				class3 = class3.getSuperclass();
				if (field_13332.containsKey(class3)) {
					j = (Integer)field_13332.get(class3) + 1;
					break;
				}
			}

			i = j;
		}

		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else {
			field_13332.put(class_, i);
			return arg.method_12717(i);
		}
	}

	public <T> void method_12784(class_2940<T> arg, T object) {
		int i = arg.method_12713();
		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else if (this.field_13331.containsKey(i)) {
			throw new IllegalArgumentException("Duplicate id value for " + i + "!");
		} else if (class_2943.method_12719(arg.method_12712()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + arg.method_12712() + " for " + i + "!");
		} else {
			this.method_12776(arg, object);
		}
	}

	private <T> void method_12776(class_2940<T> arg, T object) {
		class_2945.class_2946<T> lv = new class_2945.class_2946<>(arg, object);
		this.field_13335.writeLock().lock();
		this.field_13331.put(arg.method_12713(), lv);
		this.field_13330 = false;
		this.field_13335.writeLock().unlock();
	}

	private <T> class_2945.class_2946<T> method_12783(class_2940<T> arg) {
		this.field_13335.readLock().lock();

		class_2945.class_2946<T> lv;
		try {
			lv = (class_2945.class_2946<T>)this.field_13331.get(arg.method_12713());
		} catch (Throwable var9) {
			class_128 lv2 = class_128.method_560(var9, "Getting synched entity data");
			class_129 lv3 = lv2.method_562("Synched entity data");
			lv3.method_578("Data ID", arg);
			throw new class_148(lv2);
		} finally {
			this.field_13335.readLock().unlock();
		}

		return lv;
	}

	public <T> T method_12789(class_2940<T> arg) {
		return this.method_12783(arg).method_12794();
	}

	public <T> void method_12778(class_2940<T> arg, T object) {
		class_2945.class_2946<T> lv = this.method_12783(arg);
		if (ObjectUtils.notEqual(object, lv.method_12794())) {
			lv.method_12799(object);
			this.field_13333.method_5674(arg);
			lv.method_12795(true);
			this.field_13329 = true;
		}
	}

	public boolean method_12786() {
		return this.field_13329;
	}

	public static void method_12787(List<class_2945.class_2946<?>> list, class_2540 arg) throws IOException {
		if (list != null) {
			int i = 0;

			for (int j = list.size(); i < j; i++) {
				method_12782(arg, (class_2945.class_2946)list.get(i));
			}
		}

		arg.writeByte(255);
	}

	@Nullable
	public List<class_2945.class_2946<?>> method_12781() {
		List<class_2945.class_2946<?>> list = null;
		if (this.field_13329) {
			this.field_13335.readLock().lock();

			for (class_2945.class_2946<?> lv : this.field_13331.values()) {
				if (lv.method_12796()) {
					lv.method_12795(false);
					if (list == null) {
						list = Lists.<class_2945.class_2946<?>>newArrayList();
					}

					list.add(lv.method_12798());
				}
			}

			this.field_13335.readLock().unlock();
		}

		this.field_13329 = false;
		return list;
	}

	public void method_12780(class_2540 arg) throws IOException {
		this.field_13335.readLock().lock();

		for (class_2945.class_2946<?> lv : this.field_13331.values()) {
			method_12782(arg, lv);
		}

		this.field_13335.readLock().unlock();
		arg.writeByte(255);
	}

	@Nullable
	public List<class_2945.class_2946<?>> method_12793() {
		List<class_2945.class_2946<?>> list = null;
		this.field_13335.readLock().lock();

		for (class_2945.class_2946<?> lv : this.field_13331.values()) {
			if (list == null) {
				list = Lists.<class_2945.class_2946<?>>newArrayList();
			}

			list.add(lv.method_12798());
		}

		this.field_13335.readLock().unlock();
		return list;
	}

	private static <T> void method_12782(class_2540 arg, class_2945.class_2946<T> arg2) throws IOException {
		class_2940<T> lv = arg2.method_12797();
		int i = class_2943.method_12719(lv.method_12712());
		if (i < 0) {
			throw new EncoderException("Unknown serializer type " + lv.method_12712());
		} else {
			arg.writeByte(lv.method_12713());
			arg.method_10804(i);
			lv.method_12712().method_12715(arg, arg2.method_12794());
		}
	}

	@Nullable
	public static List<class_2945.class_2946<?>> method_12788(class_2540 arg) throws IOException {
		List<class_2945.class_2946<?>> list = null;

		int i;
		while ((i = arg.readUnsignedByte()) != 255) {
			if (list == null) {
				list = Lists.<class_2945.class_2946<?>>newArrayList();
			}

			int j = arg.method_10816();
			class_2941<?> lv = class_2943.method_12721(j);
			if (lv == null) {
				throw new DecoderException("Unknown serializer type " + j);
			}

			list.add(method_12777(arg, i, lv));
		}

		return list;
	}

	private static <T> class_2945.class_2946<T> method_12777(class_2540 arg, int i, class_2941<T> arg2) {
		return new class_2945.class_2946<>(arg2.method_12717(i), arg2.method_12716(arg));
	}

	@Environment(EnvType.CLIENT)
	public void method_12779(List<class_2945.class_2946<?>> list) {
		this.field_13335.writeLock().lock();

		for (class_2945.class_2946<?> lv : list) {
			class_2945.class_2946<?> lv2 = (class_2945.class_2946<?>)this.field_13331.get(lv.method_12797().method_12713());
			if (lv2 != null) {
				this.method_12785(lv2, lv);
				this.field_13333.method_5674(lv.method_12797());
			}
		}

		this.field_13335.writeLock().unlock();
		this.field_13329 = true;
	}

	@Environment(EnvType.CLIENT)
	private <T> void method_12785(class_2945.class_2946<T> arg, class_2945.class_2946<?> arg2) {
		if (!Objects.equals(arg2.field_13337.method_12712(), arg.field_13337.method_12712())) {
			throw new IllegalStateException(
				String.format(
					"Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)",
					arg.field_13337.method_12713(),
					this.field_13333,
					arg.field_13338,
					arg.field_13338.getClass(),
					arg2.field_13338,
					arg2.field_13338.getClass()
				)
			);
		} else {
			arg.method_12799((T)arg2.method_12794());
		}
	}

	public boolean method_12790() {
		return this.field_13330;
	}

	public void method_12792() {
		this.field_13329 = false;
		this.field_13335.readLock().lock();

		for (class_2945.class_2946<?> lv : this.field_13331.values()) {
			lv.method_12795(false);
		}

		this.field_13335.readLock().unlock();
	}

	public static class class_2946<T> {
		private final class_2940<T> field_13337;
		private T field_13338;
		private boolean field_13336;

		public class_2946(class_2940<T> arg, T object) {
			this.field_13337 = arg;
			this.field_13338 = object;
			this.field_13336 = true;
		}

		public class_2940<T> method_12797() {
			return this.field_13337;
		}

		public void method_12799(T object) {
			this.field_13338 = object;
		}

		public T method_12794() {
			return this.field_13338;
		}

		public boolean method_12796() {
			return this.field_13336;
		}

		public void method_12795(boolean bl) {
			this.field_13336 = bl;
		}

		public class_2945.class_2946<T> method_12798() {
			return new class_2945.class_2946<>(this.field_13337, this.field_13337.method_12712().method_12714(this.field_13338));
		}
	}
}
