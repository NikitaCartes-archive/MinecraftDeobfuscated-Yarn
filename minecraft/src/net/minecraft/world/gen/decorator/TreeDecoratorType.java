package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = method_28895("trunk_vine", TrunkVineTreeDecorator.field_24964);
	public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = method_28895("leave_vine", LeaveVineTreeDecorator.field_24960);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = method_28895("cocoa", CocoaBeansTreeDecorator.field_24959);
	public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = method_28895("beehive", BeehiveTreeDecorator.field_24958);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = method_28895("alter_ground", AlterGroundTreeDecorator.field_24957);
	private final Codec<P> field_24963;

	private static <P extends TreeDecorator> TreeDecoratorType<P> method_28895(String string, Codec<P> codec) {
		return Registry.register(Registry.TREE_DECORATOR_TYPE, string, new TreeDecoratorType<>(codec));
	}

	private TreeDecoratorType(Codec<P> codec) {
		this.field_24963 = codec;
	}

	public Codec<P> method_28894() {
		return this.field_24963;
	}
}
