package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RiteDimensionalAnchor extends Rite {
    private final int radius;

    public RiteDimensionalAnchor(int radius) {
        this.radius = radius;
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepDimensionalAnchor(this, initialStage));
    }

    private static class StepDimensionalAnchor extends RitualStep {
        private final RiteDimensionalAnchor rite;

        public StepDimensionalAnchor(RiteDimensionalAnchor rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            } else {
                if (!world.isRemote) {
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)(posX - this.rite.radius), (double)posY, (double)(posZ - this.rite.radius), (double)(posX + this.rite.radius + 1), (double)(posY + 3), (double)(posZ + this.rite.radius + 1));
                    List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds);
                    
                    for (EntityLivingBase entity : entities) {
                        entity.addPotionEffect(new PotionEffect(Witchery.Potions.ENDER_INHIBITION.id, 200, 0, true));
                    }

                    if (world.rand.nextInt(5) == 0) {
                        ParticleEffect.PORTAL.send(SoundEffect.NONE, world, posX + 0.5, posY + 0.5, posZ + 0.5, 1.0, 1.0, 16);
                    }
                }
                return RitualStep.Result.UPKEEP;
            }
        }
    }
}
