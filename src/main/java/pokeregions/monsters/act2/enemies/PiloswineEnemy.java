package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.Frozen;
import pokeregions.cards.pokemonAllyCards.act2.Piloswine;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.vfx.ColoredThrowDaggerEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class PiloswineEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(PiloswineEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SPEAR = 0;
    private static final byte BLIZZARD = 1;
    private static final byte AMNESIA = 2;

    public final int STATUS = calcAscensionSpecial(1);
    public final int BLOCK = 8;
    public final int STR = 1;
    public final int POWER_STR = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("ThickFat");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PiloswineEnemy() {
        this(0.0f, 0.0f);
    }

    public PiloswineEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 140.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Piloswine/Piloswine.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.1f);
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(64), calcAscensionTankiness(72));
        addMove(SPEAR, Intent.ATTACK, 3, 3);
        addMove(BLIZZARD, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        addMove(AMNESIA, Intent.DEFEND_BUFF);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_STR, "noPain") {
            @Override
            public void onExhaust(AbstractCard card) {
                this.flash();
                applyToTarget(owner, owner, new StrengthPower(owner, amount));
                applyToTarget(owner, owner, new LoseStrengthPower(owner, amount));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
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
            case SPEAR: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    atb(new VFXAction(new ColoredThrowDaggerEffect(adp().hb.cX, adp().hb.cY, Color.CYAN.cpy(), true)));
                    dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                }
                break;
            }
            case BLIZZARD: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                intoDrawMo(new Frozen(), STATUS);
                break;
            }
            case AMNESIA: {
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SPEAR) && !this.lastMoveBefore(SPEAR)) {
            possibilities.add(SPEAR);
        }
        if (!this.lastMove(BLIZZARD) && !this.lastMoveBefore(BLIZZARD)) {
            possibilities.add(BLIZZARD);
        }
        if (!this.lastMove(AMNESIA) && !this.lastMoveBefore(AMNESIA)) {
            possibilities.add(AMNESIA);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case BLIZZARD: {
                Details statusDetail = new Details(this, STATUS, FROZEN_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
            case AMNESIA: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Piloswine();
    }

}