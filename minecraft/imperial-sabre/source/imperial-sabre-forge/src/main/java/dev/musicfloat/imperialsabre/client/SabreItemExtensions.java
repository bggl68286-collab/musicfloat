package dev.musicfloat.imperialsabre.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.musicfloat.imperialsabre.ImperialSabre;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class SabreItemExtensions implements IClientItemExtensions {
    private static final HumanoidModel.ArmPose READY = HumanoidModel.ArmPose.create("IMPERIAL_SABRE_READY",false,(model,entity,arm) -> {
        var hand=arm==HumanoidArm.RIGHT?model.rightArm:model.leftArm;
        float side=arm==HumanoidArm.RIGHT?1:-1;
        float strike=ClientEffects.skillMotion(entity,0);
        if (entity.swinging) return; // Vanilla melee swing animates the complete arm.
        hand.xRot=-0.45f-0.025f*Mth.sin(entity.tickCount*0.09f)-strike*1.15f;
        hand.yRot=side*-0.1f; hand.zRot=side*0.08f;
    });
    private static final HumanoidModel.ArmPose CHARGE = HumanoidModel.ArmPose.create("IMPERIAL_SABRE_CHARGE",true,(model,entity,arm) -> {
        float side=arm==HumanoidArm.RIGHT?1:-1;
        var hand=arm==HumanoidArm.RIGHT?model.rightArm:model.leftArm;
        var other=arm==HumanoidArm.RIGHT?model.leftArm:model.rightArm;
        float t=Mth.clamp(entity.getTicksUsingItem()/20f,0,1);
        hand.xRot=-0.9f-t*1.5f; hand.yRot=side*-0.25f; hand.zRot=side*-0.18f;
        other.xRot=-0.9f-t*0.35f;other.yRot=side*0.48f;other.zRot=side*-0.1f;
    });
    @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        return entity.isUsingItem() && entity.getUsedItemHand()==hand && entity.getUseItem().is(ImperialSabre.SABRE.get()) ? CHARGE : READY;
    }
    @Override public boolean applyForgeHandTransform(PoseStack p,LocalPlayer player,HumanoidArm arm,ItemStack stack,float partialTick,float equip,float swing) {
        float side=arm==HumanoidArm.RIGHT?1:-1;
        float idle=Mth.sin((player.tickCount+partialTick)*0.075f)*0.012f;
        float slash=Mth.sin(Mth.sqrt(swing)*(float)Math.PI);
        float recoil=Mth.sin(swing*(float)Math.PI);
        p.translate(side*(0.56f-slash*0.28f),-0.49f-equip*0.6f+idle+recoil*0.08f,-0.72f-recoil*0.22f);
        p.mulPose(Axis.YP.rotationDegrees(side*(12-slash*78)));
        p.mulPose(Axis.ZP.rotationDegrees(side*(-8+slash*48)));
        p.mulPose(Axis.XP.rotationDegrees(-recoil*52));
        if(player.isUsingItem() && player.getUseItem()==stack) {
            float charge=Mth.clamp((player.getTicksUsingItem()+partialTick)/20f,0,1);
            p.translate(side*-0.2f,charge*0.16f,charge*0.1f);
            p.mulPose(Axis.XP.rotationDegrees(-32-charge*40));
            p.mulPose(Axis.ZP.rotationDegrees(side*(24+charge*14)));
        } else {
            float skill=ClientEffects.skillMotion(player,partialTick);
            p.translate(0,-skill*0.16f,-skill*0.15f);
            p.mulPose(Axis.XP.rotationDegrees(skill*58));
        }
        return true;
    }
}
