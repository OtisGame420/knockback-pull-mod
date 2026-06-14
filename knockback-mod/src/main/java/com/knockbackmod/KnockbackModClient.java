package com.knockbackmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KnockbackModClient implements ClientModInitializer {

    private static KeyBinding knockbackKey;
    // Cooldown w tickach (20 ticków = 1 sekunda)
    private int cooldown = 0;
    // Zasięg w blokach
    private static final double RANGE = 6.0;
    // Siła odpychania w stronę gracza (ujemna = przyciąganie, dodatnia = odpychanie OD gracza)
    // Tu chodzi o "odrzucanie W STRONĘ gracza" czyli enemy leci ku graczowi
    private static final double PULL_STRENGTH = 1.8;
    private static final double PULL_UP = 0.25;
    // Cooldown ticki
    private static final int COOLDOWN_TICKS = 10;

    @Override
    public void onInitializeClient() {
        // Rejestracja keybindu - domyślnie R
        knockbackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.knockbackmod.pull",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.knockbackmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Obsługa cooldownu
            if (cooldown > 0) {
                cooldown--;
            }

            // Sprawdź czy klawisz wciśnięty
            while (knockbackKey.wasPressed()) {
                if (cooldown > 0) return;

                PlayerEntity player = client.player;
                World world = client.world;

                Vec3d playerPos = player.getPos().add(0, player.getEyeHeight(player.getPose()), 0);

                // Znajdź wszystkie żywe istoty w zasięgu
                Box searchBox = player.getBoundingBox().expand(RANGE);
                List<LivingEntity> entities = world.getEntitiesByClass(
                        LivingEntity.class,
                        searchBox,
                        entity -> entity != player && entity.isAlive() && entity.squaredDistanceTo(player) <= RANGE * RANGE
                );

                if (!entities.isEmpty()) {
                    for (LivingEntity entity : entities) {
                        // Kierunek OD entity DO gracza (żeby entity leciało ku graczowi)
                        Vec3d entityPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);
                        Vec3d direction = playerPos.subtract(entityPos).normalize();

                        // Zastosuj velocity - entity leci w stronę gracza
                        double velX = direction.x * PULL_STRENGTH;
                        double velY = direction.y * PULL_STRENGTH + PULL_UP;
                        double velZ = direction.z * PULL_STRENGTH;

                        entity.setVelocity(velX, velY, velZ);
                        entity.velocityModified = true;

                        // Efekt cząsteczkowy przy entity
                        spawnParticles(world, entity);
                    }

                    // Dźwięk
                    player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.2f);

                    cooldown = COOLDOWN_TICKS;
                }
            }
        });
    }

    private void spawnParticles(World world, LivingEntity entity) {
        Vec3d pos = entity.getPos();
        for (int i = 0; i < 8; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = Math.random() * entity.getHeight();
            double offsetZ = (Math.random() - 0.5) * 0.5;
            world.addParticle(
                    ParticleTypes.WITCH,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    0, 0.1, 0
            );
        }
    }
}
