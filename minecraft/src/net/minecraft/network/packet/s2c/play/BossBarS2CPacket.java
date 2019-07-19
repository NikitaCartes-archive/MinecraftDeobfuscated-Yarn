package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class BossBarS2CPacket implements Packet<ClientPlayPacketListener> {
	private UUID uuid;
	private BossBarS2CPacket.Type type;
	private Text name;
	private float percent;
	private BossBar.Color color;
	private BossBar.Style overlay;
	private boolean darkenSky;
	private boolean dragonMusic;
	private boolean thickenFog;

	public BossBarS2CPacket() {
	}

	public BossBarS2CPacket(BossBarS2CPacket.Type action, BossBar bossBar) {
		this.type = action;
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
	public void read(PacketByteBuf buf) throws IOException {
		this.uuid = buf.readUuid();
		this.type = buf.readEnumConstant(BossBarS2CPacket.Type.class);
		switch (this.type) {
			case ADD:
				this.name = buf.readText();
				this.percent = buf.readFloat();
				this.color = buf.readEnumConstant(BossBar.Color.class);
				this.overlay = buf.readEnumConstant(BossBar.Style.class);
				this.setFlagBitfield(buf.readUnsignedByte());
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				this.percent = buf.readFloat();
				break;
			case UPDATE_NAME:
				this.name = buf.readText();
				break;
			case UPDATE_STYLE:
				this.color = buf.readEnumConstant(BossBar.Color.class);
				this.overlay = buf.readEnumConstant(BossBar.Style.class);
				break;
			case UPDATE_PROPERTIES:
				this.setFlagBitfield(buf.readUnsignedByte());
		}
	}

	private void setFlagBitfield(int i) {
		this.darkenSky = (i & 1) > 0;
		this.dragonMusic = (i & 2) > 0;
		this.thickenFog = (i & 4) > 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeUuid(this.uuid);
		buf.writeEnumConstant(this.type);
		switch (this.type) {
			case ADD:
				buf.writeText(this.name);
				buf.writeFloat(this.percent);
				buf.writeEnumConstant(this.color);
				buf.writeEnumConstant(this.overlay);
				buf.writeByte(this.getFlagBitfield());
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				buf.writeFloat(this.percent);
				break;
			case UPDATE_NAME:
				buf.writeText(this.name);
				break;
			case UPDATE_STYLE:
				buf.writeEnumConstant(this.color);
				buf.writeEnumConstant(this.overlay);
				break;
			case UPDATE_PROPERTIES:
				buf.writeByte(this.getFlagBitfield());
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

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBossBar(this);
	}

	@Environment(EnvType.CLIENT)
	public UUID getUuid() {
		return this.uuid;
	}

	@Environment(EnvType.CLIENT)
	public BossBarS2CPacket.Type getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public Text getName() {
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
	public BossBar.Style getOverlay() {
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
		UPDATE_NAME,
		UPDATE_STYLE,
		UPDATE_PROPERTIES;
	}
}
