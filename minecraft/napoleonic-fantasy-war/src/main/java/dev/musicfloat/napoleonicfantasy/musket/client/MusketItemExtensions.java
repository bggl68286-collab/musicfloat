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
        float crouch = entity.isCrouching() ? .28f : 0f;
        boolean using = entity.isUsingItem() && entity.getUseItem().is(NapoleonicFantasy.MUSKET.get());
        boolean bayonet = CharlevilleMusketItem.bayonet(entity.getMainHandItem());
        float t = using ? Mth.clamp(entity.getTicksUsingItem() / (float) CharlevilleMusketItem.LOAD_TICKS, 0, 1) : 1;
        // French manual-of-arms silhouette: the butt stays at the right hip while the
        // left hand supports the forearm. Loading phases deliberately move the hands.
        if (using && bayonet) {
            hand.xRot = -1.48f - crouch;
            hand.yRot = side * -.10f;
            hand.zRot = side * .12f;
            other.xRot = -1.58f - crouch * .55f;
            other.yRot = side * .18f;
            other.zRot = side * -.08f;
            return;
        }
        if (using && t < .20f) { // handle and tear the cartridge at the chest
            hand.xRot = -.72f - crouch;
            hand.yRot = side * -.55f;
            hand.zRot = side * .16f;
            other.xRot = -1.10f - crouch * .55f;
            other.yRot = side * .62f;
            other.zRot = side * -.10f;
        } else if (using && t < .40f) { // prime the pan and close the frizzen
            hand.xRot = -1.28f - crouch;
            hand.yRot = side * -.22f;
            hand.zRot = side * .06f;
            other.xRot = -.82f - crouch * .55f;
            other.yRot = side * .72f;
            other.zRot = side * -.12f;
        } else if (using && t < .58f) { // pour powder and ball down the muzzle
            hand.xRot = -1.58f - crouch;
            hand.yRot = side * .08f;
            hand.zRot = side * .03f;
            other.xRot = -1.22f - crouch * .55f;
            other.yRot = side * .48f;
            other.zRot = side * -.04f;
        } else if (using && t < .78f) { // draw and insert the rammer
            hand.xRot = -1.18f - crouch;
            hand.yRot = side * -.36f;
            hand.zRot = side * .08f;
            other.xRot = -.55f - crouch * .55f;
            other.yRot = side * .96f;
            other.zRot = side * -.16f;
        } else if (using && t < .94f) { // ram home and return the rod
            hand.xRot = -1.40f - crouch;
            hand.yRot = side * -.18f;
            hand.zRot = side * .04f;
            other.xRot = -1.38f - crouch * .55f;
            other.yRot = side * .30f;
            other.zRot = side * -.05f;
        } else { // shoulder arms / ready-to-fire
            hand.xRot = -1.58f - crouch;
            hand.yRot = side * -.20f;
            hand.zRot = side * .05f;
            other.xRot = -1.50f - crouch * .55f;
            other.yRot = side * .50f;
            other.zRot = side * -.07f;
        }
    });
    @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        return hand == InteractionHand.MAIN_HAND && stack.is(NapoleonicFantasy.MUSKET.get()) ? MUSKET : HumanoidModel.ArmPose.EMPTY;
    }
    @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equip, float swing) {
        float side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float bob = Mth.sin((player.tickCount + partialTick) * .08f) * .01f;
        float recoil = Mth.sin(Mth.clamp(swing * 1.8f, 0, 1) * (float)Math.PI);
        pose.translate(side * .46f, -.43f + bob - equip * .5f, -.72f - recoil * .20f);
        pose.mulPose(Axis.YP.rotationDegrees(side * (6 - recoil * 28)));
        pose.mulPose(Axis.ZP.rotationDegrees(side * (-5 + recoil * 18)));
        pose.mulPose(Axis.XP.rotationDegrees(-6 + recoil * 34));
        if (player.isUsingItem() && player.getUseItem() == stack) {
            float t = Mth.clamp((player.getTicksUsingItem() + partialTick) / (float) CharlevilleMusketItem.LOAD_TICKS, 0, 1);
            float phase = t < .2f ? -12 : t < .4f ? 8 : t < .58f ? 24 : t < .78f ? -18 : 4;
            pose.translate(side * (t < .58f ? -.10f : -.02f), t * .10f, t * .08f);
            pose.mulPose(Axis.XP.rotationDegrees(phase));
            pose.mulPose(Axis.ZP.rotationDegrees(side * (t < .2f ? -10 : t < .4f ? 5 : 0)));
        }
        return true;
    }
}
