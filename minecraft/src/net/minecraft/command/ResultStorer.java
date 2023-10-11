package net.minecraft.command;

public interface ResultStorer<T> {
	void storeResult(T context, boolean success, int result);
}
