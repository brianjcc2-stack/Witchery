package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelHerbalistDesk extends ModelBase {

    public ModelRenderer tableTop;
    public ModelRenderer legFL;
    public ModelRenderer legFR;
    public ModelRenderer legBL;
    public ModelRenderer legBR;
    public ModelRenderer apron;
    public ModelRenderer mortar;
    public ModelRenderer pestle;
    public ModelRenderer scroll;
    public ModelRenderer bottle;
    public ModelRenderer cork;

    public ModelHerbalistDesk() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.tableTop = new ModelRenderer(this, 0, 0);
        this.tableTop.addBox(-8.0F, 10.0F, -8.0F, 16, 2, 16);

        this.legFL = new ModelRenderer(this, 0, 18);
        this.legFL.addBox(-7.0F, 12.0F, -7.0F, 2, 12, 2);

        this.legFR = new ModelRenderer(this, 0, 18);
        this.legFR.addBox(5.0F, 12.0F, -7.0F, 2, 12, 2);

        this.legBL = new ModelRenderer(this, 0, 18);
        this.legBL.addBox(-7.0F, 12.0F, 5.0F, 2, 12, 2);

        this.legBR = new ModelRenderer(this, 0, 18);
        this.legBR.addBox(5.0F, 12.0F, 5.0F, 2, 12, 2);

        this.apron = new ModelRenderer(this, 8, 18);
        this.apron.addBox(-6.0F, 12.0F, -6.0F, 12, 1, 12);

        // Mortar & Pestle
        this.mortar = new ModelRenderer(this, 0, 32);
        this.mortar.addBox(-5.0F, 7.0F, -3.0F, 3, 3, 3);

        this.pestle = new ModelRenderer(this, 0, 38);
        this.pestle.addBox(-4.0F, 5.0F, -2.5F, 1, 3, 1);
        this.pestle.rotateAngleZ = 0.25F;

        // Rolled scroll
        this.scroll = new ModelRenderer(this, 12, 32);
        this.scroll.addBox(2.0F, 9.0F, -4.0F, 2, 1, 6);

        // Small herb bottle
        this.bottle = new ModelRenderer(this, 28, 32);
        this.bottle.addBox(2.0F, 7.0F, 3.0F, 2, 3, 2);

        this.cork = new ModelRenderer(this, 28, 37);
        this.cork.addBox(2.5F, 6.0F, 3.5F, 1, 1, 1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.tableTop.render(f5);
        this.legFL.render(f5);
        this.legFR.render(f5);
        this.legBL.render(f5);
        this.legBR.render(f5);
        this.apron.render(f5);
        this.mortar.render(f5);
        this.pestle.render(f5);
        this.scroll.render(f5);
        this.bottle.render(f5);
        this.cork.render(f5);
    }
}

