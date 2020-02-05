package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.TagQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection connection;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int ticks;
	private long lastKeepAliveTime;
	private boolean waitingForKeepAlive;
	private long keepAliveId;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private final Int2ShortMap transactions = new Int2ShortOpenHashMap();
	private double lastTickX;
	private double lastTickY;
	private double lastTickZ;
	private double updatedX;
	private double updatedY;
	private double updatedZ;
	private Entity topmostRiddenEntity;
	private double lastTickRiddenX;
	private double lastTickRiddenY;
	private double lastTickRiddenZ;
	private double updatedRiddenX;
	private double updatedRiddenY;
	private double updatedRiddenZ;
	private Vec3d requestedTeleportPos;
	private int requestedTeleportId;
	private int teleportRequestTick;
	private boolean floating;
	private int floatingTicks;
	private boolean ridingEntity;
	private int vehicleFloatingTicks;
	private int movePacketsCount;
	private int lastTickMovePacketsCount;

	public ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
		this.server = server;
		this.connection = connection;
		connection.setPacketListener(this);
		this.player = player;
		player.networkHandler = this;
	}

	public void tick() {
		this.syncWithPlayerPosition();
		this.player.prevX = this.player.getX();
		this.player.prevY = this.player.getY();
		this.player.prevZ = this.player.getZ();
		this.player.playerTick();
		this.player.updatePositionAndAngles(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.yaw, this.player.pitch);
		this.ticks++;
		this.lastTickMovePacketsCount = this.movePacketsCount;
		if (this.floating) {
			if (++this.floatingTicks > 80) {
				LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
				this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.floating = false;
			this.floatingTicks = 0;
		}

		this.topmostRiddenEntity = this.player.getRootVehicle();
		if (this.topmostRiddenEntity != this.player && this.topmostRiddenEntity.getPrimaryPassenger() == this.player) {
			this.lastTickRiddenX = this.topmostRiddenEntity.getX();
			this.lastTickRiddenY = this.topmostRiddenEntity.getY();
			this.lastTickRiddenZ = this.topmostRiddenEntity.getZ();
			this.updatedRiddenX = this.topmostRiddenEntity.getX();
			this.updatedRiddenY = this.topmostRiddenEntity.getY();
			this.updatedRiddenZ = this.topmostRiddenEntity.getZ();
			if (this.ridingEntity && this.player.getRootVehicle().getPrimaryPassenger() == this.player) {
				if (++this.vehicleFloatingTicks > 80) {
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
					this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
					return;
				}
			} else {
				this.ridingEntity = false;
				this.vehicleFloatingTicks = 0;
			}
		} else {
			this.topmostRiddenEntity = null;
			this.ridingEntity = false;
			this.vehicleFloatingTicks = 0;
		}

		this.server.getProfiler().push("keepAlive");
		long l = Util.getMeasuringTimeMs();
		if (l - this.lastKeepAliveTime >= 15000L) {
			if (this.waitingForKeepAlive) {
				this.disconnect(new TranslatableText("disconnect.timeout"));
			} else {
				this.waitingForKeepAlive = true;
				this.lastKeepAliveTime = l;
				this.keepAliveId = l;
				this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
			}
		}

		this.server.getProfiler().pop();
		if (this.messageCooldown > 0) {
			this.messageCooldown--;
		}

		if (this.creativeItemDropThreshold > 0) {
			this.creativeItemDropThreshold--;
		}

		if (this.player.getLastActionTime() > 0L
			&& this.server.getPlayerIdleTimeout() > 0
			&& Util.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.idling"));
		}
	}

	public void syncWithPlayerPosition() {
		this.lastTickX = this.player.getX();
		this.lastTickY = this.player.getY();
		this.lastTickZ = this.player.getZ();
		this.updatedX = this.player.getX();
		this.updatedY = this.player.getY();
		this.updatedZ = this.player.getZ();
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
	}

	private boolean isServerOwner() {
		return this.server.isOwner(this.player.getGameProfile());
	}

	public void disconnect(Text reason) {
		this.connection.send(new DisconnectS2CPacket(reason), future -> this.connection.disconnect(reason));
		this.connection.disableAutoRead();
		this.server.submitAndJoin(this.connection::handleDisconnection);
	}

	@Override
	public void onPlayerInput(PlayerInputC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.method_14218(packet.getSideways(), packet.getForward(), packet.isJumping(), packet.isSneaking());
	}

	private static boolean validatePlayerMove(PlayerMoveC2SPacket playerMoveC2SPacket) {
		return Doubles.isFinite(playerMoveC2SPacket.getX(0.0))
				&& Doubles.isFinite(playerMoveC2SPacket.getY(0.0))
				&& Doubles.isFinite(playerMoveC2SPacket.getZ(0.0))
				&& Floats.isFinite(playerMoveC2SPacket.getPitch(0.0F))
				&& Floats.isFinite(playerMoveC2SPacket.getYaw(0.0F))
			? Math.abs(playerMoveC2SPacket.getX(0.0)) > 3.0E7 || Math.abs(playerMoveC2SPacket.getY(0.0)) > 3.0E7 || Math.abs(playerMoveC2SPacket.getZ(0.0)) > 3.0E7
			: true;
	}

	private static boolean validateVehicleMove(VehicleMoveC2SPacket vehicleMoveC2SPacket) {
		return !Doubles.isFinite(vehicleMoveC2SPacket.getX())
			|| !Doubles.isFinite(vehicleMoveC2SPacket.getY())
			|| !Doubles.isFinite(vehicleMoveC2SPacket.getZ())
			|| !Floats.isFinite(vehicleMoveC2SPacket.getPitch())
			|| !Floats.isFinite(vehicleMoveC2SPacket.getYaw());
	}

	@Override
	public void onVehicleMove(VehicleMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (validateVehicleMove(packet)) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			Entity entity = this.player.getRootVehicle();
			if (entity != this.player && entity.getPrimaryPassenger() == this.player && entity == this.topmostRiddenEntity) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.getX();
				double e = entity.getY();
				double f = entity.getZ();
				double g = packet.getX();
				double h = packet.getY();
				double i = packet.getZ();
				float j = packet.getYaw();
				float k = packet.getPitch();
				double l = g - this.lastTickRiddenX;
				double m = h - this.lastTickRiddenY;
				double n = i - this.lastTickRiddenZ;
				double o = entity.getVelocity().lengthSquared();
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && !this.isServerOwner()) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), l, m, n);
					this.connection.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				boolean bl = serverWorld.doesNotCollide(entity, entity.getBoundingBox().contract(0.0625));
				l = g - this.updatedRiddenX;
				m = h - this.updatedRiddenY - 1.0E-6;
				n = i - this.updatedRiddenZ;
				entity.move(MovementType.PLAYER, new Vec3d(l, m, n));
				l = g - entity.getX();
				m = h - entity.getY();
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.getZ();
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					LOGGER.warn("{} moved wrongly!", entity.getName().getString());
				}

				entity.updatePositionAndAngles(g, h, i, j, k);
				boolean bl3 = serverWorld.doesNotCollide(entity, entity.getBoundingBox().contract(0.0625));
				if (bl && (bl2 || !bl3)) {
					entity.updatePositionAndAngles(d, e, f, j, k);
					this.connection.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
				this.player.method_7282(this.player.getX() - d, this.player.getY() - e, this.player.getZ() - f);
				this.ridingEntity = m >= -0.03125
					&& !this.server.isFlightEnabled()
					&& !serverWorld.isAreaNotEmpty(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
				this.updatedRiddenX = entity.getX();
				this.updatedRiddenY = entity.getY();
				this.updatedRiddenZ = entity.getZ();
			}
		}
	}

	@Override
	public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getTeleportId() == this.requestedTeleportId) {
			this.player
				.updatePositionAndAngles(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
			this.updatedX = this.requestedTeleportPos.x;
			this.updatedY = this.requestedTeleportPos.y;
			this.updatedZ = this.requestedTeleportPos.z;
			if (this.player.isInTeleportationState()) {
				this.player.onTeleportationDone();
			}

			this.requestedTeleportPos = null;
		}
	}

	@Override
	public void onRecipeBookData(RecipeBookDataC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getMode() == RecipeBookDataC2SPacket.Mode.SHOWN) {
			this.server.getRecipeManager().get(packet.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
		} else if (packet.getMode() == RecipeBookDataC2SPacket.Mode.SETTINGS) {
			this.player.getRecipeBook().setGuiOpen(packet.isGuiOpen());
			this.player.getRecipeBook().setFilteringCraftable(packet.isFilteringCraftable());
			this.player.getRecipeBook().setFurnaceGuiOpen(packet.isFurnaceGuiOpen());
			this.player.getRecipeBook().setFurnaceFilteringCraftable(packet.isFurnaceFilteringCraftable());
			this.player.getRecipeBook().setBlastFurnaceGuiOpen(packet.isBlastFurnaceGuiOpen());
			this.player.getRecipeBook().setBlastFurnaceFilteringCraftable(packet.isBlastFurnaceFilteringCraftable());
			this.player.getRecipeBook().setSmokerGuiOpen(packet.isSmokerGuiOpen());
			this.player.getRecipeBook().setSmokerFilteringCraftable(packet.isSmokerGuiFilteringCraftable());
		}
	}

	@Override
	public void onAdvancementTab(AdvancementTabC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			Identifier identifier = packet.getTabToOpen();
			Advancement advancement = this.server.getAdvancementLoader().get(identifier);
			if (advancement != null) {
				this.player.getAdvancementTracker().setDisplayTab(advancement);
			}
		}
	}

	@Override
	public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		StringReader stringReader = new StringReader(packet.getPartialCommand());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
		this.server
			.getCommandManager()
			.getDispatcher()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.connection.send(new CommandSuggestionsS2CPacket(packet.getCompletionId(), suggestions)));
	}

	@Override
	public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = packet.getBlockPos();
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = packet.getCommand();
			boolean bl = packet.shouldTrackOutput();
			if (commandBlockExecutor != null) {
				CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
				Direction direction = this.player.world.getBlockState(blockPos).get(CommandBlock.FACING);
				switch (packet.getType()) {
					case SEQUENCE: {
						BlockState blockState = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
						this.player
							.world
							.setBlockState(blockPos, blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(packet.isConditional())), 2);
						break;
					}
					case AUTO: {
						BlockState blockState = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
						this.player
							.world
							.setBlockState(blockPos, blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(packet.isConditional())), 2);
						break;
					}
					case REDSTONE:
					default: {
						BlockState blockState = Blocks.COMMAND_BLOCK.getDefaultState();
						this.player
							.world
							.setBlockState(blockPos, blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(packet.isConditional())), 2);
					}
				}

				blockEntity.cancelRemoval();
				this.player.world.setBlockEntity(blockPos, blockEntity);
				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.shouldTrackOutput(bl);
				if (!bl) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockBlockEntity.setAuto(packet.isAlwaysActive());
				if (type != packet.getType()) {
					commandBlockBlockEntity.method_23359();
				}

				commandBlockExecutor.markDirty();
				if (!ChatUtil.isEmpty(string)) {
					this.player.sendMessage(new TranslatableText("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = packet.getMinecartCommandExecutor(this.player.world);
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(packet.getCommand());
				commandBlockExecutor.shouldTrackOutput(packet.shouldTrackOutput());
				if (!packet.shouldTrackOutput()) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockExecutor.markDirty();
				this.player.sendMessage(new TranslatableText("advMode.setCommand.success", packet.getCommand()));
			}
		}
	}

	@Override
	public void onPickFromInventory(PickFromInventoryC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.inventory.swapSlotWithHotbar(packet.getSlot());
		this.player
			.networkHandler
			.sendPacket(new ContainerSlotUpdateS2CPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getInvStack(this.player.inventory.selectedSlot)));
		this.player.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(-2, packet.getSlot(), this.player.inventory.getInvStack(packet.getSlot())));
		this.player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(this.player.inventory.selectedSlot));
	}

	@Override
	public void onRenameItem(RenameItemC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.container instanceof AnvilContainer) {
			AnvilContainer anvilContainer = (AnvilContainer)this.player.container;
			String string = SharedConstants.stripInvalidChars(packet.getItemName());
			if (string.length() <= 35) {
				anvilContainer.setNewItemName(string);
			}
		}
	}

	@Override
	public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.container instanceof BeaconContainer) {
			((BeaconContainer)this.player.container).setEffects(packet.getPrimaryEffectId(), packet.getSecondaryEffectId());
		}
	}

	@Override
	public void onStructureBlockUpdate(UpdateStructureBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof StructureBlockBlockEntity) {
				StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
				structureBlockBlockEntity.setMode(packet.getMode());
				structureBlockBlockEntity.setStructureName(packet.getStructureName());
				structureBlockBlockEntity.setOffset(packet.getOffset());
				structureBlockBlockEntity.setSize(packet.getSize());
				structureBlockBlockEntity.setMirror(packet.getMirror());
				structureBlockBlockEntity.setRotation(packet.getRotation());
				structureBlockBlockEntity.setMetadata(packet.getMetadata());
				structureBlockBlockEntity.setIgnoreEntities(packet.getIgnoreEntities());
				structureBlockBlockEntity.setShowAir(packet.shouldShowAir());
				structureBlockBlockEntity.setShowBoundingBox(packet.shouldShowBoundingBox());
				structureBlockBlockEntity.setIntegrity(packet.getIntegrity());
				structureBlockBlockEntity.setSeed(packet.getSeed());
				if (structureBlockBlockEntity.hasStructureName()) {
					String string = structureBlockBlockEntity.getStructureName();
					if (packet.getAction() == StructureBlockBlockEntity.Action.SAVE_AREA) {
						if (structureBlockBlockEntity.saveStructure()) {
							this.player.addChatMessage(new TranslatableText("structure_block.save_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableText("structure_block.save_failure", string), false);
						}
					} else if (packet.getAction() == StructureBlockBlockEntity.Action.LOAD_AREA) {
						if (!structureBlockBlockEntity.isStructureAvailable()) {
							this.player.addChatMessage(new TranslatableText("structure_block.load_not_found", string), false);
						} else if (structureBlockBlockEntity.loadStructure()) {
							this.player.addChatMessage(new TranslatableText("structure_block.load_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableText("structure_block.load_prepare", string), false);
						}
					} else if (packet.getAction() == StructureBlockBlockEntity.Action.SCAN_AREA) {
						if (structureBlockBlockEntity.detectStructureSize()) {
							this.player.addChatMessage(new TranslatableText("structure_block.size_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableText("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player.addChatMessage(new TranslatableText("structure_block.invalid_structure_name", packet.getStructureName()), false);
				}

				structureBlockBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void onJigsawUpdate(UpdateJigsawC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof JigsawBlockEntity) {
				JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
				jigsawBlockEntity.setAttachmentType(packet.getAttachmentType());
				jigsawBlockEntity.setTargetPool(packet.getTargetPool());
				jigsawBlockEntity.setFinalState(packet.getFinalState());
				jigsawBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void onVillagerTradeSelect(SelectVillagerTradeC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		int i = packet.method_12431();
		Container container = this.player.container;
		if (container instanceof MerchantContainer) {
			MerchantContainer merchantContainer = (MerchantContainer)container;
			merchantContainer.setRecipeIndex(i);
			merchantContainer.switchTo(i);
		}
	}

	@Override
	public void onBookUpdate(BookUpdateC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ItemStack itemStack = packet.getBook();
		if (!itemStack.isEmpty()) {
			if (WritableBookItem.isValid(itemStack.getTag())) {
				ItemStack itemStack2 = this.player.getStackInHand(packet.getHand());
				if (itemStack.getItem() == Items.WRITABLE_BOOK && itemStack2.getItem() == Items.WRITABLE_BOOK) {
					if (packet.wasSigned()) {
						ItemStack itemStack3 = new ItemStack(Items.WRITTEN_BOOK);
						CompoundTag compoundTag = itemStack2.getTag();
						if (compoundTag != null) {
							itemStack3.setTag(compoundTag.copy());
						}

						itemStack3.putSubTag("author", StringTag.of(this.player.getName().getString()));
						itemStack3.putSubTag("title", StringTag.of(itemStack.getTag().getString("title")));
						ListTag listTag = itemStack.getTag().getList("pages", 8);

						for (int i = 0; i < listTag.size(); i++) {
							String string = listTag.getString(i);
							Text text = new LiteralText(string);
							string = Text.Serializer.toJson(text);
							listTag.set(i, (Tag)StringTag.of(string));
						}

						itemStack3.putSubTag("pages", listTag);
						this.player.setStackInHand(packet.getHand(), itemStack3);
					} else {
						itemStack2.putSubTag("pages", itemStack.getTag().getList("pages", 8));
					}
				}
			}
		}
	}

	@Override
	public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			Entity entity = this.player.getServerWorld().getEntityById(packet.getEntityId());
			if (entity != null) {
				CompoundTag compoundTag = entity.toTag(new CompoundTag());
				this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(packet.getTransactionId(), compoundTag));
			}
		}
	}

	@Override
	public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			BlockEntity blockEntity = this.player.getServerWorld().getBlockEntity(packet.getPos());
			CompoundTag compoundTag = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
			this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(packet.getTransactionId(), compoundTag));
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (validatePlayerMove(packet)) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
			if (!this.player.notInAnyWorld) {
				if (this.ticks == 0) {
					this.syncWithPlayerPosition();
				}

				if (this.requestedTeleportPos != null) {
					if (this.ticks - this.teleportRequestTick > 20) {
						this.teleportRequestTick = this.ticks;
						this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
					}
				} else {
					this.teleportRequestTick = this.ticks;
					if (this.player.hasVehicle()) {
						this.player
							.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), packet.getYaw(this.player.yaw), packet.getPitch(this.player.pitch));
						this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
					} else {
						double d = this.player.getX();
						double e = this.player.getY();
						double f = this.player.getZ();
						double g = this.player.getY();
						double h = packet.getX(this.player.getX());
						double i = packet.getY(this.player.getY());
						double j = packet.getZ(this.player.getZ());
						float k = packet.getYaw(this.player.yaw);
						float l = packet.getPitch(this.player.pitch);
						double m = h - this.lastTickX;
						double n = i - this.lastTickY;
						double o = j - this.lastTickZ;
						double p = this.player.getVelocity().lengthSquared();
						double q = m * m + n * n + o * o;
						if (this.player.isSleeping()) {
							if (q > 1.0) {
								this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), packet.getYaw(this.player.yaw), packet.getPitch(this.player.pitch));
							}
						} else {
							this.movePacketsCount++;
							int r = this.movePacketsCount - this.lastTickMovePacketsCount;
							if (r > 5) {
								LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), r);
								r = 1;
							}

							if (!this.player.isInTeleportationState()
								&& (!this.player.getServerWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
								float s = this.player.isFallFlying() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && !this.isServerOwner()) {
									LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), m, n, o);
									this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.yaw, this.player.pitch);
									return;
								}
							}

							boolean bl = this.method_20630(serverWorld);
							m = h - this.updatedX;
							n = i - this.updatedY;
							o = j - this.updatedZ;
							if (n > 0.0) {
								this.player.fallDistance = 0.0F;
							}

							if (this.player.onGround && !packet.isOnGround() && n > 0.0) {
								this.player.jump();
							}

							this.player.move(MovementType.PLAYER, new Vec3d(m, n, o));
							this.player.onGround = packet.isOnGround();
							m = h - this.player.getX();
							n = i - this.player.getY();
							if (n > -0.5 || n < 0.5) {
								n = 0.0;
							}

							o = j - this.player.getZ();
							q = m * m + n * n + o * o;
							boolean bl2 = false;
							if (!this.player.isInTeleportationState()
								&& q > 0.0625
								&& !this.player.isSleeping()
								&& !this.player.interactionManager.isCreative()
								&& this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
								bl2 = true;
								LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
							}

							this.player.updatePositionAndAngles(h, i, j, k, l);
							this.player.method_7282(this.player.getX() - d, this.player.getY() - e, this.player.getZ() - f);
							if (!this.player.noClip && !this.player.isSleeping()) {
								boolean bl3 = this.method_20630(serverWorld);
								if (bl && (bl2 || !bl3)) {
									this.requestTeleport(d, e, f, k, l);
									return;
								}
							}

							this.floating = n >= -0.03125
								&& this.player.interactionManager.getGameMode() != GameMode.SPECTATOR
								&& !this.server.isFlightEnabled()
								&& !this.player.abilities.allowFlying
								&& !this.player.hasStatusEffect(StatusEffects.LEVITATION)
								&& !this.player.isFallFlying()
								&& !serverWorld.isAreaNotEmpty(this.player.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
							this.player.onGround = packet.isOnGround();
							this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
							this.player.handleFall(this.player.getY() - g, packet.isOnGround());
							this.updatedX = this.player.getX();
							this.updatedY = this.player.getY();
							this.updatedZ = this.player.getZ();
						}
					}
				}
			}
		}
	}

	private boolean method_20630(WorldView worldView) {
		return worldView.doesNotCollide(this.player, this.player.getBoundingBox().contract(1.0E-5F));
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
		this.teleportRequest(x, y, z, yaw, pitch, Collections.emptySet());
	}

	public void teleportRequest(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> set) {
		double d = set.contains(PlayerPositionLookS2CPacket.Flag.X) ? this.player.getX() : 0.0;
		double e = set.contains(PlayerPositionLookS2CPacket.Flag.Y) ? this.player.getY() : 0.0;
		double f = set.contains(PlayerPositionLookS2CPacket.Flag.Z) ? this.player.getZ() : 0.0;
		float g = set.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT) ? this.player.yaw : 0.0F;
		float h = set.contains(PlayerPositionLookS2CPacket.Flag.X_ROT) ? this.player.pitch : 0.0F;
		this.requestedTeleportPos = new Vec3d(x, y, z);
		if (++this.requestedTeleportId == Integer.MAX_VALUE) {
			this.requestedTeleportId = 0;
		}

		this.teleportRequestTick = this.ticks;
		this.player.updatePositionAndAngles(x, y, z, yaw, pitch);
		this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x - d, y - e, z - f, yaw - g, pitch - h, set, this.requestedTeleportId));
	}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		BlockPos blockPos = packet.getPos();
		this.player.updateLastActionTime();
		PlayerActionC2SPacket.Action action = packet.getAction();
		switch (action) {
			case SWAP_HELD_ITEMS:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.getStackInHand(Hand.OFF_HAND);
					this.player.setStackInHand(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
					this.player.setStackInHand(Hand.MAIN_HAND, itemStack);
				}

				return;
			case DROP_ITEM:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(false);
				}

				return;
			case DROP_ALL_ITEMS:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(true);
				}

				return;
			case RELEASE_USE_ITEM:
				this.player.stopUsingItem();
				return;
			case START_DESTROY_BLOCK:
			case ABORT_DESTROY_BLOCK:
			case STOP_DESTROY_BLOCK:
				this.player.interactionManager.processBlockBreakingAction(blockPos, action, packet.getDirection(), this.server.getWorldHeight());
				return;
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		BlockHitResult blockHitResult = packet.getHitY();
		BlockPos blockPos = blockHitResult.getBlockPos();
		Direction direction = blockHitResult.getSide();
		this.player.updateLastActionTime();
		if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.UP && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.requestedTeleportPos == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& serverWorld.canPlayerModifyAt(this.player, blockPos)) {
				ActionResult actionResult = this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
				if (actionResult.shouldSwingHand()) {
					this.player.swingHand(hand, true);
				}
			}
		} else {
			Text text = new TranslatableText("build.tooHigh", this.server.getWorldHeight()).formatted(Formatting.RED);
			this.player.networkHandler.sendPacket(new ChatMessageS2CPacket(text, MessageType.GAME_INFO));
		}

		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
	}

	@Override
	public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.updateLastActionTime();
		if (!itemStack.isEmpty()) {
			this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
		}
	}

	@Override
	public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			for (ServerWorld serverWorld : this.server.getWorlds()) {
				Entity entity = packet.getTarget(serverWorld);
				if (entity != null) {
					this.player.teleport(serverWorld, entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
					return;
				}
			}
		}
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
	}

	@Override
	public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		Entity entity = this.player.getVehicle();
		if (entity instanceof BoatEntity) {
			((BoatEntity)entity).setPaddleMovings(packet.isLeftPaddling(), packet.isRightPaddling());
		}
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
		this.server.forcePlayerSampleUpdate();
		this.server.getPlayerManager().sendToAll(new TranslatableText("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW));
		this.player.method_14231();
		this.server.getPlayerManager().remove(this.player);
		if (this.isServerOwner()) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stop(false);
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.sendPacket(packet, null);
	}

	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (packet instanceof ChatMessageS2CPacket) {
			ChatMessageS2CPacket chatMessageS2CPacket = (ChatMessageS2CPacket)packet;
			ChatVisibility chatVisibility = this.player.getClientChatVisibility();
			if (chatVisibility == ChatVisibility.HIDDEN && chatMessageS2CPacket.getLocation() != MessageType.GAME_INFO) {
				return;
			}

			if (chatVisibility == ChatVisibility.SYSTEM && !chatMessageS2CPacket.isNonChat()) {
				return;
			}
		}

		try {
			this.connection.send(packet, genericFutureListener);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Sending packet");
			CrashReportSection crashReportSection = crashReport.addElement("Packet being sent");
			crashReportSection.add("Packet class", (CrashCallable<String>)(() -> packet.getClass().getCanonicalName()));
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getSelectedSlot() >= 0 && packet.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
			this.player.inventory.selectedSlot = packet.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
		}
	}

	@Override
	public void onChatMessage(ChatMessageC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
			this.sendPacket(new ChatMessageS2CPacket(new TranslatableText("chat.cannotSend").formatted(Formatting.RED)));
		} else {
			this.player.updateLastActionTime();
			String string = packet.getChatMessage();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!SharedConstants.isValidChar(string.charAt(i))) {
					this.disconnect(new TranslatableText("multiplayer.disconnect.illegal_characters"));
					return;
				}
			}

			if (string.startsWith("/")) {
				this.executeCommand(string);
			} else {
				Text text = new TranslatableText("chat.type.text", this.player.getDisplayName(), string);
				this.server.getPlayerManager().broadcastChatMessage(text, false);
			}

			this.messageCooldown += 20;
			if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
				this.disconnect(new TranslatableText("disconnect.spam"));
			}
		}
	}

	private void executeCommand(String string) {
		this.server.getCommandManager().execute(this.player.getCommandSource(), string);
	}

	@Override
	public void onHandSwing(HandSwingC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		this.player.swingHand(packet.getHand());
	}

	@Override
	public void onClientCommand(ClientCommandC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		switch (packet.getMode()) {
			case PRESS_SHIFT_KEY:
				this.player.setSneaking(true);
				break;
			case RELEASE_SHIFT_KEY:
				this.player.setSneaking(false);
				break;
			case START_SPRINTING:
				this.player.setSprinting(true);
				break;
			case STOP_SPRINTING:
				this.player.setSprinting(false);
				break;
			case STOP_SLEEPING:
				if (this.player.isSleeping()) {
					this.player.wakeUp(false, true);
					this.requestedTeleportPos = this.player.getPos();
				}
				break;
			case START_RIDING_JUMP:
				if (this.player.getVehicle() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getVehicle();
					int i = packet.getMountJumpHeight();
					if (jumpingMount.canJump() && i > 0) {
						jumpingMount.startJumping(i);
					}
				}
				break;
			case STOP_RIDING_JUMP:
				if (this.player.getVehicle() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getVehicle();
					jumpingMount.stopJumping();
				}
				break;
			case OPEN_INVENTORY:
				if (this.player.getVehicle() instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.getVehicle()).openInventory(this.player);
				}
				break;
			case START_FALL_FLYING:
				if (!this.player.method_23668()) {
					this.player.method_23670();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket rpacket) {
		NetworkThreadUtils.forceMainThread(rpacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Entity entity = rpacket.getEntity(serverWorld);
		this.player.updateLastActionTime();
		if (entity != null) {
			boolean bl = this.player.canSee(entity);
			double d = 36.0;
			if (!bl) {
				d = 9.0;
			}

			if (this.player.squaredDistanceTo(entity) < d) {
				if (rpacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.INTERACT) {
					Hand hand = rpacket.getHand();
					this.player.interact(entity, hand);
				} else if (rpacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
					Hand hand = rpacket.getHand();
					ActionResult actionResult = entity.interactAt(this.player, rpacket.getHitPosition(), hand);
					if (actionResult.shouldSwingHand()) {
						this.player.swingHand(hand, true);
					}
				} else if (rpacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ProjectileEntity || entity == this.player) {
						this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_entity_attacked"));
						this.server.warn("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
						return;
					}

					this.player.attack(entity);
				}
			}
		}
	}

	@Override
	public void onClientStatus(ClientStatusC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ClientStatusC2SPacket.Mode mode = packet.getMode();
		switch (mode) {
			case PERFORM_RESPAWN:
				if (this.player.notInAnyWorld) {
					this.player.notInAnyWorld = false;
					this.player = this.server.getPlayerManager().respawnPlayer(this.player, DimensionType.OVERWORLD, true);
					Criterions.CHANGED_DIMENSION.trigger(this.player, DimensionType.THE_END, DimensionType.OVERWORLD);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().respawnPlayer(this.player, DimensionType.OVERWORLD, false);
					if (this.server.isHardcore()) {
						this.player.setGameMode(GameMode.SPECTATOR);
						this.player.getServerWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS).set(false, this.server);
					}
				}
				break;
			case REQUEST_STATS:
				this.player.getStatHandler().sendStats(this.player);
		}
	}

	@Override
	public void onGuiClose(GuiCloseC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.closeCurrentScreen();
	}

	@Override
	public void onClickWindow(ClickWindowC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.container.syncId == packet.getSyncId() && this.player.container.isNotRestricted(this.player)) {
			if (this.player.isSpectator()) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.of();

				for (int i = 0; i < this.player.container.slots.size(); i++) {
					defaultedList.add(((Slot)this.player.container.slots.get(i)).getStack());
				}

				this.player.onContainerRegistered(this.player.container, defaultedList);
			} else {
				ItemStack itemStack = this.player.container.onSlotClick(packet.getSlot(), packet.getButton(), packet.getActionType(), this.player);
				if (ItemStack.areEqualIgnoreDamage(packet.getStack(), itemStack)) {
					this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getTransactionId(), true));
					this.player.field_13991 = true;
					this.player.container.sendContentUpdates();
					this.player.method_14241();
					this.player.field_13991 = false;
				} else {
					this.transactions.put(this.player.container.syncId, packet.getTransactionId());
					this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getTransactionId(), false));
					this.player.container.setPlayerRestriction(this.player, false);
					DefaultedList<ItemStack> defaultedList2 = DefaultedList.of();

					for (int j = 0; j < this.player.container.slots.size(); j++) {
						ItemStack itemStack2 = ((Slot)this.player.container.slots.get(j)).getStack();
						defaultedList2.add(itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
					}

					this.player.onContainerRegistered(this.player.container, defaultedList2);
				}
			}
		}
	}

	@Override
	public void onCraftRequest(CraftRequestC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (!this.player.isSpectator()
			&& this.player.container.syncId == packet.getSyncId()
			&& this.player.container.isNotRestricted(this.player)
			&& this.player.container instanceof CraftingContainer) {
			this.server
				.getRecipeManager()
				.get(packet.getRecipe())
				.ifPresent(recipe -> ((CraftingContainer)this.player.container).fillInputSlots(packet.shouldCraftAll(), recipe, this.player));
		}
	}

	@Override
	public void onButtonClick(ButtonClickC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.container.syncId == packet.getSyncId() && this.player.container.isNotRestricted(this.player) && !this.player.isSpectator()) {
			this.player.container.onButtonClick(this.player, packet.getButtonId());
			this.player.container.sendContentUpdates();
		}
	}

	@Override
	public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.interactionManager.isCreative()) {
			boolean bl = packet.getSlot() < 0;
			ItemStack itemStack = packet.getItemStack();
			CompoundTag compoundTag = itemStack.getSubTag("BlockEntityTag");
			if (!itemStack.isEmpty() && compoundTag != null && compoundTag.contains("x") && compoundTag.contains("y") && compoundTag.contains("z")) {
				BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
				BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
					compoundTag2.remove("x");
					compoundTag2.remove("y");
					compoundTag2.remove("z");
					itemStack.putSubTag("BlockEntityTag", compoundTag2);
				}
			}

			boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getCount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				if (itemStack.isEmpty()) {
					this.player.playerContainer.setStackInSlot(packet.getSlot(), ItemStack.EMPTY);
				} else {
					this.player.playerContainer.setStackInSlot(packet.getSlot(), itemStack);
				}

				this.player.playerContainer.setPlayerRestriction(this.player, true);
				this.player.playerContainer.sendContentUpdates();
			} else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
				this.creativeItemDropThreshold += 20;
				this.player.dropItem(itemStack, true);
			}
		}
	}

	@Override
	public void onConfirmTransaction(ConfirmGuiActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		int i = this.player.container.syncId;
		if (i == packet.getWindowId()
			&& this.transactions.getOrDefault(i, (short)(packet.getSyncId() + 1)) == packet.getSyncId()
			&& !this.player.container.isNotRestricted(this.player)
			&& !this.player.isSpectator()) {
			this.player.container.setPlayerRestriction(this.player, true);
		}
	}

	@Override
	public void onSignUpdate(UpdateSignC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = packet.getPos();
		if (serverWorld.isChunkLoaded(blockPos)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
			if (!(blockEntity instanceof SignBlockEntity)) {
				return;
			}

			SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
			if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
				this.server.warn("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
				return;
			}

			String[] strings = packet.getText();

			for (int i = 0; i < strings.length; i++) {
				signBlockEntity.setTextOnRow(i, new LiteralText(Formatting.strip(strings[i])));
			}

			signBlockEntity.markDirty();
			serverWorld.updateListeners(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void onKeepAlive(KeepAliveC2SPacket packet) {
		if (this.waitingForKeepAlive && packet.getId() == this.keepAliveId) {
			int i = (int)(Util.getMeasuringTimeMs() - this.lastKeepAliveTime);
			this.player.pingMilliseconds = (this.player.pingMilliseconds * 3 + i) / 4;
			this.waitingForKeepAlive = false;
		} else if (!this.isServerOwner()) {
			this.disconnect(new TranslatableText("disconnect.timeout"));
		}
	}

	@Override
	public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.abilities.flying = packet.isFlying() && this.player.abilities.allowFlying;
	}

	@Override
	public void onClientSettings(ClientSettingsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.setClientSettings(packet);
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket packet) {
	}

	@Override
	public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2) || this.isServerOwner()) {
			this.server.setDifficulty(packet.getDifficulty(), false);
		}
	}

	@Override
	public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2) || this.isServerOwner()) {
			this.server.setDifficultyLocked(packet.isDifficultyLocked());
		}
	}
}
