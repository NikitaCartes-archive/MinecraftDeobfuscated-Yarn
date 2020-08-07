package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ClientBossBar extends BossBar {
	protected float healthLatest;
	protected long timeHealthSet;

	public ClientBossBar(BossBarS2CPacket packet) {
		super(packet.getUuid(), packet.getName(), packet.getColor(), packet.getOverlay());
		this.healthLatest = packet.getPercent();
		this.percent = packet.getPercent();
		this.timeHealthSet = Util.getMeasuringTimeMs();
		this.setDarkenSky(packet.shouldDarkenSky());
		this.setDragonMusic(packet.hasDragonMusic());
		this.setThickenFog(packet.shouldThickenFog());
	}

	@Override
	public void setPercent(float percentage) {
		this.percent = this.getPercent();
		this.healthLatest = percentage;
		this.timeHealthSet = Util.getMeasuringTimeMs();
	}

	@Override
	public float getPercent() {
		long l = Util.getMeasuringTimeMs() - this.timeHealthSet;
		float f = MathHelper.clamp((float)l / 100.0F, 0.0F, 1.0F);
		return MathHelper.lerp(f, this.percent, this.healthLatest);
	}

	public void handlePacket(BossBarS2CPacket packet) {
		switch (packet.getType()) {
			case field_12084:
				this.setName(packet.getName());
				break;
			case field_12080:
				this.setPercent(packet.getPercent());
				break;
			case field_12081:
				this.setColor(packet.getColor());
				this.setOverlay(packet.getOverlay());
				break;
			case field_12083:
				this.setDarkenSky(packet.shouldDarkenSky());
				this.setDragonMusic(packet.hasDragonMusic());
		}
	}
}
