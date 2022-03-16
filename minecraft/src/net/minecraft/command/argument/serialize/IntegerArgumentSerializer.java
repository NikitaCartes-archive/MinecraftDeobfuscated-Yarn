package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.network.PacketByteBuf;

public class IntegerArgumentSerializer implements ArgumentSerializer<IntegerArgumentType, IntegerArgumentSerializer.Properties> {
	public void writePacket(IntegerArgumentSerializer.Properties properties, PacketByteBuf packetByteBuf) {
		boolean bl = properties.min != Integer.MIN_VALUE;
		boolean bl2 = properties.max != Integer.MAX_VALUE;
		packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
		if (bl) {
			packetByteBuf.writeInt(properties.min);
		}

		if (bl2) {
			packetByteBuf.writeInt(properties.max);
		}
	}

	public IntegerArgumentSerializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		int i = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
		int j = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
		return new IntegerArgumentSerializer.Properties(i, j);
	}

	public void writeJson(IntegerArgumentSerializer.Properties properties, JsonObject jsonObject) {
		if (properties.min != Integer.MIN_VALUE) {
			jsonObject.addProperty("min", properties.min);
		}

		if (properties.max != Integer.MAX_VALUE) {
			jsonObject.addProperty("max", properties.max);
		}
	}

	public IntegerArgumentSerializer.Properties getArgumentTypeProperties(IntegerArgumentType integerArgumentType) {
		return new IntegerArgumentSerializer.Properties(integerArgumentType.getMinimum(), integerArgumentType.getMaximum());
	}

	public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<IntegerArgumentType> {
		final int min;
		final int max;

		Properties(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public IntegerArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
			return IntegerArgumentType.integer(this.min, this.max);
		}

		@Override
		public ArgumentSerializer<IntegerArgumentType, ?> getSerializer() {
			return IntegerArgumentSerializer.this;
		}
	}
}
