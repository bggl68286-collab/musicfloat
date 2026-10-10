package dev.musicfloat.napoleonicfantasy.musket.combat;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.musket.item.CharlevilleMusketItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class MusketCombat {
    public static final double RANGE = 72.0;
    public static final float DAMAGE = 18.0f;

    public static void fire(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle().normalize();
        Vec3 end = start.add(direction.scale(RANGE));
        BlockHitResult block = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 impact = block.getType() == HitResult.Type.MISS ? end : block.getLocation();
        AABB search = new AABB(start, impact).inflate(1.25);
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, search, e -> e != player && e.isAlive() && !e.isSpectator())) {
            var hit = target.getBoundingBox().inflate(.18).clip(start, impact);
            if (hit.isPresent()) {
                double distance = start.distanceToSqr(hit.get());
                if (distance < bestDistance) { bestDistance = distance; best = target; }
            }
        }
        if (best != null) best.hurt(player.damageSources().playerAttack(player), DAMAGE);
        Vec3 muzzle = start.add(direction.scale(1.02));
        // Layered from the generated VFX reference: hot core, sparks, flash halo,
        // then a short-lived black-powder smoke cloud and drifting ash.
        level.sendParticles(ParticleTypes.FLAME, muzzle.x, muzzle.y, muzzle.z, 18, .10, .10, .10, .035);
        level.sendParticles(ParticleTypes.LAVA, muzzle.x, muzzle.y, muzzle.z, 4, .08, .08, .08, .01);
        level.sendParticles(ParticleTypes.CRIT, muzzle.x, muzzle.y, muzzle.z, 24, .18, .18, .18, .20);
        level.sendParticles(ParticleTypes.SMOKE, muzzle.x, muzzle.y, muzzle.z, 30, .20, .20, .20, .05);
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, muzzle.x, muzzle.y, muzzle.z, 8, .16, .16, .16, .025);
        level.sendParticles(ParticleTypes.WHITE_ASH, muzzle.x, muzzle.y, muzzle.z, 20, .25, .25, .25, .035);
        level.sendParticles(ParticleTypes.CLOUD, impact.x, impact.y, impact.z, 18, .12, .12, .12, .03);
        player.setDeltaMovement(player.getDeltaMovement().subtract(direction.scale(.035)));
        level.playSound(null, player.blockPosition(), NapoleonicFantasy.SOUND.get("musket_report").get(), SoundSource.PLAYERS, 1.15f, .92f + level.random.nextFloat() * .12f);
        player.getCooldowns().addCooldown(NapoleonicFantasy.MUSKET.get(), 16);
    }

    public static void thrust(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Vec3 center = player.getEyePosition().add(player.getLookAngle().scale(1.3));
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(1.25), e -> e != player && e.isAlive() && !e.isSpectator())) {
            Vec3 offset = target.getBoundingBox().getCenter().subtract(player.getEyePosition());
            if (offset.length() <= 2.3 && offset.normalize().dot(player.getLookAngle()) > .35) {
                target.hurt(player.damageSources().playerAttack(player), 10.0f);
                target.knockback(.45, -player.getLookAngle().x, -player.getLookAngle().z);
            }
        }
        level.playSound(null, player.blockPosition(), NapoleonicFantasy.SOUND.get("bayonet_thrust").get(), SoundSource.PLAYERS, .9f, .95f + level.random.nextFloat() * .1f);
        level.sendParticles(ParticleTypes.CRIT, center.x, center.y, center.z, 8, .08, .08, .08, .12);
        player.getCooldowns().addCooldown(NapoleonicFantasy.MUSKET.get(), 12);
    }

    private MusketCombat() {}
}
