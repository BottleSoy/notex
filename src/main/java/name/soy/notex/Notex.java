package name.soy.notex;

import com.mojang.datafixers.types.Type;
import name.soy.notex.mixin.LevelSessionAccessor;
import name.soy.notex.mixin.ServerAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Notex implements ModInitializer {
	public static GameRules.Key<GameRules.BooleanRule> noteIgnoreAirsRule;
	public static GameRules.Key<DoubleRule> customNoteVol;
	public static BlockEntityType<NoteBlockEntity> NOTE_BLOCK_ENTITY;
	public static LinkedHashMap<String, CustomIns> insNames = new LinkedHashMap<>();
	public static final LinkedHashMap<String, SavedBlockPredicate> namePre = new LinkedHashMap<>();

	public static final Set<UUID> playersee = new HashSet<>();

	public static class ReversedHashMap extends LinkedHashMap<SavedBlockPredicate, CustomIns> {
		public final LinkedList<SavedBlockPredicate> insKeySort = new LinkedList<>();

		/**
		 * 这里就是注册代码了
		 *
		 * @param key   方块条件
		 * @param value 自定义乐器
		 * @return 自定义乐器
		 */
		@Override
		public CustomIns put(SavedBlockPredicate key, CustomIns value) {
			insKeySort.addFirst(key);
			namePre.put(value.name, key);
			insNames.put(value.name, value);
			return super.put(key, value);
		}

		@Override
		public void clear() {
			insNames.clear();
			namePre.clear();
			insKeySort.clear();
			super.clear();
		}

		@Override
		public CustomIns remove(Object key) {
			insKeySort.remove(key);
			CustomIns ins = super.remove(key);
			insNames.remove(ins.name);
			namePre.remove(ins.name);
			return ins;
		}

		@Override
		public void forEach(BiConsumer<? super SavedBlockPredicate, ? super CustomIns> action) {
			for (SavedBlockPredicate predicate : insKeySort) {
				action.accept(predicate, get(predicate));
			}
		}

		public CustomIns getIf(CachedBlockPosition action) {
			for (Predicate<CachedBlockPosition> predicate : insKeySort) {
				if (predicate.test(action)) return get(predicate);
			}
			return CustomIns.HARP;
		}
	}

	public static ReversedHashMap insMap = new ReversedHashMap();

	/**
	 * 销毁的地方
	 */
	public static boolean removeIns(String ins) {
		if (insNames.remove(ins) != null) {
			insMap.remove(namePre.remove(ins));
			return true;
		}
		return false;
	}

	public static void loadDeaultIns() {
		insMap.clear();
		insMap.put(new SavedBlockPredicate(new ArrayList<>()), CustomIns.HARP);
		insMap.put(new SavedBlockPredicate.Builder()
						.material(Blocks.OAK_WOOD).material(Blocks.WARPED_STEM).build()
				, CustomIns.BASS);
		insMap.put(new SavedBlockPredicate.Builder()
				.material(Blocks.GLASS).build(), CustomIns.HAT);
		insMap.put(new SavedBlockPredicate.Builder()
				.material(Blocks.SAND).build(), CustomIns.SNARE);
		insMap.put(new SavedBlockPredicate.Builder()
				.material(Blocks.STONE).build(), CustomIns.BASEDRUM);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.GLOWSTONE).build(), CustomIns.PLING);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.HAY_BLOCK).build(), CustomIns.BANJO);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.EMERALD_BLOCK).build(), CustomIns.BIT);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.PUMPKIN).build(), CustomIns.DIDGERIDOO);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.SOUL_SAND).build(), CustomIns.COW_BELL);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.IRON_BLOCK).build(), CustomIns.IRON_XYLOPHONE);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.BONE_BLOCK).build(), CustomIns.XYLOPHONE);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.PACKED_ICE).build(), CustomIns.CHIME);
		insMap.put(new SavedBlockPredicate.Builder()
				.in(BlockTags.WOOL).build(), CustomIns.GUITAR);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.GOLD_BLOCK).build(), CustomIns.BELL);
		insMap.put(new SavedBlockPredicate.Builder()
				.of(Blocks.CLAY).build(), CustomIns.FLUTE);
	}

	@Override
	public void onInitialize() {
		noteIgnoreAirsRule = GameRuleRegistry.register("noteIgnoreAirs", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
		customNoteVol = GameRuleRegistry.register("customNoteVolume", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(3));
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			File file = ((LevelSessionAccessor) ((ServerAccessor) server).getSession()).getDirectory().toFile();
			File ins = new File(file, "data/notex.dat");
			if (ins.exists()) {
				try {
					NbtCompound read = NbtIo.readCompressed(ins);
					System.out.println(read);
					NbtList list = read.getList("ins", 10);
					insMap.clear();
					for (int i = 0; i < list.size(); i++) {
						NbtCompound compound = list.getCompound(i);
						CustomIns Cins = CustomIns.read(compound);
						SavedBlockPredicate predicate = SavedBlockPredicate.read(compound.getList("predicate", 10));
						insMap.put(predicate, Cins);
					}
				} catch (IOException e) {
					e.printStackTrace();
					loadDeaultIns();
				}
			} else {
				loadDeaultIns();
			}
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(Notex::save);
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			NotexCommand.register(dispatcher);
		});

		Type<?> note_btype = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, "note_block");
		NOTE_BLOCK_ENTITY = BlockEntityType.Builder.create(NoteBlockEntity::new, Blocks.NOTE_BLOCK).build(note_btype);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "note_block", NOTE_BLOCK_ENTITY);
	}

	public static void save(MinecraftServer server) {
		File file = ((LevelSessionAccessor) ((ServerAccessor) server).getSession()).getDirectory().toFile();
		File ins = new File(file, "data/notex.dat");
		if (!ins.exists()) {
			try {
				ins.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		NbtCompound nbt = new NbtCompound();
		NbtList insListNbt = new NbtList();
		for (Map.Entry<SavedBlockPredicate, CustomIns> entry : insMap.entrySet()) {
			SavedBlockPredicate pre = entry.getKey();
			CustomIns customIns = entry.getValue();
			NbtCompound insNbt = customIns.write();
			insNbt.put("predicate", pre.save());
			insListNbt.add(insNbt);
		}
		nbt.put("ins", insListNbt);
		try {
			NbtIo.writeCompressed(nbt, ins);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
