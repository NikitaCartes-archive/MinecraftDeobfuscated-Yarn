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

public abstract class NbtText extends BaseText implements ParsableText {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final boolean interpret;
	protected final String rawPath;
	@Nullable
	protected final NbtPathArgumentType.NbtPath path;

	@Nullable
	private static NbtPathArgumentType.NbtPath parsePath(String string) {
		try {
			return new NbtPathArgumentType().method_9362(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public NbtText(String string, boolean bl) {
		this(string, parsePath(string), bl);
	}

	protected NbtText(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl) {
		this.rawPath = string;
		this.path = nbtPath;
		this.interpret = bl;
	}

	protected abstract Stream<CompoundTag> toNbt(ServerCommandSource serverCommandSource) throws CommandSyntaxException;

	@Override
	public String asString() {
		return "";
	}

	public String getPath() {
		return this.rawPath;
	}

	public boolean shouldInterpret() {
		return this.interpret;
	}

	@Override
	public Text parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
		if (serverCommandSource != null && this.path != null) {
			Stream<String> stream = this.toNbt(serverCommandSource).flatMap(compoundTag -> {
				try {
					return this.path.get(compoundTag).stream();
				} catch (CommandSyntaxException var3) {
					return Stream.empty();
				}
			}).map(Tag::asString);
			return (Text)(this.interpret ? (Text)stream.flatMap(string -> {
				try {
					Text text = Text.Serializer.fromJson(string);
					return Stream.of(Texts.parse(serverCommandSource, text, entity, i));
				} catch (Exception var5) {
					LOGGER.warn("Failed to parse component: " + string, (Throwable)var5);
					return Stream.of();
				}
			}).reduce((text, text2) -> text.append(", ").append(text2)).orElse(new LiteralText("")) : new LiteralText(Joiner.on(", ").join(stream.iterator())));
		} else {
			return new LiteralText("");
		}
	}

	public static class BlockNbtText extends NbtText {
		private final String rawPos;
		@Nullable
		private final PosArgument pos;

		public BlockNbtText(String string, boolean bl, String string2) {
			super(string, bl);
			this.rawPos = string2;
			this.pos = this.parsePos(this.rawPos);
		}

		@Nullable
		private PosArgument parsePos(String string) {
			try {
				return BlockPosArgumentType.blockPos().method_9699(new StringReader(string));
			} catch (CommandSyntaxException var3) {
				return null;
			}
		}

		private BlockNbtText(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable PosArgument posArgument) {
			super(string, nbtPath, bl);
			this.rawPos = string2;
			this.pos = posArgument;
		}

		@Nullable
		public String getPos() {
			return this.rawPos;
		}

		@Override
		public Text copy() {
			return new NbtText.BlockNbtText(this.rawPath, this.path, this.interpret, this.rawPos, this.pos);
		}

		@Override
		protected Stream<CompoundTag> toNbt(ServerCommandSource serverCommandSource) {
			if (this.pos != null) {
				ServerWorld serverWorld = serverCommandSource.getWorld();
				BlockPos blockPos = this.pos.toAbsoluteBlockPos(serverCommandSource);
				if (serverWorld.canSetBlock(blockPos)) {
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
			} else if (!(object instanceof NbtText.BlockNbtText)) {
				return false;
			} else {
				NbtText.BlockNbtText blockNbtText = (NbtText.BlockNbtText)object;
				return Objects.equals(this.rawPos, blockNbtText.rawPos) && Objects.equals(this.rawPath, blockNbtText.rawPath) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "BlockPosArgument{pos='" + this.rawPos + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
		}
	}

	public static class EntityNbtText extends NbtText {
		private final String rawSelector;
		@Nullable
		private final EntitySelector selector;

		public EntityNbtText(String string, boolean bl, String string2) {
			super(string, bl);
			this.rawSelector = string2;
			this.selector = parseSelector(string2);
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

		private EntityNbtText(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable EntitySelector entitySelector) {
			super(string, nbtPath, bl);
			this.rawSelector = string2;
			this.selector = entitySelector;
		}

		public String getSelector() {
			return this.rawSelector;
		}

		@Override
		public Text copy() {
			return new NbtText.EntityNbtText(this.rawPath, this.path, this.interpret, this.rawSelector, this.selector);
		}

		@Override
		protected Stream<CompoundTag> toNbt(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
			if (this.selector != null) {
				List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
				return list.stream().map(NbtPredicate::entityToTag);
			} else {
				return Stream.empty();
			}
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof NbtText.EntityNbtText)) {
				return false;
			} else {
				NbtText.EntityNbtText entityNbtText = (NbtText.EntityNbtText)object;
				return Objects.equals(this.rawSelector, entityNbtText.rawSelector) && Objects.equals(this.rawPath, entityNbtText.rawPath) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "EntityNbtComponent{selector='"
				+ this.rawSelector
				+ '\''
				+ "path='"
				+ this.rawPath
				+ '\''
				+ ", siblings="
				+ this.siblings
				+ ", style="
				+ this.getStyle()
				+ '}';
		}
	}
}
