package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class class_370 implements Toast {
	private final class_370.class_371 field_2213;
	private String field_2215;
	private String field_2217;
	private long field_2216;
	private boolean field_2214;

	public class_370(class_370.class_371 arg, TextComponent textComponent, @Nullable TextComponent textComponent2) {
		this.field_2213 = arg;
		this.field_2215 = textComponent.getString();
		this.field_2217 = textComponent2 == null ? null : textComponent2.getString();
	}

	@Override
	public Toast.Visibility method_1986(ToastManager toastManager, long l) {
		if (this.field_2214) {
			this.field_2216 = l;
			this.field_2214 = false;
		}

		toastManager.getGame().method_1531().method_4618(field_2207);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		toastManager.drawTexturedRect(0, 0, 0, 64, 160, 32);
		if (this.field_2217 == null) {
			toastManager.getGame().field_1772.draw(this.field_2215, 18.0F, 12.0F, -256);
		} else {
			toastManager.getGame().field_1772.draw(this.field_2215, 18.0F, 7.0F, -256);
			toastManager.getGame().field_1772.draw(this.field_2217, 18.0F, 18.0F, -1);
		}

		return l - this.field_2216 < 5000L ? Toast.Visibility.field_2210 : Toast.Visibility.field_2209;
	}

	public void method_1991(TextComponent textComponent, @Nullable TextComponent textComponent2) {
		this.field_2215 = textComponent.getString();
		this.field_2217 = textComponent2 == null ? null : textComponent2.getString();
		this.field_2214 = true;
	}

	public class_370.class_371 method_1989() {
		return this.field_2213;
	}

	public static void method_1990(ToastManager toastManager, class_370.class_371 arg, TextComponent textComponent, @Nullable TextComponent textComponent2) {
		class_370 lv = toastManager.method_1997(class_370.class, arg);
		if (lv == null) {
			toastManager.add(new class_370(arg, textComponent, textComponent2));
		} else {
			lv.method_1991(textComponent, textComponent2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_371 {
		field_2218,
		field_2219,
		field_2220;
	}
}
