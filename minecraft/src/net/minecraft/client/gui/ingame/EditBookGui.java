package net.minecraft.client.gui.ingame;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_341;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.packet.BookUpdateServerPacket;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EditBookGui extends Gui {
	private static final Identifier TEXTURE = new Identifier("textures/gui/book.png");
	private final PlayerEntity player;
	private final ItemStack stack;
	private final boolean field_2838;
	private boolean field_2837;
	private boolean field_2828;
	private int field_2844;
	private int field_2842 = 1;
	private int field_2840;
	private ListTag pageTag;
	private String title = "";
	private List<TextComponent> field_2846;
	private int field_2836 = -1;
	private int field_2833 = 0;
	private int field_2829 = 0;
	private long field_2830;
	private int field_2827 = -1;
	private EditBookGui.class_474 field_2843;
	private EditBookGui.class_474 field_2839;
	private ButtonWidget field_2848;
	private ButtonWidget field_2831;
	private ButtonWidget field_2841;
	private ButtonWidget field_2849;
	private final Hand field_2832;

	public EditBookGui(PlayerEntity playerEntity, ItemStack itemStack, boolean bl, Hand hand) {
		this.player = playerEntity;
		this.stack = itemStack;
		this.field_2838 = bl;
		this.field_2832 = hand;
		if (itemStack.hasTag()) {
			CompoundTag compoundTag = itemStack.getTag();
			this.pageTag = compoundTag.getList("pages", 8).copy();
			this.field_2842 = this.pageTag.size();
			if (this.field_2842 < 1) {
				this.pageTag.add((Tag)(new StringTag("")));
				this.field_2842 = 1;
			}
		}

		if (this.pageTag == null && bl) {
			this.pageTag = new ListTag();
			this.pageTag.add((Tag)(new StringTag("")));
			this.field_2842 = 1;
		}
	}

	@Override
	public void update() {
		super.update();
		this.field_2844++;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		if (this.field_2838) {
			this.field_2831 = this.addButton(new ButtonWidget(3, this.width / 2 - 100, 196, 98, 20, I18n.translate("book.signButton")) {
				@Override
				public void onPressed(double d, double e) {
					EditBookGui.this.field_2828 = true;
					EditBookGui.this.method_2413();
				}
			});
			this.field_2848 = this.addButton(new ButtonWidget(0, this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.done")) {
				@Override
				public void onPressed(double d, double e) {
					EditBookGui.this.client.openGui(null);
					EditBookGui.this.method_2407(false);
				}
			});
			this.field_2841 = this.addButton(new ButtonWidget(5, this.width / 2 - 100, 196, 98, 20, I18n.translate("book.finalizeButton")) {
				@Override
				public void onPressed(double d, double e) {
					if (EditBookGui.this.field_2828) {
						EditBookGui.this.method_2407(true);
						EditBookGui.this.client.openGui(null);
					}
				}
			});
			this.field_2849 = this.addButton(new ButtonWidget(4, this.width / 2 + 2, 196, 98, 20, I18n.translate("gui.cancel")) {
				@Override
				public void onPressed(double d, double e) {
					if (EditBookGui.this.field_2828) {
						EditBookGui.this.field_2828 = false;
					}

					EditBookGui.this.method_2413();
				}
			});
		} else {
			this.field_2848 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done")) {
				@Override
				public void onPressed(double d, double e) {
					EditBookGui.this.client.openGui(null);
					EditBookGui.this.method_2407(false);
				}
			});
		}

		int i = (this.width - 192) / 2;
		int j = 2;
		this.field_2843 = this.addButton(new EditBookGui.class_474(1, i + 116, 159, true) {
			@Override
			public void onPressed(double d, double e) {
				EditBookGui.this.method_2444();
			}
		});
		this.field_2839 = this.addButton(new EditBookGui.class_474(2, i + 43, 159, false) {
			@Override
			public void onPressed(double d, double e) {
				EditBookGui.this.method_2437();
			}
		});
		this.method_2413();
	}

	private String method_16345(String string) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : string.toCharArray()) {
			if (c != 167 && c != 127) {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	private void method_2437() {
		if (this.field_2840 > 0) {
			this.field_2840--;
			this.field_2833 = 0;
			this.field_2829 = this.field_2833;
		}

		this.method_2413();
	}

	private void method_2444() {
		if (this.field_2840 < this.field_2842 - 1) {
			this.field_2840++;
			this.field_2833 = 0;
			this.field_2829 = this.field_2833;
		} else if (this.field_2838) {
			this.method_2436();
			if (this.field_2840 < this.field_2842 - 1) {
				this.field_2840++;
			}

			this.field_2833 = 0;
			this.field_2829 = this.field_2833;
		}

		this.method_2413();
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void method_2413() {
		this.field_2843.visible = !this.field_2828 && (this.field_2840 < this.field_2842 - 1 || this.field_2838);
		this.field_2839.visible = !this.field_2828 && this.field_2840 > 0;
		this.field_2848.visible = !this.field_2838 || !this.field_2828;
		if (this.field_2838) {
			this.field_2831.visible = !this.field_2828;
			this.field_2849.visible = this.field_2828;
			this.field_2841.visible = this.field_2828;
			this.field_2841.enabled = !this.title.trim().isEmpty();
		}
	}

	private void method_2407(boolean bl) {
		if (this.field_2838 && this.field_2837) {
			if (this.pageTag != null) {
				while (this.pageTag.size() > 1) {
					String string = this.pageTag.getString(this.pageTag.size() - 1);
					if (!string.isEmpty()) {
						break;
					}

					this.pageTag.remove(this.pageTag.size() - 1);
				}

				this.stack.setChildTag("pages", this.pageTag);
				if (bl) {
					this.stack.setChildTag("author", new StringTag(this.player.getGameProfile().getName()));
					this.stack.setChildTag("title", new StringTag(this.title.trim()));
				}

				this.client.getNetworkHandler().sendPacket(new BookUpdateServerPacket(this.stack, bl, this.field_2832));
			}
		}
	}

	private void method_2436() {
		if (this.pageTag != null && this.pageTag.size() < 100) {
			this.pageTag.add((Tag)(new StringTag("")));
			this.field_2842++;
			this.field_2837 = true;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (this.field_2838) {
			return this.field_2828 ? this.method_2446(i, j, k) : this.method_2411(i, j, k);
		} else {
			switch (i) {
				case 266:
					this.field_2839.onPressed(0.0, 0.0);
					return true;
				case 267:
					this.field_2843.onPressed(0.0, 0.0);
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (super.charTyped(c, i)) {
			return true;
		} else if (this.field_2838) {
			if (this.field_2828) {
				if (this.title.length() < 16 && SharedConstants.isValidChar(c)) {
					this.title = this.title + Character.toString(c);
					this.method_2413();
					this.field_2837 = true;
					return true;
				} else {
					return false;
				}
			} else if (SharedConstants.isValidChar(c)) {
				this.method_2431(Character.toString(c));
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean method_2411(int i, int j, int k) {
		String string = this.method_2427();
		if (Gui.isSelectAllShortcutPressed(i)) {
			this.field_2829 = 0;
			this.field_2833 = string.length();
			return true;
		} else if (Gui.isCopyShortcutPressed(i)) {
			this.client.keyboard.setClipbord(this.method_2442());
			return true;
		} else if (Gui.isPasteShortcutPressed(i)) {
			this.method_2431(this.method_16345(TextFormat.stripFormatting(this.client.keyboard.getClipboard().replaceAll("\\r", ""))));
			this.field_2829 = this.field_2833;
			return true;
		} else if (Gui.isCutShortcutPressed(i)) {
			this.client.keyboard.setClipbord(this.method_2442());
			this.method_2410();
			return true;
		} else {
			switch (i) {
				case 257:
				case 335:
					this.method_2431("\n");
					return true;
				case 259:
					this.method_2428(string);
					return true;
				case 261:
					this.method_2434(string);
					return true;
				case 262:
					this.method_2408(string);
					return true;
				case 263:
					this.method_2440(string);
					return true;
				case 264:
					this.method_2435(string);
					return true;
				case 265:
					this.method_2430(string);
					return true;
				case 266:
					this.field_2839.onPressed(0.0, 0.0);
					return true;
				case 267:
					this.field_2843.onPressed(0.0, 0.0);
					return true;
				case 268:
					this.method_2421(string);
					return true;
				case 269:
					this.method_2414(string);
					return true;
				default:
					return false;
			}
		}
	}

	private void method_2428(String string) {
		if (!string.isEmpty()) {
			if (this.field_2829 != this.field_2833) {
				this.method_2410();
			} else if (this.field_2833 > 0) {
				String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.field_2833 - 1)).toString();
				this.method_2439(string2);
				this.field_2833 = Math.max(0, this.field_2833 - 1);
				this.field_2829 = this.field_2833;
			}
		}
	}

	private void method_2434(String string) {
		if (!string.isEmpty()) {
			if (this.field_2829 != this.field_2833) {
				this.method_2410();
			} else if (this.field_2833 < string.length()) {
				String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.field_2833)).toString();
				this.method_2439(string2);
			}
		}
	}

	private void method_2440(String string) {
		int i = this.fontRenderer.isRightToLeft() ? 1 : -1;
		if (Gui.isControlPressed()) {
			this.field_2833 = this.fontRenderer.method_16196(string, i, this.field_2833, true);
		} else {
			this.field_2833 = Math.max(0, this.field_2833 + i);
		}

		if (!Gui.isShiftPressed()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2408(String string) {
		int i = this.fontRenderer.isRightToLeft() ? -1 : 1;
		if (Gui.isControlPressed()) {
			this.field_2833 = this.fontRenderer.method_16196(string, i, this.field_2833, true);
		} else {
			this.field_2833 = Math.min(string.length(), this.field_2833 + i);
		}

		if (!Gui.isShiftPressed()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2430(String string) {
		if (!string.isEmpty()) {
			EditBookGui.class_475 lv = this.method_2416(string, this.field_2833);
			if (lv.field_2853 == 0) {
				this.field_2833 = 0;
				if (!Gui.isShiftPressed()) {
					this.field_2829 = this.field_2833;
				}
			} else {
				int i = this.method_2404(
					string, new EditBookGui.class_475(lv.field_2854 + this.method_2412(string, this.field_2833) / 3, lv.field_2853 - this.fontRenderer.FONT_HEIGHT)
				);
				if (i >= 0) {
					this.field_2833 = i;
					if (!Gui.isShiftPressed()) {
						this.field_2829 = this.field_2833;
					}
				}
			}
		}
	}

	private void method_2435(String string) {
		if (!string.isEmpty()) {
			EditBookGui.class_475 lv = this.method_2416(string, this.field_2833);
			int i = this.fontRenderer.getStringBoundedHeight(string + "" + TextFormat.BLACK + "_", 114);
			if (lv.field_2853 + this.fontRenderer.FONT_HEIGHT == i) {
				this.field_2833 = string.length();
				if (!Gui.isShiftPressed()) {
					this.field_2829 = this.field_2833;
				}
			} else {
				int j = this.method_2404(
					string, new EditBookGui.class_475(lv.field_2854 + this.method_2412(string, this.field_2833) / 3, lv.field_2853 + this.fontRenderer.FONT_HEIGHT)
				);
				if (j >= 0) {
					this.field_2833 = j;
					if (!Gui.isShiftPressed()) {
						this.field_2829 = this.field_2833;
					}
				}
			}
		}
	}

	private void method_2421(String string) {
		this.field_2833 = this.method_2404(string, new EditBookGui.class_475(0, this.method_2416(string, this.field_2833).field_2853));
		if (!Gui.isShiftPressed()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2414(String string) {
		this.field_2833 = this.method_2404(string, new EditBookGui.class_475(113, this.method_2416(string, this.field_2833).field_2853));
		if (!Gui.isShiftPressed()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2410() {
		if (this.field_2829 != this.field_2833) {
			String string = this.method_2427();
			int i = Math.min(this.field_2833, this.field_2829);
			int j = Math.max(this.field_2833, this.field_2829);
			String string2 = string.substring(0, i) + string.substring(j);
			this.field_2833 = i;
			this.field_2829 = this.field_2833;
			this.method_2439(string2);
		}
	}

	private int method_2412(String string, int i) {
		return (int)this.fontRenderer.getCharWidth(string.charAt(MathHelper.clamp(i, 0, string.length() - 1)));
	}

	private boolean method_2446(int i, int j, int k) {
		switch (i) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.method_2407(true);
					this.client.openGui(null);
				}

				return true;
			case 259:
				if (!this.title.isEmpty()) {
					this.title = this.title.substring(0, this.title.length() - 1);
					this.method_2413();
				}

				return true;
			default:
				return false;
		}
	}

	private String method_2427() {
		return this.pageTag != null && this.field_2840 >= 0 && this.field_2840 < this.pageTag.size() ? this.pageTag.getString(this.field_2840) : "";
	}

	private void method_2439(String string) {
		if (this.pageTag != null && this.field_2840 >= 0 && this.field_2840 < this.pageTag.size()) {
			this.pageTag.set(this.field_2840, (Tag)(new StringTag(string)));
			this.field_2837 = true;
		}
	}

	private void method_2431(String string) {
		if (this.field_2829 != this.field_2833) {
			this.method_2410();
		}

		String string2 = this.method_2427();
		this.field_2833 = MathHelper.clamp(this.field_2833, 0, string2.length());
		String string3 = new StringBuilder(string2).insert(this.field_2833, string).toString();
		int i = this.fontRenderer.getStringBoundedHeight(string3 + "" + TextFormat.BLACK + "_", 114);
		if (i <= 128 && string3.length() < 1024) {
			this.method_2439(string3);
			this.field_2829 = this.field_2833 = Math.min(this.method_2427().length(), this.field_2833 + string.length());
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - 192) / 2;
		int l = 2;
		this.drawTexturedRect(k, 2, 0, 0, 192, 192);
		if (this.field_2828) {
			String string = this.title;
			if (this.field_2838) {
				if (this.field_2844 / 6 % 2 == 0) {
					string = string + "" + TextFormat.BLACK + "_";
				} else {
					string = string + "" + TextFormat.GRAY + "_";
				}
			}

			String string2 = I18n.translate("book.editTitle");
			int m = this.method_2424(string2);
			this.fontRenderer.draw(string2, (float)(k + 36 + (114 - m) / 2), 34.0F, 0);
			int n = this.method_2424(string);
			this.fontRenderer.draw(string, (float)(k + 36 + (114 - n) / 2), 50.0F, 0);
			String string3 = I18n.translate("book.byAuthor", this.player.getName().getString());
			int o = this.method_2424(string3);
			this.fontRenderer.draw(TextFormat.DARK_GRAY + string3, (float)(k + 36 + (114 - o) / 2), 60.0F, 0);
			String string4 = I18n.translate("book.finalizeWarning");
			this.fontRenderer.drawStringBounded(string4, k + 36, 82, 114, 0);
		} else {
			String string = I18n.translate("book.pageIndicator", this.field_2840 + 1, this.field_2842);
			String string2 = "";
			if (this.pageTag != null && this.field_2840 >= 0 && this.field_2840 < this.pageTag.size()) {
				string2 = this.pageTag.getString(this.field_2840);
			}

			if (!this.field_2838 && this.field_2836 != this.field_2840) {
				if (WrittenBookItem.method_8053(this.stack.getTag())) {
					try {
						TextComponent textComponent = TextComponent.Serializer.fromJsonString(string2);
						this.field_2846 = textComponent != null ? class_341.method_1850(textComponent, 114, this.fontRenderer, true, true) : null;
					} catch (JsonParseException var13) {
						this.field_2846 = null;
					}
				} else {
					StringTextComponent stringTextComponent = new StringTextComponent(TextFormat.DARK_RED + "* Invalid book tag *");
					this.field_2846 = Lists.<TextComponent>newArrayList(stringTextComponent);
				}

				this.field_2836 = this.field_2840;
			}

			int m = this.method_2424(string);
			this.fontRenderer.draw(string, (float)(k - m + 192 - 44), 18.0F, 0);
			if (this.field_2846 == null) {
				this.fontRenderer.drawStringBounded(string2, k + 36, 32, 114, 0);
				if (this.field_2838) {
					this.method_2441(string2);
					if (this.field_2844 / 6 % 2 == 0) {
						EditBookGui.class_475 lv = this.method_2416(string2, this.field_2833);
						if (this.fontRenderer.isRightToLeft()) {
							this.method_2429(lv);
							lv.field_2854 -= 4;
						}

						this.method_2415(lv);
						if (this.field_2833 < string2.length()) {
							Drawable.drawRect(lv.field_2854, lv.field_2853 - 1, lv.field_2854 + 1, lv.field_2853 + this.fontRenderer.FONT_HEIGHT, -16777216);
						} else {
							this.fontRenderer.draw("_", (float)lv.field_2854, (float)lv.field_2853, 0);
						}
					}
				}
			} else {
				int n = Math.min(128 / this.fontRenderer.FONT_HEIGHT, this.field_2846.size());

				for (int p = 0; p < n; p++) {
					TextComponent textComponent2 = (TextComponent)this.field_2846.get(p);
					this.fontRenderer.draw(textComponent2.getFormattedText(), (float)(k + 36), (float)(32 + p * this.fontRenderer.FONT_HEIGHT), 0);
				}

				TextComponent textComponent3 = this.method_2433((double)i, (double)j);
				if (textComponent3 != null) {
					this.drawTextComponentHover(textComponent3, i, j);
				}
			}
		}

		super.draw(i, j, f);
	}

	private int method_2424(String string) {
		return this.fontRenderer.getStringWidth(this.fontRenderer.isRightToLeft() ? this.fontRenderer.mirror(string) : string);
	}

	private int method_2417(String string, int i) {
		return this.fontRenderer.getCharacterCountForWidth(string, i);
	}

	private String method_2442() {
		String string = this.method_2427();
		int i = Math.min(this.field_2833, this.field_2829);
		int j = Math.max(this.field_2833, this.field_2829);
		return string.substring(i, j);
	}

	private void method_2441(String string) {
		if (this.field_2829 != this.field_2833) {
			int i = Math.min(this.field_2833, this.field_2829);
			int j = Math.max(this.field_2833, this.field_2829);
			String string2 = string.substring(i, j);
			int k = this.fontRenderer.method_16196(string, 1, j, true);
			String string3 = string.substring(i, k);
			EditBookGui.class_475 lv = this.method_2416(string, i);

			for (EditBookGui.class_475 lv2 = new EditBookGui.class_475(lv.field_2854, lv.field_2853 + this.fontRenderer.FONT_HEIGHT);
				!string2.isEmpty();
				lv2.field_2853 = lv2.field_2853 + this.fontRenderer.FONT_HEIGHT
			) {
				int l = this.method_2417(string3, 114 - lv.field_2854);
				if (string2.length() <= l) {
					lv2.field_2854 = lv.field_2854 + this.method_2424(string2);
					this.method_2409(lv, lv2);
					break;
				}

				l = Math.min(l, string2.length() - 1);
				String string4 = string2.substring(0, l);
				char c = string2.charAt(l);
				boolean bl = c == ' ' || c == '\n';
				string2 = TextFormat.method_538(string4) + string2.substring(l + (bl ? 1 : 0));
				string3 = TextFormat.method_538(string4) + string3.substring(l + (bl ? 1 : 0));
				lv2.field_2854 = lv.field_2854 + this.method_2424(string4 + " ");
				this.method_2409(lv, lv2);
				lv.field_2854 = 0;
				lv.field_2853 = lv.field_2853 + this.fontRenderer.FONT_HEIGHT;
			}
		}
	}

	private void method_2409(EditBookGui.class_475 arg, EditBookGui.class_475 arg2) {
		EditBookGui.class_475 lv = new EditBookGui.class_475(arg.field_2854, arg.field_2853);
		EditBookGui.class_475 lv2 = new EditBookGui.class_475(arg2.field_2854, arg2.field_2853);
		if (this.fontRenderer.isRightToLeft()) {
			this.method_2429(lv);
			this.method_2429(lv2);
			int i = lv2.field_2854;
			lv2.field_2854 = lv.field_2854;
			lv.field_2854 = i;
		}

		this.method_2415(lv);
		this.method_2415(lv2);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		vertexBuffer.begin(7, VertexFormats.POSITION);
		vertexBuffer.vertex((double)lv.field_2854, (double)lv2.field_2853, 0.0).next();
		vertexBuffer.vertex((double)lv2.field_2854, (double)lv2.field_2853, 0.0).next();
		vertexBuffer.vertex((double)lv2.field_2854, (double)lv.field_2853, 0.0).next();
		vertexBuffer.vertex((double)lv.field_2854, (double)lv.field_2853, 0.0).next();
		tessellator.draw();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}

	private EditBookGui.class_475 method_2416(String string, int i) {
		EditBookGui.class_475 lv = new EditBookGui.class_475();
		int j = 0;
		int k = 0;

		for (String string2 = string; !string2.isEmpty(); k = j) {
			int l = this.method_2417(string2, 114);
			if (string2.length() <= l) {
				String string3 = string2.substring(0, Math.min(Math.max(i - k, 0), string2.length()));
				lv.field_2854 = lv.field_2854 + this.method_2424(string3);
				break;
			}

			String string3 = string2.substring(0, l);
			char c = string2.charAt(l);
			boolean bl = c == ' ' || c == '\n';
			string2 = TextFormat.method_538(string3) + string2.substring(l + (bl ? 1 : 0));
			j += string3.length() + (bl ? 1 : 0);
			if (j - 1 >= i) {
				String string4 = string3.substring(0, Math.min(Math.max(i - k, 0), string3.length()));
				lv.field_2854 = lv.field_2854 + this.method_2424(string4);
				break;
			}

			lv.field_2853 = lv.field_2853 + this.fontRenderer.FONT_HEIGHT;
		}

		return lv;
	}

	private void method_2429(EditBookGui.class_475 arg) {
		if (this.fontRenderer.isRightToLeft()) {
			arg.field_2854 = 114 - arg.field_2854;
		}
	}

	private void method_2443(EditBookGui.class_475 arg) {
		arg.field_2854 = arg.field_2854 - (this.width - 192) / 2 - 36;
		arg.field_2853 -= 32;
	}

	private void method_2415(EditBookGui.class_475 arg) {
		arg.field_2854 = arg.field_2854 + (this.width - 192) / 2 + 36;
		arg.field_2853 += 32;
	}

	private int method_2425(String string, int i) {
		if (i < 0) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;
			String string2 = string + " ";

			for (int j = 0; j < string2.length(); j++) {
				char c = string2.charAt(j);
				float g = this.fontRenderer.getCharWidth(c);
				if (c == 167 && j < string2.length() - 1) {
					c = string2.charAt(++j);
					if (c == 'l' || c == 'L') {
						bl = true;
					} else if (c == 'r' || c == 'R') {
						bl = false;
					}

					g = 0.0F;
				}

				float h = f;
				f += g;
				if (bl && g > 0.0F) {
					f++;
				}

				if ((float)i >= h && (float)i < f) {
					return j;
				}
			}

			return (float)i >= f ? string2.length() - 1 : -1;
		}
	}

	private int method_2404(String string, EditBookGui.class_475 arg) {
		int i = 16 * this.fontRenderer.FONT_HEIGHT;
		if (arg.field_2853 > i) {
			return -1;
		} else {
			int j = Integer.MIN_VALUE;
			int k = this.fontRenderer.FONT_HEIGHT;
			int l = 0;

			for (String string2 = string; !string2.isEmpty() && j < i; k += this.fontRenderer.FONT_HEIGHT) {
				int m = this.method_2417(string2, 114);
				if (m < string2.length()) {
					String string3 = string2.substring(0, m);
					if (arg.field_2853 >= j && arg.field_2853 < k) {
						int n = this.method_2425(string3, arg.field_2854);
						return n < 0 ? -1 : l + n;
					}

					char c = string2.charAt(m);
					boolean bl = c == ' ' || c == '\n';
					string2 = TextFormat.method_538(string3) + string2.substring(m + (bl ? 1 : 0));
					l += string3.length() + (bl ? 1 : 0);
				} else if (arg.field_2853 >= j && arg.field_2853 < k) {
					int o = this.method_2425(string2, arg.field_2854);
					return o < 0 ? -1 : l + o;
				}

				j = k;
			}

			return string.length();
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			long l = SystemUtil.getMeasuringTimeMili();
			TextComponent textComponent = this.method_2433(d, e);
			if (textComponent != null && this.handleTextComponentClick(textComponent)) {
				return true;
			}

			String string = this.method_2427();
			if (!string.isEmpty()) {
				EditBookGui.class_475 lv = new EditBookGui.class_475((int)d, (int)e);
				this.method_2443(lv);
				this.method_2429(lv);
				int j = this.method_2404(string, lv);
				if (j >= 0) {
					if (j != this.field_2827 || l - this.field_2830 >= 250L) {
						this.field_2833 = j;
						if (!Gui.isShiftPressed()) {
							this.field_2829 = this.field_2833;
						}
					} else if (this.field_2829 == this.field_2833) {
						this.field_2829 = this.fontRenderer.method_16196(string, -1, j, false);
						this.field_2833 = this.fontRenderer.method_16196(string, 1, j, false);
					} else {
						this.field_2829 = 0;
						this.field_2833 = this.method_2427().length();
					}
				}

				this.field_2827 = j;
			}

			this.field_2830 = l;
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i == 0 && this.pageTag != null && this.field_2840 >= 0 && this.field_2840 < this.pageTag.size()) {
			String string = this.pageTag.getString(this.field_2840);
			EditBookGui.class_475 lv = new EditBookGui.class_475((int)d, (int)e);
			this.method_2443(lv);
			this.method_2429(lv);
			int j = this.method_2404(string, lv);
			if (j >= 0) {
				this.field_2833 = j;
			}
		}

		return super.mouseDragged(d, e, i, f, g);
	}

	@Override
	public boolean handleTextComponentClick(TextComponent textComponent) {
		ClickEvent clickEvent = textComponent.getStyle().getClickEvent();
		if (clickEvent == null) {
			return false;
		} else if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
			String string = clickEvent.getValue();

			try {
				int i = Integer.parseInt(string) - 1;
				if (i >= 0 && i < this.field_2842 && i != this.field_2840) {
					this.field_2840 = i;
					this.method_2413();
					return true;
				}
			} catch (Exception var5) {
			}

			return false;
		} else {
			boolean bl = super.handleTextComponentClick(textComponent);
			if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.client.openGui(null);
			}

			return bl;
		}
	}

	@Nullable
	public TextComponent method_2433(double d, double e) {
		if (this.field_2846 == null) {
			return null;
		} else {
			int i = MathHelper.floor(d - (double)((this.width - 192) / 2) - 36.0);
			int j = MathHelper.floor(e - 2.0 - 16.0 - 16.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / this.fontRenderer.FONT_HEIGHT, this.field_2846.size());
				if (i <= 114 && j < this.client.fontRenderer.FONT_HEIGHT * k + k) {
					int l = j / this.client.fontRenderer.FONT_HEIGHT;
					if (l >= 0 && l < this.field_2846.size()) {
						TextComponent textComponent = (TextComponent)this.field_2846.get(l);
						int m = 0;

						for (TextComponent textComponent2 : textComponent) {
							if (textComponent2 instanceof StringTextComponent) {
								m += this.client.fontRenderer.getStringWidth(textComponent2.getFormattedText());
								if (m > i) {
									return textComponent2;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_474 extends ButtonWidget {
		private final boolean field_2851;

		public class_474(int i, int j, int k, boolean bl) {
			super(i, j, k, 23, 13, "");
			this.field_2851 = bl;
		}

		@Override
		public void draw(int i, int j, float f) {
			if (this.visible) {
				boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				MinecraftClient.getInstance().getTextureManager().bindTexture(EditBookGui.TEXTURE);
				int k = 0;
				int l = 192;
				if (bl) {
					k += 23;
				}

				if (!this.field_2851) {
					l += 13;
				}

				this.drawTexturedRect(this.x, this.y, k, l, 23, 13);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_475 {
		int field_2854;
		int field_2853;

		class_475() {
		}

		class_475(int i, int j) {
			this.field_2854 = i;
			this.field_2853 = j;
		}
	}
}
