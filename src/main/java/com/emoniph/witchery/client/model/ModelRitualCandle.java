package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelRitualCandle extends ModelBase {

    public ModelRenderer baseDish;
    public ModelRenderer candleBody;
    public ModelRenderer drip1;
    public ModelRenderer drip2;
    public ModelRenderer wick;

    public ModelRitualCandle() {
        this.textureWidth = 32;
        this.textureHeight = 32;

        this.baseDish = new ModelRenderer(this, 8, 24);
        this.baseDish.addBox(-3.0F, 23.0F, -3.0F, 6, 1, 6);

        this.candleBody = new ModelRenderer(this, 0, 0);
        this.candleBody.addBox(-1.5F, 11.0F, -1.5F, 3, 12, 3);

        this.drip1 = new ModelRenderer(this, 0, 20);
        this.drip1.addBox(-2.0F, 14.0F, -0.5F, 1, 5, 1);

        this.drip2 = new ModelRenderer(this, 0, 20);
        this.drip2.addBox(1.0F, 16.0F, -1.0F, 1, 4, 1);

        this.wick = new ModelRenderer(this, 0, 0);
        this.wick.addBox(-0.5F, 9.0F, -0.5F, 1, 2, 1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.baseDish.render(f5);
        this.candleBody.render(f5);
        this.drip1.render(f5);
        this.drip2.render(f5);
        this.wick.render(f5);
    }
}

