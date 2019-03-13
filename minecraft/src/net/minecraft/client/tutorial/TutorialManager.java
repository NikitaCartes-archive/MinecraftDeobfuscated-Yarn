package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.text.KeybindTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TutorialManager {
	private final MinecraftClient client;
	@Nullable
	private TutorialStepHandler field_5646;

	public TutorialManager(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_4909(Input input) {
		if (this.field_5646 != null) {
			this.field_5646.method_4903(input);
		}
	}

	public void method_4908(double d, double e) {
		if (this.field_5646 != null) {
			this.field_5646.method_4901(d, e);
		}
	}

	public void method_4911(@Nullable ClientWorld clientWorld, @Nullable HitResult hitResult) {
		if (this.field_5646 != null && hitResult != null && clientWorld != null) {
			this.field_5646.method_4898(clientWorld, hitResult);
		}
	}

	public void method_4907(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
		if (this.field_5646 != null) {
			this.field_5646.method_4900(clientWorld, blockPos, blockState, f);
		}
	}

	public void onInventoryOpened() {
		if (this.field_5646 != null) {
			this.field_5646.onInventoryOpened();
		}
	}

	public void onSlotUpdate(ItemStack itemStack) {
		if (this.field_5646 != null) {
			this.field_5646.onSlotUpdate(itemStack);
		}
	}

	public void destroyHandler() {
		if (this.field_5646 != null) {
			this.field_5646.destroy();
			this.field_5646 = null;
		}
	}

	public void createHandler() {
		if (this.field_5646 != null) {
			this.destroyHandler();
		}

		this.field_5646 = this.client.field_1690.field_1875.createHandler(this);
	}

	public void tick() {
		if (this.field_5646 != null) {
			if (this.client.field_1687 != null) {
				this.field_5646.tick();
			} else {
				this.destroyHandler();
			}
		} else if (this.client.field_1687 != null) {
			this.createHandler();
		}
	}

	public void method_4910(TutorialStep tutorialStep) {
		this.client.field_1690.field_1875 = tutorialStep;
		this.client.field_1690.write();
		if (this.field_5646 != null) {
			this.field_5646.destroy();
			this.field_5646 = tutorialStep.createHandler(this);
		}
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	public GameMode getGameMode() {
		return this.client.field_1761 == null ? GameMode.INVALID : this.client.field_1761.getCurrentGameMode();
	}

	public static TextComponent method_4913(String string) {
		return new KeybindTextComponent("key." + string).applyFormat(TextFormat.field_1067);
	}
}
