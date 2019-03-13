package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.util.PacketByteBuf;

public class ConstantArgumentSerializer<T extends ArgumentType<?>> implements ArgumentSerializer<T> {
	private final Supplier<T> supplier;

	public ConstantArgumentSerializer(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public void method_10007(T argumentType, PacketByteBuf packetByteBuf) {
	}

	@Override
	public T method_10005(PacketByteBuf packetByteBuf) {
		return (T)this.supplier.get();
	}

	@Override
	public void toJson(T argumentType, JsonObject jsonObject) {
	}
}
