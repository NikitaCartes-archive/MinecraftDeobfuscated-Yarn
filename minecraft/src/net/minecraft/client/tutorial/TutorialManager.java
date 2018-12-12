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
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TutorialManager {
	private final MinecraftClient client;
	@Nullable
	private TutorialStepHandler currentHandler;

	public TutorialManager(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_4909(Input input) {
		if (this.currentHandler != null) {
			this.currentHandler.method_4903(input);
		}
	}

	public void method_4908(double d, double e) {
		if (this.currentHandler != null) {
			this.currentHandler.method_4901(d, e);
		}
	}

	public void method_4911(@Nullable ClientWorld clientWorld, @Nullable HitResult hitResult) {
		if (this.currentHandler != null && hitResult != null && clientWorld != null) {
			this.currentHandler.method_4898(clientWorld, hitResult);
		}
	}

	public void onBlockAttacked(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
		if (this.currentHandler != null) {
			this.currentHandler.onBlockAttacked(clientWorld, blockPos, blockState, f);
		}
	}

	public void onInventoryOpened() {
		if (this.currentHandler != null) {
			this.currentHandler.onInventoryOpened();
		}
	}

	public void onSlotUpdate(ItemStack itemStack) {
		if (this.currentHandler != null) {
			this.currentHandler.onSlotUpdate(itemStack);
		}
	}

	public void destroyHandler() {
		if (this.currentHandler != null) {
			this.currentHandler.destroy();
			this.currentHandler = null;
		}
	}

	public void createHandler() {
		if (this.currentHandler != null) {
			this.destroyHandler();
		}

		this.currentHandler = this.client.field_1690.tutorialStep.createHandler(this);
	}

	public void tick() {
		if (this.currentHandler != null) {
			if (this.client.world != null) {
				this.currentHandler.tick();
			} else {
				this.destroyHandler();
			}
		} else if (this.client.world != null) {
			this.createHandler();
		}
	}

	public void setStep(TutorialStep tutorialStep) {
		this.client.field_1690.tutorialStep = tutorialStep;
		this.client.field_1690.write();
		if (this.currentHandler != null) {
			this.currentHandler.destroy();
			this.currentHandler = tutorialStep.createHandler(this);
		}
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	public GameMode getGameMode() {
		return this.client.interactionManager == null ? GameMode.INVALID : this.client.interactionManager.getCurrentGameMode();
	}

	public static TextComponent getKeybindName(String string) {
		return new KeybindTextComponent("key." + string).applyFormat(TextFormat.BOLD);
	}
}
