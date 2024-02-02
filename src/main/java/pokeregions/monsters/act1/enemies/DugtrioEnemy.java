package pokeregions.monsters.act1.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.Dugtrio;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.SandVeil;
import pokeregions.util.Details;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.WaitEffect;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.PokemonRegions.STRENGTH_TEXTURE;
import static pokeregions.util.Wiz.*;

public class DugtrioEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(DugtrioEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DIG = 0;
    private static final byte FURY_SWIPES = 1;

    public final int BLOCK = 9;
    public final int STR = 2;
    public final int FORTIFY_DAMAGE_REDUCTION = calcAscensionSpecial(50);
    public boolean burrowed = false;

    public DugtrioEnemy() {
        this(0.0f, 0.0f);
    }

    public DugtrioEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 140.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Dugtrio/Dugtrio.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(50), calcAscensionTankiness(56));
        addMove(DIG, Intent.DEFEND_BUFF);
        addMove(FURY_SWIPES, Intent.ATTACK, calcAscensionDamage(5), 2);

        Player.PlayerListener listener = new DugtrioListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new SandVeil(this, FORTIFY_DAMAGE_REDUCTION));
        runAnim("Dig");
        Wiz.playAudio(ProAudio.BURROW);
        this.burrowed = true;
        block(this, BLOCK);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case DIG: {
                runAnim("Dig");
                Wiz.playAudio(ProAudio.BURROW);
                this.burrowed = true;
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case FURY_SWIPES: {
                if (this.burrowed) {
                    runAnim("Emerge");
                    this.burrowed = false;
                    atb(new VFXAction(new WaitEffect(), 0.2f));
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        useFastAttackAnimation();
                        this.isDone = true;
                    }
                });
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                    } else {
                        dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_VERTICAL);
                    }
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
       if (lastMove(FURY_SWIPES)) {
            setMoveShortcut(DIG, MOVES[DIG]);
        } else {
            setMoveShortcut(FURY_SWIPES, MOVES[FURY_SWIPES]);
        }
        super.postGetMove();
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        AbstractDungeon.onModifyPower();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case DIG: {
                Details blockDetails = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetails);
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Dugtrio();
    }

    public static class DugtrioListener implements Player.PlayerListener {

        private final DugtrioEnemy character;

        public DugtrioListener(DugtrioEnemy character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (character.burrowed) {
                if (!animation.name.equals("DigIdle")) {
                    ((BetterSpriterAnimation)character.animation).myPlayer.setAnimation("DigIdle");
                }
            } else {
                if (!animation.name.equals("Idle")) {
                    character.resetAnimation();
                }
            }
        }

        //UNUSED
        public void animationChanged(Animation var1, Animation var2){
        }

        //UNUSED
        public void preProcess(Player var1){
        }

        //UNUSED
        public void postProcess(Player var1){
        }

        //UNUSED
        public void mainlineKeyChanged(com.brashmonkey.spriter.Mainline.Key var1, com.brashmonkey.spriter.Mainline.Key var2){
        }
    }

}