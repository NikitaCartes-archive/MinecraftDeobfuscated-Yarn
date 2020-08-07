package net.minecraft.world.gen.tree;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> field_21320 = register("trunk_vine", TrunkVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<LeaveVineTreeDecorator> field_21321 = register("leave_vine", LeaveVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> field_21322 = register("cocoa", CocoaBeansTreeDecorator.CODEC);
	public static final TreeDecoratorType<BeehiveTreeDecorator> field_21323 = register("beehive", BeehiveTreeDecorator.CODEC);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> field_21324 = register("alter_ground", AlterGroundTreeDecorator.CODEC);
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
