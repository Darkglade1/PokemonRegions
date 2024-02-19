package pokeregions.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pokeregions.util.AdditionalIntent;

import java.util.ArrayList;

import static pokeregions.util.Wiz.adp;

public abstract class AbstractMultiIntentMonster extends AbstractPokemonMonster {
    public ArrayList<EnemyMoveInfo> additionalMoves = new ArrayList<>();
    protected ArrayList<ArrayList<Byte>> additionalMovesHistory = new ArrayList<>();
    public ArrayList<AdditionalIntent> additionalIntents = new ArrayList<>();
    protected int numAdditionalMoves = 0;
    protected int maxAdditionalMoves = 0;

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {

    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    protected void applyPowersToAdditionalIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent) {
        if (additionalMove.baseDamage > -1) {
            int dmg = additionalMove.baseDamage;
            DamageInfo info = new DamageInfo(this, dmg);
            info.applyPowers(this, adp());

            dmg = info.output;

            additionalIntent.updateDamage(dmg);
        }
    }

    @Override
    public void rollMove() {
        additionalIntents.clear();
        additionalMoves.clear();
        this.getMove(AbstractDungeon.aiRng.random(99));
        for (int i = 0; i < numAdditionalMoves; i++) {
            getAdditionalMoves(AbstractDungeon.aiRng.random(99), i);
        }
    }

    public void getAdditionalMoves(int num, int whichMove) {

    }

    public void setAdditionalMoveShortcut(byte next, ArrayList<Byte> moveHistory, int position) {
        EnemyMoveInfo info = this.moves.get(next);
        AdditionalIntent additionalIntent = new AdditionalIntent(this, info, position);
        additionalIntents.add(additionalIntent);
        additionalMoves.add(info);
        moveHistory.add(next);
    }

    protected boolean lastMove(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.isEmpty()) {
            return false;
        } else {
            return (Byte)moveHistory.get(moveHistory.size() - 1) == move;
        }
    }

    protected boolean lastMoveBefore(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.isEmpty()) {
            return false;
        } else if (moveHistory.size() < 2) {
            return false;
        } else {
            return (Byte)moveHistory.get(moveHistory.size() - 2) == move;
        }
    }

    protected boolean lastTwoMoves(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.size() < 2) {
            return false;
        } else {
            return (Byte)moveHistory.get(moveHistory.size() - 1) == move && (Byte)moveHistory.get(moveHistory.size() - 2) == move;
        }
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        for (AdditionalIntent additionalIntent : additionalIntents) {
            if (additionalIntent != null && !this.hasPower(StunMonsterPower.POWER_ID)) {
                additionalIntent.update();
                if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic(RunicDome.ID) && this.intent != AbstractMonster.Intent.NONE && !Settings.hideCombatElements) {
                    additionalIntent.render(sb);
                }
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        if (!adp().hasRelic(RunicDome.ID) && !this.hasPower(StunMonsterPower.POWER_ID)) {
            if (tips.size() > 0) {
                for (int i = 0; i < additionalIntents.size(); i++) {
                    AdditionalIntent additionalIntent = additionalIntents.get(i);
                    if (!additionalIntent.transparent) {
                        this.tips.add(i + 1, additionalIntent.intentTip);
                    }
                }
            }
        }
    }

    //returns the highest damaging intent for compatibility with spot weakness/go for the eyes, etc.
    @Override
    public int getIntentBaseDmg() {
        int original = super.getIntentBaseDmg();
        if (original >= 0) {
            return original;
        }
        int maxDamage = original;
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            if (additionalIntent.baseDamage > maxDamage) {
                maxDamage = additionalIntent.baseDamage;
            }
        }
        return maxDamage;
    }

    public int getRealIntentBaseDmg() {
        return super.getIntentBaseDmg();
    }

    //returns the highest damaging intent for compatibility with spot weakness/go for the eyes, etc.
    @Override
    public int getIntentDmg() {
        int original = super.getIntentDmg();
        if (original >= 0) {
            return original;
        }
        int maxDamage = original;
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            if (additionalIntent.damage > maxDamage) {
                maxDamage = additionalIntent.damage;
            }
        }
        return maxDamage;
    }

    public int getRealIntentDmg() {
        return super.getIntentDmg();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            //that way they don't fade out after the monster takes its primary intent
            additionalIntent.usePrimaryIntentsColor = false;
        }
    }
}