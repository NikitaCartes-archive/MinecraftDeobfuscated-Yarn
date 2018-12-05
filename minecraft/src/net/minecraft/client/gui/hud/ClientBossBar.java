package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BossBarClientPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ClientBossBar extends BossBar {
	protected float healthLatest;
	protected long timeHealthSet;

	public ClientBossBar(BossBarClientPacket bossBarClientPacket) {
		super(bossBarClientPacket.getUuid(), bossBarClientPacket.getName(), bossBarClientPacket.getColor(), bossBarClientPacket.getOverlay());
		this.healthLatest = bossBarClientPacket.getPercent();
		this.percent = bossBarClientPacket.getPercent();
		this.timeHealthSet = SystemUtil.getMeasuringTimeMili();
		this.setDarkenSky(bossBarClientPacket.shouldDarkenSky());
		this.setDragonMusic(bossBarClientPacket.hasDragonMusic());
		this.setThickenFog(bossBarClientPacket.shouldThickenFog());
	}

	@Override
	public void setPercent(float f) {
		this.percent = this.getPercent();
		this.healthLatest = f;
		this.timeHealthSet = SystemUtil.getMeasuringTimeMili();
	}

	@Override
	public float getPercent() {
		long l = SystemUtil.getMeasuringTimeMili() - this.timeHealthSet;
		float f = MathHelper.clamp((float)l / 100.0F, 0.0F, 1.0F);
		return MathHelper.lerp(f, this.percent, this.healthLatest);
	}

	public void handlePacket(BossBarClientPacket bossBarClientPacket) {
		switch (bossBarClientPacket.getType()) {
			case UPDATE_TITLE:
				this.setName(bossBarClientPacket.getName());
				break;
			case UPDATE_PCT:
				this.setPercent(bossBarClientPacket.getPercent());
				break;
			case UPDATE_STYLE:
				this.setColor(bossBarClientPacket.getColor());
				this.setOverlay(bossBarClientPacket.getOverlay());
				break;
			case UPDATE_FLAGS:
				this.setDarkenSky(bossBarClientPacket.shouldDarkenSky());
				this.setDragonMusic(bossBarClientPacket.hasDragonMusic());
		}
	}
}
