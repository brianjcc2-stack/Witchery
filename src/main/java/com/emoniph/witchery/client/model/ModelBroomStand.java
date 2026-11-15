package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelBroomStand extends ModelBase {

    public ModelRenderer base;
    public ModelRenderer postLeft;
    public ModelRenderer postRight;
    public ModelRenderer topBar;
    public ModelRenderer pegLeft;
    public ModelRenderer pegRight;

    public ModelBroomStand() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.base = new ModelRenderer(this, 0, 0);
        this.base.addBox(-7.0F, 22.0F, -3.0F, 14, 2, 6);

        this.postLeft = new ModelRenderer(this, 0, 10);
        this.postLeft.addBox(-6.0F, 4.0F, -1.0F, 2, 18, 2);

        this.postRight = new ModelRenderer(this, 0, 10);
        this.postRight.addBox(4.0F, 4.0F, -1.0F, 2, 18, 2);

        this.topBar = new ModelRenderer(this, 0, 32);
        this.topBar.addBox(-7.0F, 2.0F, -1.0F, 14, 2, 2);

        this.pegLeft = new ModelRenderer(this, 10, 10);
        this.pegLeft.addBox(-5.5F, 6.0F, 1.0F, 1, 1, 3);

        this.pegRight = new ModelRenderer(this, 10, 10);
        this.pegRight.addBox(4.5F, 6.0F, 1.0F, 1, 1, 3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.base.render(f5);
        this.postLeft.render(f5);
        this.postRight.render(f5);
        this.topBar.render(f5);
        this.pegLeft.render(f5);
        this.pegRight.render(f5);
    }
}

