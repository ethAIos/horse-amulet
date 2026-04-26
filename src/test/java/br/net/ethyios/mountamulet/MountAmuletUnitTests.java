package br.net.ethyios.mountamulet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class MountAmuletUnitTests {
    private static final UUID PLAYER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    private MountAmuletUnitTests() {
    }

    public static void main(String[] args) throws IOException {
        storedMountCopiesEntityTag();
        storedMountRejectsEmptyTag();
        sanitizePersistentTagRemovesTransientValues();
        sanitizePersistentTagKeepsPersistentValues();
        ownershipAcceptsOwnedHorse();
        ownershipRejectsOtherOwner();
        ownershipRejectsUnsupportedEntity();
        ownershipRejectsOwnerlessUndeadHorse();
        packageUsesEthyiosNetBrDomain();
        neoforgeCompatibilityStartsAtConfiguredVersion();
        recipeUsesApprovedShape();
        itemModelUsesAmuletTexture();
    }

    private static void storedMountCopiesEntityTag() {
        CompoundTag sourceTag = new CompoundTag();
        sourceTag.putString("CustomName", "Sprinter");

        StoredMount storedMount = StoredMount.from(ResourceLocation.withDefaultNamespace("horse"), sourceTag);
        sourceTag.putString("CustomName", "Changed");

        assertEquals("Sprinter", storedMount.entityTag().getString("CustomName"), "stored mount copies source tag");
    }

    private static void storedMountRejectsEmptyTag() {
        IllegalArgumentException exception = assertThrows(
            () -> StoredMount.from(ResourceLocation.withDefaultNamespace("horse"), new CompoundTag()),
            "empty entity tag must fail"
        );

        assertContains(exception.getMessage(), "{}", "error includes offending value");
        assertContains(exception.getMessage(), "non-empty CompoundTag", "error includes expected shape");
    }

    private static void sanitizePersistentTagRemovesTransientValues() {
        CompoundTag sourceTag = new CompoundTag();
        sourceTag.putString("Pos", "0,64,0");
        sourceTag.putString("UUID", "legacy");
        sourceTag.putString("Brain", "ai-state");

        CompoundTag sanitizedTag = MountCapture.sanitizePersistentTag(sourceTag);

        assertFalse(sanitizedTag.contains("Pos"), "position data removed");
        assertFalse(sanitizedTag.contains("UUID"), "uuid removed");
        assertFalse(sanitizedTag.contains("Brain"), "brain removed");
    }

    private static void sanitizePersistentTagKeepsPersistentValues() {
        CompoundTag sourceTag = new CompoundTag();
        sourceTag.putString("CustomName", "Sprinter");
        sourceTag.putFloat("Health", 26.0F);
        sourceTag.putBoolean("Tame", true);

        CompoundTag sanitizedTag = MountCapture.sanitizePersistentTag(sourceTag);

        assertEquals("Sprinter", sanitizedTag.getString("CustomName"), "custom name preserved");
        assertEquals(26.0F, sanitizedTag.getFloat("Health"), "health preserved");
        assertTrue(sanitizedTag.getBoolean("Tame"), "tame state preserved");
    }

    private static void ownershipAcceptsOwnedHorse() {
        boolean allowed = MountOwnership.playerMayCaptureStoredType(
            ResourceLocation.withDefaultNamespace("horse"),
            true,
            Optional.of(PLAYER_ID),
            PLAYER_ID
        );

        assertTrue(allowed, "owned horse is capturable");
    }

    private static void ownershipRejectsOtherOwner() {
        boolean allowed = MountOwnership.playerMayCaptureStoredType(
            ResourceLocation.withDefaultNamespace("llama"),
            true,
            Optional.of(OTHER_ID),
            PLAYER_ID
        );

        assertFalse(allowed, "other player's llama is rejected");
    }

    private static void ownershipRejectsUnsupportedEntity() {
        boolean allowed = MountOwnership.playerMayCaptureStoredType(
            ResourceLocation.withDefaultNamespace("pig"),
            true,
            Optional.of(PLAYER_ID),
            PLAYER_ID
        );

        assertFalse(allowed, "unsupported entity is rejected");
    }

    private static void ownershipRejectsOwnerlessUndeadHorse() {
        boolean allowed = MountOwnership.playerMayCaptureStoredType(
            ResourceLocation.withDefaultNamespace("skeleton_horse"),
            true,
            Optional.empty(),
            PLAYER_ID
        );

        assertFalse(allowed, "ownerless skeleton horse is rejected");
    }

    private static void packageUsesEthyiosNetBrDomain() throws IOException {
        String gradleProperties = Files.readString(Path.of("gradle.properties"));
        String buildGradle = Files.readString(Path.of("build.gradle"));

        assertContains(gradleProperties, "mod_group_id=br.net.ethyios.mountamulet", "mod group id domain");
        assertContains(buildGradle, "br.net.ethyios.mountamulet.MountAmuletUnitTests", "unit test main class");
    }

    private static void neoforgeCompatibilityStartsAtConfiguredVersion() throws IOException {
        String gradleProperties = Files.readString(Path.of("gradle.properties"));
        String buildGradle = Files.readString(Path.of("build.gradle"));
        String modToml = Files.readString(Path.of("src/main/templates/META-INF/neoforge.mods.toml"));

        assertContains(gradleProperties, "neo_version=21.1.226", "minimum NeoForge version");
        assertContains(gradleProperties, "neo_version_range=[21.1.226,)", "NeoForge onward range");
        assertContains(buildGradle, "neo_version_range", "metadata range expansion");
        assertContains(modToml, "versionRange=\"${neo_version_range}\"", "mod metadata NeoForge range");
    }

    private static void recipeUsesApprovedShape() throws IOException {
        String recipeJson = Files.readString(Path.of("src/main/resources/data/mount_amulet/recipe/mount_amulet.json"));

        assertContains(recipeJson, "\" S \"", "recipe top row");
        assertContains(recipeJson, "\"S S\"", "recipe middle row");
        assertContains(recipeJson, "\" L \"", "recipe bottom row");
        assertContains(recipeJson, "\"item\": \"minecraft:string\"", "recipe string ingredient");
        assertContains(recipeJson, "\"item\": \"minecraft:leather\"", "recipe leather ingredient");
    }

    private static void itemModelUsesAmuletTexture() throws IOException {
        String modelJson = Files.readString(Path.of("src/main/resources/assets/mount_amulet/models/item/mount_amulet.json"));
        Path texturePath = Path.of("src/main/resources/assets/mount_amulet/textures/item/mount_amulet.png");

        assertContains(modelJson, "\"layer0\": \"mount_amulet:item/mount_amulet\"", "item model texture path");
        assertTrue(Files.exists(texturePath), "amulet item texture exists");
    }

    private static IllegalArgumentException assertThrows(Runnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException exception) {
            return exception;
        }

        throw new AssertionError(message);
    }

    private static void assertContains(String actual, String expected, String message) {
        if (!actual.contains(expected)) {
            throw new AssertionError(message + ": expected substring " + expected + " in " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " got " + actual);
        }
    }

    private static void assertEquals(float expected, float actual, String message) {
        if (Float.compare(expected, actual) != 0) {
            throw new AssertionError(message + ": expected " + expected + " got " + actual);
        }
    }

    private static void assertTrue(boolean actual, String message) {
        if (!actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean actual, String message) {
        if (actual) {
            throw new AssertionError(message);
        }
    }
}
