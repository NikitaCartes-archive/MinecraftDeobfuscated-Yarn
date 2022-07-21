package net.minecraft.command.argument;

public interface SignedArgumentType<T> extends DecoratableArgumentType<T> {
	String toSignedString(T value);
}
