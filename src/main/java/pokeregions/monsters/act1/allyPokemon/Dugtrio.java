package pokeregions.monsters.act1.allyPokemon;

import pokeregions.BetterSpriterAnimation;
import pokeregions.CustomIntent.IntentEnums;
import pokeregions.PokemonRegions;
import pokeregions.actions.AllyDamageAllEnemiesAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.WaitEffect;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.BetterDiscardPileToHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.applyToTarget;
import static pokeregions.util.Wiz.atb;

public class Dugtrio extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Dugtrio.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Dugtrio(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Dugtrio/Dugtrio.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = IntentEnums.MASS_ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, pokeregions.cards.pokemonAllyCards.act1.Dugtrio.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                runAnim("Excavate");
                Wiz.playAudio(ProAudio.BURROW);
                atb(new VFXAction(new WaitEffect(), 1.0f));
                atb(new BetterDiscardPileToHandAction(pokeregions.cards.pokemonAllyCards.act1.Dugtrio.CARDS));
                break;
            }
            case MOVE_2: {
                Wiz.playAudio(ProAudio.EARTHQUAKE);
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.XLONG, false);
                atb(new VFXAction(new WaitEffect(), 0.3f));
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new VulnerablePower(mo, pokeregions.cards.pokemonAllyCards.act1.Dugtrio.DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                }
                break;
            }
        }
        postTurn();
    }

}