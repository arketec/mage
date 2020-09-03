package mage.cards.n;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.LoyaltyAbility;
import mage.abilities.common.PlaneswalkerEntersWithLoyaltyCountersAbility;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.PermanentsOnBattlefieldCount;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.LookLibraryAndPickControllerEffect;
import mage.constants.*;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.filter.FilterCard;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.permanent.PermanentIdPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.KorWarriorToken;
import mage.game.permanent.token.Token;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreatureOrPlaneswalker;

/**
 * @author TheElk801
 */
public final class NahiriHeirOfTheAncients extends CardImpl {

    private static final FilterCard filter = new FilterCard("Warrior or Equipment card");

    static {
        filter.add(Predicates.or(
                SubType.WARRIOR.getPredicate(),
                SubType.EQUIPMENT.getPredicate()
        ));
    }

    private static final FilterPermanent filter2 = new FilterControlledPermanent(SubType.EQUIPMENT);
    private static final DynamicValue xValue = new PermanentsOnBattlefieldCount(filter2, 2);

    public NahiriHeirOfTheAncients(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.PLANESWALKER}, "{2}{R}{W}");

        this.addSuperType(SuperType.LEGENDARY);
        this.subtype.add(SubType.NAHIRI);
        this.addAbility(new PlaneswalkerEntersWithLoyaltyCountersAbility(4));

        // +1: Create a 1/1 white Kor Warrior creature token. You may attach an Equipment you control to it.
        this.addAbility(new LoyaltyAbility(new NahiriHeirOfTheAncientsEffect(), 1));

        // −2: Look at the top six cards of your library. You may reveal a Warrior or Equipment card from among them and put it into your hand. Put the rest on the bottom of your library in a random order.
        this.addAbility(new LoyaltyAbility(new LookLibraryAndPickControllerEffect(
                StaticValue.get(6), false, StaticValue.get(1), filter,
                Zone.LIBRARY, false, true, false, Zone.HAND,
                true, false, false
        ).setBackInRandomOrder(true).setText("Look at the top six cards of your library. " +
                "You may reveal a Warrior or Equipment card from among them and put it into your hand. " +
                "Put the rest on the bottom of your library in a random order."
        ), -2));

        // −3: Nahiri, Heir of the Ancients deals damage to target creature or planeswalker equal to twice the number of Equipment you control.
        Ability ability = new LoyaltyAbility(new DamageTargetEffect(xValue)
                .setText("{this} deals damage to target creature or planeswalker equal to twice the number of Equipment you control"), -3);
        ability.addTarget(new TargetCreatureOrPlaneswalker());
        this.addAbility(ability);
    }

    private NahiriHeirOfTheAncients(final NahiriHeirOfTheAncients card) {
        super(card);
    }

    @Override
    public NahiriHeirOfTheAncients copy() {
        return new NahiriHeirOfTheAncients(this);
    }
}

class NahiriHeirOfTheAncientsEffect extends OneShotEffect {

    private static final FilterPermanent filter
            = new FilterControlledPermanent(SubType.EQUIPMENT, "Equipment you control");

    NahiriHeirOfTheAncientsEffect() {
        super(Outcome.Benefit);
        staticText = "Create a 1/1 white Kor Warrior creature token. You may attach an Equipment you control to it.";
    }

    private NahiriHeirOfTheAncientsEffect(final NahiriHeirOfTheAncientsEffect effect) {
        super(effect);
    }

    @Override
    public NahiriHeirOfTheAncientsEffect copy() {
        return new NahiriHeirOfTheAncientsEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Token token = new KorWarriorToken();
        token.putOntoBattlefield(1, game, source.getSourceId(), source.getControllerId());
        Player player = game.getPlayer(source.getControllerId());
        if (player == null) {
            return false;
        }
        List<Permanent> tokens = token
                .getLastAddedTokenIds()
                .stream()
                .map(game::getPermanent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        int equipCount = game.getBattlefield().count(filter, source.getSourceId(), source.getControllerId(), game);
        if (tokens.isEmpty() || equipCount == 0 || !player.chooseUse(outcome, "", source, game)) {
            return true;
        }
        Permanent tokenCreature = tokens.get(0);
        if (tokens.size() > 1) {
            FilterPermanent tokenFilter = new FilterPermanent("token");
            tokenFilter.add(Predicates.or(
                    tokens.stream()
                            .map(MageObject::getId)
                            .map(PermanentIdPredicate::new)
                            .collect(Collectors.toSet())
            ));
            TargetPermanent target = new TargetPermanent(tokenFilter);
            target.setNotTarget(true);
            player.choose(outcome, target, source.getSourceId(), game);
            tokenCreature = game.getPermanent(target.getFirstTarget());
        }
        Permanent equipment;
        if (equipCount > 1) {
            TargetPermanent target = new TargetPermanent(filter);
            target.setNotTarget(true);
            player.choose(outcome, target, source.getSourceId(), game);
            equipment = game.getPermanent(target.getFirstTarget());
        } else {
            equipment = game
                    .getBattlefield()
                    .getActivePermanents(filter, source.getControllerId(), source.getSourceId(), game)
                    .stream()
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        if (equipment != null && tokenCreature != null) {
            equipment.attachTo(tokenCreature.getId(), game);
        }
        return true;
    }
}