package pokeregions.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.WebLineEffect;
import com.megacrit.cardcrawl.vfx.combat.WebParticleEffect;

public class AllyWebEffect extends AbstractGameEffect {
    private float timer = 0.0F;
    private int count = 0;
    private AbstractCreature target;
    private float srcX;
    private float srcY;

    public AllyWebEffect(AbstractCreature target, float srcX, float srcY) {
        this.target = target;
        this.srcX = srcX;
        this.srcY = srcY;
        this.duration = 1.0F;
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F) {
            this.timer += 0.1F;
            switch (this.count) {
                case 0:
                    AbstractDungeon.effectsQueue.add(new WebLineEffect(this.srcX, this.srcY, false));
                    AbstractDungeon.effectsQueue.add(new WebLineEffect(this.srcX, this.srcY, false));
                    AbstractDungeon.effectsQueue.add(new WebParticleEffect(this.target.hb.cX - 90.0F * Settings.scale, this.target.hb.cY - 10.0F * Settings.scale));
                    break;
                case 1:
                    AbstractDungeon.effectsQueue.add(new WebLineEffect(this.srcX, this.srcY, false));
                    AbstractDungeon.effectsQueue.add(new WebLineEffect(this.srcX, this.srcY, false));
                    break;
                case 2:
                    AbstractDungeon.effectsQueue.add(new WebLineEffect(this.srcX, this.srcY, false));
                    AbstractDungeon.effectsQueue.add(new WebLineEffect(this.srcX, this.srcY, false));
                    AbstractDungeon.effectsQueue.add(new WebParticleEffect(this.target.hb.cX + 70.0F * Settings.scale, this.target.hb.cY + 80.0F * Settings.scale));
                case 3:
                default:
                    break;
                case 4:
                    AbstractDungeon.effectsQueue.add(new WebParticleEffect(this.target.hb.cX + 30.0F * Settings.scale, this.target.hb.cY - 100.0F * Settings.scale));
            }

            ++this.count;
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
