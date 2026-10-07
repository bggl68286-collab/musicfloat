package dev.musicfloat.imperialsabre.combat;

import dev.musicfloat.imperialsabre.ImperialSabre;
import dev.musicfloat.imperialsabre.SabreConfig;
import dev.musicfloat.imperialsabre.network.SabreNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class SabreCombat {
    public static final int MELEE = 0, WAVE = 1, NOVA = 2;
    public static boolean validTarget(Player owner, LivingEntity target) {
        return target != owner && target.isAlive() && !target.isSpectator()
            && !(target instanceof ArmorStand) && !owner.isAlliedTo(target)
            && (!(target instanceof Player) || SabreConfig.ABILITIES_DAMAGE_PLAYERS.get());
    }
    public static boolean insideWave(Vec3 offset, Vec3 forward, double range) {
        double distance = offset.length();
        return distance <= range && (distance < 1.5 || offset.scale(1.0 / distance).dot(forward) >= 0.74);
    }
    public static void melee(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        long now = level.getGameTime();
        if (now < player.getPersistentData().getLong("ImperialSabreNextTrail")) return;
        player.getPersistentData().putLong("ImperialSabreNextTrail", now + 6);
        SabreNetwork.effect(player, MELEE, 5.0f);
    }
    public static void wave(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        double range = SabreConfig.WAVE_RANGE.get();
        Vec3 center = player.position().add(0, 0.8, 0), forward = player.getLookAngle().normalize();
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range), e -> validTarget(player, e))) {
            Vec3 offset = target.getBoundingBox().getCenter().subtract(center);
            if (insideWave(offset, forward, range) && player.hasLineOfSight(target)) {
                target.hurt(player.damageSources().playerAttack(player), SabreConfig.WAVE_DAMAGE.get().floatValue());
                target.knockback(1.3, -forward.x, -forward.z);
            }
        }
        player.getCooldowns().addCooldown(ImperialSabre.SABRE.get(), SabreConfig.WAVE_COOLDOWN.get());
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.4f, 0.65f);
        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.1f);
        SabreNetwork.effect(player, WAVE, (float) range);
    }
    public static void nova(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        double radius = SabreConfig.NOVA_RADIUS.get();
        Vec3 center = player.position();
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius), e -> validTarget(player, e))) {
            Vec3 offset = target.position().subtract(center);
            if (offset.lengthSqr() <= radius * radius && player.hasLineOfSight(target)) {
                target.hurt(player.damageSources().playerAttack(player), SabreConfig.NOVA_DAMAGE.get().floatValue());
                target.knockback(1.8, -offset.x, -offset.z);
            }
        }
        player.getCooldowns().addCooldown(ImperialSabre.SABRE.get(), SabreConfig.NOVA_COOLDOWN.get());
        level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.2f, 1.5f);
        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.5f, 1.2f);
        SabreNetwork.effect(player, NOVA, (float) radius);
    }
    private SabreCombat() {}
}
