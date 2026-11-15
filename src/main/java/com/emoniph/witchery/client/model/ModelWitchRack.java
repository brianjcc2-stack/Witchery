package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelWitchRack extends ModelBase {

    public ModelRenderer baseLeg1;
    public ModelRenderer baseLeg2;
    public ModelRenderer pole;
    public ModelRenderer hookF;
    public ModelRenderer hookFTip;
    public ModelRenderer hookB;
    public ModelRenderer hookBTip;
    public ModelRenderer hookL;
    public ModelRenderer hookLTip;
    public ModelRenderer hookR;
    public ModelRenderer hookRTip;
    public ModelRenderer hatBrim;
    public ModelRenderer hatCone;
    public ModelRenderer hatTip;

    public ModelWitchRack() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.baseLeg1 = new ModelRenderer(this, 0, 0);
        this.baseLeg1.addBox(-6.0F, 22.0F, -1.0F, 12, 2, 2);

        this.baseLeg2 = new ModelRenderer(this, 0, 5);
        this.baseLeg2.addBox(-1.0F, 22.0F, -6.0F, 2, 2, 12);

        this.pole = new ModelRenderer(this, 0, 20);
        this.pole.addBox(-1.0F, -2.0F, -1.0F, 2, 24, 2);

        this.hookF = new ModelRenderer(this, 10, 20);
        this.hookF.addBox(-0.5F, -1.0F, -3.0F, 1, 1, 2);
        this.hookFTip = new ModelRenderer(this, 10, 24);
        this.hookFTip.addBox(-0.5F, -3.0F, -3.0F, 1, 2, 1);

        this.hookB = new ModelRenderer(this, 10, 20);
        this.hookB.addBox(-0.5F, -1.0F, 1.0F, 1, 1, 2);
        this.hookBTip = new ModelRenderer(this, 10, 24);
        this.hookBTip.addBox(-0.5F, -3.0F, 2.0F, 1, 2, 1);

        this.hookL = new ModelRenderer(this, 15, 20);
        this.hookL.addBox(-3.0F, -1.0F, -0.5F, 2, 1, 1);
        this.hookLTip = new ModelRenderer(this, 10, 24);
        this.hookLTip.addBox(-3.0F, -3.0F, -0.5F, 1, 2, 1);

        this.hookR = new ModelRenderer(this, 15, 20);
        this.hookR.addBox(1.0F, -1.0F, -0.5F, 2, 1, 1);
        this.hookRTip = new ModelRenderer(this, 10, 24);
        this.hookRTip.addBox(2.0F, -3.0F, -0.5F, 1, 2, 1);

        // Hanging witch hat
        this.hatBrim = new ModelRenderer(this, 28, 0);
        this.hatBrim.addBox(2.0F, -2.0F, -2.5F, 5, 1, 5);
        this.hatBrim.rotateAngleZ = -0.2F;

        this.hatCone = new ModelRenderer(this, 28, 7);
        this.hatCone.addBox(3.0F, -5.0F, -1.5F, 3, 3, 3);
        this.hatCone.rotateAngleZ = -0.2F;

        this.hatTip = new ModelRenderer(this, 28, 14);
        this.hatTip.addBox(3.5F, -7.0F, -1.0F, 2, 2, 2);
        this.hatTip.rotateAngleZ = -0.2F;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.baseLeg1.render(f5);
        this.baseLeg2.render(f5);
        this.pole.render(f5);
        this.hookF.render(f5);
        this.hookFTip.render(f5);
        this.hookB.render(f5);
        this.hookBTip.render(f5);
        this.hookL.render(f5);
        this.hookLTip.render(f5);
        this.hookR.render(f5);
        this.hookRTip.render(f5);
        this.hatBrim.render(f5);
        this.hatCone.render(f5);
        this.hatTip.render(f5);
    }
}

