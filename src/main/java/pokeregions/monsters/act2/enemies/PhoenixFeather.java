package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class PhoenixFeather extends AbstractPokemonMonster
{
    public static final String ID = makeID(PhoenixFeather.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final byte NOTHING = 0;
    public static final byte BUFF = 1;
    public final int HEAL = 15;
    private final AbstractMonster summoner;

    public PhoenixFeather() {
        this(0.0f, 0.0f, null);
    }

    public PhoenixFeather(final float x, final float y, AbstractMonster summoner) {
        super(NAME, ID, 140, 0.0F, 0, 70.0f, 70.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("HoOh/Feather/Feather.scml"));
        this.summoner = summoner;
        setHp(calcAscensionTankiness(20));
        addMove(NOTHING, Intent.NONE);
        addMove(BUFF, Intent.BUFF);
        isCatchable = false;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        rollMove();
        createIntent();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case BUFF: {
                if (summoner != null) {
                    atb(new HealAction(summoner, this, HEAL));
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove && AbstractDungeon.ascensionLevel < 19) {
            this.setMove(NOTHING, Intent.NONE);
        } else {
            setMoveShortcut(BUFF);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case BUFF: {
                Details healDetail = new Details(this, HEAL, HEAL_TEXTURE);
                details.add(healDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

}