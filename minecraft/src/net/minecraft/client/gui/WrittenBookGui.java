package net.minecraft.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_341;
import net.minecraft.client.gui.widget.BookPageButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WrittenBookGui extends Gui {
	public static final Identifier field_17117 = new Identifier("textures/gui/book.png");
	private final List<String> field_17118;
	private int field_17119;
	private List<TextComponent> field_17120 = Collections.emptyList();
	private int field_17121 = -1;
	private BookPageButtonWidget field_17122;
	private BookPageButtonWidget field_17123;

	public WrittenBookGui(ItemStack itemStack) {
		this.field_17118 = method_17051(itemStack);
	}

	private static List<String> method_17051(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag == null) {
			return ImmutableList.of();
		} else if (!WrittenBookItem.method_8053(compoundTag)) {
			return ImmutableList.of(TextFormat.DARK_RED + "* Invalid book tag *");
		} else {
			ListTag listTag = compoundTag.getList("pages", 8).copy();
			Builder<String> builder = ImmutableList.builder();

			for (int i = 0; i < listTag.size(); i++) {
				builder.add(listTag.getString(i));
			}

			return builder.build();
		}
	}

	@Override
	protected void onInitialized() {
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				WrittenBookGui.this.client.openGui(null);
			}
		});
		int i = (this.width - 192) / 2;
		int j = 2;
		this.field_17122 = this.addButton(new BookPageButtonWidget(1, i + 116, 159, true) {
			@Override
			public void onPressed(double d, double e) {
				WrittenBookGui.this.method_17058();
			}
		});
		this.field_17123 = this.addButton(new BookPageButtonWidget(2, i + 43, 159, false) {
			@Override
			public void onPressed(double d, double e) {
				WrittenBookGui.this.method_17057();
			}
		});
		this.method_17059();
	}

	private int method_17055() {
		return this.field_17118.size();
	}

	private String method_17056() {
		return this.field_17119 >= 0 && this.field_17119 < this.field_17118.size() ? (String)this.field_17118.get(this.field_17119) : "";
	}

	private void method_17057() {
		if (this.field_17119 > 0) {
			this.field_17119--;
		}

		this.method_17059();
	}

	private void method_17058() {
		if (this.field_17119 < this.method_17055() - 1) {
			this.field_17119++;
		}

		this.method_17059();
	}

	private void method_17059() {
		this.field_17122.visible = this.field_17119 < this.method_17055() - 1;
		this.field_17123.visible = this.field_17119 > 0;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			switch (i) {
				case 266:
					this.field_17123.onPressed(0.0, 0.0);
					return true;
				case 267:
					this.field_17122.onPressed(0.0, 0.0);
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(field_17117);
		int k = (this.width - 192) / 2;
		int l = 2;
		this.drawTexturedRect(k, 2, 0, 0, 192, 192);
		String string = I18n.translate("book.pageIndicator", this.field_17119 + 1, this.method_17055());
		if (this.field_17121 != this.field_17119) {
			TextComponent textComponent = method_17050(this.method_17056());
			this.field_17120 = class_341.method_1850(textComponent, 114, this.fontRenderer, true, true);
		}

		this.field_17121 = this.field_17119;
		int m = this.method_17053(string);
		this.fontRenderer.draw(string, (float)(k - m + 192 - 44), 18.0F, 0);
		int n = Math.min(128 / this.fontRenderer.fontHeight, this.field_17120.size());

		for (int o = 0; o < n; o++) {
			TextComponent textComponent2 = (TextComponent)this.field_17120.get(o);
			this.fontRenderer.draw(textComponent2.getFormattedText(), (float)(k + 36), (float)(32 + o * this.fontRenderer.fontHeight), 0);
		}

		TextComponent textComponent3 = this.method_17048((double)i, (double)j);
		if (textComponent3 != null) {
			this.drawTextComponentHover(textComponent3, i, j);
		}

		super.draw(i, j, f);
	}

	private static TextComponent method_17050(String string) {
		try {
			TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
			if (textComponent != null) {
				return textComponent;
			}
		} catch (JsonParseException var2) {
		}

		return new StringTextComponent(string);
	}

	private int method_17053(String string) {
		return this.fontRenderer.getStringWidth(this.fontRenderer.isRightToLeft() ? this.fontRenderer.mirror(string) : string);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			TextComponent textComponent = this.method_17048(d, e);
			if (textComponent != null && this.handleTextComponentClick(textComponent)) {
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
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
				if (i >= 0 && i < this.method_17055() && i != this.field_17119) {
					this.field_17119 = i;
					this.method_17059();
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
	public TextComponent method_17048(double d, double e) {
		if (this.field_17120 == null) {
			return null;
		} else {
			int i = MathHelper.floor(d - (double)((this.width - 192) / 2) - 36.0);
			int j = MathHelper.floor(e - 2.0 - 16.0 - 16.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / this.fontRenderer.fontHeight, this.field_17120.size());
				if (i <= 114 && j < this.client.fontRenderer.fontHeight * k + k) {
					int l = j / this.client.fontRenderer.fontHeight;
					if (l >= 0 && l < this.field_17120.size()) {
						TextComponent textComponent = (TextComponent)this.field_17120.get(l);
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
}
