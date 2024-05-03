package net.minecraft.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public interface ContainerComponentModifiers {
	ContainerComponentModifier<ContainerComponent> CONTAINER = new ContainerComponentModifier<ContainerComponent>() {
		@Override
		public ComponentType<ContainerComponent> getComponentType() {
			return DataComponentTypes.CONTAINER;
		}

		public Stream<ItemStack> stream(ContainerComponent containerComponent) {
			return containerComponent.stream();
		}

		public ContainerComponent getDefault() {
			return ContainerComponent.DEFAULT;
		}

		public ContainerComponent create(ContainerComponent containerComponent, Stream<ItemStack> stream) {
			return ContainerComponent.fromStacks(stream.toList());
		}
	};
	ContainerComponentModifier<BundleContentsComponent> BUNDLE_CONTENTS = new ContainerComponentModifier<BundleContentsComponent>() {
		@Override
		public ComponentType<BundleContentsComponent> getComponentType() {
			return DataComponentTypes.BUNDLE_CONTENTS;
		}

		public BundleContentsComponent getDefault() {
			return BundleContentsComponent.DEFAULT;
		}

		public Stream<ItemStack> stream(BundleContentsComponent bundleContentsComponent) {
			return bundleContentsComponent.stream();
		}

		public BundleContentsComponent create(BundleContentsComponent bundleContentsComponent, Stream<ItemStack> stream) {
			BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent).clear();
			stream.forEach(builder::add);
			return builder.build();
		}
	};
	ContainerComponentModifier<ChargedProjectilesComponent> CHARGED_PROJECTILES = new ContainerComponentModifier<ChargedProjectilesComponent>() {
		@Override
		public ComponentType<ChargedProjectilesComponent> getComponentType() {
			return DataComponentTypes.CHARGED_PROJECTILES;
		}

		public ChargedProjectilesComponent getDefault() {
			return ChargedProjectilesComponent.DEFAULT;
		}

		public Stream<ItemStack> stream(ChargedProjectilesComponent chargedProjectilesComponent) {
			return chargedProjectilesComponent.getProjectiles().stream();
		}

		public ChargedProjectilesComponent create(ChargedProjectilesComponent chargedProjectilesComponent, Stream<ItemStack> stream) {
			return ChargedProjectilesComponent.of(stream.toList());
		}
	};
	Map<ComponentType<?>, ContainerComponentModifier<?>> TYPE_TO_MODIFIER = (Map<ComponentType<?>, ContainerComponentModifier<?>>)Stream.of(
			CONTAINER, BUNDLE_CONTENTS, CHARGED_PROJECTILES
		)
		.collect(Collectors.toMap(ContainerComponentModifier::getComponentType, containerComponentModifier -> containerComponentModifier));
	Codec<ContainerComponentModifier<?>> MODIFIER_CODEC = Registries.DATA_COMPONENT_TYPE.getCodec().comapFlatMap(componentType -> {
		ContainerComponentModifier<?> containerComponentModifier = (ContainerComponentModifier<?>)TYPE_TO_MODIFIER.get(componentType);
		return containerComponentModifier != null ? DataResult.success(containerComponentModifier) : DataResult.error(() -> "No items in component");
	}, ContainerComponentModifier::getComponentType);
}
