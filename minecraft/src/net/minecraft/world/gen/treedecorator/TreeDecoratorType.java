package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<LeavesVineTreeDecorator> LEAVE_VINE = register("leave_vine", LeavesVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = register("cocoa", CocoaBeansTreeDecorator.CODEC);
	public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = register("beehive", BeehiveTreeDecorator.CODEC);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = register("alter_ground", AlterGroundTreeDecorator.CODEC);
	private final Codec<P> codec;

	private static <P extends TreeDecorator> TreeDecoratorType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.TREE_DECORATOR_TYPE, id, new TreeDecoratorType<>(codec));
	}

	private TreeDecoratorType(Codec<P> codec) {
		this.codec = codec;
	}

	public Codec<P> getCodec() {
		return this.codec;
	}
}
