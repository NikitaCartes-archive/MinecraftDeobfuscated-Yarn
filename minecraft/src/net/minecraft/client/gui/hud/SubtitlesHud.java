package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.ListenerSoundInstance;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.WeightedSoundSet;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class SubtitlesHud extends DrawableHelper implements ListenerSoundInstance {
	private final MinecraftClient client;
	private final List<SubtitlesHud.SubtitleEntry> entries = Lists.<SubtitlesHud.SubtitleEntry>newArrayList();
	private boolean enabled;

	public SubtitlesHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void draw() {
		if (!this.enabled && this.client.options.showSubtitles) {
			this.client.getSoundLoader().registerListener(this);
			this.enabled = true;
		} else if (this.enabled && !this.client.options.showSubtitles) {
			this.client.getSoundLoader().unregisterListener(this);
			this.enabled = false;
		}

		if (this.enabled && !this.entries.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			Vec3d vec3d = new Vec3d(this.client.player.x, this.client.player.y + (double)this.client.player.getStandingEyeHeight(), this.client.player.z);
			Vec3d vec3d2 = new Vec3d(0.0, 0.0, -1.0)
				.rotateX(-this.client.player.pitch * (float) (Math.PI / 180.0))
				.rotateY(-this.client.player.yaw * (float) (Math.PI / 180.0));
			Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0)
				.rotateX(-this.client.player.pitch * (float) (Math.PI / 180.0))
				.rotateY(-this.client.player.yaw * (float) (Math.PI / 180.0));
			Vec3d vec3d4 = vec3d2.crossProduct(vec3d3);
			int i = 0;
			int j = 0;
			Iterator<SubtitlesHud.SubtitleEntry> iterator = this.entries.iterator();

			while (iterator.hasNext()) {
				SubtitlesHud.SubtitleEntry subtitleEntry = (SubtitlesHud.SubtitleEntry)iterator.next();
				if (subtitleEntry.getTime() + 3000L <= SystemUtil.getMeasuringTimeMs()) {
					iterator.remove();
				} else {
					j = Math.max(j, this.client.textRenderer.getStringWidth(subtitleEntry.getText()));
				}
			}

			j += this.client.textRenderer.getStringWidth("<")
				+ this.client.textRenderer.getStringWidth(" ")
				+ this.client.textRenderer.getStringWidth(">")
				+ this.client.textRenderer.getStringWidth(" ");

			for (SubtitlesHud.SubtitleEntry subtitleEntry : this.entries) {
				int k = 255;
				String string = subtitleEntry.getText();
				Vec3d vec3d5 = subtitleEntry.getPosition().subtract(vec3d).normalize();
				double d = -vec3d4.dotProduct(vec3d5);
				double e = -vec3d2.dotProduct(vec3d5);
				boolean bl = e > 0.5;
				int l = j / 2;
				int m = 9;
				int n = m / 2;
				float f = 1.0F;
				int o = this.client.textRenderer.getStringWidth(string);
				int p = MathHelper.floor(MathHelper.lerpClamped(255.0, 75.0, (double)((float)(SystemUtil.getMeasuringTimeMs() - subtitleEntry.getTime()) / 3000.0F)));
				int q = p << 16 | p << 8 | p;
				GlStateManager.pushMatrix();
				GlStateManager.translatef(
					(float)this.client.window.getScaledWidth() - (float)l * 1.0F - 2.0F,
					(float)(this.client.window.getScaledHeight() - 30) - (float)(i * (m + 1)) * 1.0F,
					0.0F
				);
				GlStateManager.scalef(1.0F, 1.0F, 1.0F);
				drawRect(-l - 1, -n - 1, l + 1, n + 1, this.client.options.method_19345(0.8F));
				GlStateManager.enableBlend();
				if (!bl) {
					if (d > 0.0) {
						this.client.textRenderer.draw(">", (float)(l - this.client.textRenderer.getStringWidth(">")), (float)(-n), q + -16777216);
					} else if (d < 0.0) {
						this.client.textRenderer.draw("<", (float)(-l), (float)(-n), q + -16777216);
					}
				}

				this.client.textRenderer.draw(string, (float)(-o / 2), (float)(-n), q + -16777216);
				GlStateManager.popMatrix();
				i++;
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void onSoundPlayed(SoundInstance soundInstance, WeightedSoundSet weightedSoundSet) {
		if (weightedSoundSet.getSubtitle() != null) {
			String string = weightedSoundSet.getSubtitle().getFormattedText();
			if (!this.entries.isEmpty()) {
				for (SubtitlesHud.SubtitleEntry subtitleEntry : this.entries) {
					if (subtitleEntry.getText().equals(string)) {
						subtitleEntry.reset(new Vec3d((double)soundInstance.getX(), (double)soundInstance.getY(), (double)soundInstance.getZ()));
						return;
					}
				}
			}

			this.entries
				.add(new SubtitlesHud.SubtitleEntry(string, new Vec3d((double)soundInstance.getX(), (double)soundInstance.getY(), (double)soundInstance.getZ())));
		}
	}

	@Environment(EnvType.CLIENT)
	public class SubtitleEntry {
		private final String text;
		private long time;
		private Vec3d pos;

		public SubtitleEntry(String string, Vec3d vec3d) {
			this.text = string;
			this.pos = vec3d;
			this.time = SystemUtil.getMeasuringTimeMs();
		}

		public String getText() {
			return this.text;
		}

		public long getTime() {
			return this.time;
		}

		public Vec3d getPosition() {
			return this.pos;
		}

		public void reset(Vec3d vec3d) {
			this.pos = vec3d;
			this.time = SystemUtil.getMeasuringTimeMs();
		}
	}
}
