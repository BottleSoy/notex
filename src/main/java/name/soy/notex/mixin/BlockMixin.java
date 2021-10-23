package name.soy.notex.mixin;

import name.soy.notex.NoteBlockEntity;
import name.soy.notex.Notex;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
	@Inject(method = "onBreak", at = @At("RETURN"))
	public void onbreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
		if (state.getBlock().is(Blocks.NOTE_BLOCK)) {
			world.removeBlockEntity(pos);
		}
	}

	@Inject(method = "onPlaced", at = @At("RETURN"))
	public void placed(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
		if (state.getBlock().is(Blocks.NOTE_BLOCK)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof NoteBlockEntity) {
				((NoteBlockEntity) blockEntity).setIns(Notex.insMap.getIf(new CachedBlockPosition(world, pos.down(), false)).name);
			}
		}
	}
}
