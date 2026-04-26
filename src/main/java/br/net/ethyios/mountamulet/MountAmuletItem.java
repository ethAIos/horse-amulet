package br.net.ethyios.mountamulet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MountAmuletItem extends Item {
    public MountAmuletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(
        ItemStack stack,
        Player player,
        LivingEntity interactionTarget,
        InteractionHand usedHand
    ) {
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        MountCaptureResult result = MountCapture.capture(stack, player, interactionTarget);
        return captureInteractionResult(player, result);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        BlockPos targetPos = context.getClickedPos().above();
        return releaseAt(context.getItemInHand(), player, Vec3.atBottomCenterOf(targetPos));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        InteractionResult result = releaseAt(stack, player, airReleasePosition(player));
        return new InteractionResultHolder<>(result, stack);
    }

    private static InteractionResult captureInteractionResult(Player player, MountCaptureResult result) {
        if (result == MountCaptureResult.CAPTURED) {
            return InteractionResult.SUCCESS;
        }

        sendCaptureMessage(player, result);
        return InteractionResult.FAIL;
    }

    private static InteractionResult releaseAt(ItemStack stack, Player player, Vec3 position) {
        if (!stack.has(ModDataComponents.STORED_MOUNT.get())) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        MountReleaseResult result = MountRelease.release(stack, (ServerLevel)player.level(), position, player.getYRot());
        return releaseInteractionResult(player, result);
    }

    private static InteractionResult releaseInteractionResult(Player player, MountReleaseResult result) {
        if (result == MountReleaseResult.RELEASED) {
            return InteractionResult.SUCCESS;
        }

        sendReleaseMessage(player, result);
        return InteractionResult.FAIL;
    }

    private static Vec3 airReleasePosition(Player player) {
        Vec3 lookOffset = player.getLookAngle().normalize().scale(2.0);
        return player.position().add(lookOffset.x(), 0.0, lookOffset.z());
    }

    private static void sendCaptureMessage(Player player, MountCaptureResult result) {
        String message = result == MountCaptureResult.ALREADY_FILLED
            ? "amulet already contains a mount"
            : captureFailureMessage(result);
        player.displayClientMessage(Component.literal(message), true);
    }

    private static String captureFailureMessage(MountCaptureResult result) {
        return result == MountCaptureResult.NOT_OWNER
            ? "mount is not owned by this player"
            : "mount is not supported";
    }

    private static void sendReleaseMessage(Player player, MountReleaseResult result) {
        String message = result == MountReleaseResult.BLOCKED
            ? "release position is blocked"
            : "stored mount data is invalid";
        player.displayClientMessage(Component.literal(message), true);
    }
}
