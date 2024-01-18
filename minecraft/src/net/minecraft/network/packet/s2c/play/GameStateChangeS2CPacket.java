package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class GameStateChangeS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, GameStateChangeS2CPacket> CODEC = Packet.createCodec(
		GameStateChangeS2CPacket::write, GameStateChangeS2CPacket::new
	);
	public static final GameStateChangeS2CPacket.Reason NO_RESPAWN_BLOCK = new GameStateChangeS2CPacket.Reason(0);
	public static final GameStateChangeS2CPacket.Reason RAIN_STARTED = new GameStateChangeS2CPacket.Reason(1);
	public static final GameStateChangeS2CPacket.Reason RAIN_STOPPED = new GameStateChangeS2CPacket.Reason(2);
	public static final GameStateChangeS2CPacket.Reason GAME_MODE_CHANGED = new GameStateChangeS2CPacket.Reason(3);
	public static final GameStateChangeS2CPacket.Reason GAME_WON = new GameStateChangeS2CPacket.Reason(4);
	public static final GameStateChangeS2CPacket.Reason DEMO_MESSAGE_SHOWN = new GameStateChangeS2CPacket.Reason(5);
	public static final GameStateChangeS2CPacket.Reason PROJECTILE_HIT_PLAYER = new GameStateChangeS2CPacket.Reason(6);
	public static final GameStateChangeS2CPacket.Reason RAIN_GRADIENT_CHANGED = new GameStateChangeS2CPacket.Reason(7);
	public static final GameStateChangeS2CPacket.Reason THUNDER_GRADIENT_CHANGED = new GameStateChangeS2CPacket.Reason(8);
	public static final GameStateChangeS2CPacket.Reason PUFFERFISH_STING = new GameStateChangeS2CPacket.Reason(9);
	public static final GameStateChangeS2CPacket.Reason ELDER_GUARDIAN_EFFECT = new GameStateChangeS2CPacket.Reason(10);
	public static final GameStateChangeS2CPacket.Reason IMMEDIATE_RESPAWN = new GameStateChangeS2CPacket.Reason(11);
	public static final GameStateChangeS2CPacket.Reason LIMITED_CRAFTING_TOGGLED = new GameStateChangeS2CPacket.Reason(12);
	public static final GameStateChangeS2CPacket.Reason INITIAL_CHUNKS_COMING = new GameStateChangeS2CPacket.Reason(13);
	public static final int DEMO_OPEN_SCREEN = 0;
	public static final int DEMO_MOVEMENT_HELP = 101;
	public static final int DEMO_JUMP_HELP = 102;
	public static final int DEMO_INVENTORY_HELP = 103;
	public static final int DEMO_EXPIRY_NOTICE = 104;
	private final GameStateChangeS2CPacket.Reason reason;
	private final float value;

	public GameStateChangeS2CPacket(GameStateChangeS2CPacket.Reason reason, float value) {
		this.reason = reason;
		this.value = value;
	}

	private GameStateChangeS2CPacket(PacketByteBuf buf) {
		this.reason = GameStateChangeS2CPacket.Reason.REASONS.get(buf.readUnsignedByte());
		this.value = buf.readFloat();
	}

	private void write(PacketByteBuf buf) {
		buf.writeByte(this.reason.id);
		buf.writeFloat(this.value);
	}

	@Override
	public PacketType<GameStateChangeS2CPacket> getPacketId() {
		return PlayPackets.GAME_EVENT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameStateChange(this);
	}

	public GameStateChangeS2CPacket.Reason getReason() {
		return this.reason;
	}

	public float getValue() {
		return this.value;
	}

	public static class Reason {
		static final Int2ObjectMap<GameStateChangeS2CPacket.Reason> REASONS = new Int2ObjectOpenHashMap<>();
		final int id;

		public Reason(int id) {
			this.id = id;
			REASONS.put(id, this);
		}
	}
}
