package pokeregions.monsters.act3.allyPokemon;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Breloom;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.FiredUp;
import pokeregions.vfx.ThrowEffect;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class BreloomAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Breloom.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public static final String POWER_ID = makeID("SwordsDance");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public BreloomAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Breloom/Breloom.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Breloom.MOVE_2_DAMAGE, Breloom.MOVE_2_HITS);
        defaultMove = MOVE_2;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, adp(), Breloom.MOVE_1_EFFECT, "painfulStabs", 99) {
                    @Override
                    public float atDamageGive(float damage, DamageInfo.DamageType type) {
                        if (type == DamageInfo.DamageType.NORMAL) {
                            return damage * (1 + ((float)amount / 100));
                        } else {
                            return damage;
                        }
                    }
                    @Override
                    public void onPokemonSwitch(AbstractMonster pokemon) {
                        pokemon.addPower(new FiredUp(pokemon, amount));
                        AbstractDungeon.onModifyPower();
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                    }
                });
                applyToTarget(this, this, new FiredUp(this, Breloom.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    float duration = 0.5f;
                    atb(new VFXAction(ThrowEffect.throwEffect("BulletSeed.png", 1.0f, this.hb, target.hb, Color.GREEN.cpy(), duration), duration));
                    dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
        }
        postTurn();
    }

}