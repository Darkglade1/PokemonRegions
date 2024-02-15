package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import basemod.cardmods.EtherealMod;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.purple.Wish;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.DamageUpMod;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class Mossdeep extends PhasedEvent {
    public static final String ID = makeID("Mossdeep");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int DAMAGE_BONUS = 4;
    private static final float MAX_HEALTH_LOSS_PERCENTAGE = 0.06F;
    private static final float MAX_HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.08F;
    private final int maxHPCost;

    public Mossdeep() {
        super(ID, title, PokemonRegions.makeEventPath("Mossdeep.png"));
        this.noCardsInRewards = true;
        if (AbstractDungeon.ascensionLevel < 15) {
            maxHPCost = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HEALTH_LOSS_PERCENTAGE);
        } else {
            maxHPCost = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + DAMAGE_BONUS + OPTIONS[3], "g"), (i)->{
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (AbstractCard card : adp().masterDeck.group) {
                        if (card.baseDamage > 0 && !card.cardID.equals(Wish.ID)) {
                            group.addToBottom(card);
                        }
                    }
                    group.sortAlphabetically(true);
                    group.sortByRarityPlusStatusCardType(false);
                    if (group.size() > 0) {
                        AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[2] + DAMAGE_BONUS + OPTIONS[3], false);
                    }
                    transitionKey("Take");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + maxHPCost + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6], "g"), (i)->{
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    adp().decreaseMaxHealth(maxHPCost);
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

                    RewardItem reward = new RewardItem();
                    for(int counter = 0; counter < reward.cards.size(); counter++) {
                        AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE).makeCopy();
                        boolean containsDupe = true;
                        while(containsDupe) {
                            containsDupe = false;
                            for (AbstractCard c : group.group) {
                                if (c.cardID.equals(card.cardID)) {
                                    containsDupe = true;
                                    card = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE).makeCopy();
                                    break;
                                }
                            }
                        }
                        if (group.contains(card)) {
                            --counter;
                        } else {
                            group.addToBottom(card);
                        }
                    }

                    ArrayList<AbstractCard> newCards = new ArrayList<>();
                    for (AbstractCard c : group.group) {
                        UnlockTracker.markCardAsSeen(c.cardID);
                        newCards.add(c);
                    }
                    reward.cards = newCards;
                    AbstractDungeon.getCurrRoom().addCardReward(reward);
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                    AbstractDungeon.combatRewardScreen.open();
                    transitionKey("Guard");
                }));

        registerPhase("Take", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Guard", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                CardModifierManager.addModifier(c, new EtherealMod());
                CardModifierManager.addModifier(c, new DamageUpMod(DAMAGE_BONUS));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}