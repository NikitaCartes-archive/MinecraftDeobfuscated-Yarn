/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;
import net.minecraft.world.Difficulty;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OnlineOptionsScreen
extends SimpleOptionsScreen {
    @Nullable
    private final SimpleOption<Unit> difficulty;

    public static OnlineOptionsScreen create(MinecraftClient client, Screen parent, GameOptions gameOptions) {
        ArrayList<SimpleOption> list = Lists.newArrayList();
        list.add(gameOptions.getRealmsNotifications());
        list.add(gameOptions.getAllowServerListing());
        SimpleOption simpleOption = Nullables.map(client.world, world -> {
            Difficulty difficulty = world.getDifficulty();
            return new SimpleOption<Unit>("options.difficulty.online", SimpleOption.emptyTooltip(), (optionText, unit) -> difficulty.getTranslatableName(), new SimpleOption.PotentialValuesBasedCallbacks<Unit>(List.of(Unit.INSTANCE), Codec.EMPTY.codec()), Unit.INSTANCE, unit -> {});
        });
        if (simpleOption != null) {
            list.add(simpleOption);
        }
        return new OnlineOptionsScreen(parent, gameOptions, list.toArray(new SimpleOption[0]), simpleOption);
    }

    private OnlineOptionsScreen(Screen parent, GameOptions gameOptions, SimpleOption<?>[] options, @Nullable SimpleOption<Unit> difficulty) {
        super(parent, gameOptions, Text.translatable("options.online.title"), options);
        this.difficulty = difficulty;
    }

    @Override
    protected void init() {
        ClickableWidget clickableWidget;
        super.init();
        if (this.difficulty != null && (clickableWidget = this.buttonList.getButtonFor(this.difficulty)) != null) {
            clickableWidget.active = false;
        }
        if ((clickableWidget = this.buttonList.getButtonFor(this.gameOptions.getTelemetryOptInExtra())) != null) {
            clickableWidget.active = this.client.isOptionalTelemetryEnabledByApi();
        }
    }
}

