package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public final class BlockFilterDecoratorConfig extends Record implements DecoratorConfig {
	private final List<Block> allowed;
	private final List<Block> disallowed;
	private final BlockPos offset;
	public static final Codec<BlockFilterDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.BLOCK.listOf().optionalFieldOf("allowed", List.of()).forGetter(BlockFilterDecoratorConfig::allowed),
					Registry.BLOCK.listOf().optionalFieldOf("disallowed", List.of()).forGetter(BlockFilterDecoratorConfig::disallowed),
					BlockPos.CODEC.fieldOf("offset").forGetter(BlockFilterDecoratorConfig::offset)
				)
				.apply(instance, BlockFilterDecoratorConfig::new)
	);

	public BlockFilterDecoratorConfig(List<Block> list, List<Block> list2, BlockPos blockPos) {
		this.allowed = list;
		this.disallowed = list2;
		this.offset = blockPos;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",BlockFilterDecoratorConfig,"allowed;disallowed;offset",BlockFilterDecoratorConfig::allowed,BlockFilterDecoratorConfig::disallowed,BlockFilterDecoratorConfig::offset>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",BlockFilterDecoratorConfig,"allowed;disallowed;offset",BlockFilterDecoratorConfig::allowed,BlockFilterDecoratorConfig::disallowed,BlockFilterDecoratorConfig::offset>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",BlockFilterDecoratorConfig,"allowed;disallowed;offset",BlockFilterDecoratorConfig::allowed,BlockFilterDecoratorConfig::disallowed,BlockFilterDecoratorConfig::offset>(
			this, object
		);
	}

	public List<Block> allowed() {
		return this.allowed;
	}

	public List<Block> disallowed() {
		return this.disallowed;
	}

	public BlockPos offset() {
		return this.offset;
	}
}
