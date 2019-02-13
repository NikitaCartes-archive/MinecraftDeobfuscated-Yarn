package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ClientBossBar extends BossBar {
	protected float healthLatest;
	protected long timeHealthSet;

	public ClientBossBar(BossBarS2CPacket bossBarS2CPacket) {
		super(bossBarS2CPacket.getUuid(), bossBarS2CPacket.getName(), bossBarS2CPacket.getColor(), bossBarS2CPacket.getOverlay());
		this.healthLatest = bossBarS2CPacket.getPercent();
		this.percent = bossBarS2CPacket.getPercent();
		this.timeHealthSet = SystemUtil.getMeasuringTimeMs();
		this.setDarkenSky(bossBarS2CPacket.shouldDarkenSky());
		this.setDragonMusic(bossBarS2CPacket.hasDragonMusic());
		this.setThickenFog(bossBarS2CPacket.shouldThickenFog());
	}

	@Override
	public void setPercent(float f) {
		this.percent = this.getPercent();
		this.healthLatest = f;
		this.timeHealthSet = SystemUtil.getMeasuringTimeMs();
	}

	@Override
	public float getPercent() {
		long l = SystemUtil.getMeasuringTimeMs() - this.timeHealthSet;
		float f = MathHelper.clamp((float)l / 100.0F, 0.0F, 1.0F);
		return MathHelper.lerp(f, this.percent, this.healthLatest);
	}

	public void handlePacket(BossBarS2CPacket bossBarS2CPacket) {
		switch (bossBarS2CPacket.getType()) {
			case UPDATE_TITLE:
				this.setName(bossBarS2CPacket.getName());
				break;
			case UPDATE_PCT:
				this.setPercent(bossBarS2CPacket.getPercent());
				break;
			case UPDATE_STYLE:
				this.setColor(bossBarS2CPacket.getColor());
				this.setOverlay(bossBarS2CPacket.getOverlay());
				break;
			case UPDATE_FLAGS:
				this.setDarkenSky(bossBarS2CPacket.shouldDarkenSky());
				this.setDragonMusic(bossBarS2CPacket.hasDragonMusic());
		}
	}
}
