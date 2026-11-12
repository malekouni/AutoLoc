package tn.esprit.tpautoloc.service.factory;

import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.service.observer.EmailNotificationObserver;
import tn.esprit.tpautoloc.service.observer.IReservationObserver;
import tn.esprit.tpautoloc.service.observer.SmsNotificationObserver;

/**
 * Patron GoF : FACTORY METHOD appliqué aux observateurs de notification.
 *
 * Permet d'obtenir le bon canal de notification (email, sms, ...)
 * sans exposer les classes concrètes au code appelant.
 *
 * Principe DIP : le code appelant dépend de l'interface IReservationObserver,
 * pas des implémentations Email/Sms.
 */
@Component
public class NotificationFactory {

    private final EmailNotificationObserver emailNotificationObserver;
    private final SmsNotificationObserver smsNotificationObserver;

    public NotificationFactory(EmailNotificationObserver emailNotificationObserver,
                               SmsNotificationObserver smsNotificationObserver) {
        this.emailNotificationObserver = emailNotificationObserver;
        this.smsNotificationObserver = smsNotificationObserver;
    }

    /**
     * Retourne l'observateur correspondant au canal demandé.
     *
     * @param canal "EMAIL" ou "SMS" (insensible à la casse)
     * @return l'observateur concret associé
     * @throws IllegalArgumentException si le canal est inconnu
     */
    public IReservationObserver creerObservateur(String canal) {
        return switch (canal.toUpperCase()) {
            case "EMAIL" -> emailNotificationObserver;
            case "SMS"   -> smsNotificationObserver;
            default -> throw new IllegalArgumentException("Canal de notification inconnu : " + canal);
        };
    }
}
