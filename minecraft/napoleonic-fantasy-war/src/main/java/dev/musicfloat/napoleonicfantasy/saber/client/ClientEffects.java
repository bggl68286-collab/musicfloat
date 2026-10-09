package dev.musicfloat.napoleonicfantasy.saber.client;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.saber.combat.SabreCombat;
import dev.musicfloat.napoleonicfantasy.saber.network.SabreNetwork;
import dev.musicfloat.napoleonicfantasy.saber.particle.ImperialParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import java.util.*;

@Mod.EventBusSubscriber(modid=NapoleonicFantasy.MOD_ID,value=Dist.CLIENT)
public final class ClientEffects {
    private static final List<Active> ACTIVE=new ArrayList<>();
    private static final Map<UUID,Motion> MOTIONS=new HashMap<>();
    private static final DustParticleOptions GOLD=new DustParticleOptions(new Vector3f(1f,0.68f,0.12f),1.2f);
    private static ClientLevel knownLevel;
    private static class Active { final SabreNetwork.Effect fx;int age;Active(SabreNetwork.Effect fx){this.fx=fx;} }
    private record Motion(long start,int duration,int kind) {}
    public static void receive(SabreNetwork.Effect fx) {
        Minecraft mc=Minecraft.getInstance();
        if(mc.level==null || fx.kind()<0 || fx.kind()>2 || !Float.isFinite(fx.range()) || fx.range()<0 || fx.range()>32) return;
        if(fx.kind()==SabreCombat.MELEE && ACTIVE.stream().anyMatch(a -> a.age<4 && a.fx.kind()==SabreCombat.MELEE && a.fx.owner().equals(fx.owner()))) return;
        if(ACTIVE.size()>=24) ACTIVE.remove(0);
        ACTIVE.add(new Active(fx));
        if(fx.kind()!=SabreCombat.MELEE) MOTIONS.put(fx.owner(),new Motion(mc.level.getGameTime(),fx.kind()==SabreCombat.NOVA?14:9,fx.kind()));
    }
    public static float skillMotion(LivingEntity entity,float partialTick) {
        Motion m=MOTIONS.get(entity.getUUID());
        if(m==null)return 0;
        float t=(entity.level().getGameTime()-m.start+partialTick)/m.duration;
        return t>=0 && t<1 ? Mth.sin(t*(float)Math.PI) : 0;
    }
    private static void sprite(ClientLevel level,int kind,Vec3 p,float size,int life,float angle) {
        level.addAlwaysVisibleParticle(new ImperialParticleOptions(kind,size,life,angle),true,p.x,p.y,p.z,0,0,0);
    }
    private static int amount(int full) {
        ParticleStatus status=Minecraft.getInstance().options.particles().get();
        return status==ParticleStatus.MINIMAL?Math.max(1,full/5):status==ParticleStatus.DECREASED?Math.max(1,full/2):full;
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event) {
        if(event.phase!=TickEvent.Phase.END)return;
        Minecraft mc=Minecraft.getInstance();
        ClientLevel level=mc.level;
        if(level!=knownLevel) {ACTIVE.clear();MOTIONS.clear();knownLevel=level;}
        if(level==null || mc.isPaused())return;
        MOTIONS.entrySet().removeIf(e -> level.getGameTime()-e.getValue().start>e.getValue().duration+4);
        for(Iterator<Active> it=ACTIVE.iterator();it.hasNext();) {
            Active active=it.next(); SabreNetwork.Effect fx=active.fx; int age=active.age++;
            Vec3 base=fx.position(),forward=fx.direction();
            if(fx.kind()==SabreCombat.NOVA) {
                if(age==0) {
                    sprite(level,2,base.add(0,3.4,0),7.5f,26,0);
                    sprite(level,1,base.add(0,1.3,0),6f,14,0);
                }
                float radius=fx.range()*Math.min(1,(age+1)/17f);
                if(age%4==0 && age<=16) sprite(level,3,base.add(0,0.06+age*0.015,0),radius*2,12,age*.04f);
                for(int i=0;i<amount(24);i++) {
                    double a=level.random.nextDouble()*Math.PI*2;
                    Vec3 p=base.add(Math.cos(a)*radius,0.15+level.random.nextDouble()*1.5,Math.sin(a)*radius);
                    level.addParticle(GOLD,p.x,p.y,p.z,Math.cos(a)*.12,.08,Math.sin(a)*.12);
                    if(i%5==0)level.addParticle(ParticleTypes.END_ROD,p.x,p.y,p.z,0,.12,0);
                }
                if(age>=19)it.remove();
            } else {
                Vec3 p=base.add(0,1.15,0).add(forward.scale(fx.kind()==SabreCombat.WAVE?1+fx.range()*age/14:1.1));
                if(age%2==0) sprite(level,0,p,fx.kind()==SabreCombat.WAVE?4.6f:2.8f,9,(age%4==0?.3f:-.3f));
                if(age==0)sprite(level,1,p,1.8f,7,0);
                for(int i=0;i<amount(14);i++) {
                    Vec3 spark=p.add((level.random.nextDouble()-.5)*3,(level.random.nextDouble()-.5)*2,(level.random.nextDouble()-.5)*3);
                    level.addParticle(GOLD,spark.x,spark.y,spark.z,forward.x*.15,.04,forward.z*.15);
                }
                if(age>=(fx.kind()==SabreCombat.WAVE?14:4))it.remove();
            }
        }
        // Charge effects appear on nearby players as well as the local player.
        for(var player:level.players()) {
            if(player.swinging && player.swingTime==1 && player.getMainHandItem().is(NapoleonicFantasy.SABRE.get())
                && ACTIVE.stream().noneMatch(a -> a.age<4 && a.fx.kind()==SabreCombat.MELEE && a.fx.owner().equals(player.getUUID()))) {
                if(ACTIVE.size()>=24)ACTIVE.remove(0);
                ACTIVE.add(new Active(new SabreNetwork.Effect(player.getUUID(),SabreCombat.MELEE,player.position(),player.getLookAngle(),5)));
            }
            if(!player.isUsingItem() || !player.getUseItem().is(NapoleonicFantasy.SABRE.get()) || player.tickCount%5!=0)continue;
            float charge=Mth.clamp(player.getTicksUsingItem()/20f,0,1);
            sprite(level,3,player.position().add(0,.07,0),2+charge*4,8,player.tickCount*.08f);
            if(charge>=1 && player.tickCount%10==0) sprite(level,2,player.position().add(0,2.3,0),1.7f,9,0);
            for(int i=0;i<amount(8);i++) {
                double a=player.tickCount*.12+i*Math.PI/4;
                Vec3 p=player.position().add(Math.cos(a)*1.2,.3+level.random.nextDouble()*2,Math.sin(a)*1.2);
                level.addParticle(ParticleTypes.END_ROD,p.x,p.y,p.z,0,.07,0);
            }
        }
    }
    @SubscribeEvent public static void camera(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc=Minecraft.getInstance();
        if(mc.player==null || mc.level==null)return;
        Motion m=MOTIONS.get(mc.player.getUUID());
        if(m==null || m.kind!=SabreCombat.NOVA)return;
        float motion=skillMotion(mc.player,(float)event.getPartialTick());
        event.setPitch(event.getPitch()+Mth.sin((mc.player.tickCount+(float)event.getPartialTick())*2)*motion*.45f);
    }
    private ClientEffects() {}
}
