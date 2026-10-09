package dev.musicfloat.napoleonicfantasy.client;
import dev.musicfloat.napoleonicfantasy.item.HandCannonItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class CannonItemExtensions implements IClientItemExtensions {
    private CannonRenderer renderer;
    private static final HumanoidModel.ArmPose HOLD=HumanoidModel.ArmPose.create("NAPOLEONIC_CANNON_HOLD",true,(m,e,arm)->{
        float side=arm==HumanoidArm.RIGHT?1:-1;
        var main=arm==HumanoidArm.RIGHT?m.rightArm:m.leftArm;var support=arm==HumanoidArm.RIGHT?m.leftArm:m.rightArm;
        int t=e.isUsingItem()?e.getTicksUsingItem():0;
        boolean loading=e.isUsingItem()&&!HandCannonItem.loaded(e.getMainHandItem());int stage=HandCannonItem.stage(t);
        float r=CannonEffects.recoil(e,0),pitch=e.getXRot()*(float)Math.PI/180;
        main.xRot=-1.12f+pitch-r*.24f;main.yRot=side*-.13f;main.zRot=side*.08f;
        support.xRot=-1.18f+pitch-r*.18f;support.yRot=side*.58f;support.zRot=side*-.15f;
        if(loading){main.xRot=-.88f+pitch-.16f*Mth.sin(t*.38f);main.yRot=side*-.38f;support.xRot=-1.03f+pitch;if(stage==0||stage==3){main.xRot=-1.35f+pitch-.3f*Mth.sin(t*.32f);main.yRot=side*-.25f;}}
    });
    @Override public HumanoidModel.ArmPose getArmPose(LivingEntity e,InteractionHand h,ItemStack s){return HOLD;}
    @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null){var mc=Minecraft.getInstance();renderer=new CannonRenderer(mc.getBlockEntityRenderDispatcher(),mc.getEntityModels());}return renderer;}
    @Override public boolean applyForgeHandTransform(PoseStack p,LocalPlayer player,HumanoidArm arm,ItemStack s,float partial,float equip,float swing){
        float side=arm==HumanoidArm.RIGHT?1:-1;boolean using=player.isUsingItem();boolean loading=using&&!HandCannonItem.loaded(s);float r=CannonEffects.recoil(player,partial);
        float aim=using&&!loading?1:0;
        if(s.hasTag()){
            if(s.getTag().contains("IgniteAt"))aim=1;
            else if(s.getTag().contains("ShotAt")){float age=player.level().getGameTime()-s.getTag().getLong("ShotAt")+partial;aim=Mth.clamp(1-(age-14)/14,0,1);}
        }
        p.translate(side*Mth.lerp(aim,.25f,.06f),-.08-equip*.65+(loading?-.1:0)+Mth.sin((player.tickCount+partial)*.065f)*.004,-1.28+r*.16);
        p.mulPose(Axis.YP.rotationDegrees(side*Mth.lerp(aim,-6f,0f)));
        p.mulPose(Axis.XP.rotationDegrees(loading?-8:r*-7));
        return true;
    }
}
