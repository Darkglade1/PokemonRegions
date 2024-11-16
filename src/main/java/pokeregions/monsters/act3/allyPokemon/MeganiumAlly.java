package pokeregions.monsters.act3.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.vfx.combat.HemokinesisEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Meganium;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class MeganiumAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Meganium.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public MeganiumAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Meganium/Meganium.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent, Meganium.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                atb(new VFXAction(new HemokinesisEffect(target.hb.cX, target.hb.cY, adp().hb.cX, adp().hb.cY), 0.5F));
                DamageInfo hploss = new DamageInfo(this, Meganium.MOVE_2_HP_LOSS, DamageInfo.DamageType.HP_LOSS);
                atb(new DamageAction(target, hploss, AbstractGameAction.AttackEffect.POISON));
                atb(new AddTemporaryHPAction(adp(), this, Meganium.MOVE_2_TEMP_HP));
                atb(new HealAction(adp(), this, Meganium.MOVE_2_HEAL));
                break;
            }
        }
        postTurn();
    }

}