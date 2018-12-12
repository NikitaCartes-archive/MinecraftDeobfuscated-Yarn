package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface FluidState extends PropertyContainer<FluidState> {
	Fluid getFluid();

	default boolean isStill() {
		return this.getFluid().isStill(this);
	}

	default boolean isEmpty() {
		return this.getFluid().isEmpty();
	}

	default float method_15763() {
		return this.getFluid().method_15788(this);
	}

	default int method_15761() {
		return this.getFluid().method_15779(this);
	}

	@Environment(EnvType.CLIENT)
	default boolean method_15756(BlockView blockView, BlockPos blockPos) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos2 = blockPos.add(i, 0, j);
				FluidState fluidState = blockView.getFluidState(blockPos2);
				if (!fluidState.getFluid().matchesType(this.getFluid()) && !blockView.getBlockState(blockPos2).isFullOpaque(blockView, blockPos2)) {
					return true;
				}
			}
		}

		return false;
	}

	default void method_15770(World world, BlockPos blockPos) {
		this.getFluid().method_15778(world, blockPos, this);
	}

	@Environment(EnvType.CLIENT)
	default void method_15768(World world, BlockPos blockPos, Random random) {
		this.getFluid().method_15776(world, blockPos, this, random);
	}

	default boolean hasRandomTicks() {
		return this.getFluid().hasRandomTicks();
	}

	default void onRandomTick(World world, BlockPos blockPos, Random random) {
		this.getFluid().onRandomTick(world, blockPos, this, random);
	}

	default Vec3d method_15758(BlockView blockView, BlockPos blockPos) {
		return this.getFluid().method_15782(blockView, blockPos, this);
	}

	default BlockState getBlockState() {
		return this.getFluid().toBlockState(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	default ParticleParameters method_15766() {
		return this.getFluid().method_15787();
	}

	@Environment(EnvType.CLIENT)
	default BlockRenderLayer getRenderLayer() {
		return this.getFluid().getRenderLayer();
	}

	default boolean matches(Tag<Fluid> tag) {
		return this.getFluid().matches(tag);
	}

	default float getBlastResistance() {
		return this.getFluid().getBlastResistance();
	}

	default boolean method_15764(Fluid fluid, Direction direction) {
		return this.getFluid().method_15777(this, fluid, direction);
	}

	static <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps, FluidState fluidState) {
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = fluidState.getEntries();
		T object;
		if (immutableMap.isEmpty()) {
			object = dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.FLUID.getId(fluidState.getFluid()).toString()))
			);
		} else {
			object = dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("Name"),
					dynamicOps.createString(Registry.FLUID.getId(fluidState.getFluid()).toString()),
					dynamicOps.createString("Properties"),
					dynamicOps.createMap(
						(Map<T, T>)immutableMap.entrySet()
							.stream()
							.map(
								entry -> Pair.of(
										dynamicOps.createString(((Property)entry.getKey()).getName()),
										dynamicOps.createString(PropertyContainer.getValueAsString((Property<T>)entry.getKey(), (Comparable<?>)entry.getValue()))
									)
							)
							.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
					)
				)
			);
		}

		return new Dynamic<>(dynamicOps, object);
	}

	static <T> FluidState deserialize(Dynamic<T> dynamic) {
		Fluid fluid = Registry.FLUID.get(new Identifier((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:empty")));
		Optional<Map<Dynamic<T>, Dynamic<T>>> optional = dynamic.get("Properties").flatMap(Dynamic::getMapValues);
		FluidState fluidState = fluid.getDefaultState();
		if (optional.isPresent()) {
			StateFactory<Fluid, FluidState> stateFactory = fluid.getStateFactory();

			for (Entry<Dynamic<T>, Dynamic<T>> entry : ((Map)optional.get()).entrySet()) {
				String string = (String)((Dynamic)entry.getKey()).getStringValue().orElse("");
				Property<?> property = stateFactory.getProperty(string);
				if (property != null) {
					fluidState = PropertyContainer.deserialize(
						fluidState, property, string, dynamic.toString(), (String)((Dynamic)entry.getValue()).getStringValue().orElse("")
					);
				}
			}
		}

		return fluidState;
	}
}
