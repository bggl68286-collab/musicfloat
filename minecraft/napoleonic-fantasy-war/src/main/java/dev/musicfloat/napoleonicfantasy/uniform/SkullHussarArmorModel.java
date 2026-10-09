package dev.musicfloat.napoleonicfantasy.uniform;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/** A close fitting four-piece uniform model. The detail parts are children of the
 * vanilla body parts, so Forge hides them together with the relevant armor slot. */
public final class SkullHussarArmorModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
        new ResourceLocation(NapoleonicFantasy.MOD_ID, "skull_hussar_armor"), "main");

    public SkullHussarArmorModel(ModelPart root) { super(root); }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.15f);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("shako_crown", CubeListBuilder.create().texOffs(0, 0)
            .addBox(-4.65f, -10.8f, -4.65f, 9.3f, 7.4f, 9.3f, new CubeDeformation(.08f)), PartPose.ZERO);
        head.addOrReplaceChild("shako_brim", CubeListBuilder.create().texOffs(0, 18)
            .addBox(-5.25f, -4.35f, -5.35f, 10.5f, 1.05f, 10.7f, new CubeDeformation(.05f)), PartPose.ZERO);
        head.addOrReplaceChild("skull_plate", CubeListBuilder.create().texOffs(26, 18)
            .addBox(-2.0f, -8.15f, -4.86f, 4.0f, 3.1f, .28f, new CubeDeformation(.03f)), PartPose.ZERO);
        head.addOrReplaceChild("plume", CubeListBuilder.create().texOffs(0, 30)
            .addBox(2.9f, -10.6f, -1.0f, 1.2f, 4.5f, 2.0f, new CubeDeformation(.15f)), PartPose.ZERO);

        PartDefinition body = root.getChild("body");
        body.addOrReplaceChild("red_collar", CubeListBuilder.create().texOffs(40, 0)
            .addBox(-4.25f, -4.5f, -2.35f, 8.5f, 2.3f, 1.25f, new CubeDeformation(.08f)), PartPose.ZERO);
        body.addOrReplaceChild("fur_collar", CubeListBuilder.create().texOffs(40, 8)
            .addBox(-4.9f, -3.55f, -2.65f, 9.8f, 1.0f, 1.35f, new CubeDeformation(.12f)), PartPose.ZERO);
        body.addOrReplaceChild("front_panel", CubeListBuilder.create().texOffs(0, 42)
            .addBox(-3.65f, -2.3f, -2.55f, 7.3f, 9.0f, .52f, new CubeDeformation(.06f)), PartPose.ZERO);
        body.addOrReplaceChild("sash", CubeListBuilder.create().texOffs(24, 42)
            .addBox(-4.25f, 2.9f, -2.7f, 8.5f, 1.2f, .7f, new CubeDeformation(.05f)), PartPose.ZERO);
        body.addOrReplaceChild("waist_pouch", CubeListBuilder.create().texOffs(42, 42)
            .addBox(2.0f, 4.0f, -3.0f, 2.9f, 3.0f, 1.15f, new CubeDeformation(.08f)), PartPose.ZERO);
        for (int i = 0; i < 5; i++) {
            body.addOrReplaceChild("silver_braid_" + i, CubeListBuilder.create().texOffs(56, 0)
                .addBox(-3.0f, -1.6f + i * 1.48f, -2.9f, 6.0f, .32f, .34f, new CubeDeformation(.02f)), PartPose.ZERO);
        }

        PartDefinition rightArm = root.getChild("right_arm");
        PartDefinition leftArm = root.getChild("left_arm");
        for (PartDefinition arm : new PartDefinition[]{rightArm, leftArm}) {
            arm.addOrReplaceChild("shoulder_epaulette", CubeListBuilder.create().texOffs(0, 58)
                .addBox(-3.0f, -2.8f, -2.45f, 6.0f, 1.0f, 4.9f, new CubeDeformation(.1f)), PartPose.ZERO);
            arm.addOrReplaceChild("red_cuff", CubeListBuilder.create().texOffs(22, 58)
                .addBox(-2.45f, 9.0f, -2.4f, 4.9f, 1.4f, 4.8f, new CubeDeformation(.08f)), PartPose.ZERO);
            arm.addOrReplaceChild("sleeve_braid", CubeListBuilder.create().texOffs(40, 58)
                .addBox(-2.5f, 1.0f, -2.3f, .35f, 7.0f, .35f, new CubeDeformation(.02f)), PartPose.ZERO);
        }
        for (PartDefinition leg : new PartDefinition[]{root.getChild("right_leg"), root.getChild("left_leg")}) {
            leg.addOrReplaceChild("trouser_side_stripe", CubeListBuilder.create().texOffs(52, 42)
                .addBox(-2.22f, .2f, -2.15f, .34f, 11.6f, .34f, new CubeDeformation(.02f)), PartPose.ZERO);
            leg.addOrReplaceChild("boot_cuff", CubeListBuilder.create().texOffs(0, 68)
                .addBox(-2.25f, 9.7f, -2.25f, 4.5f, 2.2f, 4.5f, new CubeDeformation(.08f)), PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 128, 128);
    }
}
