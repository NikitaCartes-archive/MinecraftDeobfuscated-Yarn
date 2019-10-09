package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineTreeDecorator::new);
	public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = register("leave_vine", LeaveVineTreeDecorator::new);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = register("cocoa", CocoaBeansTreeDecorator::new);
	public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = register("beehive", BeehiveTreeDecorator::new);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = register("alter_ground", AlterGroundTreeDecorator::new);
	private final Function<Dynamic<?>, P> field_21325;

	private static <P extends TreeDecorator> TreeDecoratorType<P> register(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.TREE_DECORATOR_TYPE, string, new TreeDecoratorType<>(function));
	}

	private TreeDecoratorType(Function<Dynamic<?>, P> function) {
		this.field_21325 = function;
	}

	public P method_23472(Dynamic<?> dynamic) {
		return (P)this.field_21325.apply(dynamic);
	}
}
