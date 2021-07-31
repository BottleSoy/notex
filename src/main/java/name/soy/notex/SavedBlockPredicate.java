package name.soy.notex;

import com.google.gson.Gson;
import name.soy.notex.mixin.MaterialAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SavedBlockPredicate implements Predicate<CachedBlockPosition> {
	public SavedBlockPredicate(List<BasicCondition> conds) {
		this.conds = conds;
	}

	public interface BasicCondition {
		boolean test(CachedBlockPosition pos);

		NbtCompound save(NbtCompound nbt);
	}

	public static class OfCondition implements BasicCondition {
		public Block block;

		public OfCondition(Block block) {
			this.block = block;
		}

		@Override
		public boolean test(CachedBlockPosition pos) {
			return pos.getBlockState().isOf(block);
		}

		@Override
		public NbtCompound save(NbtCompound nbt) {
			nbt.putString("type", "of");
			nbt.putString("data", Registry.BLOCK.getId(block).toString());
			return nbt;
		}
	}

	public static class InCondition implements BasicCondition {
		public Tag.Identified<Block> tag;

		public InCondition(Tag.Identified<Block> tag) {
			this.tag = tag;
		}

		@Override
		public boolean test(CachedBlockPosition pos) {
			return pos.getBlockState().isIn(tag);
		}

		@Override
		public NbtCompound save(NbtCompound nbt) {
			nbt.putString("type", "in");
			nbt.putString("data", tag.getId().toString());
			return nbt;
		}
	}

	public static class MaterialCondition implements BasicCondition {
		public Block blockmaterial;

		public MaterialCondition(Block blockmaterial) {
			this.blockmaterial = blockmaterial;
		}

		@Override
		public boolean test(CachedBlockPosition pos) {
			return pos.getBlockState().getMaterial() == ((MaterialAccessor) blockmaterial).getMaterial();
		}

		@Override
		public NbtCompound save(NbtCompound nbt) {
			nbt.putString("type", "material");
			nbt.putString("data", Registry.BLOCK.getId(blockmaterial).toString());
			return nbt;
		}
	}

	List<BasicCondition> conds;

	public NbtList save() {
		NbtList list = new NbtList();
		int i = 0;
		for (BasicCondition basicCondition : conds) {
			list.add(i, basicCondition.save(new NbtCompound()));
			++i;
		}
		return list;
	}

	public static SavedBlockPredicate read(NbtList list) {
		Gson gson = new Gson();
		List<BasicCondition> conds = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			NbtCompound nbt = list.getCompound(i);
			String data = nbt.getString("data");
			switch (nbt.getString("type")) {
				case "of":
					conds.add(new OfCondition(Registry.BLOCK.get(new Identifier(data))));
					break;
				case "in":
					Identifier id = new Identifier(data);
					BlockTags.getRequiredTags().stream().
							filter(tag -> tag.getId().equals(id)).findFirst()
							.ifPresent(tag -> conds.add(new InCondition(tag)));
					break;
				case "material":
					new MaterialCondition(Registry.BLOCK.get(new Identifier(data)));
					break;
			}
		}
		return new SavedBlockPredicate(conds);
	}

	public static class Builder {
		List<BasicCondition> conds = new ArrayList<>();

		public Builder of(Block block) {
			conds.add(new OfCondition(block));
			return this;
		}

		public Builder in(Tag.Identified<Block> tag) {
			conds.add(new InCondition(tag));
			return this;
		}

		public Builder material(Block blockmaterial) {
			conds.add(new MaterialCondition(blockmaterial));
			return this;
		}

		public SavedBlockPredicate build() {
			return new SavedBlockPredicate(conds);
		}
	}

	@Override
	public boolean test(CachedBlockPosition pos) {
		return conds.stream().anyMatch(cond -> cond.test(pos));
	}
}
