package com.emoniph.witchery.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelPotionShelf extends ModelBase {

    public ModelRenderer backPanel;
    public ModelRenderer bottomShelf;
    public ModelRenderer middleShelf;
    public ModelRenderer topCrown;
    public ModelRenderer leftPanel;
    public ModelRenderer rightPanel;
    public ModelRenderer bottle1;
    public ModelRenderer bottle2;
    public ModelRenderer bottle3;
    public ModelRenderer bottle4;
    public ModelRenderer jar1;

    public ModelPotionShelf() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.backPanel = new ModelRenderer(this, 0, 0);
        this.backPanel.addBox(-7.0F, 8.0F, 5.0F, 14, 16, 2);

        this.bottomShelf = new ModelRenderer(this, 0, 20);
        this.bottomShelf.addBox(-7.0F, 22.0F, 0.0F, 14, 2, 6);

        this.middleShelf = new ModelRenderer(this, 0, 20);
        this.middleShelf.addBox(-7.0F, 14.0F, 0.0F, 14, 2, 6);

        this.topCrown = new ModelRenderer(this, 0, 20);
        this.topCrown.addBox(-7.0F, 7.0F, 0.0F, 14, 2, 6);

        this.leftPanel = new ModelRenderer(this, 0, 30);
        this.leftPanel.addBox(-7.0F, 8.0F, 0.0F, 1, 15, 6);

        this.rightPanel = new ModelRenderer(this, 0, 30);
        this.rightPanel.addBox(6.0F, 8.0F, 0.0F, 1, 15, 6);

        // Decorative bottles and jars
        this.bottle1 = new ModelRenderer(this, 40, 0);
        this.bottle1.addBox(-5.0F, 18.0F, 2.0F, 2, 4, 2);

        this.bottle2 = new ModelRenderer(this, 40, 8);
        this.bottle2.addBox(-1.0F, 17.0F, 1.5F, 2, 5, 2);

        this.bottle3 = new ModelRenderer(this, 40, 16);
        this.bottle3.addBox(3.0F, 19.0F, 2.0F, 2, 3, 2);

        this.bottle4 = new ModelRenderer(this, 40, 0);
        this.bottle4.addBox(-4.0F, 10.0F, 2.0F, 2, 4, 2);

        this.jar1 = new ModelRenderer(this, 50, 0);
        this.jar1.addBox(1.0F, 10.0F, 1.5F, 3, 4, 3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.backPanel.render(f5);
        this.bottomShelf.render(f5);
        this.middleShelf.render(f5);
        this.topCrown.render(f5);
        this.leftPanel.render(f5);
        this.rightPanel.render(f5);
        this.bottle1.render(f5);
        this.bottle2.render(f5);
        this.bottle3.render(f5);
        this.bottle4.render(f5);
        this.jar1.render(f5);
    }
}

