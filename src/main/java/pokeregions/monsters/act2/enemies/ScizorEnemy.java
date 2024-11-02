package pokeregions.monsters.act2.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Scizor;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class ScizorEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(ScizorEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte BULLET_PUNCH = 0;
    private static final byte SWORDS_DANCE = 1;
    private static final byte AGILITY = 2;
    private static final byte NOTHING = 3;

    public final int STR = calcAscensionSpecial(4);
    public final int POWER_TRIGGER = 3;
    public final int AGILITY_MAX_USE = 2;
    private int agilityUses = 0;

    public static final String POWER_ID = makeID("Technician");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ScizorEnemy() {
        this(0.0f, 0.0f);
    }

    public ScizorEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 180.0f, 190.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Scizor/Scizor.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        setHp(calcAscensionTankiness(200));
        addMove(BULLET_PUNCH, Intent.ATTACK, calcAscensionDamage(10), 2);
        addMove(SWORDS_DANCE, Intent.BUFF);
        addMove(AGILITY, Intent.BUFF);
        addMove(NOTHING, Intent.NONE);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        CustomDungeon.playTempMusicInstantly("WildPokemon");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_TRIGGER, "repair") {

            private int numCardsToTrigger = POWER_TRIGGER;

            @Override
            public void onInitialApplication() {
                updateDescription();
            }

            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                if (this.amount > 0) {
                    this.amount--;
                    if (this.amount <= 0) {
                        flash();
                        this.amount = 0;
                        if (!owner.hasPower(StunMonsterPower.POWER_ID)) {
                            takeTurn();
                        }
                    } else {
                        flashWithoutSound();
                    }
                }
            }

            @Override
            public void onSpecificTrigger() {
                this.flash();
                numCardsToTrigger--;
                amount = numCardsToTrigger;
                updateDescription();
            }

            @Override
            public void atEndOfRound() {
                amount = numCardsToTrigger;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + numCardsToTrigger + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case BULLET_PUNCH: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
            case SWORDS_DANCE: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case AGILITY: {
                applyToTarget(this, this, new StrengthPower(this, STR / 2));
                AbstractPower power = this.getPower(POWER_ID);
                if (power != null) {
                    power.onSpecificTrigger();
                }
                agilityUses++;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(BULLET_PUNCH, MOVES[BULLET_PUNCH]);
        } else {
            if (!AbstractDungeon.actionManager.turnHasEnded) {
                setMoveShortcut(NOTHING);
                //remove the skip turn from the move history so we don't have to account for it
                this.moveHistory.remove(this.moveHistory.size() - 1);
            } else {
                if (lastMove(SWORDS_DANCE)) {
                    setMoveShortcut(BULLET_PUNCH, MOVES[BULLET_PUNCH]);
                } else if (lastMove(BULLET_PUNCH)) {
                    if (lastMoveBefore(SWORDS_DANCE) && agilityUses < AGILITY_MAX_USE) {
                        setMoveShortcut(AGILITY, MOVES[AGILITY]);
                    } else {
                        setMoveShortcut(SWORDS_DANCE, MOVES[SWORDS_DANCE]);
                    }
                } else {
                    setMoveShortcut(BULLET_PUNCH, MOVES[BULLET_PUNCH]);
                }
            }
        }
        createIntent();
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makeUIPath("Technician.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case SWORDS_DANCE: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case AGILITY: {
                Details powerDetail = new Details(this, STR / 2, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, -1, texture);
                details.add(powerDetail2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Scizor();
    }

}