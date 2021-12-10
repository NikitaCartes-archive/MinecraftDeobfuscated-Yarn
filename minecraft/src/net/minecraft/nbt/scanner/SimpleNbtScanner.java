package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtType;

/**
 * A simple NBT scanner visits all elements shallowly, allowing
 * implementations to override it and perform more actions.
 */
public interface SimpleNbtScanner extends NbtScanner {
	/**
	 * The simple NBT scanner that performs no action.
	 */
	SimpleNbtScanner NOOP = new SimpleNbtScanner() {
	};

	@Override
	default NbtScanner.Result visitEnd() {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitString(String value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitByte(byte value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitShort(short value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitInt(int value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitLong(long value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitFloat(float value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitDouble(double value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitByteArray(byte[] value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitIntArray(int[] value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitLongArray(long[] value) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result visitListMeta(NbtType<?> entryType, int length) {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.NestedResult startListItem(NbtType<?> type, int index) {
		return NbtScanner.NestedResult.SKIP;
	}

	@Override
	default NbtScanner.NestedResult visitSubNbtType(NbtType<?> type) {
		return NbtScanner.NestedResult.SKIP;
	}

	@Override
	default NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
		return NbtScanner.NestedResult.SKIP;
	}

	@Override
	default NbtScanner.Result endNested() {
		return NbtScanner.Result.CONTINUE;
	}

	@Override
	default NbtScanner.Result start(NbtType<?> rootType) {
		return NbtScanner.Result.CONTINUE;
	}
}
