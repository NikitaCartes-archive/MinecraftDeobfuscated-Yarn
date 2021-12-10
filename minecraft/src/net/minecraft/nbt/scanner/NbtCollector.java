package net.minecraft.nbt.scanner;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;

/**
 * An NBT collector scans an NBT structure and builds an object
 * representation out of it.
 */
public class NbtCollector implements NbtScanner {
	private String currentKey = "";
	@Nullable
	private NbtElement root;
	private final Deque<Consumer<NbtElement>> stack = new ArrayDeque();

	@Nullable
	public NbtElement getRoot() {
		return this.root;
	}

	protected int getDepth() {
		return this.stack.size();
	}

	private void append(NbtElement nbt) {
		((Consumer)this.stack.getLast()).accept(nbt);
	}

	@Override
	public NbtScanner.Result visitEnd() {
		this.append(NbtEnd.INSTANCE);
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitString(String value) {
		this.append(NbtString.of(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitByte(byte value) {
		this.append(NbtByte.of(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitShort(short value) {
		this.append(NbtShort.of(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitInt(int value) {
		this.append(NbtInt.of(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitLong(long value) {
		this.append(NbtLong.of(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitFloat(float value) {
		this.append(NbtFloat.of(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitDouble(double value) {
		this.append(NbtDouble.of(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitByteArray(byte[] value) {
		this.append(new NbtByteArray(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitIntArray(int[] value) {
		this.append(new NbtIntArray(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitLongArray(long[] value) {
		this.append(new NbtLongArray(value));
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result visitListMeta(NbtType<?> entryType, int length) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.NestedResult startListItem(NbtType<?> type, int index) {
		this.pushStack(type);
		return NbtScanner.NestedResult.ENTER;
	}

	@Override
	public NbtScanner.NestedResult visitSubNbtType(NbtType<?> type) {
		return NbtScanner.NestedResult.ENTER;
	}

	@Override
	public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
		this.currentKey = key;
		this.pushStack(type);
		return NbtScanner.NestedResult.ENTER;
	}

	private void pushStack(NbtType<?> type) {
		if (type == NbtList.TYPE) {
			NbtList nbtList = new NbtList();
			this.append(nbtList);
			this.stack.addLast(nbtList::add);
		} else if (type == NbtCompound.TYPE) {
			NbtCompound nbtCompound = new NbtCompound();
			this.append(nbtCompound);
			this.stack.addLast((Consumer)nbt -> nbtCompound.put(this.currentKey, nbt));
		}
	}

	@Override
	public NbtScanner.Result endNested() {
		this.stack.removeLast();
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	public NbtScanner.Result start(NbtType<?> rootType) {
		if (rootType == NbtList.TYPE) {
			NbtList nbtList = new NbtList();
			this.root = nbtList;
			this.stack.addLast(nbtList::add);
		} else if (rootType == NbtCompound.TYPE) {
			NbtCompound nbtCompound = new NbtCompound();
			this.root = nbtCompound;
			this.stack.addLast((Consumer)nbt -> nbtCompound.put(this.currentKey, nbt));
		} else {
			this.stack.addLast((Consumer)nbt -> this.root = nbt);
		}

		return NbtScanner.Result.CONTINUE;
	}
}
