package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_5405 extends Screen {
	private final StringRenderable field_25675;
	private final ImmutableList<class_5405.class_5406> field_25676;
	private List<StringRenderable> field_25677;
	private int field_25678;
	private int field_25679;

	public class_5405(Text text, List<StringRenderable> list, ImmutableList<class_5405.class_5406> immutableList) {
		super(text);
		this.field_25675 = StringRenderable.concat(list);
		this.field_25676 = immutableList;
	}

	@Override
	public String getNarrationMessage() {
		return super.getNarrationMessage() + ". " + this.field_25675.getString();
	}

	@Override
	public void init(MinecraftClient client, int width, int height) {
		super.init(client, width, height);

		for (class_5405.class_5406 lv : this.field_25676) {
			this.field_25679 = Math.max(this.field_25679, 20 + this.textRenderer.getWidth(lv.field_25680) + 20);
		}

		int i = 5 + this.field_25679 + 5;
		int j = i * this.field_25676.size();
		this.field_25677 = this.textRenderer.wrapLines(this.field_25675, j);
		int k = this.field_25677.size() * 9;
		this.field_25678 = (int)((double)height / 2.0 - (double)k / 2.0);
		int l = this.field_25678 + k + 9 * 2;
		int m = (int)((double)width / 2.0 - (double)j / 2.0);

		for (class_5405.class_5406 lv2 : this.field_25676) {
			this.addButton(new ButtonWidget(m, l, this.field_25679, 20, lv2.field_25680, lv2.field_25681));
			m += i;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.field_25678 - 9 * 2, -1);
		int i = this.field_25678;

		for (StringRenderable stringRenderable : this.field_25677) {
			this.drawCenteredText(matrices, this.textRenderer, stringRenderable, this.width / 2, i, -1);
			i += 9;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public static final class class_5406 {
		private final Text field_25680;
		private final ButtonWidget.PressAction field_25681;

		public class_5406(Text text, ButtonWidget.PressAction pressAction) {
			this.field_25680 = text;
			this.field_25681 = pressAction;
		}
	}
}
