package pokeregions.monsters.act1.allyPokemon;

import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.actions.GyaradosWaterfallAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.Wiz;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.applyToTarget;
import static pokeregions.util.Wiz.atb;

public class Gyarados extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Gyarados.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Gyarados(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 170.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gyarados/Gyarados.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_BUFF;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act1.Gyarados.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                atb(new GyaradosWaterfallAction(target, info, pokeregions.cards.pokemonAllyCards.act1.Gyarados.MOVE_1_STAMINA_HEAL, allyCard));
                break;
            }
            case MOVE_2: {
                atb(new SFXAction("ATTACK_PIERCING_WAIL"));
                if (Settings.FAST_MODE) {
                    atb(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.3F));
                } else {
                    atb(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 1.5F));
                }

                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new WeakPower(mo, pokeregions.cards.pokemonAllyCards.act1.Gyarados.MOVE_2_DEBUFF, false));
                    applyToTarget(mo, this, new VulnerablePower(mo, pokeregions.cards.pokemonAllyCards.act1.Gyarados.MOVE_2_DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                }
                break;
            }
        }
        postTurn();
    }

}