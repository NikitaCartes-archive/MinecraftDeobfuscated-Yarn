package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class BossBarClientPacket implements Packet<ClientPlayPacketListener> {
	private UUID uuid;
	private BossBarClientPacket.Type type;
	private TextComponent name;
	private float percent;
	private BossBar.Color color;
	private BossBar.Overlay overlay;
	private boolean darkenSky;
	private boolean dragonMusic;
	private boolean thickenFog;

	public BossBarClientPacket() {
	}

	public BossBarClientPacket(BossBarClientPacket.Type type, BossBar bossBar) {
		this.type = type;
		this.uuid = bossBar.getUuid();
		this.name = bossBar.getName();
		this.percent = bossBar.getPercent();
		this.color = bossBar.getColor();
		this.overlay = bossBar.getOverlay();
		this.darkenSky = bossBar.getDarkenSky();
		this.dragonMusic = bossBar.hasDragonMusic();
		this.thickenFog = bossBar.getThickenFog();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.uuid = packetByteBuf.readUuid();
		this.type = packetByteBuf.readEnumConstant(BossBarClientPacket.Type.class);
		switch (this.type) {
			case ADD:
				this.name = packetByteBuf.readTextComponent();
				this.percent = packetByteBuf.readFloat();
				this.color = packetByteBuf.readEnumConstant(BossBar.Color.class);
				this.overlay = packetByteBuf.readEnumConstant(BossBar.Overlay.class);
				this.setFlagBitfield(packetByteBuf.readUnsignedByte());
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				this.percent = packetByteBuf.readFloat();
				break;
			case UPDATE_TITLE:
				this.name = packetByteBuf.readTextComponent();
				break;
			case UPDATE_STYLE:
				this.color = packetByteBuf.readEnumConstant(BossBar.Color.class);
				this.overlay = packetByteBuf.readEnumConstant(BossBar.Overlay.class);
				break;
			case UPDATE_FLAGS:
				this.setFlagBitfield(packetByteBuf.readUnsignedByte());
		}
	}

	private void setFlagBitfield(int i) {
		this.darkenSky = (i & 1) > 0;
		this.dragonMusic = (i & 2) > 0;
		this.thickenFog = (i & 4) > 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeEnumConstant(this.type);
		switch (this.type) {
			case ADD:
				packetByteBuf.writeTextComponent(this.name);
				packetByteBuf.writeFloat(this.percent);
				packetByteBuf.writeEnumConstant(this.color);
				packetByteBuf.writeEnumConstant(this.overlay);
				packetByteBuf.writeByte(this.getFlagBitfield());
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				packetByteBuf.writeFloat(this.percent);
				break;
			case UPDATE_TITLE:
				packetByteBuf.writeTextComponent(this.name);
				break;
			case UPDATE_STYLE:
				packetByteBuf.writeEnumConstant(this.color);
				packetByteBuf.writeEnumConstant(this.overlay);
				break;
			case UPDATE_FLAGS:
				packetByteBuf.writeByte(this.getFlagBitfield());
		}
	}

	private int getFlagBitfield() {
		int i = 0;
		if (this.darkenSky) {
			i |= 1;
		}

		if (this.dragonMusic) {
			i |= 2;
		}

		if (this.thickenFog) {
			i |= 4;
		}

		return i;
	}

	public void method_11330(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBossBar(this);
	}

	@Environment(EnvType.CLIENT)
	public UUID getUuid() {
		return this.uuid;
	}

	@Environment(EnvType.CLIENT)
	public BossBarClientPacket.Type getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public float getPercent() {
		return this.percent;
	}

	@Environment(EnvType.CLIENT)
	public BossBar.Color getColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	public BossBar.Overlay getOverlay() {
		return this.overlay;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldDarkenSky() {
		return this.darkenSky;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasDragonMusic() {
		return this.dragonMusic;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldThickenFog() {
		return this.thickenFog;
	}

	public static enum Type {
		ADD,
		REMOVE,
		UPDATE_PCT,
		UPDATE_TITLE,
		UPDATE_STYLE,
		UPDATE_FLAGS;
	}
}
