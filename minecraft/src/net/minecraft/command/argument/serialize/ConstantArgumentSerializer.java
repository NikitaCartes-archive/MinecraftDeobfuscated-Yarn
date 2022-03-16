package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;

public class ConstantArgumentSerializer<A extends ArgumentType<?>> implements ArgumentSerializer<A, ConstantArgumentSerializer<A>.Properties> {
	private final ConstantArgumentSerializer<A>.Properties properties;

	private ConstantArgumentSerializer(Function<CommandRegistryAccess, A> typeSupplier) {
		this.properties = new ConstantArgumentSerializer.Properties(typeSupplier);
	}

	public static <T extends ArgumentType<?>> ConstantArgumentSerializer<T> of(Supplier<T> typeSupplier) {
		return new ConstantArgumentSerializer<>(commandRegistryAccess -> (ArgumentType)typeSupplier.get());
	}

	public static <T extends ArgumentType<?>> ConstantArgumentSerializer<T> of(Function<CommandRegistryAccess, T> typeSupplier) {
		return new ConstantArgumentSerializer<>(typeSupplier);
	}

	public void writePacket(ConstantArgumentSerializer<A>.Properties properties, PacketByteBuf packetByteBuf) {
	}

	public void writeJson(ConstantArgumentSerializer<A>.Properties properties, JsonObject jsonObject) {
	}

	public ConstantArgumentSerializer<A>.Properties fromPacket(PacketByteBuf packetByteBuf) {
		return this.properties;
	}

	public ConstantArgumentSerializer<A>.Properties getArgumentTypeProperties(A argumentType) {
		return this.properties;
	}

	public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<A> {
		private final Function<CommandRegistryAccess, A> typeSupplier;

		public Properties(Function<CommandRegistryAccess, A> typeSupplier) {
			this.typeSupplier = typeSupplier;
		}

		@Override
		public A createType(CommandRegistryAccess commandRegistryAccess) {
			return (A)this.typeSupplier.apply(commandRegistryAccess);
		}

		@Override
		public ArgumentSerializer<A, ?> getSerializer() {
			return ConstantArgumentSerializer.this;
		}
	}
}
