package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class PhoenixFeather extends AbstractPokemonMonster
{
    public static final String ID = makeID(PhoenixFeather.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    public static final byte BUFF = 0;
    public final int REGEN = calcAscensionSpecial(4);

    private final AbstractMonster summoner;

    public PhoenixFeather() {
        this(0.0f, 0.0f, null);
    }

    public PhoenixFeather(final float x, final float y, AbstractMonster summoner) {
        super(NAME, ID, 140, 0.0F, 0, 70.0f, 70.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("HoOh/Feather/Feather.scml"));
        this.summoner = summoner;
        setHp(calcAscensionTankiness(40));
        addMove(BUFF, Intent.BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
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
                    applyToTarget(summoner, this, new RegenerateMonsterPower(summoner, REGEN));
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(BUFF);
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case BUFF: {
                Details powerDetail = new Details(this, REGEN, REGEN_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

}