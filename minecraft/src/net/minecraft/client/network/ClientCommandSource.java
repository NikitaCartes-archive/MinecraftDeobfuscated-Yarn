package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.packet.RequestCommandCompletionsServerPacket;
import net.minecraft.util.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ClientCommandSource implements CommandSource {
	private final ClientPlayNetworkHandler networkHandler;
	private final MinecraftClient client;
	private int completionId = -1;
	private CompletableFuture<Suggestions> pendingCompletion;

	public ClientCommandSource(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
		this.networkHandler = clientPlayNetworkHandler;
		this.client = minecraftClient;
	}

	@Override
	public Collection<String> getPlayerNames() {
		List<String> list = Lists.<String>newArrayList();

		for (ScoreboardEntry scoreboardEntry : this.networkHandler.method_2880()) {
			list.add(scoreboardEntry.getProfile().getName());
		}

		return list;
	}

	@Override
	public Collection<String> method_9269() {
		return (Collection<String>)(this.client.hitResult != null && this.client.hitResult.type == HitResult.Type.ENTITY
			? Collections.singleton(this.client.hitResult.entity.getUuidAsString())
			: Collections.emptyList());
	}

	@Override
	public Collection<String> getTeamNames() {
		return this.networkHandler.getWorld().getScoreboard().getTeamNames();
	}

	@Override
	public Collection<Identifier> getSoundIds() {
		return this.client.getSoundLoader().getKeys();
	}

	@Override
	public Collection<Identifier> getRecipeIds() {
		return this.networkHandler.getRecipeManager().keys();
	}

	@Override
	public boolean hasPermissionLevel(int i) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		return clientPlayerEntity != null ? clientPlayerEntity.allowsPermissionLevel(i) : i == 0;
	}

	@Override
	public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (this.pendingCompletion != null) {
			this.pendingCompletion.cancel(false);
		}

		this.pendingCompletion = new CompletableFuture();
		int i = ++this.completionId;
		this.networkHandler.sendPacket(new RequestCommandCompletionsServerPacket(i, commandContext.getInput()));
		return this.pendingCompletion;
	}

	private static String formatDouble(double d) {
		return String.format(Locale.ROOT, "%.2f", d);
	}

	private static String formatInt(int i) {
		return Integer.toString(i);
	}

	@Override
	public Collection<CommandSource.RelativePosition> method_9274(boolean bl) {
		if (this.client.hitResult == null || this.client.hitResult.type != HitResult.Type.BLOCK) {
			return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
		} else if (bl) {
			Vec3d vec3d = this.client.hitResult.pos;
			return Collections.singleton(new CommandSource.RelativePosition(formatDouble(vec3d.x), formatDouble(vec3d.y), formatDouble(vec3d.z)));
		} else {
			BlockPos blockPos = this.client.hitResult.getBlockPos();
			return Collections.singleton(new CommandSource.RelativePosition(formatInt(blockPos.getX()), formatInt(blockPos.getY()), formatInt(blockPos.getZ())));
		}
	}

	public void method_2931(int i, Suggestions suggestions) {
		if (i == this.completionId) {
			this.pendingCompletion.complete(suggestions);
			this.pendingCompletion = null;
			this.completionId = -1;
		}
	}
}
