package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsButtonProxy;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4367 extends RealmsButton {
	private final Supplier<RealmsServer> field_19672;
	private final Consumer<String> field_19673;
	private final class_4367.class_4369 field_19674;
	private final int field_19675;
	private int field_19676;
	private class_4367.class_4370 field_19677;

	public class_4367(int i, int j, int k, int l, Supplier<RealmsServer> supplier, Consumer<String> consumer, int m, int n, class_4367.class_4369 arg) {
		super(m, i, j, k, l, "");
		this.field_19672 = supplier;
		this.field_19675 = n;
		this.field_19673 = consumer;
		this.field_19674 = arg;
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
	}

	@Override
	public void tick() {
		this.field_19676++;
		RealmsServer realmsServer = (RealmsServer)this.field_19672.get();
		if (realmsServer != null) {
			RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)realmsServer.slots.get(this.field_19675);
			boolean bl = this.field_19675 == 4;
			boolean bl2;
			String string;
			long l;
			String string2;
			boolean bl3;
			if (bl) {
				bl2 = realmsServer.worldType.equals(RealmsServer.class_4321.MINIGAME);
				string = "Minigame";
				l = (long)realmsServer.minigameId;
				string2 = realmsServer.minigameImage;
				bl3 = realmsServer.minigameId == -1;
			} else {
				bl2 = realmsServer.activeSlot == this.field_19675 && !realmsServer.worldType.equals(RealmsServer.class_4321.MINIGAME);
				string = realmsWorldOptions.getSlotName(this.field_19675);
				l = realmsWorldOptions.templateId;
				string2 = realmsWorldOptions.templateImage;
				bl3 = realmsWorldOptions.empty;
			}

			String string3 = null;
			class_4367.class_4368 lv;
			if (bl2) {
				boolean bl4 = realmsServer.state == RealmsServer.class_4320.OPEN || realmsServer.state == RealmsServer.class_4320.CLOSED;
				if (!realmsServer.expired && bl4) {
					lv = class_4367.class_4368.JOIN;
					string3 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.active");
				} else {
					lv = class_4367.class_4368.NOTHING;
				}
			} else if (bl) {
				if (realmsServer.expired) {
					lv = class_4367.class_4368.NOTHING;
				} else {
					lv = class_4367.class_4368.SWITCH_SLOT;
					string3 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.minigame");
				}
			} else {
				lv = class_4367.class_4368.SWITCH_SLOT;
				string3 = Realms.getLocalizedString("mco.configure.world.slot.tooltip");
			}

			this.field_19677 = new class_4367.class_4370(bl2, string, l, string2, bl3, bl, lv, string3);
			String string4;
			if (lv == class_4367.class_4368.NOTHING) {
				string4 = string;
			} else if (bl) {
				if (bl3) {
					string4 = string3;
				} else {
					string4 = string3 + " " + string + " " + realmsServer.minigameName;
				}
			} else {
				string4 = string3 + " " + string;
			}

			this.setMessage(string4);
		}
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.field_19677 != null) {
			RealmsButtonProxy realmsButtonProxy = this.getProxy();
			this.method_21107(
				realmsButtonProxy.x,
				realmsButtonProxy.y,
				i,
				j,
				this.field_19677.field_19682,
				this.field_19677.field_19683,
				this.field_19675,
				this.field_19677.field_19684,
				this.field_19677.field_19685,
				this.field_19677.field_19686,
				this.field_19677.field_19687,
				this.field_19677.field_19688,
				this.field_19677.field_19689
			);
		}
	}

	private void method_21107(
		int i,
		int j,
		int k,
		int l,
		boolean bl,
		String string,
		int m,
		long n,
		@Nullable String string2,
		boolean bl2,
		boolean bl3,
		class_4367.class_4368 arg,
		@Nullable String string3
	) {
		boolean bl4 = this.getProxy().isHovered();
		if (this.getProxy().isMouseOver((double)k, (double)l) && string3 != null) {
			this.field_19673.accept(string3);
		}

		if (bl3) {
			class_4446.method_21560(String.valueOf(n), string2);
		} else if (bl2) {
			Realms.bind("realms:textures/gui/realms/empty_frame.png");
		} else if (string2 != null && n != -1L) {
			class_4446.method_21560(String.valueOf(n), string2);
		} else if (m == 1) {
			Realms.bind("textures/gui/title/background/panorama_0.png");
		} else if (m == 2) {
			Realms.bind("textures/gui/title/background/panorama_2.png");
		} else if (m == 3) {
			Realms.bind("textures/gui/title/background/panorama_3.png");
		}

		if (bl) {
			float f = 0.85F + 0.15F * RealmsMth.cos((float)this.field_19676 * 0.2F);
			GlStateManager.color4f(f, f, f, 1.0F);
		} else {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		RealmsScreen.blit(i + 3, j + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		Realms.bind("realms:textures/gui/realms/slot_frame.png");
		boolean bl5 = bl4 && arg != class_4367.class_4368.NOTHING;
		if (bl5) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else if (bl) {
			GlStateManager.color4f(0.8F, 0.8F, 0.8F, 1.0F);
		} else {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		RealmsScreen.blit(i, j, 0.0F, 0.0F, 80, 80, 80, 80);
		this.drawCenteredString(string, i + 40, j + 66, 16777215);
	}

	@Override
	public void onPress() {
		this.field_19674.method_21108(this.field_19675, this.field_19677.field_19688, this.field_19677.field_19687, this.field_19677.field_19686);
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4368 {
		NOTHING,
		SWITCH_SLOT,
		JOIN;
	}

	@Environment(EnvType.CLIENT)
	public interface class_4369 {
		void method_21108(int i, @Nonnull class_4367.class_4368 arg, boolean bl, boolean bl2);
	}

	@Environment(EnvType.CLIENT)
	public static class class_4370 {
		final boolean field_19682;
		final String field_19683;
		final long field_19684;
		public final String field_19685;
		public final boolean field_19686;
		final boolean field_19687;
		public final class_4367.class_4368 field_19688;
		final String field_19689;

		class_4370(
			boolean bl, String string, long l, @Nullable String string2, boolean bl2, boolean bl3, @Nonnull class_4367.class_4368 arg, @Nullable String string3
		) {
			this.field_19682 = bl;
			this.field_19683 = string;
			this.field_19684 = l;
			this.field_19685 = string2;
			this.field_19686 = bl2;
			this.field_19687 = bl3;
			this.field_19688 = arg;
			this.field_19689 = string3;
		}
	}
}
