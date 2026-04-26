package br.net.ethyios.mountamulet;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;

public final class MountOwnership {
    private static final Set<ResourceLocation> SUPPORTED_ENTITY_IDS = Set.of(
        ResourceLocation.withDefaultNamespace("horse"),
        ResourceLocation.withDefaultNamespace("donkey"),
        ResourceLocation.withDefaultNamespace("mule"),
        ResourceLocation.withDefaultNamespace("llama"),
        ResourceLocation.withDefaultNamespace("trader_llama"),
        ResourceLocation.withDefaultNamespace("camel"),
        ResourceLocation.withDefaultNamespace("skeleton_horse"),
        ResourceLocation.withDefaultNamespace("zombie_horse")
    );

    private MountOwnership() {
    }

    public static boolean playerMayCapture(Entity entity, Player player) {
        if (!(entity instanceof AbstractHorse horse)) {
            return false;
        }

        if (entity.isVehicle() || entity.isPassenger()) {
            return false;
        }

        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return playerMayCaptureStoredType(entityTypeId, horse.isTamed(), ownerUuid(horse), player.getUUID());
    }

    public static boolean playerMayCaptureStoredType(
        ResourceLocation entityTypeId,
        boolean tamed,
        Optional<UUID> ownerUuid,
        UUID playerUuid
    ) {
        if (!SUPPORTED_ENTITY_IDS.contains(entityTypeId)) {
            return false;
        }

        if (ResourceLocation.withDefaultNamespace("camel").equals(entityTypeId)) {
            return tamed;
        }

        return tamed && ownerUuid.filter(playerUuid::equals).isPresent();
    }

    public static boolean supportedMount(Entity entity) {
        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return entity instanceof AbstractHorse && SUPPORTED_ENTITY_IDS.contains(entityTypeId);
    }

    private static Optional<UUID> ownerUuid(AbstractHorse horse) {
        if (horse instanceof Camel) {
            return Optional.empty();
        }

        return Optional.ofNullable(horse.getOwnerUUID());
    }
}
