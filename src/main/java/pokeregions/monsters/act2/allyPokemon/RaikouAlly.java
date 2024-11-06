package pokeregions.monsters.act2.allyPokemon;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Raikou;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.orbs.RaikouLightning;
import pokeregions.powers.AbstractLambdaPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class RaikouAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Raikou.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public static final String POWER_ID = makeID("Electroweb");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public RaikouAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Raikou/Raikou.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.MAGIC;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                for (int i = 0; i < Raikou.MOVE_1_EFFECT; i++) {
                    atb(new ChannelAction(new RaikouLightning(3)));
                }
                break;
            }
            case MOVE_2: {
                applyToTarget(adp(), this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, adp(), 1, "mastery") {

                    @Override
                    public void atStartOfTurn() {
                        this.flash();
                        atb(new GainEnergyAction(this.amount));
                    }

                    @Override
                    public void updateDescription() {
                        StringBuilder sb = new StringBuilder();
                        sb.append(POWER_DESCRIPTIONS[0]);

                        for(int i = 0; i < this.amount; ++i) {
                            sb.append("[E] ");
                        }

                        sb.append(LocalizedStrings.PERIOD);
                        this.description = sb.toString();
                    }
                });
                break;
            }
        }
        postTurn();
    }

}