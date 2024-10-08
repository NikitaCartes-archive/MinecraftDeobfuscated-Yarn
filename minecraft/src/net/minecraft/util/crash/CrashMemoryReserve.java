package net.minecraft.util.crash;

import javax.annotation.Nullable;

public class CrashMemoryReserve {
	@Nullable
	private static byte[] reservedMemory;

	public static void reserveMemory() {
		reservedMemory = new byte[10485760];
	}

	public static void releaseMemory() {
		if (reservedMemory != null) {
			reservedMemory = null;

			try {
				System.gc();
				System.gc();
				System.gc();
			} catch (Throwable var1) {
			}
		}
	}
}
