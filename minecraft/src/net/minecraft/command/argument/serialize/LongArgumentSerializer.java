package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.network.PacketByteBuf;

public class LongArgumentSerializer implements ArgumentSerializer<LongArgumentType, LongArgumentSerializer.Properties> {
	public void writePacket(LongArgumentSerializer.Properties properties, PacketByteBuf packetByteBuf) {
		boolean bl = properties.min != Long.MIN_VALUE;
		boolean bl2 = properties.max != Long.MAX_VALUE;
		packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
		if (bl) {
			packetByteBuf.writeLong(properties.min);
		}

		if (bl2) {
			packetByteBuf.writeLong(properties.max);
		}
	}

	public LongArgumentSerializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		long l = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readLong() : Long.MIN_VALUE;
		long m = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readLong() : Long.MAX_VALUE;
		return new LongArgumentSerializer.Properties(l, m);
	}

	public void writeJson(LongArgumentSerializer.Properties properties, JsonObject jsonObject) {
		if (properties.min != Long.MIN_VALUE) {
			jsonObject.addProperty("min", properties.min);
		}

		if (properties.max != Long.MAX_VALUE) {
			jsonObject.addProperty("max", properties.max);
		}
	}

	public LongArgumentSerializer.Properties getArgumentTypeProperties(LongArgumentType longArgumentType) {
		return new LongArgumentSerializer.Properties(longArgumentType.getMinimum(), longArgumentType.getMaximum());
	}

	public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<LongArgumentType> {
		final long min;
		final long max;

		Properties(long min, long max) {
			this.min = min;
			this.max = max;
		}

		public LongArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
			return LongArgumentType.longArg(this.min, this.max);
		}

		@Override
		public ArgumentSerializer<LongArgumentType, ?> getSerializer() {
			return LongArgumentSerializer.this;
		}
	}
}
