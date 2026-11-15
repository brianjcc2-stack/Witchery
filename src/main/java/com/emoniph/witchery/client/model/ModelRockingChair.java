package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelRockingChair extends ModelBase {

    public ModelRenderer runnerL;
    public ModelRenderer runnerR;
    public ModelRenderer runnerTipFL;
    public ModelRenderer runnerTipFR;
    public ModelRenderer runnerTipBL;
    public ModelRenderer runnerTipBR;
    public ModelRenderer legFL;
    public ModelRenderer legFR;
    public ModelRenderer legBL;
    public ModelRenderer legBR;
    public ModelRenderer seat;
    public ModelRenderer backPostL;
    public ModelRenderer backPostR;
    public ModelRenderer backTop;
    public ModelRenderer backSlat1;
    public ModelRenderer backSlat2;
    public ModelRenderer backSlat3;
    public ModelRenderer armL;
    public ModelRenderer armLSupp;
    public ModelRenderer armR;
    public ModelRenderer armRSupp;

    public ModelRockingChair() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        // Rocking Runners
        this.runnerL = new ModelRenderer(this, 0, 0);
        this.runnerL.addBox(-6.0F, 23.0F, -7.0F, 2, 1, 14);

        this.runnerR = new ModelRenderer(this, 0, 0);
        this.runnerR.addBox(4.0F, 23.0F, -7.0F, 2, 1, 14);

        this.runnerTipFL = new ModelRenderer(this, 0, 16);
        this.runnerTipFL.addBox(-6.0F, 22.0F, -8.0F, 2, 1, 1);

        this.runnerTipFR = new ModelRenderer(this, 0, 16);
        this.runnerTipFR.addBox(4.0F, 22.0F, -8.0F, 2, 1, 1);

        this.runnerTipBL = new ModelRenderer(this, 0, 16);
        this.runnerTipBL.addBox(-6.0F, 22.0F, 7.0F, 2, 1, 1);

        this.runnerTipBR = new ModelRenderer(this, 0, 16);
        this.runnerTipBR.addBox(4.0F, 22.0F, 7.0F, 2, 1, 1);

        // Legs
        this.legFL = new ModelRenderer(this, 0, 20);
        this.legFL.addBox(-5.5F, 15.0F, -4.0F, 1, 8, 1);

        this.legFR = new ModelRenderer(this, 0, 20);
        this.legFR.addBox(4.5F, 15.0F, -4.0F, 1, 8, 1);

        this.legBL = new ModelRenderer(this, 0, 20);
        this.legBL.addBox(-5.5F, 15.0F, 3.0F, 1, 8, 1);

        this.legBR = new ModelRenderer(this, 0, 20);
        this.legBR.addBox(4.5F, 15.0F, 3.0F, 1, 8, 1);

        // Seat
        this.seat = new ModelRenderer(this, 8, 20);
        this.seat.addBox(-6.0F, 14.0F, -5.0F, 12, 1, 10);

        // Backrest
        this.backPostL = new ModelRenderer(this, 0, 32);
        this.backPostL.addBox(-5.5F, 0.0F, 4.0F, 1, 14, 1);

        this.backPostR = new ModelRenderer(this, 0, 32);
        this.backPostR.addBox(4.5F, 0.0F, 4.0F, 1, 14, 1);

        this.backTop = new ModelRenderer(this, 8, 32);
        this.backTop.addBox(-6.0F, -1.0F, 4.0F, 12, 2, 1);

        this.backSlat1 = new ModelRenderer(this, 6, 32);
        this.backSlat1.addBox(-3.0F, 1.0F, 4.0F, 1, 13, 1);

        this.backSlat2 = new ModelRenderer(this, 6, 32);
        this.backSlat2.addBox(-0.5F, 1.0F, 4.0F, 1, 13, 1);

        this.backSlat3 = new ModelRenderer(this, 6, 32);
        this.backSlat3.addBox(2.0F, 1.0F, 4.0F, 1, 13, 1);

        // Armrests
        this.armL = new ModelRenderer(this, 36, 0);
        this.armL.addBox(-6.5F, 9.0F, -4.5F, 2, 1, 9);

        this.armLSupp = new ModelRenderer(this, 0, 20);
        this.armLSupp.addBox(-6.0F, 10.0F, -4.0F, 1, 4, 1);

        this.armR = new ModelRenderer(this, 36, 0);
        this.armR.addBox(4.5F, 9.0F, -4.5F, 2, 1, 9);

        this.armRSupp = new ModelRenderer(this, 0, 20);
        this.armRSupp.addBox(5.0F, 10.0F, -4.0F, 1, 4, 1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.runnerL.render(f5);
        this.runnerR.render(f5);
        this.runnerTipFL.render(f5);
        this.runnerTipFR.render(f5);
        this.runnerTipBL.render(f5);
        this.runnerTipBR.render(f5);
        this.legFL.render(f5);
        this.legFR.render(f5);
        this.legBL.render(f5);
        this.legBR.render(f5);
        this.seat.render(f5);
        this.backPostL.render(f5);
        this.backPostR.render(f5);
        this.backTop.render(f5);
        this.backSlat1.render(f5);
        this.backSlat2.render(f5);
        this.backSlat3.render(f5);
        this.armL.render(f5);
        this.armLSupp.render(f5);
        this.armR.render(f5);
        this.armRSupp.render(f5);
    }
}

