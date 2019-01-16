package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_536;
import net.minecraft.class_539;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class SpectatorMenuImpl {
	private static final SpectatorMenuElement field_3261 = new SpectatorMenuImpl.class_532();
	private static final SpectatorMenuElement field_3262 = new SpectatorMenuImpl.class_533(-1, true);
	private static final SpectatorMenuElement field_3256 = new SpectatorMenuImpl.class_533(1, true);
	private static final SpectatorMenuElement field_3259 = new SpectatorMenuImpl.class_533(1, false);
	public static final SpectatorMenuElement field_3260 = new SpectatorMenuElement() {
		@Override
		public void selectElement(SpectatorMenuImpl spectatorMenuImpl) {
		}

		@Override
		public TextComponent method_16892() {
			return new StringTextComponent("");
		}

		@Override
		public void renderIcon(float f, int i) {
		}

		@Override
		public boolean enabled() {
			return false;
		}
	};
	private final class_536 field_3255;
	private final List<class_539> field_3257 = Lists.<class_539>newArrayList();
	private SpectatorMenu field_3258;
	private int field_3254 = -1;
	private int field_3263;

	public SpectatorMenuImpl(class_536 arg) {
		this.field_3258 = new SpectatorRootMenu();
		this.field_3255 = arg;
	}

	public SpectatorMenuElement method_2777(int i) {
		int j = i + this.field_3263 * 6;
		if (this.field_3263 > 0 && i == 0) {
			return field_3262;
		} else if (i == 7) {
			return j < this.field_3258.getElements().size() ? field_3256 : field_3259;
		} else if (i == 8) {
			return field_3261;
		} else {
			return j >= 0 && j < this.field_3258.getElements().size()
				? MoreObjects.firstNonNull((SpectatorMenuElement)this.field_3258.getElements().get(j), field_3260)
				: field_3260;
		}
	}

	public List<SpectatorMenuElement> method_2770() {
		List<SpectatorMenuElement> list = Lists.<SpectatorMenuElement>newArrayList();

		for (int i = 0; i <= 8; i++) {
			list.add(this.method_2777(i));
		}

		return list;
	}

	public SpectatorMenuElement method_2774() {
		return this.method_2777(this.field_3254);
	}

	public SpectatorMenu method_2776() {
		return this.field_3258;
	}

	public void method_2771(int i) {
		SpectatorMenuElement spectatorMenuElement = this.method_2777(i);
		if (spectatorMenuElement != field_3260) {
			if (this.field_3254 == i && spectatorMenuElement.enabled()) {
				spectatorMenuElement.selectElement(this);
			} else {
				this.field_3254 = i;
			}
		}
	}

	public void method_2779() {
		this.field_3255.method_2782(this);
	}

	public int method_2773() {
		return this.field_3254;
	}

	public void selectElement(SpectatorMenu spectatorMenu) {
		this.field_3257.add(this.method_2772());
		this.field_3258 = spectatorMenu;
		this.field_3254 = -1;
		this.field_3263 = 0;
	}

	public class_539 method_2772() {
		return new class_539(this.field_3258, this.method_2770(), this.field_3254);
	}

	@Environment(EnvType.CLIENT)
	static class class_532 implements SpectatorMenuElement {
		private class_532() {
		}

		@Override
		public void selectElement(SpectatorMenuImpl spectatorMenuImpl) {
			spectatorMenuImpl.method_2779();
		}

		@Override
		public TextComponent method_16892() {
			return new TranslatableTextComponent("spectatorMenu.close");
		}

		@Override
		public void renderIcon(float f, int i) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
			Drawable.drawTexturedRect(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
		}

		@Override
		public boolean enabled() {
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_533 implements SpectatorMenuElement {
		private final int field_3264;
		private final boolean field_3265;

		public class_533(int i, boolean bl) {
			this.field_3264 = i;
			this.field_3265 = bl;
		}

		@Override
		public void selectElement(SpectatorMenuImpl spectatorMenuImpl) {
			spectatorMenuImpl.field_3263 = spectatorMenuImpl.field_3263 + this.field_3264;
		}

		@Override
		public TextComponent method_16892() {
			return this.field_3264 < 0 ? new TranslatableTextComponent("spectatorMenu.previous_page") : new TranslatableTextComponent("spectatorMenu.next_page");
		}

		@Override
		public void renderIcon(float f, int i) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
			if (this.field_3264 < 0) {
				Drawable.drawTexturedRect(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
			} else {
				Drawable.drawTexturedRect(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
			}
		}

		@Override
		public boolean enabled() {
			return this.field_3265;
		}
	}
}
