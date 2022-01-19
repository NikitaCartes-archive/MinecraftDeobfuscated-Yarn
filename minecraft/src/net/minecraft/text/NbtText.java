package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public abstract class NbtText extends BaseText implements ParsableText {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final boolean interpret;
	protected final Optional<Text> separator;
	protected final String rawPath;
	@Nullable
	protected final NbtPathArgumentType.NbtPath path;

	@Nullable
	private static NbtPathArgumentType.NbtPath parsePath(String rawPath) {
		try {
			return new NbtPathArgumentType().parse(new StringReader(rawPath));
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public NbtText(String rawPath, boolean interpret, Optional<Text> separator) {
		this(rawPath, parsePath(rawPath), interpret, separator);
	}

	protected NbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, Optional<Text> separator) {
		this.rawPath = rawPath;
		this.path = path;
		this.interpret = interpret;
		this.separator = separator;
	}

	protected abstract Stream<NbtCompound> toNbt(ServerCommandSource source) throws CommandSyntaxException;

	public String getPath() {
		return this.rawPath;
	}

	public boolean shouldInterpret() {
		return this.interpret;
	}

	@Override
	public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (source != null && this.path != null) {
			Stream<String> stream = this.toNbt(source).flatMap(nbt -> {
				try {
					return this.path.get(nbt).stream();
				} catch (CommandSyntaxException var3) {
					return Stream.empty();
				}
			}).map(NbtElement::asString);
			if (this.interpret) {
				Text text = DataFixUtils.orElse(Texts.parse(source, this.separator, sender, depth), Texts.DEFAULT_SEPARATOR_TEXT);
				return (MutableText)stream.flatMap(textx -> {
					try {
						MutableText mutableText = Text.Serializer.fromJson(textx);
						return Stream.of(Texts.parse(source, mutableText, sender, depth));
					} catch (Exception var5x) {
						LOGGER.warn("Failed to parse component: {}", textx, var5x);
						return Stream.of();
					}
				}).reduce((accumulator, current) -> accumulator.append(text).append(current)).orElseGet(() -> new LiteralText(""));
			} else {
				return (MutableText)Texts.parse(source, this.separator, sender, depth)
					.map(
						textx -> (MutableText)stream.map(string -> new LiteralText(string))
								.reduce((accumulator, current) -> accumulator.append(textx).append(current))
								.orElseGet(() -> new LiteralText(""))
					)
					.orElseGet(() -> new LiteralText((String)stream.collect(Collectors.joining(", "))));
			}
		} else {
			return new LiteralText("");
		}
	}

	public static class BlockNbtText extends NbtText {
		private final String rawPos;
		@Nullable
		private final PosArgument pos;

		public BlockNbtText(String rawPath, boolean rawJson, String rawPos, Optional<Text> separator) {
			super(rawPath, rawJson, separator);
			this.rawPos = rawPos;
			this.pos = this.parsePos(this.rawPos);
		}

		@Nullable
		private PosArgument parsePos(String rawPos) {
			try {
				return BlockPosArgumentType.blockPos().parse(new StringReader(rawPos));
			} catch (CommandSyntaxException var3) {
				return null;
			}
		}

		private BlockNbtText(
			String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, String rawPos, @Nullable PosArgument pos, Optional<Text> separator
		) {
			super(rawPath, path, interpret, separator);
			this.rawPos = rawPos;
			this.pos = pos;
		}

		@Nullable
		public String getPos() {
			return this.rawPos;
		}

		public NbtText.BlockNbtText copy() {
			return new NbtText.BlockNbtText(this.rawPath, this.path, this.interpret, this.rawPos, this.pos, this.separator);
		}

		@Override
		protected Stream<NbtCompound> toNbt(ServerCommandSource source) {
			if (this.pos != null) {
				ServerWorld serverWorld = source.getWorld();
				BlockPos blockPos = this.pos.toAbsoluteBlockPos(source);
				if (serverWorld.canSetBlock(blockPos)) {
					BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
					if (blockEntity != null) {
						return Stream.of(blockEntity.createNbtWithIdentifyingData());
					}
				}
			}

			return Stream.empty();
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else {
				return !(object instanceof NbtText.BlockNbtText blockNbtText)
					? false
					: Objects.equals(this.rawPos, blockNbtText.rawPos) && Objects.equals(this.rawPath, blockNbtText.rawPath) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "BlockPosArgument{pos='" + this.rawPos + "'path='" + this.rawPath + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
		}
	}

	public static class EntityNbtText extends NbtText {
		private final String rawSelector;
		@Nullable
		private final EntitySelector selector;

		public EntityNbtText(String rawPath, boolean interpret, String rawSelector, Optional<Text> separator) {
			super(rawPath, interpret, separator);
			this.rawSelector = rawSelector;
			this.selector = parseSelector(rawSelector);
		}

		@Nullable
		private static EntitySelector parseSelector(String rawSelector) {
			try {
				EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector));
				return entitySelectorReader.read();
			} catch (CommandSyntaxException var2) {
				return null;
			}
		}

		private EntityNbtText(
			String rawPath,
			@Nullable NbtPathArgumentType.NbtPath path,
			boolean interpret,
			String rawSelector,
			@Nullable EntitySelector selector,
			Optional<Text> separator
		) {
			super(rawPath, path, interpret, separator);
			this.rawSelector = rawSelector;
			this.selector = selector;
		}

		public String getSelector() {
			return this.rawSelector;
		}

		public NbtText.EntityNbtText copy() {
			return new NbtText.EntityNbtText(this.rawPath, this.path, this.interpret, this.rawSelector, this.selector, this.separator);
		}

		@Override
		protected Stream<NbtCompound> toNbt(ServerCommandSource source) throws CommandSyntaxException {
			if (this.selector != null) {
				List<? extends Entity> list = this.selector.getEntities(source);
				return list.stream().map(NbtPredicate::entityToNbt);
			} else {
				return Stream.empty();
			}
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else {
				return !(object instanceof NbtText.EntityNbtText entityNbtText)
					? false
					: Objects.equals(this.rawSelector, entityNbtText.rawSelector) && Objects.equals(this.rawPath, entityNbtText.rawPath) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "EntityNbtComponent{selector='" + this.rawSelector + "'path='" + this.rawPath + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
		}
	}

	public static class StorageNbtText extends NbtText {
		private final Identifier id;

		public StorageNbtText(String rawPath, boolean interpret, Identifier id, Optional<Text> separator) {
			super(rawPath, interpret, separator);
			this.id = id;
		}

		public StorageNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, Identifier id, Optional<Text> separator) {
			super(rawPath, path, interpret, separator);
			this.id = id;
		}

		public Identifier getId() {
			return this.id;
		}

		public NbtText.StorageNbtText copy() {
			return new NbtText.StorageNbtText(this.rawPath, this.path, this.interpret, this.id, this.separator);
		}

		@Override
		protected Stream<NbtCompound> toNbt(ServerCommandSource source) {
			NbtCompound nbtCompound = source.getServer().getDataCommandStorage().get(this.id);
			return Stream.of(nbtCompound);
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else {
				return !(object instanceof NbtText.StorageNbtText storageNbtText)
					? false
					: Objects.equals(this.id, storageNbtText.id) && Objects.equals(this.rawPath, storageNbtText.rawPath) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "StorageNbtComponent{id='" + this.id + "'path='" + this.rawPath + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
		}
	}
}
