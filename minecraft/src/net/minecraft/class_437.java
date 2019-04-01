package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class class_437 extends class_362 implements class_4068, class_411 {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.<String>newHashSet("http", "https");
	protected static final int CONFIRM_URL_BUTTON_ID = 31102009;
	protected final class_2561 title;
	protected final List<class_364> children = Lists.<class_364>newArrayList();
	@Nullable
	protected class_310 minecraft;
	protected class_918 itemRenderer;
	public int width;
	public int height;
	protected final List<class_339> buttons = Lists.<class_339>newArrayList();
	public boolean passEvents;
	protected class_327 font;
	private URI clickedLink;

	protected class_437(class_2561 arg) {
		this.title = arg;
	}

	public class_2561 getTitle() {
		return this.title;
	}

	public String getNarrationMessage() {
		return this.getTitle().getString();
	}

	@Override
	public void render(int i, int j, float f) {
		for (int k = 0; k < this.buttons.size(); k++) {
			((class_339)this.buttons.get(k)).render(i, j, f);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256 && this.shouldCloseOnEsc()) {
			this.onClose();
			return true;
		} else if (i == 258) {
			boolean bl = !hasShiftDown();
			if (!this.method_20087(bl)) {
				this.method_20087(bl);
			}

			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	public boolean shouldCloseOnEsc() {
		return true;
	}

	public void onClose() {
		this.minecraft.method_1507(null);
	}

	protected <T extends class_339> T addButton(T arg) {
		this.buttons.add(arg);
		this.children.add(arg);
		return arg;
	}

	protected void renderTooltip(class_1799 arg, int i, int j) {
		this.renderTooltip(this.getTooltipFromItem(arg), i, j);
	}

	public List<String> getTooltipFromItem(class_1799 arg) {
		List<class_2561> list = arg.method_7950(
			this.minecraft.field_1724, this.minecraft.field_1690.field_1827 ? class_1836.class_1837.field_8935 : class_1836.class_1837.field_8934
		);
		List<String> list2 = Lists.<String>newArrayList();

		for (class_2561 lv : list) {
			list2.add(lv.method_10863());
		}

		return list2;
	}

	public void renderTooltip(String string, int i, int j) {
		this.renderTooltip(Arrays.asList(string), i, j);
	}

	public void renderTooltip(List<String> list, int i, int j) {
		if (!list.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			class_308.method_1450();
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			int k = 0;

			for (String string : list) {
				int l = this.font.method_1727(string);
				if (l > k) {
					k = l;
				}
			}

			int m = i + 12;
			int n = j - 12;
			int o = 8;
			if (list.size() > 1) {
				o += 2 + (list.size() - 1) * 10;
			}

			if (m + k > this.width) {
				m -= 28 + k;
			}

			if (n + o + 6 > this.height) {
				n = this.height - o - 6;
			}

			this.blitOffset = 300.0F;
			this.itemRenderer.field_4730 = 300.0F;
			int p = -267386864;
			this.fillGradient(m - 3, n - 4, m + k + 3, n - 3, -267386864, -267386864);
			this.fillGradient(m - 3, n + o + 3, m + k + 3, n + o + 4, -267386864, -267386864);
			this.fillGradient(m - 3, n - 3, m + k + 3, n + o + 3, -267386864, -267386864);
			this.fillGradient(m - 4, n - 3, m - 3, n + o + 3, -267386864, -267386864);
			this.fillGradient(m + k + 3, n - 3, m + k + 4, n + o + 3, -267386864, -267386864);
			int q = 1347420415;
			int r = 1344798847;
			this.fillGradient(m - 3, n - 3 + 1, m - 3 + 1, n + o + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(m + k + 2, n - 3 + 1, m + k + 3, n + o + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(m - 3, n - 3, m + k + 3, n - 3 + 1, 1347420415, 1347420415);
			this.fillGradient(m - 3, n + o + 2, m + k + 3, n + o + 3, 1344798847, 1344798847);

			for (int s = 0; s < list.size(); s++) {
				String string2 = (String)list.get(s);
				this.font.method_1720(string2, (float)m, (float)n, -1);
				if (s == 0) {
					n += 2;
				}

				n += 10;
			}

			this.blitOffset = 0.0F;
			this.itemRenderer.field_4730 = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			class_308.method_1452();
			GlStateManager.enableRescaleNormal();
		}
	}

	protected void renderComponentHoverEffect(class_2561 arg, int i, int j) {
		if (arg != null && arg.method_10866().method_10969() != null) {
			class_2568 lv = arg.method_10866().method_10969();
			if (lv.method_10892() == class_2568.class_2569.field_11757) {
				class_1799 lv2 = class_1799.field_8037;

				try {
					class_2520 lv3 = class_2522.method_10718(lv.method_10891().getString());
					if (lv3 instanceof class_2487) {
						lv2 = class_1799.method_7915((class_2487)lv3);
					}
				} catch (CommandSyntaxException var10) {
				}

				if (lv2.method_7960()) {
					this.renderTooltip(class_124.field_1061 + "Invalid Item!", i, j);
				} else {
					this.renderTooltip(lv2, i, j);
				}
			} else if (lv.method_10892() == class_2568.class_2569.field_11761) {
				if (this.minecraft.field_1690.field_1827) {
					try {
						class_2487 lv4 = class_2522.method_10718(lv.method_10891().getString());
						List<String> list = Lists.<String>newArrayList();
						class_2561 lv5 = class_2561.class_2562.method_10877(lv4.method_10558("name"));
						if (lv5 != null) {
							list.add(lv5.method_10863());
						}

						if (lv4.method_10573("type", 8)) {
							String string = lv4.method_10558("type");
							list.add("Type: " + string);
						}

						list.add(lv4.method_10558("id"));
						this.renderTooltip(list, i, j);
					} catch (CommandSyntaxException | JsonSyntaxException var9) {
						this.renderTooltip(class_124.field_1061 + "Invalid Entity!", i, j);
					}
				}
			} else if (lv.method_10892() == class_2568.class_2569.field_11762) {
				this.renderTooltip(this.minecraft.field_1772.method_1728(lv.method_10891().method_10863(), Math.max(this.width / 2, 200)), i, j);
			}

			GlStateManager.disableLighting();
		}
	}

	protected void insertText(String string, boolean bl) {
	}

	public boolean handleComponentClicked(class_2561 arg) {
		if (arg == null) {
			return false;
		} else {
			class_2558 lv = arg.method_10866().method_10970();
			if (hasShiftDown()) {
				if (arg.method_10866().method_10955() != null) {
					this.insertText(arg.method_10866().method_10955(), false);
				}
			} else if (lv != null) {
				if (lv.method_10845() == class_2558.class_2559.field_11749) {
					if (!this.minecraft.field_1690.field_1911) {
						return false;
					}

					try {
						URI uRI = new URI(lv.method_10844());
						String string = uRI.getScheme();
						if (string == null) {
							throw new URISyntaxException(lv.method_10844(), "Missing protocol");
						}

						if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
							throw new URISyntaxException(lv.method_10844(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
						}

						if (this.minecraft.field_1690.field_1817) {
							this.clickedLink = uRI;
							this.minecraft.method_1507(new class_407(this, lv.method_10844(), 31102009, false));
						} else {
							this.openLink(uRI);
						}
					} catch (URISyntaxException var5) {
						LOGGER.error("Can't open url for {}", lv, var5);
					}
				} else if (lv.method_10845() == class_2558.class_2559.field_11746) {
					URI uRIx = new File(lv.method_10844()).toURI();
					this.openLink(uRIx);
				} else if (lv.method_10845() == class_2558.class_2559.field_11745) {
					this.insertText(lv.method_10844(), true);
				} else if (lv.method_10845() == class_2558.class_2559.field_11750) {
					this.sendMessage(lv.method_10844(), false);
				} else {
					LOGGER.error("Don't know how to handle {}", lv);
				}

				return true;
			}

			return false;
		}
	}

	public void sendMessage(String string) {
		this.sendMessage(string, true);
	}

	public void sendMessage(String string, boolean bl) {
		if (bl) {
			this.minecraft.field_1705.method_1743().method_1803(string);
		}

		this.minecraft.field_1724.method_3142(string);
	}

	public void init(class_310 arg, int i, int j) {
		this.minecraft = arg;
		this.itemRenderer = arg.method_1480();
		this.font = arg.field_1772;
		this.width = i;
		this.height = j;
		this.buttons.clear();
		this.children.clear();
		this.init();
	}

	public void setSize(int i, int j) {
		this.width = i;
		this.height = j;
	}

	@Override
	public List<? extends class_364> children() {
		return this.children;
	}

	protected void init() {
	}

	public void tick() {
	}

	public void removed() {
	}

	public void renderBackground() {
		this.renderBackground(0);
	}

	public void renderBackground(int i) {
		if (this.minecraft.field_1687 != null) {
			this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.renderDirtBackground(i);
		}
	}

	public void renderDirtBackground(int i) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.minecraft.method_1531().method_4618(BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315(0.0, (double)this.height, 0.0).method_1312(0.0, (double)((float)this.height / 32.0F + (float)i)).method_1323(64, 64, 64, 255).method_1344();
		lv2.method_1315((double)this.width, (double)this.height, 0.0)
			.method_1312((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)i))
			.method_1323(64, 64, 64, 255)
			.method_1344();
		lv2.method_1315((double)this.width, 0.0, 0.0).method_1312((double)((float)this.width / 32.0F), (double)i).method_1323(64, 64, 64, 255).method_1344();
		lv2.method_1315(0.0, 0.0, 0.0).method_1312(0.0, (double)i).method_1323(64, 64, 64, 255).method_1344();
		lv.method_1350();
	}

	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 31102009) {
			if (bl) {
				this.openLink(this.clickedLink);
			}

			this.clickedLink = null;
			this.minecraft.method_1507(this);
		}
	}

	private void openLink(URI uRI) {
		class_156.method_668().method_673(uRI);
	}

	public static boolean hasControlDown() {
		return class_310.field_1703
			? class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 343)
				|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 347)
			: class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 341)
				|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 345);
	}

	public static boolean hasShiftDown() {
		return class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 340)
			|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 344);
	}

	public static boolean hasAltDown() {
		return class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 342)
			|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 346);
	}

	public static boolean isCut(int i) {
		return i == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isPaste(int i) {
		return i == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isCopy(int i) {
		return i == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isSelectAll(int i) {
		return i == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public void resize(class_310 arg, int i, int j) {
		this.init(arg, i, j);
	}

	public static void wrapScreenError(Runnable runnable, String string, String string2) {
		try {
			runnable.run();
		} catch (Throwable var6) {
			class_128 lv = class_128.method_560(var6, string);
			class_129 lv2 = lv.method_562("Affected screen");
			lv2.method_577("Screen name", () -> string2);
			throw new class_148(lv);
		}
	}

	protected boolean isValidCharacterForName(String string, char c, int i) {
		int j = string.indexOf(58);
		int k = string.indexOf(47);
		if (c == ':') {
			return (k == -1 || i <= k) && j == -1;
		} else {
			return c == '/' ? i > j : c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
		}
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return true;
	}
}
