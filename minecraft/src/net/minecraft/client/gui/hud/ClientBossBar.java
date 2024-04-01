package net.minecraft.client.gui.hud;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ClientBossBar extends BossBar {
	private static final long HEALTH_CHANGE_ANIMATION_MS = 100L;
	protected float healthLatest;
	protected long timeHealthSet;

	public ClientBossBar(
		UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog, Vec3d vec3d, int i
	) {
		super(uuid, name, color, style, vec3d, i);
		this.healthLatest = percent;
		this.percent = percent;
		this.timeHealthSet = Util.getMeasuringTimeMs();
		this.setDarkenSky(darkenSky);
		this.setDragonMusic(dragonMusic);
		this.setThickenFog(thickenFog);
	}

	@Override
	public void setPercent(float percent) {
		this.percent = this.getPercent();
		this.healthLatest = percent;
		this.timeHealthSet = Util.getMeasuringTimeMs();
	}

	@Override
	public float getPercent() {
		long l = Util.getMeasuringTimeMs() - this.timeHealthSet;
		float f = MathHelper.clamp((float)l / 100.0F, 0.0F, 1.0F);
		return MathHelper.lerp(f, this.percent, this.healthLatest);
	}

	public boolean method_59327(Vec3d vec3d) {
		return this.field_50379 > 0 ? this.field_50378.squaredDistanceTo(vec3d) < (double)(this.field_50379 * this.field_50379) : this.field_50379 >= 0;
	}
}
