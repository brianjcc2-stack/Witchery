package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSkullCandle extends ModelBase {

    public ModelRenderer cranium;
    public ModelRenderer jaw;
    public ModelRenderer waxPool;
    public ModelRenderer dripFront;
    public ModelRenderer candle;
    public ModelRenderer wick;

    public ModelSkullCandle() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.cranium = new ModelRenderer(this, 0, 0);
        this.cranium.addBox(-4.0F, 16.0F, -4.0F, 8, 7, 8);

        this.jaw = new ModelRenderer(this, 32, 0);
        this.jaw.addBox(-2.5F, 22.0F, -3.5F, 5, 2, 3);

        this.waxPool = new ModelRenderer(this, 0, 16);
        this.waxPool.addBox(-2.5F, 15.0F, -2.5F, 5, 1, 5);

        this.dripFront = new ModelRenderer(this, 20, 16);
        this.dripFront.addBox(-1.0F, 16.0F, -4.5F, 1, 4, 1);

        this.candle = new ModelRenderer(this, 26, 16);
        this.candle.addBox(-1.0F, 9.0F, -1.0F, 2, 6, 2);

        this.wick = new ModelRenderer(this, 36, 16);
        this.wick.addBox(-0.5F, 7.0F, -0.5F, 1, 2, 1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.cranium.render(f5);
        this.jaw.render(f5);
        this.waxPool.render(f5);
        this.dripFront.render(f5);
        this.candle.render(f5);
        this.wick.render(f5);
    }
}

