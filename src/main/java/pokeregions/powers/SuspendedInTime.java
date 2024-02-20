package pokeregions.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pokeregions.PokemonRegions;
import pokeregions.actions.SuspendedInTimePlayCardAction;

import java.util.ArrayList;

import static pokeregions.util.Wiz.atb;

public class SuspendedInTime extends AbstractUnremovablePower {

    public static final String POWER_ID = PokemonRegions.makeID(SuspendedInTime.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private ArrayList<CardInfo> delayedCards = new ArrayList<>();
    public ArrayList<CardInfo> playingCards = new ArrayList<>(); //The cards to not delay

    public SuspendedInTime(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.loadRegion("time");
    }

    public void increment(AbstractCard c, AbstractMonster monster) {
        this.flash();
        int energy = c.energyOnUse;
        if (c.cost == -1) {
            energy = EnergyPanel.totalCount;
            AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
        }
        CardInfo cardInfo = new CardInfo(c.makeSameInstanceOf(), monster, energy);
        delayedCards.add(cardInfo);
        System.out.println(delayedCards);
        playingCards.clear();
    }

    public void playCards() {
        for (CardInfo cardInfo : delayedCards) {
            AbstractCard card = cardInfo.card;
            card.purgeOnUse = true;
            playingCards.add(cardInfo);
            atb(new SuspendedInTimePlayCardAction(card, cardInfo.target, false));
        }
        delayedCards.clear();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb, x, y, c);
        float drawScale = 0.20F;
        float offsetX1 = 100.0F * Settings.scale;
        float offsetX2 = -100.0F * Settings.scale;
        float offsetX3 = -300.0F * Settings.scale;
        float offsetY = 100.0F * Settings.scale;
        for (int i = 0; i < delayedCards.size(); i++) {
            AbstractCard card = delayedCards.get(i).card;
            card.drawScale = drawScale;
            card.current_x = AbstractDungeon.player.drawX + offsetX1;
            card.current_y = AbstractDungeon.player.drawY + offsetY * (i + 2);
            card.render(sb);
        }
    }

    public static class CardInfo {
        public AbstractCard card;
        public AbstractMonster target;
        public int energyOnUse;

        public CardInfo(AbstractCard card, AbstractMonster target, int energyOnUse) {
            this.card = card;
            this.target = target;
            this.energyOnUse = energyOnUse;
        }

        @Override
        public String toString() {
            return card.toString() + energyOnUse;
        }
    }
}