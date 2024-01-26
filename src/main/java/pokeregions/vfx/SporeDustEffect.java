package pokeregions.vfx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.FallingDustEffect;
import com.megacrit.cardcrawl.vfx.scene.CeilingDustCloudEffect;

public class SporeDustEffect extends AbstractGameEffect {
    private int count = 15;
    private float x;
    private float y;

    public SporeDustEffect(float x, float y) {
        setPosition(x, y);
    }

    private void setPosition(float x, float y) {
        this.x =  x + MathUtils.random(-40.0F, 40.0F) * Settings.scale;
        this.y =  y;
    }

    public void update() {
        if (this.count != 0) {
            int num = MathUtils.random(4, 8);
            this.count -= num;
            for (int i = 0; i < num; i++) {
                AbstractDungeon.effectsQueue.add(new FallingDustEffect(this.x, this.y));
                if (MathUtils.randomBoolean(0.8F))
                    AbstractDungeon.effectsQueue.add(new CeilingDustCloudEffect(this.x, this.y));
            }
            if (this.count <= 0)
                this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {}

    public void dispose() {}
}
