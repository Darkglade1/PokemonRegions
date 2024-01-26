package pokeregions.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class WaterGunEffect extends AbstractGameEffect {
    private AbstractCreature target;
    private float srcX;
    private float srcY;

    public WaterGunEffect(AbstractCreature target, float srcX, float srcY) {
        this.target = target;
        this.srcX = srcX;
        this.srcY = srcY;
        this.duration = 1.0F;
    }

    public void update() {
        AbstractDungeon.effectsQueue.add(new WaterLineEffect(this.srcX, this.srcY, false));
        //AbstractDungeon.effectsQueue.add(new WebParticleEffect(this.target.hb.cX - 90.0F * Settings.scale, this.target.hb.cY - 10.0F * Settings.scale));
        this.isDone = true;

    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
