package dev.musicfloat.napoleonicfantasy.musket.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.musket.item.CharlevilleMusketItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class MusketItemExtensions implements IClientItemExtensions {
    private static final HumanoidModel.ArmPose MUSKET = HumanoidModel.ArmPose.create("CHARLEVILLE_MUSKET", true, (model, entity, arm) -> {
        var hand = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        var other = arm == HumanoidArm.RIGHT ? model.leftArm : model.rightArm;
        float side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float t = Mth.clamp(entity.getTicksUsingItem() / 100f, 0, 1);
        float crouch = entity.isCrouching() ? .28f : 0f;
        hand.xRot = -1.0f - t * .42f - crouch;
        hand.yRot = side * -.22f;
        hand.zRot = side * .08f;
        other.xRot = -1.18f - t * .18f - crouch * .6f;
        other.yRot = side * .52f;
        other.zRot = side * -.08f;
    });
    @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        return entity.isUsingItem() && entity.getUsedItemHand() == hand && stack.is(NapoleonicFantasy.MUSKET.get()) ? MUSKET : HumanoidModel.ArmPose.EMPTY;
    }
    @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equip, float swing) {
        float side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float bob = Mth.sin((player.tickCount + partialTick) * .08f) * .01f;
        float recoil = Mth.sin(Mth.clamp(swing * 1.8f, 0, 1) * (float)Math.PI);
        pose.translate(side * .48f, -.43f + bob - equip * .5f, -.74f - recoil * .18f);
        pose.mulPose(Axis.YP.rotationDegrees(side * (8 - recoil * 24)));
        pose.mulPose(Axis.ZP.rotationDegrees(side * (-7 + recoil * 16)));
        pose.mulPose(Axis.XP.rotationDegrees(-8 + recoil * 30));
        if (player.isUsingItem() && player.getUseItem() == stack) {
            float t = Mth.clamp((player.getTicksUsingItem() + partialTick) / 100f, 0, 1);
            pose.translate(side * -.12f, t * .12f, t * .1f);
            pose.mulPose(Axis.XP.rotationDegrees(-18 - t * 18));
        }
        return true;
    }
}
