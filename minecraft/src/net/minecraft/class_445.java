package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_445 extends class_437 {
	private static final Logger field_2632 = LogManager.getLogger();
	private static final class_2960 field_2626 = new class_2960("textures/gui/title/minecraft.png");
	private static final class_2960 field_2631 = new class_2960("textures/gui/title/edition.png");
	private static final class_2960 field_2633 = new class_2960("textures/misc/vignette.png");
	private final boolean field_2627;
	private final Runnable field_2630;
	private float field_2628;
	private List<String> field_2634;
	private int field_2629;
	private float field_2635 = 0.5F;

	public class_445(boolean bl, Runnable runnable) {
		super(class_333.field_18967);
		this.field_2627 = bl;
		this.field_2630 = runnable;
		if (!bl) {
			this.field_2635 = 0.75F;
		}
	}

	@Override
	public void tick() {
		this.minecraft.method_1538().method_18669();
		this.minecraft.method_1483().method_18670(false);
		float f = (float)(this.field_2629 + this.height + this.height + 24) / this.field_2635;
		if (this.field_2628 > f) {
			this.method_2257();
		}
	}

	@Override
	public void onClose() {
		this.method_2257();
	}

	private void method_2257() {
		this.field_2630.run();
		this.minecraft.method_1507(null);
	}

	@Override
	protected void init() {
		if (this.field_2634 == null) {
			this.field_2634 = Lists.<String>newArrayList();
			class_3298 lv = null;

			try {
				String string = "" + class_124.field_1068 + class_124.field_1051 + class_124.field_1060 + class_124.field_1075;
				int i = 274;
				if (this.field_2627) {
					lv = this.minecraft.method_1478().method_14486(new class_2960("texts/end.txt"));
					InputStream inputStream = lv.method_14482();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					Random random = new Random(8124371L);

					String string2;
					while ((string2 = bufferedReader.readLine()) != null) {
						string2 = string2.replaceAll("PLAYERNAME", this.minecraft.method_1548().method_1676());

						while (string2.contains(string)) {
							int j = string2.indexOf(string);
							String string3 = string2.substring(0, j);
							String string4 = string2.substring(j + string.length());
							string2 = string3 + class_124.field_1068 + class_124.field_1051 + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
						}

						this.field_2634.addAll(this.minecraft.field_1772.method_1728(string2, 274));
						this.field_2634.add("");
					}

					inputStream.close();

					for (int j = 0; j < 8; j++) {
						this.field_2634.add("");
					}
				}

				InputStream inputStream = this.minecraft.method_1478().method_14486(new class_2960("texts/credits.txt")).method_14482();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

				String string5;
				while ((string5 = bufferedReader.readLine()) != null) {
					string5 = string5.replaceAll("PLAYERNAME", this.minecraft.method_1548().method_1676());
					string5 = string5.replaceAll("\t", "    ");
					this.field_2634.addAll(this.minecraft.field_1772.method_1728(string5, 274));
					this.field_2634.add("");
				}

				inputStream.close();
				this.field_2629 = this.field_2634.size() * 12;
			} catch (Exception var14) {
				field_2632.error("Couldn't load credits", (Throwable)var14);
			} finally {
				IOUtils.closeQuietly(lv);
			}
		}
	}

	private void method_2258(int i, int j, float f) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.minecraft.method_1531().method_4618(class_332.BACKGROUND_LOCATION);
		lv2.method_1328(7, class_290.field_1575);
		int k = this.width;
		float g = -this.field_2628 * 0.5F * this.field_2635;
		float h = (float)this.height - this.field_2628 * 0.5F * this.field_2635;
		float l = 0.015625F;
		float m = this.field_2628 * 0.02F;
		float n = (float)(this.field_2629 + this.height + this.height + 24) / this.field_2635;
		float o = (n - 20.0F - this.field_2628) * 0.005F;
		if (o < m) {
			m = o;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		m *= m;
		m = m * 96.0F / 255.0F;
		lv2.method_1315(0.0, (double)this.height, (double)this.blitOffset).method_1312(0.0, (double)(g * 0.015625F)).method_1336(m, m, m, 1.0F).method_1344();
		lv2.method_1315((double)k, (double)this.height, (double)this.blitOffset)
			.method_1312((double)((float)k * 0.015625F), (double)(g * 0.015625F))
			.method_1336(m, m, m, 1.0F)
			.method_1344();
		lv2.method_1315((double)k, 0.0, (double)this.blitOffset)
			.method_1312((double)((float)k * 0.015625F), (double)(h * 0.015625F))
			.method_1336(m, m, m, 1.0F)
			.method_1344();
		lv2.method_1315(0.0, 0.0, (double)this.blitOffset).method_1312(0.0, (double)(h * 0.015625F)).method_1336(m, m, m, 1.0F).method_1344();
		lv.method_1350();
	}

	@Override
	public void render(int i, int j, float f) {
		this.method_2258(i, j, f);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		int k = 274;
		int l = this.width / 2 - 137;
		int m = this.height + 50;
		this.field_2628 += f;
		float g = -this.field_2628 * this.field_2635;
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, g, 0.0F);
		this.minecraft.method_1531().method_4618(field_2626);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.blit(l, m, 0, 0, 155, 44);
		this.blit(l + 155, m, 0, 45, 155, 44);
		this.minecraft.method_1531().method_4618(field_2631);
		blit(l + 88, m + 37, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
		GlStateManager.disableAlphaTest();
		int n = m + 100;

		for (int o = 0; o < this.field_2634.size(); o++) {
			if (o == this.field_2634.size() - 1) {
				float h = (float)n + g - (float)(this.height / 2 - 6);
				if (h < 0.0F) {
					GlStateManager.translatef(0.0F, -h, 0.0F);
				}
			}

			if ((float)n + g + 12.0F + 8.0F > 0.0F && (float)n + g < (float)this.height) {
				String string = (String)this.field_2634.get(o);
				if (string.startsWith("[C]")) {
					this.font.method_1720(string.substring(3), (float)(l + (274 - this.font.method_1727(string.substring(3))) / 2), (float)n, 16777215);
				} else {
					this.font.field_2001.setSeed((long)((float)((long)o * 4238972211L) + this.field_2628 / 4.0F));
					this.font.method_1720(string, (float)l, (float)n, 16777215);
				}
			}

			n += 12;
		}

		GlStateManager.popMatrix();
		this.minecraft.method_1531().method_4618(field_2633);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
		int o = this.width;
		int p = this.height;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315(0.0, (double)p, (double)this.blitOffset).method_1312(0.0, 1.0).method_1336(1.0F, 1.0F, 1.0F, 1.0F).method_1344();
		lv2.method_1315((double)o, (double)p, (double)this.blitOffset).method_1312(1.0, 1.0).method_1336(1.0F, 1.0F, 1.0F, 1.0F).method_1344();
		lv2.method_1315((double)o, 0.0, (double)this.blitOffset).method_1312(1.0, 0.0).method_1336(1.0F, 1.0F, 1.0F, 1.0F).method_1344();
		lv2.method_1315(0.0, 0.0, (double)this.blitOffset).method_1312(0.0, 0.0).method_1336(1.0F, 1.0F, 1.0F, 1.0F).method_1344();
		lv.method_1350();
		GlStateManager.disableBlend();
		super.render(i, j, f);
	}
}
