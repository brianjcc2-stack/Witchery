package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.world.World;

import java.util.ArrayList;

public class RiteLumosMaxima extends Rite {
    
    public RiteLumosMaxima() {
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepLumosMaxima(this, initialStage));
    }

    private static class StepLumosMaxima extends RitualStep {
        private final RiteLumosMaxima rite;

        public StepLumosMaxima(RiteLumosMaxima rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            } else {
                if (!world.isRemote) {
                    int targetY = posY + 3;
                    if (world.isAirBlock(posX, targetY, posZ)) {
                        world.setBlock(posX, targetY, posZ, Witchery.Blocks.GLOW_GLOBE);
                        ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_LEVELUP, world, posX + 0.5, targetY + 0.5, posZ + 0.5, 1.0, 1.0, 16);
                        return RitualStep.Result.COMPLETED;
                    } else {
                        return RitualStep.Result.ABORTED_REFUND;
                    }
                }
                return RitualStep.Result.COMPLETED;
            }
        }
    }
}
