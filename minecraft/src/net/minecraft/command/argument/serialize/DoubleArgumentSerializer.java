package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.network.PacketByteBuf;

public class DoubleArgumentSerializer implements ArgumentSerializer<DoubleArgumentType, DoubleArgumentSerializer.Properties> {
	public void writePacket(DoubleArgumentSerializer.Properties properties, PacketByteBuf packetByteBuf) {
		boolean bl = properties.min != -Double.MAX_VALUE;
		boolean bl2 = properties.max != Double.MAX_VALUE;
		packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
		if (bl) {
			packetByteBuf.writeDouble(properties.min);
		}

		if (bl2) {
			packetByteBuf.writeDouble(properties.max);
		}
	}

	public DoubleArgumentSerializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		double d = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readDouble() : -Double.MAX_VALUE;
		double e = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
		return new DoubleArgumentSerializer.Properties(d, e);
	}

	public void writeJson(DoubleArgumentSerializer.Properties properties, JsonObject jsonObject) {
		if (properties.min != -Double.MAX_VALUE) {
			jsonObject.addProperty("min", properties.min);
		}

		if (properties.max != Double.MAX_VALUE) {
			jsonObject.addProperty("max", properties.max);
		}
	}

	public DoubleArgumentSerializer.Properties getArgumentTypeProperties(DoubleArgumentType doubleArgumentType) {
		return new DoubleArgumentSerializer.Properties(doubleArgumentType.getMinimum(), doubleArgumentType.getMaximum());
	}

	public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<DoubleArgumentType> {
		final double min;
		final double max;

		Properties(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public DoubleArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
			return DoubleArgumentType.doubleArg(this.min, this.max);
		}

		@Override
		public ArgumentSerializer<DoubleArgumentType, ?> getSerializer() {
			return DoubleArgumentSerializer.this;
		}
	}
}
