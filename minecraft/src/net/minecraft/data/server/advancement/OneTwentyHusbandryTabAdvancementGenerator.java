package net.minecraft.data.server.advancement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;

public class OneTwentyHusbandryTabAdvancementGenerator extends HusbandryTabAdvancementGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<Advancement> exporter) {
		Advancement advancement = this.createRootAdvancement(exporter);
		Advancement advancement2 = this.createBreedAnimalAdvancement(advancement, exporter);
		this.createBreedAllAnimalsAdvancement(advancement2, exporter);
	}

	@Override
	public EntityType<?>[] getBreedableAnimals() {
		EntityType<?>[] entityTypes = super.getBreedableAnimals();
		List<EntityType<?>> list = (List<EntityType<?>>)Arrays.stream(entityTypes).collect(Collectors.toList());
		list.add(EntityType.CAMEL);
		return (EntityType<?>[])list.toArray(entityTypes);
	}
}
