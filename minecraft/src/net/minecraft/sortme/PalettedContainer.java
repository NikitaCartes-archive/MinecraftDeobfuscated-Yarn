package net.minecraft.sortme;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2814;
import net.minecraft.class_2834;
import net.minecraft.class_2835;
import net.minecraft.class_2837;
import net.minecraft.class_3508;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.math.MathHelper;

public class PalettedContainer<T> implements class_2835<T> {
	private final class_2837<T> field_12940;
	private final class_2835<T> field_12942 = (i, objectx) -> 0;
	private final IdList<T> field_12938;
	private final Function<CompoundTag, T> field_12943;
	private final Function<T, CompoundTag> field_12939;
	private final T field_12935;
	protected class_3508 field_12941;
	private class_2837<T> field_12936;
	private int field_12934;
	private final ReentrantLock writeLock = new ReentrantLock();

	public void lock() {
		if (this.writeLock.isLocked() && !this.writeLock.isHeldByCurrentThread()) {
			String string = (String)Thread.getAllStackTraces()
				.keySet()
				.stream()
				.filter(Objects::nonNull)
				.map(thread -> thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat ")))
				.collect(Collectors.joining("\n"));
			CrashReport crashReport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
			CrashReportElement crashReportElement = crashReport.addElement("Thread dumps");
			crashReportElement.add("Thread dumps", string);
			throw new CrashException(crashReport);
		} else {
			this.writeLock.lock();
		}
	}

	public void unlock() {
		this.writeLock.unlock();
	}

	public PalettedContainer(class_2837<T> arg, IdList<T> idList, Function<CompoundTag, T> function, Function<T, CompoundTag> function2, T object) {
		this.field_12940 = arg;
		this.field_12938 = idList;
		this.field_12943 = function;
		this.field_12939 = function2;
		this.field_12935 = object;
		this.method_12324(4);
	}

	private static int toIndex(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private void method_12324(int i) {
		if (i != this.field_12934) {
			this.field_12934 = i;
			if (this.field_12934 <= 4) {
				this.field_12934 = 4;
				this.field_12936 = new class_2834<>(this.field_12938, this.field_12934, this, this.field_12943);
			} else if (this.field_12934 < 9) {
				this.field_12936 = new class_2814<>(this.field_12938, this.field_12934, this, this.field_12943, this.field_12939);
			} else {
				this.field_12936 = this.field_12940;
				this.field_12934 = MathHelper.log2DeBrujin(this.field_12938.size());
			}

			this.field_12936.method_12291(this.field_12935);
			this.field_12941 = new class_3508(this.field_12934, 4096);
		}
	}

	@Override
	public int onResize(int i, T object) {
		this.lock();
		class_3508 lv = this.field_12941;
		class_2837<T> lv2 = this.field_12936;
		this.method_12324(i);

		for (int j = 0; j < lv.method_15215(); j++) {
			T object2 = lv2.method_12288(lv.method_15211(j));
			if (object2 != null) {
				this.method_12322(j, object2);
			}
		}

		int jx = this.field_12936.method_12291(object);
		this.unlock();
		return jx;
	}

	public T method_12328(int i, int j, int k, T object) {
		this.lock();
		T object2 = this.method_12336(toIndex(i, j, k), object);
		this.unlock();
		return object2;
	}

	public T method_16678(int i, int j, int k, T object) {
		return this.method_12336(toIndex(i, j, k), object);
	}

	protected T method_12336(int i, T object) {
		int j = this.field_12936.method_12291(object);
		int k = this.field_12941.method_15214(i, j);
		T object2 = this.field_12936.method_12288(k);
		return object2 == null ? this.field_12935 : object2;
	}

	protected void method_12322(int i, T object) {
		int j = this.field_12936.method_12291(object);
		this.field_12941.method_15210(i, j);
	}

	public T get(int i, int j, int k) {
		return this.get(toIndex(i, j, k));
	}

	public T get(int i) {
		T object = this.field_12936.method_12288(this.field_12941.method_15211(i));
		return object == null ? this.field_12935 : object;
	}

	@Environment(EnvType.CLIENT)
	public void fromPacket(PacketByteBuf packetByteBuf) {
		this.lock();
		int i = packetByteBuf.readByte();
		if (this.field_12934 != i) {
			this.method_12324(i);
		}

		this.field_12936.method_12289(packetByteBuf);
		packetByteBuf.readLongArray(this.field_12941.asLongArray());
		this.unlock();
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		this.lock();
		packetByteBuf.writeByte(this.field_12934);
		this.field_12936.method_12287(packetByteBuf);
		packetByteBuf.writeLongArray(this.field_12941.asLongArray());
		this.unlock();
	}

	public void method_12329(ListTag listTag, long[] ls) {
		this.lock();
		int i = Math.max(4, MathHelper.log2DeBrujin(listTag.size()));
		if (i != this.field_12934) {
			this.method_12324(i);
		}

		this.field_12936.method_12286(listTag);
		int j = ls.length * 64 / 4096;
		if (this.field_12936 == this.field_12940) {
			class_2837<T> lv = new class_2814<>(this.field_12938, i, this.field_12942, this.field_12943, this.field_12939);
			lv.method_12286(listTag);
			class_3508 lv2 = new class_3508(i, 4096, ls);

			for (int k = 0; k < 4096; k++) {
				this.field_12941.method_15210(k, this.field_12940.method_12291(lv.method_12288(lv2.method_15211(k))));
			}
		} else if (j == this.field_12934) {
			System.arraycopy(ls, 0, this.field_12941.asLongArray(), 0, ls.length);
		} else {
			class_3508 lv3 = new class_3508(j, 4096, ls);

			for (int l = 0; l < 4096; l++) {
				this.field_12941.method_15210(l, lv3.method_15211(l));
			}
		}

		this.unlock();
	}

	public void method_12330(CompoundTag compoundTag, String string, String string2) {
		this.lock();
		class_2814<T> lv = new class_2814<>(this.field_12938, this.field_12934, this.field_12942, this.field_12943, this.field_12939);
		lv.method_12291(this.field_12935);
		int[] is = new int[4096];

		for (int i = 0; i < 4096; i++) {
			is[i] = lv.method_12291(this.get(i));
		}

		ListTag listTag = new ListTag();
		lv.method_12196(listTag);
		compoundTag.put(string, listTag);
		int j = Math.max(4, MathHelper.log2DeBrujin(listTag.size()));
		class_3508 lv2 = new class_3508(j, 4096);

		for (int k = 0; k < is.length; k++) {
			lv2.method_15210(k, is[k]);
		}

		compoundTag.putLongArray(string2, lv2.asLongArray());
		this.unlock();
	}

	public int getPacketSize() {
		return 1 + this.field_12936.method_12290() + PacketByteBuf.getVarIntSizeBytes(this.field_12941.method_15215()) + this.field_12941.asLongArray().length * 8;
	}
}
