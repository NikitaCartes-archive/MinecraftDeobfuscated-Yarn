package net.minecraft.client.gui.screen.world;

import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;

@Environment(EnvType.CLIENT)
public record InitialWorldOptions(
	WorldCreator.Mode selectedGameMode,
	Set<GameRules.Key<GameRules.BooleanRule>> disabledGameRules,
	@Nullable RegistryKey<FlatLevelGeneratorPreset> flatLevelPreset
) {
}
