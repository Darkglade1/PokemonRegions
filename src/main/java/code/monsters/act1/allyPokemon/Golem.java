package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.CustomIntent.IntentEnums;
import code.PokemonRegions;
import code.actions.AllyDamageAllEnemiesAction;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.util.ProAudio;
import code.util.Wiz;
import code.vfx.WaitEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CardStrings;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class Golem extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Golem.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Golem(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Golem/Golem.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEFEND;
        move2Intent = IntentEnums.MASS_ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, code.cards.pokemonAllyCards.Golem.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                block(adp(), code.cards.pokemonAllyCards.Golem.MOVE_1_BLOCK);
                break;
            }
            case MOVE_2: {
                Wiz.playAudio(ProAudio.EARTHQUAKE);
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.XLONG, false);
                atb(new VFXAction(new WaitEffect(), 0.3f));
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            }
        }
        postTurn();
    }

}