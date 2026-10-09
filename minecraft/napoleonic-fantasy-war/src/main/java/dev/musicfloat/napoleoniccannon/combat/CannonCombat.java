package dev.musicfloat.napoleonicfantasy.combat;
import dev.musicfloat.napoleonicfantasy.*;
import dev.musicfloat.napoleonicfantasy.entity.Cannonball;
import dev.musicfloat.napoleonicfantasy.item.HandCannonItem;
import dev.musicfloat.napoleonicfantasy.network.CannonNetwork;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.ClipContext;

public final class CannonCombat {
    public static Vec3 muzzle(Player p){
        Vec3 eye=p.getEyePosition(),d=p.getLookAngle(),right=d.cross(new Vec3(0,1,0)).normalize();
        Vec3 muzzle=eye.add(d.scale(1.5)).add(right.scale(p.getMainArm()==HumanoidArm.RIGHT?.23:-.23)).add(0,-.25,0);
        var hit=p.level().clip(new ClipContext(eye,muzzle,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,p));
        return hit.getType()==HitResult.Type.MISS?muzzle:hit.getLocation().subtract(d.scale(.04));
    }
    public static void fire(Player p,ItemStack s){
        if(p.level().isClientSide)return;
        Vec3 m=muzzle(p);boolean canister=HandCannonItem.mode(s)==1;
        int count=canister?CannonConfig.PELLETS.get():1;
        for(int i=0;i<count;i++){
            Cannonball ball=new Cannonball(NapoleonicFantasy.BALL.get(),p.level());ball.setOwner(p);ball.setPos(m);
            ball.configure(canister,CannonConfig.ROUND_DAMAGE.get().floatValue(),CannonConfig.PELLET_DAMAGE.get().floatValue());
            ball.shootFromRotation(p,p.getXRot(),p.getYRot(),0,CannonConfig.SPEED.get().floatValue(),canister?9f:.15f);
            p.level().addFreshEntity(ball);
        }
        CannonNetwork.send(p.getUUID(),0,m,p.getLookAngle(),p.level(),CannonConfig.SOUND.get());
        p.getCooldowns().addCooldown(NapoleonicFantasy.CANNON.get(),25);
    }
    private CannonCombat(){}
}
