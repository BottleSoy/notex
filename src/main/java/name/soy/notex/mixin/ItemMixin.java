package name.soy.notex.mixin;

import name.soy.notex.NoteBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemMixin extends Entity {
	public ItemMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract ItemStack getStack();

	@Shadow
	public abstract void setPickupDelay(int pickupDelay);

	@Inject(method = "tick", at = @At("RETURN"))
	public void handleItemTick(CallbackInfo ci) {
		if (isOnGround()) {
			BlockPos pos = getBlockPos().down();
			BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock().is(Blocks.NOTE_BLOCK)) {
				ItemStack stack = this.getStack();
				if (stack.getItem().equals(Items.NOTE_BLOCK)) {
					NoteBlockEntity nbe = (NoteBlockEntity) world.getBlockEntity(pos);
					NbtCompound tag = new NbtCompound();
					{
						NbtList enchantList = new NbtList();
						NbtCompound einfo = new NbtCompound();
						einfo.putString("id", "fortune");
						einfo.putInt("lvl", 1);
						enchantList.addElement(0, einfo);
						tag.put("Enchantments", enchantList);
					}
					assert nbe != null;
					NbtCompound blockEntityTag = nbe.writeNbt(new NbtCompound());
					blockEntityTag.putBoolean("locked", true);
					tag.put("BlockEntityTag", blockEntityTag);
					NbtCompound display = new NbtCompound();
					NbtList lore = new NbtList();
					lore.add(0, NbtString.of("\"ins:" + nbe.getIns() + "\""));
					lore.add(0, NbtString.of("\"note:" + nbe.getNote() + "\""));
					lore.add(0, NbtString.of("\"locked:" + nbe.locked + "\""));
					display.put("Lore", lore);
					tag.put("display", display);
					tag.putInt("HideFlags", 1);
					stack.setTag(tag);
					setPickupDelay(0);
				}
			}
		}
	}
}
