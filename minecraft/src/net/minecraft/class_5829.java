package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;

@Environment(EnvType.CLIENT)
public class class_5829 {
	private final TutorialManager field_28798;
	private final GameOptions field_28799;
	@Nullable
	private TutorialToast field_28800;

	public class_5829(TutorialManager tutorialManager, GameOptions gameOptions) {
		this.field_28798 = tutorialManager;
		this.field_28799 = gameOptions;
	}

	private void method_33701() {
		if (this.field_28800 != null) {
			this.field_28798.method_31364(this.field_28800);
		}

		Text text = new TranslatableText("tutorial.bundleInsert.title");
		Text text2 = new TranslatableText("tutorial.bundleInsert.description");
		this.field_28800 = new TutorialToast(TutorialToast.Type.RIGHT_CLICK, text, text2, true);
		this.field_28798.method_31365(this.field_28800, 160);
	}

	private void method_33703() {
		if (this.field_28800 != null) {
			this.field_28798.method_31364(this.field_28800);
			this.field_28800 = null;
		}

		if (!this.field_28799.field_28777) {
			this.field_28799.field_28777 = true;
			this.field_28799.write();
		}
	}

	public void method_33702(ItemStack itemStack, ItemStack itemStack2, ClickType clickType) {
		if (!this.field_28799.field_28777) {
			if (!itemStack.isEmpty() && itemStack2.isOf(Items.BUNDLE)) {
				if (clickType == ClickType.LEFT) {
					this.method_33701();
				} else if (clickType == ClickType.RIGHT) {
					this.method_33703();
				}
			} else if (itemStack.isOf(Items.BUNDLE) && !itemStack2.isEmpty() && clickType == ClickType.RIGHT) {
				this.method_33703();
			}
		}
	}
}
