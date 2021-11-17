package net.minecraft;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;

public class class_6844 implements class_6836 {
	private String field_36261 = "";
	@Nullable
	private NbtElement field_36262;
	private final Deque<Consumer<NbtElement>> field_36263 = new ArrayDeque();

	@Nullable
	public NbtElement method_39887() {
		return this.field_36262;
	}

	protected int method_39888() {
		return this.field_36263.size();
	}

	private void method_39883(NbtElement nbtElement) {
		((Consumer)this.field_36263.getLast()).accept(nbtElement);
	}

	@Override
	public class_6836.class_6838 method_39856() {
		this.method_39883(NbtNull.INSTANCE);
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39862(String string) {
		this.method_39883(NbtString.of(string));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39857(byte b) {
		this.method_39883(NbtByte.of(b));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39866(short s) {
		this.method_39883(NbtShort.of(s));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39860(int i) {
		this.method_39883(NbtInt.of(i));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39861(long l) {
		this.method_39883(NbtLong.of(l));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39859(float f) {
		this.method_39883(NbtFloat.of(f));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39858(double d) {
		this.method_39883(NbtDouble.of(d));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39867(byte[] bs) {
		this.method_39883(new NbtByteArray(bs));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39868(int[] is) {
		this.method_39883(new NbtIntArray(is));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39869(long[] ls) {
		this.method_39883(new NbtLongArray(ls));
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39864(NbtType<?> nbtType, int i) {
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6837 method_39872(NbtType<?> nbtType, int i) {
		this.method_39886(nbtType);
		return class_6836.class_6837.ENTER;
	}

	@Override
	public class_6836.class_6837 method_39863(NbtType<?> nbtType) {
		return class_6836.class_6837.ENTER;
	}

	@Override
	public class_6836.class_6837 method_39865(NbtType<?> nbtType, String string) {
		this.field_36261 = string;
		this.method_39886(nbtType);
		return class_6836.class_6837.ENTER;
	}

	private void method_39886(NbtType<?> nbtType) {
		if (nbtType == NbtList.TYPE) {
			NbtList nbtList = new NbtList();
			this.method_39883(nbtList);
			this.field_36263.addLast(nbtList::add);
		} else if (nbtType == NbtCompound.TYPE) {
			NbtCompound nbtCompound = new NbtCompound();
			this.method_39883(nbtCompound);
			this.field_36263.addLast((Consumer)nbtElement -> nbtCompound.put(this.field_36261, nbtElement));
		}
	}

	@Override
	public class_6836.class_6838 method_39870() {
		this.field_36263.removeLast();
		return class_6836.class_6838.CONTINUE;
	}

	@Override
	public class_6836.class_6838 method_39871(NbtType<?> nbtType) {
		if (nbtType == NbtList.TYPE) {
			NbtList nbtList = new NbtList();
			this.field_36262 = nbtList;
			this.field_36263.addLast(nbtList::add);
		} else if (nbtType == NbtCompound.TYPE) {
			NbtCompound nbtCompound = new NbtCompound();
			this.field_36262 = nbtCompound;
			this.field_36263.addLast((Consumer)nbtElement -> nbtCompound.put(this.field_36261, nbtElement));
		} else {
			this.field_36263.addLast((Consumer)nbtElement -> this.field_36262 = nbtElement);
		}

		return class_6836.class_6838.CONTINUE;
	}
}
