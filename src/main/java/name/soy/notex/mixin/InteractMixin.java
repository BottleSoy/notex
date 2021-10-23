package name.soy.notex.mixin;

import name.soy.notex.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class InteractMixin {
	@Shadow
	public ServerWorld world;

	@Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"), cancellable = true)
	public void tryBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock().is(Blocks.NOTE_BLOCK)) {
			NoteBlockEntity blockEntity = (NoteBlockEntity) world.getBlockEntity(pos);
			if (blockEntity != null && blockEntity.locked)
				cir.setReturnValue(false);
		}
	}

	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	public void click(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		BlockState blockState = world.getBlockState(hitResult.getBlockPos());
		if (blockState.getBlock().equals(Blocks.NOTE_BLOCK)) {
//			((NoteBlock) blockState.getBlock()).onSyncedBlockEvent(blockState, world, hitResult.getBlockPos(), 0, 0);
			NoteBlockEntity blockEntity = (NoteBlockEntity) world.getBlockEntity(hitResult.getBlockPos());
			assert blockEntity != null;
			if (player.isSpectator()) {
				player.sendMessage(new LiteralText("音符盒:{乐器:\"" + blockEntity.getIns() + "\",音高:\"" + blockEntity.getNote() + "\"}"), true);
				return;
			}
			if (!player.isSneaking() && player.getMainHandStack().getItem() == Items.DIAMOND_SWORD && hand == Hand.MAIN_HAND) {
				NXVisiualize.PLAYER_SCROLLS.add(player.getUuid());
				player.sendMessage(new LiteralText(Lang.get(player, "scroll.enabled")), true);
				cir.setReturnValue(ActionResult.SUCCESS);
				return;
			}

			if (!player.isSneaking() && player.getMainHandStack().getItem() == Items.DIAMOND_PICKAXE && hand == Hand.MAIN_HAND) {
				blockEntity.locked = !blockEntity.locked;
				player.sendMessage(new LiteralText(Lang.get(player, blockEntity.locked ? "note.lock" : "note.unlock")), true);
				cir.setReturnValue(ActionResult.SUCCESS);
				return;
			}
			if (player.isSneaking() && hand == Hand.MAIN_HAND && player.getMainHandStack().getItem() == Items.DEBUG_STICK) {
				if (Notex.insNames.containsKey(blockEntity.getIns())) {
					CustomIns custom = Notex.insNames.get(blockEntity.getIns());
					blockEntity.setNote(blockEntity.getNote() - 1);
					if (blockEntity.getNote() < custom.min || blockEntity.getNote() > custom.max) {
						blockEntity.setNote(custom.max);
					}
				} else {
					player.sendMessage(new LiteralText(Lang.get(player, "ins.notregistered")), true);
				}
				blockEntity.play();
				cir.setReturnValue(ActionResult.SUCCESS);
				return;
			}

			if (player.getOffHandStack().getItem() == Items.DEBUG_STICK) {
//				System.out.println("debug to noteblock");
				if (NXVisiualize.PLAYER_SCROLLS.contains(player.getUuid())) {
					NXVisiualize.PLAYER_SCROLLS.remove(player.getUuid());
					player.sendMessage(new LiteralText(Lang.get(player, "scroll.disabled")), true);
					cir.setReturnValue(ActionResult.FAIL);
					return;
				}

				if (player.isSneaking() && hand == Hand.OFF_HAND) {
					if (Notex.insNames.containsKey(blockEntity.getIns())) {
						CustomIns custom = Notex.insNames.get(blockEntity.getIns());
						blockEntity.setNote(blockEntity.getNote() - 1);
						if (blockEntity.getNote() < custom.min || blockEntity.getNote() > custom.max) {
							blockEntity.setNote(custom.max);
						}
					} else {
						player.sendMessage(new LiteralText(Lang.get(player, "ins.notregistered")), true);
					}
					blockEntity.play();
					cir.setReturnValue(ActionResult.SUCCESS);
					return;
				}
//				cir.setReturnValue(ActionResult.PASS);
			}
		}
	}
}
