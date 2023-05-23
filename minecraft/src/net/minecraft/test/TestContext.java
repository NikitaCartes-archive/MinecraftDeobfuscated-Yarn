package net.minecraft.test;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class TestContext {
	private final GameTestState test;
	private boolean hasFinalClause;

	public TestContext(GameTestState test) {
		this.test = test;
	}

	public ServerWorld getWorld() {
		return this.test.getWorld();
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.getWorld().getBlockState(this.getAbsolutePos(pos));
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.getWorld().getBlockEntity(this.getAbsolutePos(pos));
	}

	public void killAllEntities() {
		this.killAllEntities(Entity.class);
	}

	public void killAllEntities(Class entityClass) {
		Box box = this.getTestBox();
		List<Entity> list = this.getWorld().getEntitiesByClass(entityClass, box.expand(1.0), entity -> !(entity instanceof PlayerEntity));
		list.forEach(Entity::kill);
	}

	public ItemEntity spawnItem(Item item, float x, float y, float z) {
		ServerWorld serverWorld = this.getWorld();
		Vec3d vec3d = this.getAbsolute(new Vec3d((double)x, (double)y, (double)z));
		ItemEntity itemEntity = new ItemEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, new ItemStack(item, 1));
		itemEntity.setVelocity(0.0, 0.0, 0.0);
		serverWorld.spawnEntity(itemEntity);
		return itemEntity;
	}

	public ItemEntity spawnItem(Item item, BlockPos pos) {
		return this.spawnItem(item, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
	}

	public <E extends Entity> E spawnEntity(EntityType<E> type, BlockPos pos) {
		return this.spawnEntity(type, Vec3d.ofBottomCenter(pos));
	}

	public <E extends Entity> E spawnEntity(EntityType<E> type, Vec3d pos) {
		ServerWorld serverWorld = this.getWorld();
		E entity = type.create(serverWorld);
		if (entity == null) {
			throw new NullPointerException("Failed to create entity " + type.getRegistryEntry().registryKey().getValue());
		} else {
			if (entity instanceof MobEntity mobEntity) {
				mobEntity.setPersistent();
			}

			Vec3d vec3d = this.getAbsolute(pos);
			entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entity.getYaw(), entity.getPitch());
			serverWorld.spawnEntity(entity);
			return entity;
		}
	}

	public <E extends Entity> E spawnEntity(EntityType<E> type, int x, int y, int z) {
		return this.spawnEntity(type, new BlockPos(x, y, z));
	}

	public <E extends Entity> E spawnEntity(EntityType<E> type, float x, float y, float z) {
		return this.spawnEntity(type, new Vec3d((double)x, (double)y, (double)z));
	}

	public <E extends MobEntity> E spawnMob(EntityType<E> type, BlockPos pos) {
		E mobEntity = (E)this.spawnEntity(type, pos);
		mobEntity.clearGoalsAndTasks();
		return mobEntity;
	}

	public <E extends MobEntity> E spawnMob(EntityType<E> type, int x, int y, int z) {
		return this.spawnMob(type, new BlockPos(x, y, z));
	}

	public <E extends MobEntity> E spawnMob(EntityType<E> type, Vec3d pos) {
		E mobEntity = (E)this.spawnEntity(type, pos);
		mobEntity.clearGoalsAndTasks();
		return mobEntity;
	}

	public <E extends MobEntity> E spawnMob(EntityType<E> type, float x, float y, float z) {
		return this.spawnMob(type, new Vec3d((double)x, (double)y, (double)z));
	}

	public TimedTaskRunner startMovingTowards(MobEntity entity, BlockPos pos, float speed) {
		return this.createTimedTaskRunner().expectMinDurationAndRun(2, () -> {
			Path path = entity.getNavigation().findPathTo(this.getAbsolutePos(pos), 0);
			entity.getNavigation().startMovingAlong(path, (double)speed);
		});
	}

	public void pushButton(int x, int y, int z) {
		this.pushButton(new BlockPos(x, y, z));
	}

	public void pushButton(BlockPos pos) {
		this.checkBlockState(pos, state -> state.isIn(BlockTags.BUTTONS), () -> "Expected button");
		BlockPos blockPos = this.getAbsolutePos(pos);
		BlockState blockState = this.getWorld().getBlockState(blockPos);
		ButtonBlock buttonBlock = (ButtonBlock)blockState.getBlock();
		buttonBlock.powerOn(blockState, this.getWorld(), blockPos);
	}

	public void useBlock(BlockPos pos) {
		this.useBlock(pos, this.createMockCreativePlayer());
	}

	public void useBlock(BlockPos pos, PlayerEntity player) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		this.useBlock(pos, player, new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.NORTH, blockPos, true));
	}

	public void useBlock(BlockPos pos, PlayerEntity player, BlockHitResult result) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		BlockState blockState = this.getWorld().getBlockState(blockPos);
		ActionResult actionResult = blockState.onUse(this.getWorld(), player, Hand.MAIN_HAND, result);
		if (!actionResult.isAccepted()) {
			ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, result);
			player.getStackInHand(Hand.MAIN_HAND).useOnBlock(itemUsageContext);
		}
	}

	public LivingEntity drown(LivingEntity entity) {
		entity.setAir(0);
		entity.setHealth(0.25F);
		return entity;
	}

	public PlayerEntity createMockSurvivalPlayer() {
		return new PlayerEntity(this.getWorld(), BlockPos.ORIGIN, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
			@Override
			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return false;
			}
		};
	}

	public LivingEntity setHealthLow(LivingEntity entity) {
		entity.setHealth(0.25F);
		return entity;
	}

	public PlayerEntity createMockCreativePlayer() {
		return new PlayerEntity(this.getWorld(), BlockPos.ORIGIN, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
			@Override
			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return true;
			}

			@Override
			public boolean isMainPlayer() {
				return true;
			}
		};
	}

	public ServerPlayerEntity createMockCreativeServerPlayerInWorld() {
		ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(
			this.getWorld().getServer(), this.getWorld(), new GameProfile(UUID.randomUUID(), "test-mock-player")
		) {
			@Override
			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return true;
			}
		};
		this.getWorld().getServer().getPlayerManager().onPlayerConnect(new ClientConnection(NetworkSide.SERVERBOUND), serverPlayerEntity);
		return serverPlayerEntity;
	}

	public void toggleLever(int x, int y, int z) {
		this.toggleLever(new BlockPos(x, y, z));
	}

	public void toggleLever(BlockPos pos) {
		this.expectBlock(Blocks.LEVER, pos);
		BlockPos blockPos = this.getAbsolutePos(pos);
		BlockState blockState = this.getWorld().getBlockState(blockPos);
		LeverBlock leverBlock = (LeverBlock)blockState.getBlock();
		leverBlock.togglePower(blockState, this.getWorld(), blockPos);
	}

	public void putAndRemoveRedstoneBlock(BlockPos pos, long delay) {
		this.setBlockState(pos, Blocks.REDSTONE_BLOCK);
		this.waitAndRun(delay, () -> this.setBlockState(pos, Blocks.AIR));
	}

	public void removeBlock(BlockPos pos) {
		this.getWorld().breakBlock(this.getAbsolutePos(pos), false, null);
	}

	public void setBlockState(int x, int y, int z, Block block) {
		this.setBlockState(new BlockPos(x, y, z), block);
	}

	public void setBlockState(int x, int y, int z, BlockState state) {
		this.setBlockState(new BlockPos(x, y, z), state);
	}

	public void setBlockState(BlockPos pos, Block block) {
		this.setBlockState(pos, block.getDefaultState());
	}

	public void setBlockState(BlockPos pos, BlockState state) {
		this.getWorld().setBlockState(this.getAbsolutePos(pos), state, Block.NOTIFY_ALL);
	}

	public void useNightTime() {
		this.setTime(13000);
	}

	public void setTime(int timeOfDay) {
		this.getWorld().setTimeOfDay((long)timeOfDay);
	}

	public void expectBlock(Block block, int x, int y, int z) {
		this.expectBlock(block, new BlockPos(x, y, z));
	}

	public void expectBlock(Block block, BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);
		this.checkBlock(pos, block1 -> blockState.isOf(block), "Expected " + block.getName().getString() + ", got " + blockState.getBlock().getName().getString());
	}

	public void dontExpectBlock(Block block, int x, int y, int z) {
		this.dontExpectBlock(block, new BlockPos(x, y, z));
	}

	public void dontExpectBlock(Block block, BlockPos pos) {
		this.checkBlock(pos, block1 -> !this.getBlockState(pos).isOf(block), "Did not expect " + block.getName().getString());
	}

	public void expectBlockAtEnd(Block block, int x, int y, int z) {
		this.expectBlockAtEnd(block, new BlockPos(x, y, z));
	}

	public void expectBlockAtEnd(Block block, BlockPos pos) {
		this.addInstantFinalTask(() -> this.expectBlock(block, pos));
	}

	public void checkBlock(BlockPos pos, Predicate<Block> predicate, String errorMessage) {
		this.checkBlock(pos, predicate, (Supplier<String>)(() -> errorMessage));
	}

	public void checkBlock(BlockPos pos, Predicate<Block> predicate, Supplier<String> errorMessageSupplier) {
		this.checkBlockState(pos, state -> predicate.test(state.getBlock()), errorMessageSupplier);
	}

	public <T extends Comparable<T>> void expectBlockProperty(BlockPos pos, Property<T> property, T value) {
		BlockState blockState = this.getBlockState(pos);
		boolean bl = blockState.contains(property);
		if (!bl || !blockState.get(property).equals(value)) {
			String string = bl ? "was " + blockState.get(property) : "property " + property.getName() + " is missing";
			String string2 = String.format(Locale.ROOT, "Expected property %s to be %s, %s", property.getName(), value, string);
			throw new PositionedException(string2, this.getAbsolutePos(pos), pos, this.test.getTick());
		}
	}

	public <T extends Comparable<T>> void checkBlockProperty(BlockPos pos, Property<T> property, Predicate<T> predicate, String errorMessage) {
		this.checkBlockState(pos, state -> {
			if (!state.contains(property)) {
				return false;
			} else {
				T comparable = state.get(property);
				return predicate.test(comparable);
			}
		}, () -> errorMessage);
	}

	public void checkBlockState(BlockPos pos, Predicate<BlockState> predicate, Supplier<String> errorMessageSupplier) {
		BlockState blockState = this.getBlockState(pos);
		if (!predicate.test(blockState)) {
			throw new PositionedException((String)errorMessageSupplier.get(), this.getAbsolutePos(pos), pos, this.test.getTick());
		}
	}

	public void expectRedstonePower(BlockPos pos, Direction direction, IntPredicate powerPredicate, Supplier<String> errorMessage) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		ServerWorld serverWorld = this.getWorld();
		BlockState blockState = serverWorld.getBlockState(blockPos);
		int i = blockState.getWeakRedstonePower(serverWorld, blockPos, direction);
		if (!powerPredicate.test(i)) {
			throw new PositionedException((String)errorMessage.get(), blockPos, pos, this.test.getTick());
		}
	}

	public void expectEntity(EntityType<?> type) {
		List<? extends Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
		if (list.isEmpty()) {
			throw new GameTestException("Expected " + type.getUntranslatedName() + " to exist");
		}
	}

	public void expectEntityAt(EntityType<?> type, int x, int y, int z) {
		this.expectEntityAt(type, new BlockPos(x, y, z));
	}

	public void expectEntityAt(EntityType<?> type, BlockPos pos) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<? extends Entity> list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
		if (list.isEmpty()) {
			throw new PositionedException("Expected " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
		}
	}

	public void expectEntityInside(EntityType<?> type, Vec3d pos1, Vec3d pos2) {
		List<? extends Entity> list = this.getWorld().getEntitiesByType(type, new Box(pos1, pos2), Entity::isAlive);
		if (list.isEmpty()) {
			throw new PositionedException(
				"Expected " + type.getUntranslatedName() + " between ", BlockPos.ofFloored(pos1), BlockPos.ofFloored(pos2), this.test.getTick()
			);
		}
	}

	public void expectEntitiesAround(EntityType<?> type, BlockPos pos, int amount, double radius) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<? extends Entity> list = this.getEntitiesAround((EntityType<? extends Entity>)type, pos, radius);
		if (list.size() != amount) {
			throw new PositionedException(
				"Expected " + amount + " entities of type " + type.getUntranslatedName() + ", actual number of entities found=" + list.size(),
				blockPos,
				pos,
				this.test.getTick()
			);
		}
	}

	public void expectEntityAround(EntityType<?> type, BlockPos pos, double radius) {
		List<? extends Entity> list = this.getEntitiesAround((EntityType<? extends Entity>)type, pos, radius);
		if (list.isEmpty()) {
			BlockPos blockPos = this.getAbsolutePos(pos);
			throw new PositionedException("Expected " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
		}
	}

	public <T extends Entity> List<T> getEntitiesAround(EntityType<T> type, BlockPos pos, double radius) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		return this.getWorld().getEntitiesByType(type, new Box(blockPos).expand(radius), Entity::isAlive);
	}

	public void expectEntityAt(Entity entity, int x, int y, int z) {
		this.expectEntityAt(entity, new BlockPos(x, y, z));
	}

	public void expectEntityAt(Entity entity, BlockPos pos) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<? extends Entity> list = this.getWorld().getEntitiesByType(entity.getType(), new Box(blockPos), Entity::isAlive);
		list.stream()
			.filter(e -> e == entity)
			.findFirst()
			.orElseThrow(() -> new PositionedException("Expected " + entity.getType().getUntranslatedName(), blockPos, pos, this.test.getTick()));
	}

	public void expectItemsAt(Item item, BlockPos pos, double radius, int amount) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<ItemEntity> list = this.getWorld().getEntitiesByType(EntityType.ITEM, new Box(blockPos).expand(radius), Entity::isAlive);
		int i = 0;

		for (ItemEntity itemEntity : list) {
			ItemStack itemStack = itemEntity.getStack();
			if (itemStack.isOf(item)) {
				i += itemStack.getCount();
			}
		}

		if (i != amount) {
			throw new PositionedException(
				"Expected " + amount + " " + item.getName().getString() + " items to exist (found " + i + ")", blockPos, pos, this.test.getTick()
			);
		}
	}

	public void expectItemAt(Item item, BlockPos pos, double radius) {
		BlockPos blockPos = this.getAbsolutePos(pos);

		for (Entity entity : this.getWorld().getEntitiesByType(EntityType.ITEM, new Box(blockPos).expand(radius), Entity::isAlive)) {
			ItemEntity itemEntity = (ItemEntity)entity;
			if (itemEntity.getStack().getItem().equals(item)) {
				return;
			}
		}

		throw new PositionedException("Expected " + item.getName().getString() + " item", blockPos, pos, this.test.getTick());
	}

	public void dontExpectItemAt(Item item, BlockPos pos, double radius) {
		BlockPos blockPos = this.getAbsolutePos(pos);

		for (Entity entity : this.getWorld().getEntitiesByType(EntityType.ITEM, new Box(blockPos).expand(radius), Entity::isAlive)) {
			ItemEntity itemEntity = (ItemEntity)entity;
			if (itemEntity.getStack().getItem().equals(item)) {
				throw new PositionedException("Did not expect " + item.getName().getString() + " item", blockPos, pos, this.test.getTick());
			}
		}
	}

	public void dontExpectEntity(EntityType<?> type) {
		List<? extends Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
		if (!list.isEmpty()) {
			throw new GameTestException("Did not expect " + type.getUntranslatedName() + " to exist");
		}
	}

	public void dontExpectEntityAt(EntityType<?> type, int x, int y, int z) {
		this.dontExpectEntityAt(type, new BlockPos(x, y, z));
	}

	public void dontExpectEntityAt(EntityType<?> type, BlockPos pos) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<? extends Entity> list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
		if (!list.isEmpty()) {
			throw new PositionedException("Did not expect " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
		}
	}

	public void expectEntityToTouch(EntityType<?> type, double x, double y, double z) {
		Vec3d vec3d = new Vec3d(x, y, z);
		Vec3d vec3d2 = this.getAbsolute(vec3d);
		Predicate<? super Entity> predicate = entity -> entity.getBoundingBox().intersects(vec3d2, vec3d2);
		List<? extends Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), predicate);
		if (list.isEmpty()) {
			throw new GameTestException("Expected " + type.getUntranslatedName() + " to touch " + vec3d2 + " (relative " + vec3d + ")");
		}
	}

	public void dontExpectEntityToTouch(EntityType<?> type, double x, double y, double z) {
		Vec3d vec3d = new Vec3d(x, y, z);
		Vec3d vec3d2 = this.getAbsolute(vec3d);
		Predicate<? super Entity> predicate = entity -> !entity.getBoundingBox().intersects(vec3d2, vec3d2);
		List<? extends Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), predicate);
		if (list.isEmpty()) {
			throw new GameTestException("Did not expect " + type.getUntranslatedName() + " to touch " + vec3d2 + " (relative " + vec3d + ")");
		}
	}

	public <E extends Entity, T> void expectEntityWithData(BlockPos pos, EntityType<E> type, Function<? super E, T> entityDataGetter, @Nullable T data) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<E> list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
		if (list.isEmpty()) {
			throw new PositionedException("Expected " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
		} else {
			for (E entity : list) {
				T object = (T)entityDataGetter.apply(entity);
				if (object == null) {
					if (data != null) {
						throw new GameTestException("Expected entity data to be: " + data + ", but was: " + object);
					}
				} else if (!object.equals(data)) {
					throw new GameTestException("Expected entity data to be: " + data + ", but was: " + object);
				}
			}
		}
	}

	public <E extends LivingEntity> void expectEntityHoldingItem(BlockPos pos, EntityType<E> entityType, Item item) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<E> list = this.getWorld().getEntitiesByType(entityType, new Box(blockPos), Entity::isAlive);
		if (list.isEmpty()) {
			throw new PositionedException("Expected entity of type: " + entityType, blockPos, pos, this.getTick());
		} else {
			for (E livingEntity : list) {
				if (livingEntity.isHolding(item)) {
					return;
				}
			}

			throw new PositionedException("Entity should be holding: " + item, blockPos, pos, this.getTick());
		}
	}

	public <E extends Entity & InventoryOwner> void expectEntityWithItem(BlockPos pos, EntityType<E> entityType, Item item) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		List<E> list = this.getWorld().getEntitiesByType(entityType, new Box(blockPos), entityx -> ((Entity)entityx).isAlive());
		if (list.isEmpty()) {
			throw new PositionedException("Expected " + entityType.getUntranslatedName() + " to exist", blockPos, pos, this.getTick());
		} else {
			for (E entity : list) {
				if (entity.getInventory().containsAny(stack -> stack.isOf(item))) {
					return;
				}
			}

			throw new PositionedException("Entity inventory should contain: " + item, blockPos, pos, this.getTick());
		}
	}

	public void expectEmptyContainer(BlockPos pos) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
		if (blockEntity instanceof LockableContainerBlockEntity && !((LockableContainerBlockEntity)blockEntity).isEmpty()) {
			throw new GameTestException("Container should be empty");
		}
	}

	public void expectContainerWith(BlockPos pos, Item item) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
		if (!(blockEntity instanceof LockableContainerBlockEntity)) {
			throw new GameTestException("Expected a container at " + pos + ", found " + Registries.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()));
		} else if (((LockableContainerBlockEntity)blockEntity).count(item) != 1) {
			throw new GameTestException("Container should contain: " + item);
		}
	}

	public void expectSameStates(BlockBox checkedBlockBox, BlockPos correctStatePos) {
		BlockPos.stream(checkedBlockBox)
			.forEach(
				checkedPos -> {
					BlockPos blockPos2 = correctStatePos.add(
						checkedPos.getX() - checkedBlockBox.getMinX(), checkedPos.getY() - checkedBlockBox.getMinY(), checkedPos.getZ() - checkedBlockBox.getMinZ()
					);
					this.expectSameStates(checkedPos, blockPos2);
				}
			);
	}

	public void expectSameStates(BlockPos checkedPos, BlockPos correctStatePos) {
		BlockState blockState = this.getBlockState(checkedPos);
		BlockState blockState2 = this.getBlockState(correctStatePos);
		if (blockState != blockState2) {
			this.throwPositionedException("Incorrect state. Expected " + blockState2 + ", got " + blockState, checkedPos);
		}
	}

	public void expectContainerWith(long delay, BlockPos pos, Item item) {
		this.runAtTick(delay, () -> this.expectContainerWith(pos, item));
	}

	public void expectEmptyContainer(long delay, BlockPos pos) {
		this.runAtTick(delay, () -> this.expectEmptyContainer(pos));
	}

	public <E extends Entity, T> void expectEntityWithDataEnd(BlockPos pos, EntityType<E> type, Function<E, T> entityDataGetter, T data) {
		this.addInstantFinalTask(() -> this.expectEntityWithData(pos, type, entityDataGetter, data));
	}

	public <E extends Entity> void testEntity(E entity, Predicate<E> predicate, String testName) {
		if (!predicate.test(entity)) {
			throw new GameTestException("Entity " + entity + " failed " + testName + " test");
		}
	}

	public <E extends Entity, T> void testEntityProperty(E entity, Function<E, T> propertyGetter, String propertyName, T expectedValue) {
		T object = (T)propertyGetter.apply(entity);
		if (!object.equals(expectedValue)) {
			throw new GameTestException("Entity " + entity + " value " + propertyName + "=" + object + " is not equal to expected " + expectedValue);
		}
	}

	public void expectEntityAtEnd(EntityType<?> type, int x, int y, int z) {
		this.expectEntityAtEnd(type, new BlockPos(x, y, z));
	}

	public void expectEntityAtEnd(EntityType<?> type, BlockPos pos) {
		this.addInstantFinalTask(() -> this.expectEntityAt(type, pos));
	}

	public void dontExpectEntityAtEnd(EntityType<?> type, int x, int y, int z) {
		this.dontExpectEntityAtEnd(type, new BlockPos(x, y, z));
	}

	public void dontExpectEntityAtEnd(EntityType<?> type, BlockPos pos) {
		this.addInstantFinalTask(() -> this.dontExpectEntityAt(type, pos));
	}

	public void complete() {
		this.test.completeIfSuccessful();
	}

	private void markFinalCause() {
		if (this.hasFinalClause) {
			throw new IllegalStateException("This test already has final clause");
		} else {
			this.hasFinalClause = true;
		}
	}

	public void addFinalTask(Runnable runnable) {
		this.markFinalCause();
		this.test.createTimedTaskRunner().createAndAdd(0L, runnable).completeIfSuccessful();
	}

	public void addInstantFinalTask(Runnable runnable) {
		this.markFinalCause();
		this.test.createTimedTaskRunner().createAndAdd(runnable).completeIfSuccessful();
	}

	public void addFinalTaskWithDuration(int duration, Runnable runnable) {
		this.markFinalCause();
		this.test.createTimedTaskRunner().createAndAdd((long)duration, runnable).completeIfSuccessful();
	}

	public void runAtTick(long tick, Runnable runnable) {
		this.test.runAtTick(tick, runnable);
	}

	public void waitAndRun(long ticks, Runnable runnable) {
		this.runAtTick(this.test.getTick() + ticks, runnable);
	}

	public void forceRandomTick(BlockPos pos) {
		BlockPos blockPos = this.getAbsolutePos(pos);
		ServerWorld serverWorld = this.getWorld();
		serverWorld.getBlockState(blockPos).randomTick(serverWorld, blockPos, serverWorld.random);
	}

	public int getRelativeTopY(Heightmap.Type heightmap, int x, int z) {
		BlockPos blockPos = this.getAbsolutePos(new BlockPos(x, 0, z));
		return this.getRelativePos(this.getWorld().getTopPosition(heightmap, blockPos)).getY();
	}

	public void throwPositionedException(String message, BlockPos pos) {
		throw new PositionedException(message, this.getAbsolutePos(pos), pos, this.getTick());
	}

	public void throwPositionedException(String message, Entity entity) {
		throw new PositionedException(message, entity.getBlockPos(), this.getRelativePos(entity.getBlockPos()), this.getTick());
	}

	public void throwGameTestException(String message) {
		throw new GameTestException(message);
	}

	public void addTask(Runnable task) {
		this.test.createTimedTaskRunner().createAndAdd(task).fail(() -> new GameTestException("Fail conditions met"));
	}

	public void runAtEveryTick(Runnable task) {
		LongStream.range(this.test.getTick(), (long)this.test.getTicksLeft()).forEach(tick -> this.test.runAtTick(tick, task::run));
	}

	public TimedTaskRunner createTimedTaskRunner() {
		return this.test.createTimedTaskRunner();
	}

	public BlockPos getAbsolutePos(BlockPos pos) {
		BlockPos blockPos = this.test.getPos();
		BlockPos blockPos2 = blockPos.add(pos);
		return StructureTemplate.transformAround(blockPos2, BlockMirror.NONE, this.test.getRotation(), blockPos);
	}

	public BlockPos getRelativePos(BlockPos pos) {
		BlockPos blockPos = this.test.getPos();
		BlockRotation blockRotation = this.test.getRotation().rotate(BlockRotation.CLOCKWISE_180);
		BlockPos blockPos2 = StructureTemplate.transformAround(pos, BlockMirror.NONE, blockRotation, blockPos);
		return blockPos2.subtract(blockPos);
	}

	public Vec3d getAbsolute(Vec3d pos) {
		Vec3d vec3d = Vec3d.of(this.test.getPos());
		return StructureTemplate.transformAround(vec3d.add(pos), BlockMirror.NONE, this.test.getRotation(), this.test.getPos());
	}

	public Vec3d getRelative(Vec3d pos) {
		Vec3d vec3d = Vec3d.of(this.test.getPos());
		return StructureTemplate.transformAround(pos.subtract(vec3d), BlockMirror.NONE, this.test.getRotation(), this.test.getPos());
	}

	public void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new GameTestException(message);
		}
	}

	public void assertFalse(boolean condition, String message) {
		if (condition) {
			throw new GameTestException(message);
		}
	}

	public long getTick() {
		return this.test.getTick();
	}

	private Box getTestBox() {
		return this.test.getBoundingBox();
	}

	private Box getRelativeTestBox() {
		Box box = this.test.getBoundingBox();
		return box.offset(BlockPos.ORIGIN.subtract(this.getAbsolutePos(BlockPos.ORIGIN)));
	}

	public void forEachRelativePos(Consumer<BlockPos> posConsumer) {
		Box box = this.getRelativeTestBox();
		BlockPos.Mutable.stream(box.offset(0.0, 1.0, 0.0)).forEach(posConsumer);
	}

	public void forEachRemainingTick(Runnable runnable) {
		LongStream.range(this.test.getTick(), (long)this.test.getTicksLeft()).forEach(tick -> this.test.runAtTick(tick, runnable::run));
	}

	public void useStackOnBlock(PlayerEntity player, ItemStack stack, BlockPos pos, Direction direction) {
		BlockPos blockPos = this.getAbsolutePos(pos.offset(direction));
		BlockHitResult blockHitResult = new BlockHitResult(Vec3d.ofCenter(blockPos), direction, blockPos, false);
		ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, blockHitResult);
		stack.useOnBlock(itemUsageContext);
	}
}
