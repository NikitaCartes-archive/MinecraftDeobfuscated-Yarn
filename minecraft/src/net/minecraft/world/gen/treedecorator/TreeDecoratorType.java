package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<LeavesVineTreeDecorator> LEAVE_VINE = register("leave_vine", LeavesVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<PaleMossTreeDecorator> PALE_MOSS = register("pale_moss", PaleMossTreeDecorator.CODEC);
	public static final TreeDecoratorType<CreakingHeartTreeDecorator> CREAKING_HEART = register("creaking_heart", CreakingHeartTreeDecorator.CODEC);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = register("cocoa", CocoaBeansTreeDecorator.CODEC);
	public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = register("beehive", BeehiveTreeDecorator.CODEC);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = register("alter_ground", AlterGroundTreeDecorator.CODEC);
	public static final TreeDecoratorType<AttachedToLeavesTreeDecorator> ATTACHED_TO_LEAVES = register("attached_to_leaves", AttachedToLeavesTreeDecorator.CODEC);
	private final MapCodec<P> codec;

	private static <P extends TreeDecorator> TreeDecoratorType<P> register(String id, MapCodec<P> codec) {
		return Registry.register(Registries.TREE_DECORATOR_TYPE, id, new TreeDecoratorType<>(codec));
	}

	private TreeDecoratorType(MapCodec<P> codec) {
		this.codec = codec;
	}

	public MapCodec<P> getCodec() {
		return this.codec;
	}
}
