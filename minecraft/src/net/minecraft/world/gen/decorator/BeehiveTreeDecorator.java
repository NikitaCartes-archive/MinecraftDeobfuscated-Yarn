package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BeeHiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class BeehiveTreeDecorator extends TreeDecorator {
	private final float field_21317;

	public BeehiveTreeDecorator(float f) {
		super(TreeDecoratorType.BEEHIVE);
		this.field_21317 = f;
	}

	public <T> BeehiveTreeDecorator(Dynamic<T> dynamic) {
		this(dynamic.get("probability").asFloat(0.0F));
	}

	@Override
	public void method_23469(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox) {
		if (!(random.nextFloat() >= this.field_21317)) {
			Direction direction = BeeHiveBlock.field_20418[random.nextInt(BeeHiveBlock.field_20418.length)];
			int i = !list2.isEmpty()
				? Math.max(((BlockPos)list2.get(0)).getY() - 1, ((BlockPos)list.get(0)).getY())
				: Math.min(((BlockPos)list.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)list.get(list.size() - 1)).getY());
			List<BlockPos> list3 = (List<BlockPos>)list.stream().filter(blockPosx -> blockPosx.getY() == i).collect(Collectors.toList());
			BlockPos blockPos = (BlockPos)list3.get(random.nextInt(list3.size()));
			BlockPos blockPos2 = blockPos.offset(direction);
			if (AbstractTreeFeature.isAir(iWorld, blockPos2) && AbstractTreeFeature.isAir(iWorld, blockPos2.offset(Direction.SOUTH))) {
				BlockState blockState = Blocks.BEE_NEST.getDefaultState().with(BeeHiveBlock.FACING, Direction.SOUTH);
				this.method_23470(iWorld, blockPos2, blockState, set, blockBox);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos2);
				if (blockEntity instanceof BeeHiveBlockEntity) {
					BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
					int j = 2 + random.nextInt(2);

					for (int k = 0; k < j; k++) {
						BeeEntity beeEntity = new BeeEntity(EntityType.BEE, iWorld.getWorld());
						beeHiveBlockEntity.tryEnterHive(beeEntity, false, random.nextInt(599));
					}
				}
			}
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
				ops,
				ops.createMap(
					ImmutableMap.of(
						ops.createString("type"),
						ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.field_21319).toString()),
						ops.createString("probability"),
						ops.createFloat(this.field_21317)
					)
				)
			)
			.getValue();
	}
}
