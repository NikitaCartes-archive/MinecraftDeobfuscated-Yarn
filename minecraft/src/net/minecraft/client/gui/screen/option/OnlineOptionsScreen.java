package net.minecraft.client.gui.screen.option;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
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
import org.apache.commons.compress.utils.Lists;

@Environment(EnvType.CLIENT)
public class OnlineOptionsScreen extends SimpleOptionsScreen {
	@Nullable
	private final SimpleOption<Unit> difficulty;

	public static OnlineOptionsScreen create(MinecraftClient client, Screen parent, GameOptions gameOptions) {
		List<SimpleOption<?>> list = Lists.<SimpleOption<?>>newArrayList();
		list.add(gameOptions.getRealmsNotifications());
		list.add(gameOptions.getAllowServerListing());
		SimpleOption<Unit> simpleOption = Nullables.map(
			client.world,
			world -> {
				Difficulty difficulty = world.getDifficulty();
				return new SimpleOption<>(
					"options.difficulty.online",
					SimpleOption.emptyTooltip(),
					(optionText, unit) -> difficulty.getTranslatableName(),
					new SimpleOption.PotentialValuesBasedCallbacks<>(List.of(Unit.INSTANCE), Codec.EMPTY.codec()),
					Unit.INSTANCE,
					unit -> {
					}
				);
			}
		);
		if (simpleOption != null) {
			list.add(simpleOption);
		}

		return new OnlineOptionsScreen(parent, gameOptions, (SimpleOption<?>[])list.toArray(new SimpleOption[0]), simpleOption);
	}

	private OnlineOptionsScreen(Screen parent, GameOptions gameOptions, SimpleOption<?>[] options, @Nullable SimpleOption<Unit> difficulty) {
		super(parent, gameOptions, Text.translatable("options.online.title"), options);
		this.difficulty = difficulty;
	}

	@Override
	protected void init() {
		super.init();
		if (this.difficulty != null) {
			ClickableWidget clickableWidget = this.buttonList.getWidgetFor(this.difficulty);
			if (clickableWidget != null) {
				clickableWidget.active = false;
			}
		}

		ClickableWidget clickableWidget = this.buttonList.getWidgetFor(this.gameOptions.getTelemetryOptInExtra());
		if (clickableWidget != null) {
			clickableWidget.active = this.client.isOptionalTelemetryEnabledByApi();
		}
	}
}
