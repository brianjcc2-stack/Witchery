package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelApothecaryJar extends ModelBase {

    public ModelRenderer jarBase;
    public ModelRenderer jarGlass;
    public ModelRenderer jarNeck;
    public ModelRenderer jarLid;

    public ModelApothecaryJar() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        // Base plate of jar (8x1x8)
        this.jarBase = new ModelRenderer(this, 0, 0);
        this.jarBase.addBox(-4.0F, 23.0F, -4.0F, 8, 1, 8);

        // Glass body (10x11x10)
        this.jarGlass = new ModelRenderer(this, 0, 10);
        this.jarGlass.addBox(-5.0F, 12.0F, -5.0F, 10, 11, 10);

        // Jar neck (6x2x6)
        this.jarNeck = new ModelRenderer(this, 0, 32);
        this.jarNeck.addBox(-3.0F, 10.0F, -3.0F, 6, 2, 6);

        // Cork / Wooden stopper (7x2x7)
        this.jarLid = new ModelRenderer(this, 0, 42);
        this.jarLid.addBox(-3.5F, 8.0F, -3.5F, 7, 2, 7);
    }

    public void renderBase(float f5) {
        this.jarBase.render(f5);
        this.jarNeck.render(f5);
        this.jarLid.render(f5);
    }

    public void renderGlass(float f5) {
        this.jarGlass.render(f5);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.renderBase(f5);
        this.renderGlass(f5);
    }
}
