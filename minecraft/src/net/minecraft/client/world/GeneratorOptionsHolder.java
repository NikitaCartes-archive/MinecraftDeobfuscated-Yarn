package net.minecraft.client.world;

import com.mojang.serialization.Lifecycle;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.DataPackContents;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;

@Environment(EnvType.CLIENT)
public record GeneratorOptionsHolder(
	GeneratorOptions generatorOptions,
	Lifecycle worldSettingsStability,
	DynamicRegistryManager.Immutable dynamicRegistryManager,
	DataPackContents dataPackContents
) {
	public GeneratorOptionsHolder with(GeneratorOptions generatorOptions) {
		return new GeneratorOptionsHolder(generatorOptions, this.worldSettingsStability, this.dynamicRegistryManager, this.dataPackContents);
	}

	public GeneratorOptionsHolder apply(GeneratorOptionsHolder.Modifier modifier) {
		GeneratorOptions generatorOptions = (GeneratorOptions)modifier.apply(this.generatorOptions);
		return this.with(generatorOptions);
	}

	public GeneratorOptionsHolder apply(GeneratorOptionsHolder.RegistryAwareModifier modifier) {
		GeneratorOptions generatorOptions = (GeneratorOptions)modifier.apply(this.dynamicRegistryManager, this.generatorOptions);
		return this.with(generatorOptions);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Modifier extends UnaryOperator<GeneratorOptions> {
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface RegistryAwareModifier extends BiFunction<DynamicRegistryManager.Immutable, GeneratorOptions, GeneratorOptions> {
	}
}
