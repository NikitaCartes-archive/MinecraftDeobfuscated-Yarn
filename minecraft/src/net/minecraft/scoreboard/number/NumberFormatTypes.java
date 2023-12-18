package net.minecraft.scoreboard.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class NumberFormatTypes {
	public static final MapCodec<NumberFormat> REGISTRY_CODEC = Registries.NUMBER_FORMAT_TYPE
		.getCodec()
		.dispatchMap(NumberFormat::getType, formatType -> formatType.getCodec().codec());
	public static final Codec<NumberFormat> CODEC = REGISTRY_CODEC.codec();

	public static NumberFormatType<?> registerAndGetDefault(Registry<NumberFormatType<?>> registry) {
		Registry.register(registry, "blank", BlankNumberFormat.TYPE);
		Registry.register(registry, "styled", StyledNumberFormat.TYPE);
		return Registry.register(registry, "fixed", FixedNumberFormat.TYPE);
	}

	public static <T extends NumberFormat> void toBuf(PacketByteBuf buf, T format) {
		NumberFormatType<T> numberFormatType = (NumberFormatType<T>)format.getType();
		buf.writeRegistryValue(Registries.NUMBER_FORMAT_TYPE, numberFormatType);
		numberFormatType.toBuf(buf, format);
	}

	public static NumberFormat fromBuf(PacketByteBuf buf) {
		NumberFormatType<?> numberFormatType = buf.readRegistryValue(Registries.NUMBER_FORMAT_TYPE);
		return numberFormatType.fromBuf(buf);
	}
}
