package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.network.PacketByteBuf;

public class FloatArgumentSerializer implements ArgumentSerializer<FloatArgumentType, FloatArgumentSerializer.Properties> {
	public void writePacket(FloatArgumentSerializer.Properties properties, PacketByteBuf packetByteBuf) {
		boolean bl = properties.min != -Float.MAX_VALUE;
		boolean bl2 = properties.max != Float.MAX_VALUE;
		packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
		if (bl) {
			packetByteBuf.writeFloat(properties.min);
		}

		if (bl2) {
			packetByteBuf.writeFloat(properties.max);
		}
	}

	public FloatArgumentSerializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		float f = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readFloat() : -Float.MAX_VALUE;
		float g = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
		return new FloatArgumentSerializer.Properties(f, g);
	}

	public void writeJson(FloatArgumentSerializer.Properties properties, JsonObject jsonObject) {
		if (properties.min != -Float.MAX_VALUE) {
			jsonObject.addProperty("min", properties.min);
		}

		if (properties.max != Float.MAX_VALUE) {
			jsonObject.addProperty("max", properties.max);
		}
	}

	public FloatArgumentSerializer.Properties getArgumentTypeProperties(FloatArgumentType floatArgumentType) {
		return new FloatArgumentSerializer.Properties(floatArgumentType.getMinimum(), floatArgumentType.getMaximum());
	}

	public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<FloatArgumentType> {
		final float min;
		final float max;

		Properties(float min, float max) {
			this.min = min;
			this.max = max;
		}

		public FloatArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
			return FloatArgumentType.floatArg(this.min, this.max);
		}

		@Override
		public ArgumentSerializer<FloatArgumentType, ?> getSerializer() {
			return FloatArgumentSerializer.this;
		}
	}
}
