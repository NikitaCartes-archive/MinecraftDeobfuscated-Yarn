package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class GameStateChangeS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final GameStateChangeS2CPacket.class_5402 field_25645 = new GameStateChangeS2CPacket.class_5402(0);
	public static final GameStateChangeS2CPacket.class_5402 field_25646 = new GameStateChangeS2CPacket.class_5402(1);
	public static final GameStateChangeS2CPacket.class_5402 field_25647 = new GameStateChangeS2CPacket.class_5402(2);
	public static final GameStateChangeS2CPacket.class_5402 field_25648 = new GameStateChangeS2CPacket.class_5402(3);
	public static final GameStateChangeS2CPacket.class_5402 field_25649 = new GameStateChangeS2CPacket.class_5402(4);
	public static final GameStateChangeS2CPacket.class_5402 field_25650 = new GameStateChangeS2CPacket.class_5402(5);
	public static final GameStateChangeS2CPacket.class_5402 field_25651 = new GameStateChangeS2CPacket.class_5402(6);
	public static final GameStateChangeS2CPacket.class_5402 field_25652 = new GameStateChangeS2CPacket.class_5402(7);
	public static final GameStateChangeS2CPacket.class_5402 field_25653 = new GameStateChangeS2CPacket.class_5402(8);
	public static final GameStateChangeS2CPacket.class_5402 field_25654 = new GameStateChangeS2CPacket.class_5402(9);
	public static final GameStateChangeS2CPacket.class_5402 field_25655 = new GameStateChangeS2CPacket.class_5402(10);
	public static final GameStateChangeS2CPacket.class_5402 field_25656 = new GameStateChangeS2CPacket.class_5402(11);
	private GameStateChangeS2CPacket.class_5402 reason;
	private float value;

	public GameStateChangeS2CPacket() {
	}

	public GameStateChangeS2CPacket(GameStateChangeS2CPacket.class_5402 arg, float value) {
		this.reason = arg;
		this.value = value;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.reason = GameStateChangeS2CPacket.class_5402.field_25657.get(buf.readUnsignedByte());
		this.value = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.reason.field_25658);
		buf.writeFloat(this.value);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameStateChange(this);
	}

	@Environment(EnvType.CLIENT)
	public GameStateChangeS2CPacket.class_5402 getReason() {
		return this.reason;
	}

	@Environment(EnvType.CLIENT)
	public float getValue() {
		return this.value;
	}

	public static class class_5402 {
		private static final Int2ObjectMap<GameStateChangeS2CPacket.class_5402> field_25657 = new Int2ObjectOpenHashMap<>();
		private final int field_25658;

		public class_5402(int i) {
			this.field_25658 = i;
			field_25657.put(i, this);
		}
	}
}
