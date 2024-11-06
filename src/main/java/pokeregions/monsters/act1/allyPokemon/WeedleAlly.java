package pokeregions.monsters.act1.allyPokemon;

import com.badlogic.gdx.math.MathUtils;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Weedle;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.ToxicPower;
import pokeregions.vfx.AllyWebEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class WeedleAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Weedle.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public WeedleAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 110.0f, 80.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Weedle/Weedle.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
        move2Intent = Intent.ATTACK_DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Weedle.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                atb(new SFXAction("ATTACK_MAGIC_FAST_3", MathUtils.random(0.88F, 0.92F), true));
                atb(new VFXAction(new AllyWebEffect(target, this.hb.cX - 70.0F * Settings.scale, this.hb.cY + 10.0F * Settings.scale), 1.0f));
                applyToTarget(target, this, new StrengthPower(target, -Weedle.MOVE_1_EFFECT));
                if (!target.hasPower(ArtifactPower.POWER_ID)) {
                    applyToTarget(target, this, new GainStrengthPower(target, Weedle.MOVE_1_EFFECT));
                }
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.POISON);
                applyToTarget(target, this, new ToxicPower(target, Weedle.MOVE_2_TOXIC));
                break;
            }
        }
        postTurn();
    }

}