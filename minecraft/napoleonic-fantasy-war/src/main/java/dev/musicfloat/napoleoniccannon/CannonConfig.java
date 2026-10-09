package dev.musicfloat.napoleonicfantasy;
import net.minecraftforge.common.ForgeConfigSpec;
public final class CannonConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue ROUND_DAMAGE,PELLET_DAMAGE,SPEED,WAVE_DAMAGE,NOVA_DAMAGE,WAVE_RANGE,NOVA_RADIUS;
    public static final ForgeConfigSpec.IntValue PELLETS,WAVE_COOLDOWN,NOVA_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue DAMAGE_PLAYERS,SOUND,ABILITIES_DAMAGE_PLAYERS;
    static{var b=new ForgeConfigSpec.Builder();
        ROUND_DAMAGE=b.defineInRange("roundShotDamage",78d,1,500);
        PELLET_DAMAGE=b.defineInRange("canisterPelletDamage",8d,1,100);
        PELLETS=b.defineInRange("canisterPellets",14,3,24);
        SPEED=b.defineInRange("projectileSpeed",4.2d,1,6);
        DAMAGE_PLAYERS=b.define("damagePlayers",false);SOUND=b.define("soundEffects",true);
        b.push("combat");
        WAVE_DAMAGE=b.defineInRange("waveDamage",36d,1d,500d);NOVA_DAMAGE=b.defineInRange("novaDamage",90d,1d,1000d);
        WAVE_RANGE=b.defineInRange("waveRange",18d,3d,32d);NOVA_RADIUS=b.defineInRange("novaRadius",12d,3d,24d);
        WAVE_COOLDOWN=b.defineInRange("waveCooldownTicks",60,10,1200);NOVA_COOLDOWN=b.defineInRange("novaCooldownTicks",160,20,2400);
        ABILITIES_DAMAGE_PLAYERS=b.define("abilitiesDamagePlayers",false);b.pop();SPEC=b.build();}
    private CannonConfig(){}
}
