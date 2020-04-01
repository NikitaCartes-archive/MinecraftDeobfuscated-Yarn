package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = register(
		"trunk_vine", TrunkVineTreeDecorator::new, TrunkVineTreeDecorator::method_26668
	);
	public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = register(
		"leave_vine", LeaveVineTreeDecorator::new, LeaveVineTreeDecorator::method_26666
	);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = register("cocoa", CocoaBeansTreeDecorator::new, CocoaBeansTreeDecorator::method_26665);
	public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = register("beehive", BeehiveTreeDecorator::new, BeehiveTreeDecorator::method_26664);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = register(
		"alter_ground", AlterGroundTreeDecorator::new, AlterGroundTreeDecorator::method_26663
	);
	private final Function<Dynamic<?>, P> field_21325;
	private final Function<Random, P> field_23595;

	private static <P extends TreeDecorator> TreeDecoratorType<P> register(String string, Function<Dynamic<?>, P> function, Function<Random, P> function2) {
		return Registry.register(Registry.TREE_DECORATOR_TYPE, string, new TreeDecoratorType<>(function, function2));
	}

	public TreeDecoratorType(Function<Dynamic<?>, P> function, Function<Random, P> function2) {
		this.field_21325 = function;
		this.field_23595 = function2;
	}

	public P method_23472(Dynamic<?> dynamic) {
		return (P)this.field_21325.apply(dynamic);
	}

	public P method_26667(Random random) {
		return (P)this.field_23595.apply(random);
	}
}
