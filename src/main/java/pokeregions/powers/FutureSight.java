package pokeregions.powers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;
import pokeregions.util.TexLoader;

import static pokeregions.PokemonRegions.makeUIPath;
import static pokeregions.util.Wiz.*;

public class FutureSight extends AbstractUnremovablePower implements NonStackablePower {
    public static final String POWER_ID = PokemonRegions.makeID(FutureSight.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int turns;

    private float flashTimer;
    private final Color flashColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);
    private boolean pulse;
    private final Texture texture;

    public FutureSight(AbstractCreature owner, int amount, int turns) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.turns = turns;
        this.loadRegion("mantra");
        texture = TexLoader.getTexture(makeUIPath("FutureSightFlash.png"));
        updateDescription();
    }

    @Override
    public void duringTurn() {
        if (turns == 1) {
            this.flash();
            atb(new DamageAction(adp(), new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
            makePowerRemovable(this);
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            turns--;
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        if (turns == 1) {
            beginPulse();
            this.description = DESCRIPTIONS[0] + DESCRIPTIONS[1] + DESCRIPTIONS[3] + amount + DESCRIPTIONS[4];
        } else if (turns == 2) {
            this.description = DESCRIPTIONS[0] + DESCRIPTIONS[2] + DESCRIPTIONS[3] + amount + DESCRIPTIONS[4];
        }
    }

    private void beginPulse() {
        this.flashTimer = 1.0F;
        this.pulse = true;
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb, x, y, c);
        this.renderPulseFlash(sb, x, y);
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        this.updatePulseFlash();
    }

    private void updatePulseFlash() {
        if (this.flashTimer != 0.0F) {
            this.flashTimer -= Gdx.graphics.getDeltaTime();
            if (this.flashTimer < 0.0F) {
                if (this.pulse) {
                    this.flashTimer = 1.0F;
                } else {
                    this.flashTimer = 0.0F;
                }
            }
        }

    }

    private void renderPulseFlash(SpriteBatch sb, float currentX, float currentY) {
        float tmp = Interpolation.exp10In.apply(0.0F, 4.0F, this.flashTimer / 2.0F);
        sb.setBlendFunction(770, 1);
        this.flashColor.a = this.flashTimer * 0.2F;
        sb.setColor(this.flashColor);
        float size = 84.0f;
        float origin = size / 2;
        float tmpX = currentX - origin;

        sb.draw(this.texture, tmpX, currentY - origin, origin, origin, size, size, Settings.scale + tmp, Settings.scale + tmp, 0.0f, 0, 0, (int)size, (int)size, false, false);
        sb.draw(this.texture, tmpX, currentY - origin, origin, origin, size, size, Settings.scale + tmp * 0.66F, Settings.scale + tmp * 0.66F, 0.0f, 0, 0, (int)size, (int)size, false, false);
        sb.draw(this.texture, tmpX, currentY - origin, origin, origin, size, size, Settings.scale + tmp / 3.0F, Settings.scale + tmp / 3.0F, 0.0f, 0, 0, (int)size, (int)size, false, false);
        sb.setBlendFunction(770, 771);
    }
}
