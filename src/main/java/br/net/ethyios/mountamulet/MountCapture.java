package br.net.ethyios.mountamulet;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class MountCapture {
    private MountCapture() {
    }

    public static MountCaptureResult capture(ItemStack amuletStack, Player player, LivingEntity target) {
        if (amuletStack.has(ModDataComponents.STORED_MOUNT.get())) {
            return MountCaptureResult.ALREADY_FILLED;
        }

        if (!MountOwnership.supportedMount(target)) {
            return MountCaptureResult.UNSUPPORTED;
        }

        if (!MountOwnership.playerMayCapture(target, player)) {
            return MountCaptureResult.NOT_OWNER;
        }

        StoredMount storedMount = StoredMount.from(entityTypeId(target), target.saveWithoutId(new CompoundTag()));
        amuletStack.set(ModDataComponents.STORED_MOUNT.get(), storedMount);
        target.discard();
        return MountCaptureResult.CAPTURED;
    }

    private static ResourceLocation entityTypeId(LivingEntity target) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
    }
}
