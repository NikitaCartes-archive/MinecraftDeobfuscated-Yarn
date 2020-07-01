package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = register("leave_vine", LeaveVineTreeDecorator.CODEC);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = register("cocoa", CocoaBeansTreeDecorator.CODEC);
	public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = register("beehive", BeehiveTreeDecorator.CODEC);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = register("alter_ground", AlterGroundTreeDecorator.CODEC);
	private final Codec<P> codec;

	private static <P extends TreeDecorator> TreeDecoratorType<P> register(String type, Codec<P> codec) {
		return Registry.register(Registry.TREE_DECORATOR_TYPE, type, new TreeDecoratorType<>(codec));
	}

	private TreeDecoratorType(Codec<P> codec) {
		this.codec = codec;
	}

	public Codec<P> getCodec() {
		return this.codec;
	}
}
