package dev.musicfloat.napoleonicfantasy.client;
import dev.musicfloat.napoleonicfantasy.*;
import dev.musicfloat.napoleonicfantasy.item.HandCannonItem;
import dev.musicfloat.napoleonicfantasy.network.CannonNetwork;
import dev.musicfloat.napoleonicfantasy.particle.CannonParticleOptions;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid=NapoleonicFantasy.MOD_ID,value=Dist.CLIENT)
public final class CannonEffects {
    private static ClientLevel knownLevel;
    private static final Map<UUID,Long> SHOTS=new HashMap<>();
    public static LivingEntity renderEntity;
    private static int amount(int count){var p=Minecraft.getInstance().options.particles().get();return p==ParticleStatus.MINIMAL?Math.max(2,count/4):p==ParticleStatus.DECREASED?count/2:count;}
    private static void sprite(ClientLevel l,int k,Vec3 p,Vec3 v,float size,int life,float angle){l.addAlwaysVisibleParticle(new CannonParticleOptions(k,size,life,angle),true,p.x,p.y,p.z,v.x,v.y,v.z);}
    public static void receive(CannonNetwork.Effect e){
        var mc=Minecraft.getInstance();var l=mc.level;
        if(l==null||e.kind()<0||e.kind()>2||!Double.isFinite(e.position().lengthSqr())||!Double.isFinite(e.direction().lengthSqr()))return;
        if(knownLevel!=l){SHOTS.clear();knownLevel=l;}
        Vec3 p=e.position(),d=e.direction().normalize();
        if(e.kind()==0){
            if(SHOTS.size()>64)SHOTS.clear();SHOTS.put(e.owner(),l.getGameTime());
            sprite(l,0,p.add(d.scale(.1)),d.scale(.08),1.8f,5,0);
            sprite(l,2,p.add(d.scale(.3)),d.scale(.06),1.6f,8,0);
            for(int i=0;i<amount(9);i++){
                Vec3 spread=new Vec3(l.random.nextGaussian()*.18,l.random.nextGaussian()*.12,l.random.nextGaussian()*.18);
                sprite(l,1,p.add(d.scale(i*.22)).add(spread),d.scale(.035+i*.007).add(.008,.012,0),1.15f+i*.18f,70+l.random.nextInt(24),l.random.nextFloat()*6.28f);
            }
            for(int i=0;i<amount(42);i++){
                Vec3 v=d.scale(.25+l.random.nextDouble()*.35).add(l.random.nextGaussian()*.07,l.random.nextGaussian()*.07,l.random.nextGaussian()*.07);
                l.addParticle(ParticleTypes.ASH,p.x,p.y,p.z,v.x,v.y,v.z);
                if(i%6==0)sprite(l,2,p.add(d.scale(.2)),v,.12f,11,l.random.nextFloat()*6.28f);
            }
            if(e.sound())l.playLocalSound(p.x,p.y,p.z,NapoleonicFantasy.SOUND.get("report").get(),SoundSource.PLAYERS,4f,.96f+l.random.nextFloat()*.08f,true);
        }else if(e.kind()==1){
            sprite(l,3,p.add(0,.35,0),new Vec3(0,.022,0),2.2f,30,0);
            for(int i=0;i<amount(18);i++)l.addParticle(ParticleTypes.ASH,p.x,p.y,p.z,l.random.nextGaussian()*.1,l.random.nextDouble()*.14,l.random.nextGaussian()*.1);
            if(e.sound())l.playLocalSound(p.x,p.y,p.z,NapoleonicFantasy.SOUND.get("impact").get(),SoundSource.PLAYERS,1.2f,1,true);
        }else sprite(l,2,p,Vec3.ZERO,.5f,6,0);
    }
    public static float recoil(LivingEntity entity,float partial){
        if(entity==null)return 0;Long time=SHOTS.get(entity.getUUID());
        if(time==null&&entity.getMainHandItem().hasTag()&&entity.getMainHandItem().getTag().contains("ShotAt"))time=entity.getMainHandItem().getTag().getLong("ShotAt");
        if(time==null)return 0;float t=entity.level().getGameTime()-time+partial;
        return t<0||t>=22?0:t<2?t/2f:(float)Math.exp(-(t-2)*.15);
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(e.phase!=TickEvent.Phase.END)return;var l=Minecraft.getInstance().level;if(l!=knownLevel){SHOTS.clear();renderEntity=null;knownLevel=l;}if(l!=null)SHOTS.entrySet().removeIf(x->l.getGameTime()-x.getValue()>28);}
    @SubscribeEvent public static void pre(RenderLivingEvent.Pre<?,?> e){renderEntity=e.getEntity();}
    @SubscribeEvent public static void post(RenderLivingEvent.Post<?,?> e){renderEntity=null;}
    @SubscribeEvent public static void hand(RenderHandEvent e){var p=Minecraft.getInstance().player;if(p!=null&&p.getMainHandItem().is(NapoleonicFantasy.CANNON.get())&&e.getHand()==InteractionHand.OFF_HAND)e.setCanceled(true);}
    @SubscribeEvent public static void camera(ViewportEvent.ComputeCameraAngles e){var mc=Minecraft.getInstance();if(mc.player==null)return;float r=recoil(mc.player,(float)e.getPartialTick());e.setPitch(e.getPitch()+Mth.sin((mc.player.tickCount+(float)e.getPartialTick())*2.4f)*r*.7f);}
    @SubscribeEvent public static void hud(RenderGuiEvent.Post e){
        var mc=Minecraft.getInstance();var p=mc.player;if(p==null||mc.options.hideGui||!p.getMainHandItem().is(NapoleonicFantasy.CANNON.get()))return;
        var stack=p.getMainHandItem();boolean using=p.isUsingItem()&&p.getUsedItemHand()==InteractionHand.MAIN_HAND;
        int t=using?p.getTicksUsingItem():0,stage=HandCannonItem.loaded(stack)?5:HandCannonItem.stage(t);
        Component text=Component.translatable(using?"stage.napoleonic_fantasy."+stage:HandCannonItem.loaded(stack)?"state.napoleonic_fantasy.loaded":"hud.napoleonic_fantasy.empty");
        int x=e.getGuiGraphics().guiWidth()/2,y=e.getGuiGraphics().guiHeight()-79;
        e.getGuiGraphics().drawCenteredString(mc.font,text,x,y,0xEFE1BB);
        if(using){float fill=HandCannonItem.loaded(stack)?1:Mth.clamp(t/(float)HandCannonItem.LOAD_TICKS,0,1);e.getGuiGraphics().fill(x-80,y+12,x+80,y+16,0x99252520);e.getGuiGraphics().fill(x-80,y+12,x-80+(int)(160*fill),y+16,0xFFD0A74B);}
    }
    private CannonEffects(){}
}
