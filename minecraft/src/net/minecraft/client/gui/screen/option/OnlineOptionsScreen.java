package net.minecraft.client.gui.screen.option;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class OnlineOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.online.title");
	@Nullable
	private SimpleOption<Unit> difficulty;

	public OnlineOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, TITLE_TEXT);
	}

	@Override
	protected void init() {
		super.init();
		if (this.difficulty != null) {
			ClickableWidget clickableWidget = this.field_51824.getWidgetFor(this.difficulty);
			if (clickableWidget != null) {
				clickableWidget.active = false;
			}
		}
	}

	private SimpleOption<?>[] method_60333(GameOptions gameOptions, MinecraftClient minecraftClient) {
		List<SimpleOption<?>> list = new ArrayList();
		list.add(gameOptions.getRealmsNotifications());
		list.add(gameOptions.getAllowServerListing());
		SimpleOption<Unit> simpleOption = Nullables.map(
			minecraftClient.world,
			clientWorld -> {
				Difficulty difficulty = clientWorld.getDifficulty();
				return new SimpleOption<>(
					"options.difficulty.online",
					SimpleOption.emptyTooltip(),
					(text, unit) -> difficulty.getTranslatableName(),
					new SimpleOption.PotentialValuesBasedCallbacks<>(List.of(Unit.INSTANCE), Codec.EMPTY.codec()),
					Unit.INSTANCE,
					unit -> {
					}
				);
			}
		);
		if (simpleOption != null) {
			this.difficulty = simpleOption;
			list.add(simpleOption);
		}

		return (SimpleOption<?>[])list.toArray(new SimpleOption[0]);
	}

	@Override
	protected void method_60325() {
		this.field_51824.addAll(this.method_60333(this.gameOptions, this.client));
	}
}
