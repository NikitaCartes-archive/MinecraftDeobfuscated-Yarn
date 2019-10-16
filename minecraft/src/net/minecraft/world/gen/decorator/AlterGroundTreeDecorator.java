package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.stateprovider.StateProvider;

public class AlterGroundTreeDecorator extends TreeDecorator {
	private final StateProvider field_21316;

	public AlterGroundTreeDecorator(StateProvider stateProvider) {
		super(TreeDecoratorType.ALTER_GROUND);
		this.field_21316 = stateProvider;
	}

	public <T> AlterGroundTreeDecorator(Dynamic<T> dynamic) {
		this(
			Registry.BLOCK_STATE_PROVIDER_TYPE
				.get(new Identifier((String)dynamic.get("provider").get("type").asString().orElseThrow(RuntimeException::new)))
				.deserialize(dynamic.get("provider").orElseEmptyMap())
		);
	}

	@Override
	public void method_23469(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox) {
		int i = ((BlockPos)list.get(0)).getY();
		list.stream().filter(blockPos -> blockPos.getY() == i).forEach(blockPos -> {
			this.method_23462(iWorld, random, blockPos.west().north());
			this.method_23462(iWorld, random, blockPos.east(2).north());
			this.method_23462(iWorld, random, blockPos.west().south(2));
			this.method_23462(iWorld, random, blockPos.east(2).south(2));

			for (int ix = 0; ix < 5; ix++) {
				int j = random.nextInt(64);
				int k = j % 8;
				int l = j / 8;
				if (k == 0 || k == 7 || l == 0 || l == 7) {
					this.method_23462(iWorld, random, blockPos.add(-3 + k, 0, -3 + l));
				}
			}
		});
	}

	private void method_23462(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.method_23463(modifiableTestableWorld, random, blockPos.add(i, 0, j));
				}
			}
		}
	}

	private void method_23463(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		for (int i = 2; i >= -3; i--) {
			BlockPos blockPos2 = blockPos.up(i);
			if (AbstractTreeFeature.isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2)) {
				modifiableTestableWorld.setBlockState(blockPos2, this.field_21316.getBlockState(random, blockPos), 19);
				break;
			}

			if (!AbstractTreeFeature.isAir(modifiableTestableWorld, blockPos2) && i < 0) {
				break;
			}
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
				dynamicOps,
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getId(this.field_21319).toString()),
						dynamicOps.createString("provider"),
						this.field_21316.serialize(dynamicOps)
					)
				)
			)
			.getValue();
	}
}
