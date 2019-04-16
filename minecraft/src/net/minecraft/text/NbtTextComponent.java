package net.minecraft.text;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NbtTextComponent extends AbstractTextComponent implements TextComponentWithSelectors {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final boolean componentJson;
	protected final String path;
	@Nullable
	protected final NbtPathArgumentType.NbtPath parsedPath;

	@Nullable
	private static NbtPathArgumentType.NbtPath parsePath(String string) {
		try {
			return new NbtPathArgumentType().method_9362(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public NbtTextComponent(String string, boolean bl) {
		this(string, parsePath(string), bl);
	}

	protected NbtTextComponent(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl) {
		this.path = string;
		this.parsedPath = nbtPath;
		this.componentJson = bl;
	}

	protected abstract Stream<CompoundTag> resolve(ServerCommandSource serverCommandSource) throws CommandSyntaxException;

	@Override
	public String getText() {
		return "";
	}

	public String getPath() {
		return this.path;
	}

	public boolean isComponentJson() {
		return this.componentJson;
	}

	@Override
	public TextComponent resolve(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
		if (serverCommandSource != null && this.parsedPath != null) {
			Stream<String> stream = this.resolve(serverCommandSource).flatMap(compoundTag -> {
				try {
					return this.parsedPath.get(compoundTag).stream();
				} catch (CommandSyntaxException var3x) {
					return Stream.empty();
				}
			}).map(Tag::asString);
			return (TextComponent)(this.componentJson
				? (TextComponent)stream.flatMap(string -> {
					try {
						TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
						return Stream.of(TextFormatter.resolveAndStyle(serverCommandSource, textComponent, entity));
					} catch (Exception var4) {
						LOGGER.warn("Failed to parse component: " + string, (Throwable)var4);
						return Stream.of();
					}
				}).reduce((textComponent, textComponent2) -> textComponent.append(", ").append(textComponent2)).orElse(new StringTextComponent(""))
				: new StringTextComponent(Joiner.on(", ").join(stream.iterator())));
		} else {
			return new StringTextComponent("");
		}
	}

	public static class BlockPosArgument extends NbtTextComponent {
		private final String pos;
		@Nullable
		private final PosArgument parsedPos;

		public BlockPosArgument(String string, boolean bl, String string2) {
			super(string, bl);
			this.pos = string2;
			this.parsedPos = this.parsePos(this.pos);
		}

		@Nullable
		private PosArgument parsePos(String string) {
			try {
				return BlockPosArgumentType.create().method_9699(new StringReader(string));
			} catch (CommandSyntaxException var3) {
				return null;
			}
		}

		private BlockPosArgument(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable PosArgument posArgument) {
			super(string, nbtPath, bl);
			this.pos = string2;
			this.parsedPos = posArgument;
		}

		@Nullable
		public String getPos() {
			return this.pos;
		}

		@Override
		public TextComponent copyShallow() {
			return new NbtTextComponent.BlockPosArgument(this.path, this.parsedPath, this.componentJson, this.pos, this.parsedPos);
		}

		@Override
		protected Stream<CompoundTag> resolve(ServerCommandSource serverCommandSource) {
			if (this.parsedPos != null) {
				ServerWorld serverWorld = serverCommandSource.getWorld();
				BlockPos blockPos = this.parsedPos.toAbsoluteBlockPos(serverCommandSource);
				if (serverWorld.isHeightValidAndBlockLoaded(blockPos)) {
					BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
					if (blockEntity != null) {
						return Stream.of(blockEntity.toTag(new CompoundTag()));
					}
				}
			}

			return Stream.empty();
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof NbtTextComponent.BlockPosArgument)) {
				return false;
			} else {
				NbtTextComponent.BlockPosArgument blockPosArgument = (NbtTextComponent.BlockPosArgument)object;
				return Objects.equals(this.pos, blockPosArgument.pos) && Objects.equals(this.path, blockPosArgument.path) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "BlockPosArgument{pos='" + this.pos + '\'' + "path='" + this.path + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
		}
	}

	public static class EntityNbtTextComponent extends NbtTextComponent {
		private final String selector;
		@Nullable
		private final EntitySelector parsedSelector;

		public EntityNbtTextComponent(String string, boolean bl, String string2) {
			super(string, bl);
			this.selector = string2;
			this.parsedSelector = parseSelector(string2);
		}

		@Nullable
		private static EntitySelector parseSelector(String string) {
			try {
				EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
				return entitySelectorReader.read();
			} catch (CommandSyntaxException var2) {
				return null;
			}
		}

		private EntityNbtTextComponent(
			String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable EntitySelector entitySelector
		) {
			super(string, nbtPath, bl);
			this.selector = string2;
			this.parsedSelector = entitySelector;
		}

		public String getSelector() {
			return this.selector;
		}

		@Override
		public TextComponent copyShallow() {
			return new NbtTextComponent.EntityNbtTextComponent(this.path, this.parsedPath, this.componentJson, this.selector, this.parsedSelector);
		}

		@Override
		protected Stream<CompoundTag> resolve(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
			if (this.parsedSelector != null) {
				List<? extends Entity> list = this.parsedSelector.getEntities(serverCommandSource);
				return list.stream().map(NbtPredicate::entityToTag);
			} else {
				return Stream.empty();
			}
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof NbtTextComponent.EntityNbtTextComponent)) {
				return false;
			} else {
				NbtTextComponent.EntityNbtTextComponent entityNbtTextComponent = (NbtTextComponent.EntityNbtTextComponent)object;
				return Objects.equals(this.selector, entityNbtTextComponent.selector) && Objects.equals(this.path, entityNbtTextComponent.path) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "EntityNbtComponent{selector='"
				+ this.selector
				+ '\''
				+ "path='"
				+ this.path
				+ '\''
				+ ", siblings="
				+ this.siblings
				+ ", style="
				+ this.getStyle()
				+ '}';
		}
	}
}
