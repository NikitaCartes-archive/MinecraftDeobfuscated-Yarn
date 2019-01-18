package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.network.packet.ChatMessageClientPacket;
import net.minecraft.client.network.packet.CommandSuggestionsClientPacket;
import net.minecraft.client.network.packet.DisconnectClientPacket;
import net.minecraft.client.network.packet.GuiActionConfirmClientPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateClientPacket;
import net.minecraft.client.network.packet.HeldItemChangeClientPacket;
import net.minecraft.client.network.packet.KeepAliveClientPacket;
import net.minecraft.client.network.packet.PlayerPositionLookClientPacket;
import net.minecraft.client.network.packet.TagQueryResponseClientPacket;
import net.minecraft.client.network.packet.VehicleMoveClientPacket;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.ButtonClickServerPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.packet.AdvancementTabServerPacket;
import net.minecraft.server.network.packet.BoatPaddleStateServerPacket;
import net.minecraft.server.network.packet.BookUpdateServerPacket;
import net.minecraft.server.network.packet.ChatMessageServerPacket;
import net.minecraft.server.network.packet.ClickWindowServerPacket;
import net.minecraft.server.network.packet.ClientCommandServerPacket;
import net.minecraft.server.network.packet.ClientSettingsServerPacket;
import net.minecraft.server.network.packet.ClientStatusServerPacket;
import net.minecraft.server.network.packet.CraftRequestServerPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionServerPacket;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.server.network.packet.GuiActionConfirmServerPacket;
import net.minecraft.server.network.packet.GuiCloseServerPacket;
import net.minecraft.server.network.packet.HandSwingServerPacket;
import net.minecraft.server.network.packet.KeepAliveServerPacket;
import net.minecraft.server.network.packet.PickFromInventoryServerPacket;
import net.minecraft.server.network.packet.PlayerActionServerPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockServerPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityServerPacket;
import net.minecraft.server.network.packet.PlayerInteractItemServerPacket;
import net.minecraft.server.network.packet.PlayerLookServerPacket;
import net.minecraft.server.network.packet.PlayerMoveServerMessage;
import net.minecraft.server.network.packet.QueryBlockNbtServerPacket;
import net.minecraft.server.network.packet.QueryEntityNbtServerPacket;
import net.minecraft.server.network.packet.RecipeBookDataServerPacket;
import net.minecraft.server.network.packet.RenameItemServerPacket;
import net.minecraft.server.network.packet.RequestCommandCompletionsServerPacket;
import net.minecraft.server.network.packet.ResourcePackStatusServerPacket;
import net.minecraft.server.network.packet.SelectVillagerTradeServerPacket;
import net.minecraft.server.network.packet.SpectatorTeleportServerPacket;
import net.minecraft.server.network.packet.TeleportConfirmServerPacket;
import net.minecraft.server.network.packet.UpdateBeaconServerPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartServerPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockServerPacket;
import net.minecraft.server.network.packet.UpdateJigsawServerPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesServerPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotServerPacket;
import net.minecraft.server.network.packet.UpdateSignServerPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockServerPacket;
import net.minecraft.server.network.packet.VehicleMoveServerPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener, Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection client;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int ticks;
	private long lastKeepAliveTime;
	private boolean waitingForKeepAlive;
	private long keepAliveId;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private final IntHashMap<Short> transactions = new IntHashMap<>();
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

	public ServerPlayNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.client = clientConnection;
		clientConnection.setPacketListener(this);
		this.player = serverPlayerEntity;
		serverPlayerEntity.networkHandler = this;
	}

	@Override
	public void tick() {
		this.syncWithPlayerPosition();
		this.player.method_14226();
		this.player.setPositionAnglesAndUpdate(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.yaw, this.player.pitch);
		this.ticks++;
		this.lastTickMovePacketsCount = this.movePacketsCount;
		if (this.floating) {
			if (++this.floatingTicks > 80) {
				LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
				this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.floating = false;
			this.floatingTicks = 0;
		}

		this.topmostRiddenEntity = this.player.getTopmostRiddenEntity();
		if (this.topmostRiddenEntity != this.player && this.topmostRiddenEntity.getPrimaryPassenger() == this.player) {
			this.lastTickRiddenX = this.topmostRiddenEntity.x;
			this.lastTickRiddenY = this.topmostRiddenEntity.y;
			this.lastTickRiddenZ = this.topmostRiddenEntity.z;
			this.updatedRiddenX = this.topmostRiddenEntity.x;
			this.updatedRiddenY = this.topmostRiddenEntity.y;
			this.updatedRiddenZ = this.topmostRiddenEntity.z;
			if (this.ridingEntity && this.player.getTopmostRiddenEntity().getPrimaryPassenger() == this.player) {
				if (++this.vehicleFloatingTicks > 80) {
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
					this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.flying"));
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
		long l = SystemUtil.getMeasuringTimeMs();
		if (l - this.lastKeepAliveTime >= 15000L) {
			if (this.waitingForKeepAlive) {
				this.disconnect(new TranslatableTextComponent("disconnect.timeout"));
			} else {
				this.waitingForKeepAlive = true;
				this.lastKeepAliveTime = l;
				this.keepAliveId = l;
				this.sendPacket(new KeepAliveClientPacket(this.keepAliveId));
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
			&& SystemUtil.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.idling"));
		}
	}

	public void syncWithPlayerPosition() {
		this.lastTickX = this.player.x;
		this.lastTickY = this.player.y;
		this.lastTickZ = this.player.z;
		this.updatedX = this.player.x;
		this.updatedY = this.player.y;
		this.updatedZ = this.player.z;
	}

	public ClientConnection getConnection() {
		return this.client;
	}

	public void disconnect(TextComponent textComponent) {
		this.client.sendPacket(new DisconnectClientPacket(textComponent), future -> this.client.disconnect(textComponent));
		this.client.method_10757();
		this.server.executeFuture(this.client::handleDisconnection).join();
	}

	@Override
	public void onPlayerLook(PlayerLookServerPacket playerLookServerPacket) {
		NetworkThreadUtils.forceMainThread(playerLookServerPacket, this, this.player.getServerWorld());
		this.player
			.method_14218(playerLookServerPacket.getYaw(), playerLookServerPacket.getPitch(), playerLookServerPacket.isJumping(), playerLookServerPacket.isSneaking());
	}

	private static boolean validatePlayerMove(PlayerMoveServerMessage playerMoveServerMessage) {
		return Doubles.isFinite(playerMoveServerMessage.getX(0.0))
				&& Doubles.isFinite(playerMoveServerMessage.getY(0.0))
				&& Doubles.isFinite(playerMoveServerMessage.getZ(0.0))
				&& Floats.isFinite(playerMoveServerMessage.getPitch(0.0F))
				&& Floats.isFinite(playerMoveServerMessage.getYaw(0.0F))
			? Math.abs(playerMoveServerMessage.getX(0.0)) > 3.0E7
				|| Math.abs(playerMoveServerMessage.getY(0.0)) > 3.0E7
				|| Math.abs(playerMoveServerMessage.getZ(0.0)) > 3.0E7
			: true;
	}

	private static boolean validateVehicleMove(VehicleMoveServerPacket vehicleMoveServerPacket) {
		return !Doubles.isFinite(vehicleMoveServerPacket.getX())
			|| !Doubles.isFinite(vehicleMoveServerPacket.getY())
			|| !Doubles.isFinite(vehicleMoveServerPacket.getZ())
			|| !Floats.isFinite(vehicleMoveServerPacket.getPitch())
			|| !Floats.isFinite(vehicleMoveServerPacket.getYaw());
	}

	@Override
	public void onVehicleMove(VehicleMoveServerPacket vehicleMoveServerPacket) {
		NetworkThreadUtils.forceMainThread(vehicleMoveServerPacket, this, this.player.getServerWorld());
		if (validateVehicleMove(vehicleMoveServerPacket)) {
			this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			Entity entity = this.player.getTopmostRiddenEntity();
			if (entity != this.player && entity.getPrimaryPassenger() == this.player && entity == this.topmostRiddenEntity) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.x;
				double e = entity.y;
				double f = entity.z;
				double g = vehicleMoveServerPacket.getX();
				double h = vehicleMoveServerPacket.getY();
				double i = vehicleMoveServerPacket.getZ();
				float j = vehicleMoveServerPacket.getYaw();
				float k = vehicleMoveServerPacket.getPitch();
				double l = g - this.lastTickRiddenX;
				double m = h - this.lastTickRiddenY;
				double n = i - this.lastTickRiddenZ;
				double o = entity.velocityX * entity.velocityX + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ;
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(entity.getName().getString()))) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), l, m, n);
					this.client.sendPacket(new VehicleMoveClientPacket(entity));
					return;
				}

				boolean bl = serverWorld.method_8587(entity, entity.getBoundingBox().contract(0.0625));
				l = g - this.updatedRiddenX;
				m = h - this.updatedRiddenY - 1.0E-6;
				n = i - this.updatedRiddenZ;
				entity.move(MovementType.PLAYER, l, m, n);
				l = g - entity.x;
				m = h - entity.y;
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.z;
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					LOGGER.warn("{} moved wrongly!", entity.getName().getString());
				}

				entity.setPositionAnglesAndUpdate(g, h, i, j, k);
				boolean bl3 = serverWorld.method_8587(entity, entity.getBoundingBox().contract(0.0625));
				if (bl && (bl2 || !bl3)) {
					entity.setPositionAnglesAndUpdate(d, e, f, j, k);
					this.client.sendPacket(new VehicleMoveClientPacket(entity));
					return;
				}

				this.server.getPlayerManager().method_14575(this.player);
				this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
				this.ridingEntity = m >= -0.03125
					&& !this.server.isFlightEnabled()
					&& !serverWorld.isAreaNotEmpty(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
				this.updatedRiddenX = entity.x;
				this.updatedRiddenY = entity.y;
				this.updatedRiddenZ = entity.z;
			}
		}
	}

	@Override
	public void onTeleportConfirm(TeleportConfirmServerPacket teleportConfirmServerPacket) {
		NetworkThreadUtils.forceMainThread(teleportConfirmServerPacket, this, this.player.getServerWorld());
		if (teleportConfirmServerPacket.getTeleportId() == this.requestedTeleportId) {
			this.player
				.setPositionAnglesAndUpdate(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
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
	public void onRecipeBookData(RecipeBookDataServerPacket recipeBookDataServerPacket) {
		NetworkThreadUtils.forceMainThread(recipeBookDataServerPacket, this, this.player.getServerWorld());
		if (recipeBookDataServerPacket.getMode() == RecipeBookDataServerPacket.Mode.field_13011) {
			this.server.getRecipeManager().get(recipeBookDataServerPacket.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
		} else if (recipeBookDataServerPacket.getMode() == RecipeBookDataServerPacket.Mode.field_13010) {
			this.player.getRecipeBook().setGuiOpen(recipeBookDataServerPacket.isGuiOpen());
			this.player.getRecipeBook().setFilteringCraftable(recipeBookDataServerPacket.isFilteringCraftable());
			this.player.getRecipeBook().setFurnaceGuiOpen(recipeBookDataServerPacket.isFurnaceGuiOpen());
			this.player.getRecipeBook().setFurnaceFilteringCraftable(recipeBookDataServerPacket.isFurnaceFilteringCraftable());
			this.player.getRecipeBook().setBlastFurnaceGuiOpen(recipeBookDataServerPacket.isBlastFurnaceGuiOpen());
			this.player.getRecipeBook().setBlastFurnaceFilteringCraftable(recipeBookDataServerPacket.isBlastFurnaceFilteringCraftable());
			this.player.getRecipeBook().setSmokerGuiOpen(recipeBookDataServerPacket.isSmokerGuiOpen());
			this.player.getRecipeBook().setSmokerFilteringCraftable(recipeBookDataServerPacket.isSmokerGuiFilteringCraftable());
		}
	}

	@Override
	public void onAdvancementTab(AdvancementTabServerPacket advancementTabServerPacket) {
		NetworkThreadUtils.forceMainThread(advancementTabServerPacket, this, this.player.getServerWorld());
		if (advancementTabServerPacket.getAction() == AdvancementTabServerPacket.Action.field_13024) {
			Identifier identifier = advancementTabServerPacket.getTabToOpen();
			SimpleAdvancement simpleAdvancement = this.server.getAdvancementManager().get(identifier);
			if (simpleAdvancement != null) {
				this.player.getAdvancementManager().setDisplayTab(simpleAdvancement);
			}
		}
	}

	@Override
	public void onRequestCommandCompletions(RequestCommandCompletionsServerPacket requestCommandCompletionsServerPacket) {
		NetworkThreadUtils.forceMainThread(requestCommandCompletionsServerPacket, this, this.player.getServerWorld());
		StringReader stringReader = new StringReader(requestCommandCompletionsServerPacket.getPartialCommand());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
		this.server
			.getCommandManager()
			.getDispatcher()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.client.sendPacket(new CommandSuggestionsClientPacket(requestCommandCompletionsServerPacket.getCompletionId(), suggestions)));
	}

	@Override
	public void onUpdateCommandBlock(UpdateCommandBlockServerPacket updateCommandBlockServerPacket) {
		NetworkThreadUtils.forceMainThread(updateCommandBlockServerPacket, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notEnabled"));
		} else if (!this.player.method_7338()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = updateCommandBlockServerPacket.getBlockPos();
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = updateCommandBlockServerPacket.getCommand();
			boolean bl = updateCommandBlockServerPacket.shouldTrackOutput();
			if (commandBlockExecutor != null) {
				Direction direction = this.player.world.getBlockState(blockPos).get(CommandBlock.FACING);
				switch (updateCommandBlockServerPacket.getType()) {
					case CHAIN: {
						BlockState blockState = Blocks.field_10395.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos,
								blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(updateCommandBlockServerPacket.isConditional())),
								2
							);
						break;
					}
					case REPEATING: {
						BlockState blockState = Blocks.field_10263.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos,
								blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(updateCommandBlockServerPacket.isConditional())),
								2
							);
						break;
					}
					case NORMAL:
					default: {
						BlockState blockState = Blocks.field_10525.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos,
								blockState.with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(updateCommandBlockServerPacket.isConditional())),
								2
							);
					}
				}

				blockEntity.validate();
				this.player.world.setBlockEntity(blockPos, blockEntity);
				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.shouldTrackOutput(bl);
				if (!bl) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockBlockEntity.setAuto(updateCommandBlockServerPacket.isAlwaysActive());
				commandBlockExecutor.method_8295();
				if (!ChatUtil.isEmpty(string)) {
					this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartServerPacket updateCommandBlockMinecartServerPacket) {
		NetworkThreadUtils.forceMainThread(updateCommandBlockMinecartServerPacket, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notEnabled"));
		} else if (!this.player.method_7338()) {
			this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = updateCommandBlockMinecartServerPacket.getMinecartCommandExecutor(this.player.world);
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(updateCommandBlockMinecartServerPacket.getCommand());
				commandBlockExecutor.shouldTrackOutput(updateCommandBlockMinecartServerPacket.shouldTrackOutput());
				if (!updateCommandBlockMinecartServerPacket.shouldTrackOutput()) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockExecutor.method_8295();
				this.player.appendCommandFeedback(new TranslatableTextComponent("advMode.setCommand.success", updateCommandBlockMinecartServerPacket.getCommand()));
			}
		}
	}

	@Override
	public void onPickFromInventory(PickFromInventoryServerPacket pickFromInventoryServerPacket) {
		NetworkThreadUtils.forceMainThread(pickFromInventoryServerPacket, this, this.player.getServerWorld());
		this.player.inventory.swapSlotWithHotbar(pickFromInventoryServerPacket.getSlot());
		this.player
			.networkHandler
			.sendPacket(new GuiSlotUpdateClientPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getInvStack(this.player.inventory.selectedSlot)));
		this.player
			.networkHandler
			.sendPacket(
				new GuiSlotUpdateClientPacket(-2, pickFromInventoryServerPacket.getSlot(), this.player.inventory.getInvStack(pickFromInventoryServerPacket.getSlot()))
			);
		this.player.networkHandler.sendPacket(new HeldItemChangeClientPacket(this.player.inventory.selectedSlot));
	}

	@Override
	public void onRenameItem(RenameItemServerPacket renameItemServerPacket) {
		NetworkThreadUtils.forceMainThread(renameItemServerPacket, this, this.player.getServerWorld());
		if (this.player.container instanceof AnvilContainer) {
			AnvilContainer anvilContainer = (AnvilContainer)this.player.container;
			String string = SharedConstants.stripInvalidChars(renameItemServerPacket.getItemName());
			if (string.length() <= 35) {
				anvilContainer.setNewItemName(string);
			}
		}
	}

	@Override
	public void onUpdateBeacon(UpdateBeaconServerPacket updateBeaconServerPacket) {
		NetworkThreadUtils.forceMainThread(updateBeaconServerPacket, this, this.player.getServerWorld());
		if (this.player.container instanceof BeaconContainer) {
			((BeaconContainer)this.player.container).method_17372(updateBeaconServerPacket.getPrimaryEffectId(), updateBeaconServerPacket.getSecondaryEffectId());
		}
	}

	@Override
	public void onStructureBlockUpdate(UpdateStructureBlockServerPacket updateStructureBlockServerPacket) {
		NetworkThreadUtils.forceMainThread(updateStructureBlockServerPacket, this, this.player.getServerWorld());
		if (this.player.method_7338()) {
			BlockPos blockPos = updateStructureBlockServerPacket.getPos();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof StructureBlockBlockEntity) {
				StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
				structureBlockBlockEntity.method_11381(updateStructureBlockServerPacket.getMode());
				structureBlockBlockEntity.setStructureName(updateStructureBlockServerPacket.getStructureName());
				structureBlockBlockEntity.setOffset(updateStructureBlockServerPacket.getOffset());
				structureBlockBlockEntity.setSize(updateStructureBlockServerPacket.getSize());
				structureBlockBlockEntity.setMirror(updateStructureBlockServerPacket.getMirror());
				structureBlockBlockEntity.setRotation(updateStructureBlockServerPacket.getRotation());
				structureBlockBlockEntity.setMetadata(updateStructureBlockServerPacket.getMetadata());
				structureBlockBlockEntity.setIgnoreEntities(updateStructureBlockServerPacket.getIgnoreEntities());
				structureBlockBlockEntity.setShowAir(updateStructureBlockServerPacket.shouldShowAir());
				structureBlockBlockEntity.setShowBoundingBox(updateStructureBlockServerPacket.shouldShowBoundingBox());
				structureBlockBlockEntity.setIntegrity(updateStructureBlockServerPacket.getIntegrity());
				structureBlockBlockEntity.setSeed(updateStructureBlockServerPacket.getSeed());
				if (structureBlockBlockEntity.hasStructureName()) {
					String string = structureBlockBlockEntity.getStructureName();
					if (updateStructureBlockServerPacket.getAction() == StructureBlockBlockEntity.Action.field_12110) {
						if (structureBlockBlockEntity.saveStructure()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.save_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.save_failure", string), false);
						}
					} else if (updateStructureBlockServerPacket.getAction() == StructureBlockBlockEntity.Action.field_12109) {
						if (!structureBlockBlockEntity.isStructureAvailable()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_not_found", string), false);
						} else if (structureBlockBlockEntity.loadStructure()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.load_prepare", string), false);
						}
					} else if (updateStructureBlockServerPacket.getAction() == StructureBlockBlockEntity.Action.field_12106) {
						if (structureBlockBlockEntity.detectStructureSize()) {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.size_success", string), false);
						} else {
							this.player.addChatMessage(new TranslatableTextComponent("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player
						.addChatMessage(new TranslatableTextComponent("structure_block.invalid_structure_name", updateStructureBlockServerPacket.getStructureName()), false);
				}

				structureBlockBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onJigsawUpdate(UpdateJigsawServerPacket updateJigsawServerPacket) {
		NetworkThreadUtils.forceMainThread(updateJigsawServerPacket, this, this.player.getServerWorld());
		if (this.player.method_7338()) {
			BlockPos blockPos = updateJigsawServerPacket.method_16396();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof JigsawBlockEntity) {
				JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
				jigsawBlockEntity.setAttachmentType(updateJigsawServerPacket.method_16395());
				jigsawBlockEntity.setTargetPool(updateJigsawServerPacket.method_16394());
				jigsawBlockEntity.setFinalState(updateJigsawServerPacket.method_16393());
				jigsawBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void onVillagerTradeSelect(SelectVillagerTradeServerPacket selectVillagerTradeServerPacket) {
		NetworkThreadUtils.forceMainThread(selectVillagerTradeServerPacket, this, this.player.getServerWorld());
		int i = selectVillagerTradeServerPacket.method_12431();
		Container container = this.player.container;
		if (container instanceof MerchantContainer) {
			((MerchantContainer)container).setRecipeIndex(i);
		}
	}

	@Override
	public void onBookUpdate(BookUpdateServerPacket bookUpdateServerPacket) {
		ItemStack itemStack = bookUpdateServerPacket.stack();
		if (!itemStack.isEmpty()) {
			if (WritableBookItem.method_8047(itemStack.getTag())) {
				ItemStack itemStack2 = this.player.getStackInHand(bookUpdateServerPacket.hand());
				if (itemStack.getItem() == Items.field_8674 && itemStack2.getItem() == Items.field_8674) {
					if (bookUpdateServerPacket.method_12238()) {
						ItemStack itemStack3 = new ItemStack(Items.field_8360);
						CompoundTag compoundTag = itemStack2.getTag();
						if (compoundTag != null) {
							itemStack3.setTag(compoundTag.copy());
						}

						itemStack3.setChildTag("author", new StringTag(this.player.getName().getString()));
						itemStack3.setChildTag("title", new StringTag(itemStack.getTag().getString("title")));
						ListTag listTag = itemStack.getTag().getList("pages", 8);

						for (int i = 0; i < listTag.size(); i++) {
							String string = listTag.getString(i);
							TextComponent textComponent = new StringTextComponent(string);
							string = TextComponent.Serializer.toJsonString(textComponent);
							listTag.getOrDefault(i, new StringTag(string));
						}

						itemStack3.setChildTag("pages", listTag);
						this.player.setStackInHand(bookUpdateServerPacket.hand(), itemStack3);
					} else {
						itemStack2.setChildTag("pages", itemStack.getTag().getList("pages", 8));
					}
				}
			}
		}
	}

	@Override
	public void onQueryEntityNbt(QueryEntityNbtServerPacket queryEntityNbtServerPacket) {
		NetworkThreadUtils.forceMainThread(queryEntityNbtServerPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			Entity entity = this.player.getServerWorld().getEntityById(queryEntityNbtServerPacket.getEntityId());
			if (entity != null) {
				CompoundTag compoundTag = entity.toTag(new CompoundTag());
				this.player.networkHandler.sendPacket(new TagQueryResponseClientPacket(queryEntityNbtServerPacket.getTransactionId(), compoundTag));
			}
		}
	}

	@Override
	public void onQueryBlockNbt(QueryBlockNbtServerPacket queryBlockNbtServerPacket) {
		NetworkThreadUtils.forceMainThread(queryBlockNbtServerPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			BlockEntity blockEntity = this.player.getServerWorld().getBlockEntity(queryBlockNbtServerPacket.getPos());
			CompoundTag compoundTag = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
			this.player.networkHandler.sendPacket(new TagQueryResponseClientPacket(queryBlockNbtServerPacket.getTransactionId(), compoundTag));
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveServerMessage playerMoveServerMessage) {
		NetworkThreadUtils.forceMainThread(playerMoveServerMessage, this, this.player.getServerWorld());
		if (validatePlayerMove(playerMoveServerMessage)) {
			this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
			if (!this.player.notInAnyWorld) {
				if (this.ticks == 0) {
					this.syncWithPlayerPosition();
				}

				if (this.requestedTeleportPos != null) {
					if (this.ticks - this.teleportRequestTick > 20) {
						this.teleportRequestTick = this.ticks;
						this.teleportRequest(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
					}
				} else {
					this.teleportRequestTick = this.ticks;
					if (this.player.hasVehicle()) {
						this.player
							.setPositionAnglesAndUpdate(
								this.player.x, this.player.y, this.player.z, playerMoveServerMessage.getYaw(this.player.yaw), playerMoveServerMessage.getPitch(this.player.pitch)
							);
						this.server.getPlayerManager().method_14575(this.player);
					} else {
						double d = this.player.x;
						double e = this.player.y;
						double f = this.player.z;
						double g = this.player.y;
						double h = playerMoveServerMessage.getX(this.player.x);
						double i = playerMoveServerMessage.getY(this.player.y);
						double j = playerMoveServerMessage.getZ(this.player.z);
						float k = playerMoveServerMessage.getYaw(this.player.yaw);
						float l = playerMoveServerMessage.getPitch(this.player.pitch);
						double m = h - this.lastTickX;
						double n = i - this.lastTickY;
						double o = j - this.lastTickZ;
						double p = this.player.velocityX * this.player.velocityX + this.player.velocityY * this.player.velocityY + this.player.velocityZ * this.player.velocityZ;
						double q = m * m + n * n + o * o;
						if (this.player.isSleeping()) {
							if (q > 1.0) {
								this.teleportRequest(
									this.player.x, this.player.y, this.player.z, playerMoveServerMessage.getYaw(this.player.yaw), playerMoveServerMessage.getPitch(this.player.pitch)
								);
							}
						} else {
							this.movePacketsCount++;
							int r = this.movePacketsCount - this.lastTickMovePacketsCount;
							if (r > 5) {
								LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), r);
								r = 1;
							}

							if (!this.player.isInTeleportationState()
								&& (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.isFallFlying())) {
								float s = this.player.isFallFlying() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(this.player.getGameProfile().getName()))) {
									LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), m, n, o);
									this.teleportRequest(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
									return;
								}
							}

							boolean bl = serverWorld.method_8587(this.player, this.player.getBoundingBox().contract(0.0625));
							m = h - this.updatedX;
							n = i - this.updatedY;
							o = j - this.updatedZ;
							if (this.player.onGround && !playerMoveServerMessage.isOnGround() && n > 0.0) {
								this.player.method_6043();
							}

							this.player.move(MovementType.PLAYER, m, n, o);
							this.player.onGround = playerMoveServerMessage.isOnGround();
							m = h - this.player.x;
							n = i - this.player.y;
							if (n > -0.5 || n < 0.5) {
								n = 0.0;
							}

							o = j - this.player.z;
							q = m * m + n * n + o * o;
							boolean bl2 = false;
							if (!this.player.isInTeleportationState()
								&& q > 0.0625
								&& !this.player.isSleeping()
								&& !this.player.interactionManager.isCreative()
								&& this.player.interactionManager.getGameMode() != GameMode.field_9219) {
								bl2 = true;
								LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
							}

							this.player.setPositionAnglesAndUpdate(h, i, j, k, l);
							this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
							if (!this.player.noClip && !this.player.isSleeping()) {
								boolean bl3 = serverWorld.method_8587(this.player, this.player.getBoundingBox().contract(0.0625));
								if (bl && (bl2 || !bl3)) {
									this.teleportRequest(d, e, f, k, l);
									return;
								}
							}

							this.floating = n >= -0.03125
								&& this.player.interactionManager.getGameMode() != GameMode.field_9219
								&& !this.server.isFlightEnabled()
								&& !this.player.abilities.allowFlying
								&& !this.player.hasPotionEffect(StatusEffects.field_5902)
								&& !this.player.isFallFlying()
								&& !serverWorld.isAreaNotEmpty(this.player.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
							this.player.onGround = playerMoveServerMessage.isOnGround();
							this.server.getPlayerManager().method_14575(this.player);
							this.player.method_14207(this.player.y - g, playerMoveServerMessage.isOnGround());
							this.updatedX = this.player.x;
							this.updatedY = this.player.y;
							this.updatedZ = this.player.z;
						}
					}
				}
			}
		}
	}

	public void teleportRequest(double d, double e, double f, float g, float h) {
		this.teleportRequest(d, e, f, g, h, Collections.emptySet());
	}

	public void teleportRequest(double d, double e, double f, float g, float h, Set<PlayerPositionLookClientPacket.Flag> set) {
		double i = set.contains(PlayerPositionLookClientPacket.Flag.X) ? this.player.x : 0.0;
		double j = set.contains(PlayerPositionLookClientPacket.Flag.Y) ? this.player.y : 0.0;
		double k = set.contains(PlayerPositionLookClientPacket.Flag.Z) ? this.player.z : 0.0;
		float l = set.contains(PlayerPositionLookClientPacket.Flag.Y_ROT) ? this.player.yaw : 0.0F;
		float m = set.contains(PlayerPositionLookClientPacket.Flag.X_ROT) ? this.player.pitch : 0.0F;
		this.requestedTeleportPos = new Vec3d(d, e, f);
		if (++this.requestedTeleportId == Integer.MAX_VALUE) {
			this.requestedTeleportId = 0;
		}

		this.teleportRequestTick = this.ticks;
		this.player.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.player.networkHandler.sendPacket(new PlayerPositionLookClientPacket(d - i, e - j, f - k, g - l, h - m, set, this.requestedTeleportId));
	}

	@Override
	public void onPlayerAction(PlayerActionServerPacket playerActionServerPacket) {
		NetworkThreadUtils.forceMainThread(playerActionServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = playerActionServerPacket.getPos();
		this.player.updateLastActionTime();
		switch (playerActionServerPacket.getAction()) {
			case field_12969:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.getStackInHand(Hand.OFF);
					this.player.setStackInHand(Hand.OFF, this.player.getStackInHand(Hand.MAIN));
					this.player.setStackInHand(Hand.MAIN, itemStack);
				}

				return;
			case field_12975:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(false);
				}

				return;
			case field_12970:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(true);
				}

				return;
			case field_12974:
				this.player.method_6075();
				return;
			case field_12968:
			case field_12971:
			case field_12973:
				double d = this.player.x - ((double)blockPos.getX() + 0.5);
				double e = this.player.y - ((double)blockPos.getY() + 0.5) + 1.5;
				double f = this.player.z - ((double)blockPos.getZ() + 0.5);
				double g = d * d + e * e + f * f;
				if (g > 36.0) {
					return;
				} else if (blockPos.getY() >= this.server.getWorldHeight()) {
					return;
				} else {
					if (playerActionServerPacket.getAction() == PlayerActionServerPacket.Action.field_12968) {
						if (!this.server.isSpawnProtected(serverWorld, blockPos, this.player) && serverWorld.getWorldBorder().contains(blockPos)) {
							this.player.interactionManager.method_14263(blockPos, playerActionServerPacket.getDirection());
						} else {
							this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos));
						}
					} else {
						if (playerActionServerPacket.getAction() == PlayerActionServerPacket.Action.field_12973) {
							this.player.interactionManager.method_14258(blockPos);
						} else if (playerActionServerPacket.getAction() == PlayerActionServerPacket.Action.field_12971) {
							this.player.interactionManager.method_14269();
						}

						if (!serverWorld.getBlockState(blockPos).isAir()) {
							this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos));
						}
					}

					return;
				}
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockServerPacket playerInteractBlockServerPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractBlockServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = playerInteractBlockServerPacket.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		BlockHitResult blockHitResult = playerInteractBlockServerPacket.getHitY();
		BlockPos blockPos = blockHitResult.getBlockPos();
		Direction direction = blockHitResult.getSide();
		this.player.updateLastActionTime();
		if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.UP && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.requestedTeleportPos == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& !this.server.isSpawnProtected(serverWorld, blockPos, this.player)
				&& serverWorld.getWorldBorder().contains(blockPos)) {
				this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
			}
		} else {
			TextComponent textComponent = new TranslatableTextComponent("build.tooHigh", this.server.getWorldHeight()).applyFormat(TextFormat.RED);
			this.player.networkHandler.sendPacket(new ChatMessageClientPacket(textComponent, ChatMessageType.field_11733));
		}

		this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos));
		this.player.networkHandler.sendPacket(new BlockUpdateClientPacket(serverWorld, blockPos.offset(direction)));
	}

	@Override
	public void onPlayerInteractItem(PlayerInteractItemServerPacket playerInteractItemServerPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractItemServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = playerInteractItemServerPacket.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.updateLastActionTime();
		if (!itemStack.isEmpty()) {
			this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
		}
	}

	@Override
	public void onSpectatorTeleport(SpectatorTeleportServerPacket spectatorTeleportServerPacket) {
		NetworkThreadUtils.forceMainThread(spectatorTeleportServerPacket, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			Entity entity = null;

			for (ServerWorld serverWorld : this.server.getWorlds()) {
				entity = spectatorTeleportServerPacket.getTarget(serverWorld);
				if (entity != null) {
					break;
				}
			}

			if (entity != null) {
				this.player.method_14251((ServerWorld)entity.world, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
			}
		}
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusServerPacket resourcePackStatusServerPacket) {
	}

	@Override
	public void onBoatPaddleState(BoatPaddleStateServerPacket boatPaddleStateServerPacket) {
		NetworkThreadUtils.forceMainThread(boatPaddleStateServerPacket, this, this.player.getServerWorld());
		Entity entity = this.player.getRiddenEntity();
		if (entity instanceof BoatEntity) {
			((BoatEntity)entity).setPaddleState(boatPaddleStateServerPacket.isLeftPaddling(), boatPaddleStateServerPacket.isRightPaddling());
		}
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), textComponent.getString());
		this.server.method_3856();
		this.server
			.getPlayerManager()
			.sendToAll(new TranslatableTextComponent("multiplayer.player.left", this.player.getDisplayName()).applyFormat(TextFormat.YELLOW));
		this.player.method_14231();
		this.server.getPlayerManager().method_14611(this.player);
		if (this.server.isSinglePlayer() && this.player.getName().getString().equals(this.server.getUserName())) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stop(false);
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.sendPacket(packet, null);
	}

	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (packet instanceof ChatMessageClientPacket) {
			ChatMessageClientPacket chatMessageClientPacket = (ChatMessageClientPacket)packet;
			PlayerEntity.ChatVisibility chatVisibility = this.player.getClientChatVisibility();
			if (chatVisibility == PlayerEntity.ChatVisibility.HIDDEN && chatMessageClientPacket.getLocation() != ChatMessageType.field_11733) {
				return;
			}

			if (chatVisibility == PlayerEntity.ChatVisibility.COMMANDS && !chatMessageClientPacket.isNonChat()) {
				return;
			}
		}

		try {
			this.client.sendPacket(packet, genericFutureListener);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Sending packet");
			CrashReportSection crashReportSection = crashReport.addElement("Packet being sent");
			crashReportSection.add("Packet class", (ICrashCallable<String>)(() -> packet.getClass().getCanonicalName()));
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void onUpdateSelectedSlot(UpdateSelectedSlotServerPacket updateSelectedSlotServerPacket) {
		NetworkThreadUtils.forceMainThread(updateSelectedSlotServerPacket, this, this.player.getServerWorld());
		if (updateSelectedSlotServerPacket.getSelectedSlot() >= 0 && updateSelectedSlotServerPacket.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
			this.player.inventory.selectedSlot = updateSelectedSlotServerPacket.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
		}
	}

	@Override
	public void onChatMessage(ChatMessageServerPacket chatMessageServerPacket) {
		NetworkThreadUtils.forceMainThread(chatMessageServerPacket, this, this.player.getServerWorld());
		if (this.player.getClientChatVisibility() == PlayerEntity.ChatVisibility.HIDDEN) {
			this.sendPacket(new ChatMessageClientPacket(new TranslatableTextComponent("chat.cannotSend").applyFormat(TextFormat.RED)));
		} else {
			this.player.updateLastActionTime();
			String string = chatMessageServerPacket.getChatMessage();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!SharedConstants.isValidChar(string.charAt(i))) {
					this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.illegal_characters"));
					return;
				}
			}

			if (string.startsWith("/")) {
				this.executeCommand(string);
			} else {
				TextComponent textComponent = new TranslatableTextComponent("chat.type.text", this.player.getDisplayName(), string);
				this.server.getPlayerManager().broadcastChatMessage(textComponent, false);
			}

			this.messageCooldown += 20;
			if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
				this.disconnect(new TranslatableTextComponent("disconnect.spam"));
			}
		}
	}

	private void executeCommand(String string) {
		this.server.getCommandManager().execute(this.player.getCommandSource(), string);
	}

	@Override
	public void onHandSwing(HandSwingServerPacket handSwingServerPacket) {
		NetworkThreadUtils.forceMainThread(handSwingServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		this.player.swingHand(handSwingServerPacket.getHand());
	}

	@Override
	public void onClientCommand(ClientCommandServerPacket clientCommandServerPacket) {
		NetworkThreadUtils.forceMainThread(clientCommandServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		switch (clientCommandServerPacket.getMode()) {
			case field_12979:
				this.player.setSneaking(true);
				break;
			case field_12984:
				this.player.setSneaking(false);
				break;
			case field_12981:
				this.player.setSprinting(true);
				break;
			case field_12985:
				this.player.setSprinting(false);
				break;
			case field_12986:
				if (this.player.isSleeping()) {
					this.player.method_7358(false, true, true);
					this.requestedTeleportPos = new Vec3d(this.player.x, this.player.y, this.player.z);
				}
				break;
			case field_12987:
				if (this.player.getRiddenEntity() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getRiddenEntity();
					int i = clientCommandServerPacket.getMountJumpHeight();
					if (jumpingMount.canJump() && i > 0) {
						jumpingMount.startJumping(i);
					}
				}
				break;
			case field_12980:
				if (this.player.getRiddenEntity() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getRiddenEntity();
					jumpingMount.stopJumping();
				}
				break;
			case field_12988:
				if (this.player.getRiddenEntity() instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.getRiddenEntity()).method_6722(this.player);
				}
				break;
			case field_12982:
				if (!this.player.onGround && this.player.velocityY < 0.0 && !this.player.isFallFlying() && !this.player.isInsideWater()) {
					ItemStack itemStack = this.player.getEquippedStack(EquipmentSlot.CHEST);
					if (itemStack.getItem() == Items.field_8833 && ElytraItem.isUsable(itemStack)) {
						this.player.method_14243();
					}
				} else {
					this.player.method_14229();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityServerPacket playerInteractEntityServerPacket) {
		NetworkThreadUtils.forceMainThread(playerInteractEntityServerPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Entity entity = playerInteractEntityServerPacket.getEntity(serverWorld);
		this.player.updateLastActionTime();
		if (entity != null) {
			boolean bl = this.player.canSee(entity);
			double d = 36.0;
			if (!bl) {
				d = 9.0;
			}

			if (this.player.squaredDistanceTo(entity) < d) {
				if (playerInteractEntityServerPacket.getType() == PlayerInteractEntityServerPacket.InteractionType.field_12876) {
					Hand hand = playerInteractEntityServerPacket.getHand();
					this.player.interact(entity, hand);
				} else if (playerInteractEntityServerPacket.getType() == PlayerInteractEntityServerPacket.InteractionType.field_12873) {
					Hand hand = playerInteractEntityServerPacket.getHand();
					entity.interactAt(this.player, playerInteractEntityServerPacket.getHitPosition(), hand);
				} else if (playerInteractEntityServerPacket.getType() == PlayerInteractEntityServerPacket.InteractionType.field_12875) {
					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ProjectileEntity || entity == this.player) {
						this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_entity_attacked"));
						this.server.warn("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
						return;
					}

					this.player.attack(entity);
				}
			}
		}
	}

	@Override
	public void onClientStatus(ClientStatusServerPacket clientStatusServerPacket) {
		NetworkThreadUtils.forceMainThread(clientStatusServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ClientStatusServerPacket.Mode mode = clientStatusServerPacket.getMode();
		switch (mode) {
			case field_12774:
				if (this.player.notInAnyWorld) {
					this.player.notInAnyWorld = false;
					this.player = this.server.getPlayerManager().method_14556(this.player, DimensionType.field_13072, true);
					Criterions.CHANGED_DIMENSION.handle(this.player, DimensionType.field_13078, DimensionType.field_13072);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().method_14556(this.player, DimensionType.field_13072, false);
					if (this.server.isHardcore()) {
						this.player.setGameMode(GameMode.field_9219);
						this.player.getServerWorld().getGameRules().put("spectatorsGenerateChunks", "false", this.server);
					}
				}
				break;
			case field_12775:
				this.player.method_14248().method_14910(this.player);
		}
	}

	@Override
	public void onGuiClose(GuiCloseServerPacket guiCloseServerPacket) {
		NetworkThreadUtils.forceMainThread(guiCloseServerPacket, this, this.player.getServerWorld());
		this.player.method_14247();
	}

	@Override
	public void onClickWindow(ClickWindowServerPacket clickWindowServerPacket) {
		NetworkThreadUtils.forceMainThread(clickWindowServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.container.syncId == clickWindowServerPacket.getSyncId() && this.player.container.method_7622(this.player)) {
			if (this.player.isSpectator()) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.create();

				for (int i = 0; i < this.player.container.slotList.size(); i++) {
					defaultedList.add(((Slot)this.player.container.slotList.get(i)).getStack());
				}

				this.player.onContainerRegistered(this.player.container, defaultedList);
			} else {
				ItemStack itemStack = this.player
					.container
					.onSlotClick(clickWindowServerPacket.getSlot(), clickWindowServerPacket.getButton(), clickWindowServerPacket.getActionType(), this.player);
				if (ItemStack.areEqual(clickWindowServerPacket.getStack(), itemStack)) {
					this.player
						.networkHandler
						.sendPacket(new GuiActionConfirmClientPacket(clickWindowServerPacket.getSyncId(), clickWindowServerPacket.getTransactionId(), true));
					this.player.field_13991 = true;
					this.player.container.sendContentUpdates();
					this.player.method_14241();
					this.player.field_13991 = false;
				} else {
					this.transactions.put(this.player.container.syncId, clickWindowServerPacket.getTransactionId());
					this.player
						.networkHandler
						.sendPacket(new GuiActionConfirmClientPacket(clickWindowServerPacket.getSyncId(), clickWindowServerPacket.getTransactionId(), false));
					this.player.container.method_7590(this.player, false);
					DefaultedList<ItemStack> defaultedList2 = DefaultedList.create();

					for (int j = 0; j < this.player.container.slotList.size(); j++) {
						ItemStack itemStack2 = ((Slot)this.player.container.slotList.get(j)).getStack();
						defaultedList2.add(itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
					}

					this.player.onContainerRegistered(this.player.container, defaultedList2);
				}
			}
		}
	}

	@Override
	public void onCraftRequest(CraftRequestServerPacket craftRequestServerPacket) {
		NetworkThreadUtils.forceMainThread(craftRequestServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (!this.player.isSpectator()
			&& this.player.container.syncId == craftRequestServerPacket.getSyncId()
			&& this.player.container.method_7622(this.player)
			&& this.player.container instanceof CraftingContainer) {
			this.server
				.getRecipeManager()
				.get(craftRequestServerPacket.getRecipe())
				.ifPresent(recipe -> ((CraftingContainer)this.player.container).method_17697(craftRequestServerPacket.shouldCraftAll(), recipe, this.player));
		}
	}

	@Override
	public void onButtonClick(ButtonClickServerPacket buttonClickServerPacket) {
		NetworkThreadUtils.forceMainThread(buttonClickServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.container.syncId == buttonClickServerPacket.getSyncId() && this.player.container.method_7622(this.player) && !this.player.isSpectator()) {
			this.player.container.onButtonClick(this.player, buttonClickServerPacket.method_12186());
			this.player.container.sendContentUpdates();
		}
	}

	@Override
	public void onCreativeInventoryAction(CreativeInventoryActionServerPacket creativeInventoryActionServerPacket) {
		NetworkThreadUtils.forceMainThread(creativeInventoryActionServerPacket, this, this.player.getServerWorld());
		if (this.player.interactionManager.isCreative()) {
			boolean bl = creativeInventoryActionServerPacket.getSlot() < 0;
			ItemStack itemStack = creativeInventoryActionServerPacket.getItemStack();
			CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
			if (!itemStack.isEmpty() && compoundTag != null && compoundTag.containsKey("x") && compoundTag.containsKey("y") && compoundTag.containsKey("z")) {
				BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
				BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
					compoundTag2.remove("x");
					compoundTag2.remove("y");
					compoundTag2.remove("z");
					itemStack.setChildTag("BlockEntityTag", compoundTag2);
				}
			}

			boolean bl2 = creativeInventoryActionServerPacket.getSlot() >= 1 && creativeInventoryActionServerPacket.getSlot() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getAmount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				if (itemStack.isEmpty()) {
					this.player.containerPlayer.setStackInSlot(creativeInventoryActionServerPacket.getSlot(), ItemStack.EMPTY);
				} else {
					this.player.containerPlayer.setStackInSlot(creativeInventoryActionServerPacket.getSlot(), itemStack);
				}

				this.player.containerPlayer.method_7590(this.player, true);
			} else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
				this.creativeItemDropThreshold += 20;
				ItemEntity itemEntity = this.player.dropItem(itemStack, true);
				if (itemEntity != null) {
					itemEntity.method_6980();
				}
			}
		}
	}

	@Override
	public void onConfirmTransaction(GuiActionConfirmServerPacket guiActionConfirmServerPacket) {
		NetworkThreadUtils.forceMainThread(guiActionConfirmServerPacket, this, this.player.getServerWorld());
		Short short_ = this.transactions.get(this.player.container.syncId);
		if (short_ != null
			&& guiActionConfirmServerPacket.getSyncId() == short_
			&& this.player.container.syncId == guiActionConfirmServerPacket.getWindowId()
			&& !this.player.container.method_7622(this.player)
			&& !this.player.isSpectator()) {
			this.player.container.method_7590(this.player, true);
		}
	}

	@Override
	public void onSignUpdate(UpdateSignServerPacket updateSignServerPacket) {
		NetworkThreadUtils.forceMainThread(updateSignServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = updateSignServerPacket.getPos();
		if (serverWorld.isBlockLoaded(blockPos)) {
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

			String[] strings = updateSignServerPacket.getText();

			for (int i = 0; i < strings.length; i++) {
				signBlockEntity.setTextOnRow(i, new StringTextComponent(TextFormat.stripFormatting(strings[i])));
			}

			signBlockEntity.markDirty();
			serverWorld.updateListeners(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void onKeepAlive(KeepAliveServerPacket keepAliveServerPacket) {
		if (this.waitingForKeepAlive && keepAliveServerPacket.getId() == this.keepAliveId) {
			int i = (int)(SystemUtil.getMeasuringTimeMs() - this.lastKeepAliveTime);
			this.player.field_13967 = (this.player.field_13967 * 3 + i) / 4;
			this.waitingForKeepAlive = false;
		} else if (!this.player.getName().getString().equals(this.server.getUserName())) {
			this.disconnect(new TranslatableTextComponent("disconnect.timeout"));
		}
	}

	@Override
	public void onPlayerAbilities(UpdatePlayerAbilitiesServerPacket updatePlayerAbilitiesServerPacket) {
		NetworkThreadUtils.forceMainThread(updatePlayerAbilitiesServerPacket, this, this.player.getServerWorld());
		this.player.abilities.flying = updatePlayerAbilitiesServerPacket.isFlying() && this.player.abilities.allowFlying;
	}

	@Override
	public void onClientSettings(ClientSettingsServerPacket clientSettingsServerPacket) {
		NetworkThreadUtils.forceMainThread(clientSettingsServerPacket, this, this.player.getServerWorld());
		this.player.setClientSettings(clientSettingsServerPacket);
	}

	@Override
	public void onCustomPayload(CustomPayloadServerPacket customPayloadServerPacket) {
	}
}
